package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ShiftLogEntity
import com.example.ui.AppLanguage
import com.example.ui.AppUiState
import com.example.ui.AppViewModel
import com.example.ui.NavigationScreen
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.FurnaceAmber
import com.example.ui.theme.StatusGreen
import com.example.util.BackupManager
import com.example.util.PeerSyncServer
import com.example.util.QrCodeGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DataExchangeScreen(
    uiState: AppUiState,
    viewModel: AppViewModel,
    contentPadding: PaddingValues
) {
    BackHandler {
        viewModel.navigateTo(NavigationScreen.HOME)
    }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isAr = uiState.language == AppLanguage.ARABIC
    val shiftLogs by viewModel.shiftLogs.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    // File Import state
    var importedLogsPreview by remember { mutableStateOf<List<ShiftLogEntity>?>(null) }
    var importedHealthPreview by remember { mutableStateOf<Map<String, Int>?>(null) }
    var showImportConfirmDialog by remember { mutableStateOf(false) }

    // Document Picker launcher for JSON backup
    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            val jsonText = BackupManager.readTextFromUri(context, uri)
            if (!jsonText.isNullOrBlank()) {
                val parsedLogs = BackupManager.parseBackupJson(jsonText)
                val parsedHealth = BackupManager.extractHealthMap(jsonText)
                if (parsedLogs.isNotEmpty() || parsedHealth.isNotEmpty()) {
                    importedLogsPreview = parsedLogs
                    importedHealthPreview = parsedHealth
                    showImportConfirmDialog = true
                } else {
                    Toast.makeText(
                        context,
                        if (isAr) "الملف لا يحتوي على بيانات صالحة لفرن TAV 911" else "Invalid TAV 911 backup file",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    context,
                    if (isAr) "تعذر قراءة محتوى الملف" else "Could not read file",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Local Wi-Fi Server State
    var isServerRunning by remember { mutableStateOf(PeerSyncServer.isRunning) }
    var localIpAddress by remember { mutableStateOf(PeerSyncServer.getLocalIpAddress() ?: "127.0.0.1") }
    var peerHostUrlInput by remember { mutableStateOf(localIpAddress) }
    var isSyncingByWifi by remember { mutableStateOf(false) }
    var wifiSyncResult by remember { mutableStateOf<String?>(null) }

    // Auto-stop server when leaving screen if desired
    DisposableEffect(Unit) {
        onDispose {
            // Keep server running in background or stop as needed
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp)
            .testTag("data_exchange_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(CyanPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = null,
                                    tint = CyanPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isAr) "مركز تبادل البيانات والمزامنة الميدانية" else "Industrial Peer Data Exchange",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isAr) "مزامنة الشحنات والسجلات بين الهواتف بدون إنترنت" else "P2P Zero-Internet Sync for Multiple Devices",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Shortcut to open Scanner
                        IconButton(onClick = { viewModel.navigateTo(NavigationScreen.SCANNER) }) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = "Scanner", tint = CyanPrimary)
                        }
                    }
                }
            }
        }

        // Navigation Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = CyanPrimary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = if (isAr) "رمز QR مباشر" else "P2P QR Beam",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = if (isAr) "ملف JSON" else "File Exchange",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            text = if (isAr) "Wi-Fi محلي" else "Local Wi-Fi",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = {
                        Text(
                            text = if (isAr) "دليل العمل" else "Field Guide",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                // Tab 0: P2P QR Beam Transfer
                item {
                    P2pQrBeamSection(
                        isAr = isAr,
                        shiftLogs = shiftLogs,
                        healthRunHours = uiState.healthRunHours,
                        onOpenScanner = { viewModel.navigateTo(NavigationScreen.SCANNER) }
                    )
                }
            }
            1 -> {
                // Tab 1: File Exchange (JSON Backup & Restore via Bluetooth / Share)
                item {
                    FileExchangeSection(
                        isAr = isAr,
                        shiftLogs = shiftLogs,
                        healthRunHours = uiState.healthRunHours,
                        onExportAndShare = {
                            val backupFile = BackupManager.createBackupJson(context, shiftLogs, uiState.healthRunHours)
                            if (backupFile != null) {
                                BackupManager.shareBackup(context, backupFile)
                            } else {
                                Toast.makeText(context, if (isAr) "تعذر إنشاء ملف النسخ الاحتياطي" else "Failed to generate backup", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onPickImportFile = {
                            documentPickerLauncher.launch(arrayOf("application/json", "text/*"))
                        }
                    )
                }
            }
            2 -> {
                // Tab 2: Local Wi-Fi / Hotspot Peer Sync
                item {
                    LocalWifiSyncSection(
                        isAr = isAr,
                        isServerRunning = isServerRunning,
                        localIpAddress = localIpAddress,
                        peerHostUrlInput = peerHostUrlInput,
                        onHostUrlChange = { peerHostUrlInput = it },
                        isSyncing = isSyncingByWifi,
                        wifiSyncResult = wifiSyncResult,
                        onToggleServer = { shouldRun ->
                            if (shouldRun) {
                                PeerSyncServer.startServer(
                                    scope = coroutineScope,
                                    getLocalDataJson = {
                                        BackupManager.generateBackupJsonString(shiftLogs, uiState.healthRunHours)
                                    },
                                    onDataReceived = { incomingJson ->
                                        val logs = BackupManager.parseBackupJson(incomingJson)
                                        val health = BackupManager.extractHealthMap(incomingJson)
                                        coroutineScope.launch(Dispatchers.Main) {
                                            if (logs.isNotEmpty()) {
                                                viewModel.importShiftLogs(logs)
                                            }
                                            health.forEach { (key, hrs) ->
                                                viewModel.updateComponentRunHours(key, hrs)
                                            }
                                            Toast.makeText(
                                                context,
                                                if (isAr) "تم استلام ${logs.size} شحنة عبر Wi-Fi ودمجها بنجاح!" else "Received & merged ${logs.size} batches via Wi-Fi!",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                )
                                isServerRunning = true
                                localIpAddress = PeerSyncServer.getLocalIpAddress() ?: "127.0.0.1"
                                peerHostUrlInput = localIpAddress
                            } else {
                                PeerSyncServer.stopServer()
                                isServerRunning = false
                            }
                        },
                        onSyncNow = {
                            coroutineScope.launch {
                                isSyncingByWifi = true
                                wifiSyncResult = null
                                val myJson = BackupManager.generateBackupJsonString(shiftLogs, uiState.healthRunHours)
                                val (ok, resultText) = PeerSyncServer.syncWithPeer(peerHostUrlInput, myJson)
                                isSyncingByWifi = false
                                if (ok && resultText != null) {
                                    val remoteLogs = BackupManager.parseBackupJson(resultText)
                                    val remoteHealth = BackupManager.extractHealthMap(resultText)
                                    if (remoteLogs.isNotEmpty()) {
                                        viewModel.importShiftLogs(remoteLogs)
                                    }
                                    remoteHealth.forEach { (k, h) ->
                                        viewModel.updateComponentRunHours(k, h)
                                    }
                                    wifiSyncResult = if (isAr) "تمت المزامنة بنجاح! تم دمج ${remoteLogs.size} شحنة." else "Sync successful! Merged ${remoteLogs.size} batches."
                                } else {
                                    wifiSyncResult = if (isAr) "فشلت المزامنة: $resultText" else "Sync failed: $resultText"
                                }
                            }
                        }
                    )
                }
            }
            3 -> {
                // Tab 3: Interactive Field Guide
                item {
                    FieldGuideSection(isAr = isAr)
                }
            }
        }
    }

    // Confirmation Dialog for File Import
    if (showImportConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showImportConfirmDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = CyanPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAr) "تأكيد استيراد حزمة البيانات" else "Confirm Data Import",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isAr)
                            "تم العثور على ${importedLogsPreview?.size ?: 0} شحنة تشغيل و ${importedHealthPreview?.size ?: 0} مؤشر ساعات للقطع في الملف. هل ترغب في دمجها في قاعدة بيانات هاتفك؟"
                        else
                            "Found ${importedLogsPreview?.size ?: 0} shift logs and ${importedHealthPreview?.size ?: 0} component hour records. Merge into your local database?",
                        fontSize = 13.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            importedLogsPreview?.take(3)?.forEach { log ->
                                Text("• ${log.batchNumber} (${log.operatorName}) - ${log.processType}", fontSize = 11.sp)
                            }
                            if ((importedLogsPreview?.size ?: 0) > 3) {
                                Text("... و ${(importedLogsPreview?.size ?: 0) - 3} شحنات أخرى", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        importedLogsPreview?.let { viewModel.importShiftLogs(it) }
                        importedHealthPreview?.forEach { (key, hours) ->
                            viewModel.updateComponentRunHours(key, hours)
                        }
                        showImportConfirmDialog = false
                        Toast.makeText(
                            context,
                            if (isAr) "تم استيراد ودمج البيانات بنجاح!" else "Data imported and merged successfully!",
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                ) {
                    Text(if (isAr) "دمج في قاعدة البيانات" else "Merge & Save", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportConfirmDialog = false }) {
                    Text(if (isAr) "إلغاء" else "Cancel")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// Sub-Components for DataExchangeScreen
// -------------------------------------------------------------

@Composable
private fun P2pQrBeamSection(
    isAr: Boolean,
    shiftLogs: List<ShiftLogEntity>,
    healthRunHours: Map<String, Int>,
    onOpenScanner: () -> Unit
) {
    var shareType by remember { mutableIntStateOf(0) } // 0: Batch, 1: Health Run-Hours
    var selectedLogIndex by remember { mutableIntStateOf(0) }

    val qrContent = remember(shareType, selectedLogIndex, shiftLogs, healthRunHours) {
        if (shareType == 0) {
            val log = shiftLogs.getOrNull(selectedLogIndex) ?: ShiftLogEntity(
                batchNumber = "B-DEMO-01",
                operatorName = "Field Tech",
                processType = "Vacuum Hardening",
                loadWeightKg = 850.0,
                targetTempC = 1050.0,
                quenchPressureBar = 6.0,
                status = "Completed",
                leakRateQ = 0.0008,
                notes = "High-vac soak 120min at 1050C"
            )
            BackupManager.generateBatchQrPayload(log)
        } else {
            BackupManager.generateHealthQrPayload(healthRunHours)
        }
    }

    val qrBitmap = remember(qrContent) {
        QrCodeGenerator.generateQrBitmap(qrContent, size = 320)
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAr) "بث البيانات عبر رمز الاستجابة السريعة (QR Beam)" else "Direct P2P QR Beam",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                // Mode Selector
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (shareType == 0) CyanPrimary else Color.Transparent,
                        border = BorderStroke(1.dp, if (shareType == 0) CyanPrimary else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.clickable { shareType = 0 }
                    ) {
                        Text(
                            text = if (isAr) "شحنة وردية" else "Shift Batch",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (shareType == 0) Color.Black else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (shareType == 1) CyanPrimary else Color.Transparent,
                        border = BorderStroke(1.dp, if (shareType == 1) CyanPrimary else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.clickable { shareType = 1 }
                    ) {
                        Text(
                            text = if (isAr) "صحة القطع" else "Health Hours",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (shareType == 1) Color.Black else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // If batches available, allow selecting which batch to beam
            if (shareType == 0 && shiftLogs.isNotEmpty()) {
                Text(
                    text = if (isAr) "اختر الشحنة المراد إرسالها إلى هاتف الزميل:" else "Select Batch to beam to peer device:",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    shiftLogs.take(3).forEachIndexed { idx, log ->
                        val isSel = selectedLogIndex == idx
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSel) CyanPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (isSel) CyanPrimary else Color.Transparent),
                            modifier = Modifier.weight(1f).clickable { selectedLogIndex = idx }
                        ) {
                            Column(modifier = Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(log.batchNumber, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                Text("${log.targetTempC.toInt()}°C", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // QR Code Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = BorderStroke(3.dp, CyanPrimary),
                modifier = Modifier.padding(8.dp)
            ) {
                Image(
                    bitmap = qrBitmap.asImageBitmap(),
                    contentDescription = "Sync QR Code",
                    modifier = Modifier
                        .size(240.dp)
                        .padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isAr)
                    "📲 اطلب من زميلك فتح الماسح الضوئي (QR Scanner) وتوجيه كاميرا هاتفه نحو هذا الرمز لنسخ الشحنة فورياً بدون إنترنت!"
                else
                    "📲 Ask your peer to open the QR Scanner and point their camera at this code to import instantly without internet!",
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onOpenScanner,
                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isAr) "فتح الماسح الضوئي لاستلام كود من هاتف آخر" else "Open Scanner to Receive from Peer Phone",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FileExchangeSection(
    isAr: Boolean,
    shiftLogs: List<ShiftLogEntity>,
    healthRunHours: Map<String, Int>,
    onExportAndShare: () -> Unit,
    onPickImportFile: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Export Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = CyanPrimary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isAr) "1. تصدير حزمة المزامنة (Share Database Bundle)" else "1. Export Sync Package",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isAr)
                        "إنشاء ملف JSON مشفر يحتوي على كافة شحنات الوردية (${shiftLogs.size} شحنة) ومؤشرات ساعات القطع (${healthRunHours.size} قطعة) ومشاركته عبر البلوتوث أو Quick Share أو واتساب."
                    else
                        "Generate an encrypted JSON backup file containing all shift logs (${shiftLogs.size} batches) and component hours (${healthRunHours.size} items) to share via Bluetooth, Quick Share, or WhatsApp.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onExportAndShare,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAr) "تصدير ومشاركة الملف (Bluetooth / WhatsApp)" else "Export & Share File (Bluetooth / Quick Share)",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Import Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            border = BorderStroke(1.dp, StatusGreen.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = StatusGreen)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isAr) "2. استيراد ملف مزامنة من هاتف آخر" else "2. Import Sync Package from Peer Phone",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isAr)
                        "اختر ملف النسخ الاحتياطي (TAV_911_Backup_*.json) الذي تم استلامه عبر البلوتوث أو واتساب من زميلك لدمج الشحنات وساعات القطع في قاعدة بياناتك فورياً."
                    else
                        "Select a backup JSON file received via Bluetooth or messaging from your peer to merge shift logs and health hours directly into your local database.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onPickImportFile,
                    colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAr) "اختيار واستيراد ملف JSON" else "Select & Import JSON File",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun LocalWifiSyncSection(
    isAr: Boolean,
    isServerRunning: Boolean,
    localIpAddress: String,
    peerHostUrlInput: String,
    onHostUrlChange: (String) -> Unit,
    isSyncing: Boolean,
    wifiSyncResult: String?,
    onToggleServer: (Boolean) -> Unit,
    onSyncNow: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Mode A: Server Host
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Wifi, contentDescription = null, tint = if (isServerRunning) StatusGreen else FurnaceAmber)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isAr) "سيرفر المزامنة في هذا الهاتف" else "Local Sync Server (Host Mode)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isServerRunning)
                                (if (isAr) "السيرفر نشط على IP: $localIpAddress:8911" else "Active on $localIpAddress:8911")
                            else
                                (if (isAr) "غير نشط • فعّله لاستقبال بيانات الهواتف" else "Offline • Toggle on to receive data"),
                            fontSize = 11.sp,
                            color = if (isServerRunning) StatusGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Switch(
                    checked = isServerRunning,
                    onCheckedChange = { onToggleServer(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = StatusGreen,
                        checkedTrackColor = StatusGreen.copy(alpha = 0.3f)
                    )
                )
            }

            if (isServerRunning) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.3f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (isAr) "📡 هذا الهاتف يعمل كخادم محلي في الورشة." else "📡 Device is acting as local factory server.",
                            fontSize = 11.sp,
                            color = StatusGreen
                        )
                        Text(
                            text = if (isAr)
                                "تأكد أن هاتف زميلك متصل بنفس شبكة Wi-Fi أو بنقطة اتصال (Hotspot) هاتفك، ثم اطلب منه إدخال العنوان أعلاه أو مسحه بالماسح."
                            else
                                "Ensure peer phone is on the same Wi-Fi or connected to your Mobile Hotspot, then have them enter this IP to sync.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(16.dp))

            // Mode B: Client Sync
            Text(
                text = if (isAr) "الاتصال بهاتف زميلك والمزامنة معه (Client Mode):" else "Connect to Peer Device & Sync:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = peerHostUrlInput,
                    onValueChange = onHostUrlChange,
                    label = { Text(if (isAr) "عنوان IP لهاتف الزميل" else "Peer IP Address") },
                    placeholder = { Text("192.168.1.50") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Button(
                    onClick = onSyncNow,
                    enabled = !isSyncing && peerHostUrlInput.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isSyncing) (if (isAr) "جاري..." else "Syncing...") else (if (isAr) "مزامنة الآن" else "Sync Now"),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (wifiSyncResult != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (wifiSyncResult.contains("نجاح") || wifiSyncResult.contains("successful")) StatusGreen.copy(alpha = 0.2f) else AlertRed.copy(alpha = 0.2f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = wifiSyncResult,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (wifiSyncResult.contains("نجاح") || wifiSyncResult.contains("successful")) StatusGreen else AlertRed,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FieldGuideSection(isAr: Boolean) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.HelpOutline, contentDescription = null, tint = CyanPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isAr) "دليل تبادل البيانات الميدانية بين نسخ التطبيق" else "Peer Exchange Field Best Practices",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Scenario 1
            Column {
                Text(
                    text = if (isAr) "🟢 السيناريو 1: داخل الورشة بدون أي إنترنت أو شبكة (Offline)" else "🟢 Scenario 1: Isolated Factory Floor (Zero Internet)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = CyanPrimary
                )
                Text(
                    text = if (isAr)
                        "1. افتح تبويب 'رمز QR مباشر' واختر الشحنة أو ساعات القطع.\n2. اطلب من زميلك فتح 'الماسح الضوئي' في هاتفه وتوجيه الكاميرا نحو شاشتك.\n3. سيصدر هاتف زميلك رنيناً وتظهر نافذة التأكيد ليحفظ البيانات بضغطة زر واحدة."
                    else
                        "1. Open 'P2P QR Beam' tab and pick the batch or health hours.\n2. Have your peer open 'QR Scanner' on their device and point at your screen.\n3. The peer device beeps and imports the record into its local database instantly.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Scenario 2
            Column {
                Text(
                    text = if (isAr) "🔵 السيناريو 2: تسليم الوردية بالكامل وإرسال التقرير" else "🔵 Scenario 2: Complete Shift Handover & File Share",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = FurnaceAmber
                )
                Text(
                    text = if (isAr)
                        "1. افتح تبويب 'ملف JSON' واضغط 'تصدير ومشاركة الملف'.\n2. أرسل الملف بالبلوتوث (Bluetooth) أو Quick Share إلى هاتف المهندس أو المشغل التالي.\n3. يضغط المستلم 'اختيار واستيراد ملف JSON' ليتم دمج كافة السجلات دفعة واحدة."
                    else
                        "1. Open 'File Exchange' tab and tap 'Export & Share File'.\n2. Send the JSON bundle via Bluetooth or Quick Share to the incoming shift engineer.\n3. The recipient taps 'Select & Import JSON File' to merge all records at once.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Scenario 3
            Column {
                Text(
                    text = if (isAr) "🟣 السيناريو 3: مزامنة فورية عبر شبكة Wi-Fi أو نقطة اتصال" else "🟣 Scenario 3: Local Wi-Fi or Mobile Hotspot Sync",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = StatusGreen
                )
                Text(
                    text = if (isAr)
                        "1. افتح نقطة اتصال (Personal Hotspot) من هاتف الوردية ودع الأجهزة الأخرى تتصل بها.\n2. شغّل مفتاح 'سيرفر المزامنة' في الهاتف الرئيسي.\n3. في الهواتف الأخرى، ادخل عنوان IP واضغط 'مزامنة الآن' لتبادل البيانات بالاتجاهين."
                    else
                        "1. Enable Personal Hotspot on one phone and have other devices connect to it.\n2. Toggle ON 'Local Sync Server' on the host device.\n3. On other devices, enter the host IP and tap 'Sync Now' for bidirectional synchronization.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
