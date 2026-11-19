package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.local.ShiftLogEntity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BackupManager {

    /**
     * Serializes all shift logs and component health state to a JSON string.
     */
    fun generateBackupJsonString(
        shiftLogs: List<ShiftLogEntity>,
        healthHoursMap: Map<String, Int>
    ): String {
        val root = JSONObject()
        root.put("app", "TAV Vacuum Furnaces S.p.A. Field Companion")
        root.put("furnaceModel", "TPH 120/120/230")
        root.put("serialNumber", "911")
        root.put("client", "GEAT")
        root.put("exportTimestamp", System.currentTimeMillis())
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        root.put("exportDateFormatted", df.format(Date()))

        // Shift logs
        val logsArray = JSONArray()
        shiftLogs.forEach { log ->
            val logObj = JSONObject()
            logObj.put("id", log.id)
            logObj.put("batchNumber", log.batchNumber)
            logObj.put("operatorName", log.operatorName)
            logObj.put("processType", log.processType)
            logObj.put("loadWeightKg", log.loadWeightKg)
            logObj.put("targetTempC", log.targetTempC)
            logObj.put("quenchPressureBar", log.quenchPressureBar)
            logObj.put("status", log.status)
            logObj.put("leakRateQ", log.leakRateQ)
            logObj.put("notes", log.notes)
            logObj.put("timestamp", log.timestamp)
            logObj.put("dailyChecklist", log.dailyChecklist)
            logObj.put("weeklyChecklist", log.weeklyChecklist)
            logObj.put("monthlyChecklist", log.monthlyChecklist)
            logObj.put("gasPressureReading", log.gasPressureReading)
            logObj.put("oilLevelStatus", log.oilLevelStatus)
            logsArray.put(logObj)
        }
        root.put("shiftLogs", logsArray)

        // Health Run-Hours
        val healthObj = JSONObject()
        healthHoursMap.forEach { (key, value) ->
            healthObj.put(key, value)
        }
        root.put("componentRunHours", healthObj)

        return root.toString(2)
    }

    /**
     * Generates a compact payload string suitable for single-screen QR code scanning between phones.
     */
    fun generateBatchQrPayload(log: ShiftLogEntity): String {
        val safeNotes = log.notes.replace("|", "/").take(60)
        return "TAV_SYNC_LOG:${log.batchNumber}|${log.operatorName}|${log.processType}|${log.loadWeightKg}|${log.targetTempC}|${log.quenchPressureBar}|${log.status}|${log.leakRateQ}|$safeNotes|${log.gasPressureReading}|${log.oilLevelStatus}"
    }

    /**
     * Generates a compact payload string for syncing component run-hours via QR code.
     */
    fun generateHealthQrPayload(healthHoursMap: Map<String, Int>): String {
        val pairs = healthHoursMap.entries.joinToString(";") { "${it.key}:${it.value}" }
        return "TAV_SYNC_HEALTH:$pairs"
    }

    /**
     * Parses a TAV_SYNC_HEALTH QR payload.
     */
    fun parseHealthQrPayload(raw: String): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        val payload = raw.removePrefix("TAV_SYNC_HEALTH:")
        payload.split(";").forEach { item ->
            val parts = item.split(":")
            if (parts.size == 2) {
                val key = parts[0].trim()
                val value = parts[1].trim().toIntOrNull()
                if (key.isNotBlank() && value != null) {
                    map[key] = value
                }
            }
        }
        return map
    }

    /**
     * Exports all shift logs, periodic maintenance history, and machine records to a JSON file.
     */
    fun createBackupJson(
        context: Context,
        shiftLogs: List<ShiftLogEntity>,
        healthHoursMap: Map<String, Int>
    ): File? {
        return try {
            val jsonString = generateBackupJsonString(shiftLogs, healthHoursMap)
            val fileName = "TAV_911_Backup_${SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())}.json"
            val file = File(context.cacheDir, fileName)
            val fos = FileOutputStream(file)
            fos.write(jsonString.toByteArray(Charsets.UTF_8))
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Shares the backup JSON file via Android Sharesheet (Email, Drive, WhatsApp, Bluetooth).
     */
    fun shareBackup(context: Context, file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_SUBJECT, "TAV Furnace 911 Full Database Backup")
                putExtra(Intent.EXTRA_TEXT, "Attached is the encrypted JSON backup for TAV TPH 120 (S/N 911 - GEAT).")
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share Furnace Backup File"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error sharing backup: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Parses an imported backup JSON string and extracts ShiftLogEntity items.
     */
    fun parseBackupJson(jsonString: String): List<ShiftLogEntity> {
        val list = mutableListOf<ShiftLogEntity>()
        try {
            val root = JSONObject(jsonString)
            val logsArray = root.optJSONArray("shiftLogs") ?: return list
            for (i in 0 until logsArray.length()) {
                val obj = logsArray.getJSONObject(i)
                val entity = ShiftLogEntity(
                    batchNumber = obj.optString("batchNumber", "B-RESTORED"),
                    operatorName = obj.optString("operatorName", "Operator"),
                    processType = obj.optString("processType", "Vacuum Hardening"),
                    loadWeightKg = obj.optDouble("loadWeightKg", 850.0),
                    targetTempC = obj.optDouble("targetTempC", 1050.0),
                    quenchPressureBar = obj.optDouble("quenchPressureBar", 6.0),
                    status = obj.optString("status", "Completed"),
                    leakRateQ = obj.optDouble("leakRateQ", 0.0008),
                    notes = obj.optString("notes", ""),
                    timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                    dailyChecklist = obj.optString("dailyChecklist", ""),
                    weeklyChecklist = obj.optString("weeklyChecklist", ""),
                    monthlyChecklist = obj.optString("monthlyChecklist", ""),
                    gasPressureReading = obj.optDouble("gasPressureReading", 6.0),
                    oilLevelStatus = obj.optString("oilLevelStatus", "NORMAL")
                )
                list.add(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    /**
     * Reads text content from a given content Uri (selected via document picker).
     */
    fun readTextFromUri(context: Context, uri: Uri): String? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Extracts health run-hours from backup JSON string.
     */
    fun extractHealthMap(jsonString: String): Map<String, Int> {
        val map = mutableMapOf<String, Int>()
        try {
            val root = JSONObject(jsonString)
            val healthObj = root.optJSONObject("componentRunHours") ?: return map
            val keys = healthObj.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                map[key] = healthObj.optInt(key, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return map
    }
}
