package com.example.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.net.URL

/**
 * Local Wi-Fi / Hotspot Peer-to-Peer Data Exchange Server.
 * Allows multiple Android phones/tablets running this app to exchange Shift Logs,
 * Recipes, and Health hours directly over the same Wi-Fi network or mobile Hotspot
 * without requiring any internet access or cloud server.
 */
object PeerSyncServer {

    const val DEFAULT_PORT = 8911
    private var serverJob: Job? = null
    private var serverSocket: ServerSocket? = null
    var isRunning: Boolean = false
        private set

    /**
     * Resolves the current local IPv4 address (e.g., 192.168.1.x on Wi-Fi or 192.168.43.1 on Hotspot).
     */
    fun getLocalIpAddress(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                if (networkInterface.isLoopback || !networkInterface.isUp) continue

                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (address is Inet4Address && !address.isLoopbackAddress) {
                        val hostAddress = address.hostAddress
                        if (hostAddress != null && (hostAddress.startsWith("192.168.") || hostAddress.startsWith("10.") || hostAddress.startsWith("172."))) {
                            return hostAddress
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("PeerSyncServer", "Error finding IP address", e)
        }
        return null
    }

    /**
     * Starts the lightweight peer HTTP server on port 8911.
     * @param getLocalDataJson Callback returning current JSON export of this device.
     * @param onDataReceived Callback when peer sends new data to merge.
     */
    fun startServer(
        scope: CoroutineScope,
        getLocalDataJson: () -> String,
        onDataReceived: (String) -> Unit
    ) {
        if (isRunning) return

        serverJob = scope.launch(Dispatchers.IO) {
            try {
                serverSocket = ServerSocket(DEFAULT_PORT).apply {
                    reuseAddress = true
                }
                isRunning = true
                Log.d("PeerSyncServer", "Server started on port $DEFAULT_PORT")

                while (isActive && !serverSocket!!.isClosed) {
                    val clientSocket = try {
                        serverSocket?.accept() ?: break
                    } catch (_: Exception) {
                        break
                    }
                    launch(Dispatchers.IO) {
                        handleClient(clientSocket, getLocalDataJson, onDataReceived)
                    }
                }
            } catch (e: Exception) {
                Log.e("PeerSyncServer", "Server exception", e)
            } finally {
                isRunning = false
            }
        }
    }

    /**
     * Stops the local peer sync server.
     */
    fun stopServer() {
        try {
            isRunning = false
            serverSocket?.close()
            serverSocket = null
            serverJob?.cancel()
            serverJob = null
        } catch (e: Exception) {
            Log.e("PeerSyncServer", "Error stopping server", e)
        }
    }

    private fun handleClient(
        socket: Socket,
        getLocalDataJson: () -> String,
        onDataReceived: (String) -> Unit
    ) {
        try {
            socket.soTimeout = 10000
            val reader = BufferedReader(InputStreamReader(socket.getInputStream(), Charsets.UTF_8))
            val writer = OutputStreamWriter(socket.getOutputStream(), Charsets.UTF_8)

            val requestLine = reader.readLine() ?: return
            val parts = requestLine.split(" ")
            val method = parts.getOrNull(0) ?: "GET"
            val path = parts.getOrNull(1) ?: "/"

            var contentLength = 0
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (line.isNullOrEmpty()) break
                if (line!!.startsWith("Content-Length:", ignoreCase = true)) {
                    contentLength = line!!.substringAfter(":").trim().toIntOrNull() ?: 0
                }
            }

            if (method.equals("POST", ignoreCase = true) && path.startsWith("/sync")) {
                val bodyChars = CharArray(contentLength)
                var readTotal = 0
                while (readTotal < contentLength) {
                    val count = reader.read(bodyChars, readTotal, contentLength - readTotal)
                    if (count == -1) break
                    readTotal += count
                }
                val incomingJson = String(bodyChars, 0, readTotal)
                if (incomingJson.isNotBlank()) {
                    onDataReceived(incomingJson)
                }

                // Return current device data in response so both sides sync
                val responseJson = getLocalDataJson()
                val responseBytes = responseJson.toByteArray(Charsets.UTF_8)
                writer.write("HTTP/1.1 200 OK\r\n")
                writer.write("Content-Type: application/json; charset=UTF-8\r\n")
                writer.write("Content-Length: ${responseBytes.size}\r\n")
                writer.write("Connection: close\r\n\r\n")
                writer.write(responseJson)
                writer.flush()
            } else {
                // Default GET /sync returns device backup
                val responseJson = getLocalDataJson()
                val responseBytes = responseJson.toByteArray(Charsets.UTF_8)
                writer.write("HTTP/1.1 200 OK\r\n")
                writer.write("Content-Type: application/json; charset=UTF-8\r\n")
                writer.write("Content-Length: ${responseBytes.size}\r\n")
                writer.write("Connection: close\r\n\r\n")
                writer.write(responseJson)
                writer.flush()
            }
        } catch (e: Exception) {
            Log.e("PeerSyncServer", "Error handling peer client", e)
        } finally {
            try { socket.close() } catch (_: Exception) {}
        }
    }

    /**
     * Client call: connects to peer server at [hostUrl], posts local data and retrieves remote data.
     * Returns Pair(isSuccess, remoteJsonOrError).
     */
    suspend fun syncWithPeer(
        hostUrl: String,
        localDataJson: String
    ): Pair<Boolean, String?> = withContext(Dispatchers.IO) {
        try {
            var fullUrl = hostUrl.trim()
            if (!fullUrl.startsWith("http://") && !fullUrl.startsWith("https://")) {
                fullUrl = "http://$fullUrl"
            }
            if (!fullUrl.contains(":$DEFAULT_PORT") && !fullUrl.contains("/sync")) {
                fullUrl = "$fullUrl:$DEFAULT_PORT/sync"
            } else if (!fullUrl.endsWith("/sync")) {
                fullUrl = "$fullUrl/sync"
            }

            val url = URL(fullUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.connectTimeout = 8000
            conn.readTimeout = 12000
            conn.doOutput = true
            conn.doInput = true
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")

            val outBytes = localDataJson.toByteArray(Charsets.UTF_8)
            conn.setRequestProperty("Content-Length", outBytes.size.toString())
            conn.outputStream.use { os ->
                os.write(outBytes)
                os.flush()
            }

            val responseCode = conn.responseCode
            if (responseCode == 200) {
                val responseText = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                Pair(true, responseText)
            } else {
                Pair(false, "Server returned HTTP $responseCode")
            }
        } catch (e: Exception) {
            Pair(false, e.localizedMessage ?: "Connection error")
        }
    }
}
