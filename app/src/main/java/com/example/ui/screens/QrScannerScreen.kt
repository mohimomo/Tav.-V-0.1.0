package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.FurnaceRepository
import com.example.model.ComponentItem
import com.example.ui.AppLanguage
import com.example.ui.AppUiState
import com.example.ui.AppViewModel
import com.example.ui.NavigationScreen
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.FurnaceAmber
import com.example.ui.theme.StatusGreen

@Composable
fun QrScannerScreen(
    uiState: AppUiState,
    viewModel: AppViewModel,
    contentPadding: PaddingValues
) {
    BackHandler {
        viewModel.navigateTo(NavigationScreen.HOME)
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val isAr = uiState.language == AppLanguage.ARABIC

    // Camera permission state
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var cameraError by remember { mutableStateOf<String?>(null) }
    var isTorchOn by remember { mutableStateOf(false) }
    var cameraInstance by remember { mutableStateOf<Camera?>(null) }
    var manualInputTag by remember { mutableStateOf("6GQZ10") }
    var matchedComponent by remember {
        mutableStateOf<ComponentItem?>(
            FurnaceRepository.components.find { it.tag.contains("6GQZ10") }
        )
    }

    var scannedSuccessBanner by remember { mutableStateOf<String?>(null) }
    var incomingP2pLog by remember { mutableStateOf<com.example.data.local.ShiftLogEntity?>(null) }
    var showP2pImportDialog by remember { mutableStateOf(false) }

    fun triggerScanHaptic() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                val vm = context.getSystemService(android.content.Context.VIBRATOR_MANAGER_SERVICE) as? android.os.VibratorManager
                vm?.defaultVibrator?.vibrate(android.os.VibrationEffect.createOneShot(180, android.os.VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val v = context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? android.os.Vibrator
                v?.vibrate(180)
            }
            // Industrial beep on scan detection
            val toneG = android.media.ToneGenerator(android.media.AudioManager.STREAM_NOTIFICATION, 85)
            toneG.startTone(android.media.ToneGenerator.TONE_PROP_BEEP, 130)
        } catch (_: Exception) {}
    }

    fun handleScannedCode(rawText: String) {
        triggerScanHaptic()

        // 1. Check P2P Shift Log Sync from another phone
        if (rawText.startsWith("TAV_SYNC_LOG:")) {
            val payload = rawText.removePrefix("TAV_SYNC_LOG:")
            val parts = payload.split("|")
            if (parts.size >= 4) {
                val entity = com.example.data.local.ShiftLogEntity(
                    batchNumber = parts.getOrNull(0) ?: "B-SYNC",
                    operatorName = parts.getOrNull(1) ?: "Peer Operator",
                    processType = parts.getOrNull(2) ?: "Vacuum Hardening",
                    loadWeightKg = parts.getOrNull(3)?.toDoubleOrNull() ?: 850.0,
                    targetTempC = parts.getOrNull(4)?.toDoubleOrNull() ?: 1050.0,
                    quenchPressureBar = parts.getOrNull(5)?.toDoubleOrNull() ?: 6.0,
                    status = parts.getOrNull(6) ?: "Completed",
                    leakRateQ = parts.getOrNull(7)?.toDoubleOrNull() ?: 0.0008,
                    notes = parts.getOrNull(8) ?: "P2P QR Synced from Peer Device"
                )
                incomingP2pLog = entity
                showP2pImportDialog = true
                scannedSuccessBanner = if (isAr) "تم استلام شحنة من هاتف آخر [${entity.batchNumber}]" else "Received Batch [${entity.batchNumber}]"
                return
            }
        }

        // 2. Check P2P Health Run-Hours Sync
        if (rawText.startsWith("TAV_SYNC_HEALTH:")) {
            val healthMap = com.example.util.BackupManager.parseHealthQrPayload(rawText)
            healthMap.forEach { (key, hours) ->
                viewModel.updateComponentRunHours(key, hours)
            }
            scannedSuccessBanner = if (isAr) "تمت مزامنة ساعات تشغيل ${healthMap.size} مكونات!" else "Synced ${healthMap.size} component run-hours!"
            return
        }

        // 3. Component Tag Recognition
        var extractedTag = rawText.trim()
        if (rawText.contains("TAG=")) {
            val tagPart = rawText.substringAfter("TAG=").substringBefore(":")
            if (tagPart.isNotBlank()) extractedTag = tagPart.trim()
        }

        val found = FurnaceRepository.components.find {
            it.tag.equals(extractedTag, ignoreCase = true) ||
            it.tag.contains(extractedTag, ignoreCase = true) ||
            rawText.contains(it.tag, ignoreCase = true) ||
            it.pidCode.equals(extractedTag, ignoreCase = true) ||
            it.manufacturerModel.contains(extractedTag, ignoreCase = true) ||
            it.nameEn.contains(extractedTag, ignoreCase = true)
        }

        if (found != null) {
            matchedComponent = found
            manualInputTag = found.tag
            scannedSuccessBanner = if (isAr) "تم التعرف بنجاح على: ${found.tag} (${found.nameAr})" else "Recognized: ${found.tag} (${found.nameEn})"
        } else {
            manualInputTag = extractedTag
            scannedSuccessBanner = if (isAr) "تم مسح الرمز: $extractedTag" else "Scanned: $extractedTag"
        }
    }

    // Gallery Photo Picker launcher to decode QR from photo or screenshot
    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                if (bitmap != null) {
                    val decoded = com.example.util.QrCodeAnalyzer.decodeBitmap(bitmap)
                    if (!decoded.isNullOrBlank()) {
                        handleScannedCode(decoded)
                    } else {
                        android.widget.Toast.makeText(
                            context,
                            if (isAr) "لم يتم العثور على رمز QR أو باركود في الصورة المحددة" else "No valid QR/barcode found in image",
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                android.widget.Toast.makeText(context, "Error: ${e.localizedMessage}", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            cameraError = if (isAr) "تم رفض إذن الكاميرا. يرجى تفعيله من إعدادات الهاتف لمسح الرموز مباشرة."
            else "Camera permission was denied. Please grant permission in device settings."
        } else {
            cameraError = null
        }
    }

    // Auto-request permission on first load if not granted
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Viewfinder laser animation
    val infiniteTransition = rememberInfiniteTransition(label = "scanner_laser")
    val laserOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 180f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_offset"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp)
            .testTag("qr_scanner_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
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
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(CyanPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    tint = CyanPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isAr) "الماسح الضوئي الذكي للمعدات" else "Equipment Barcode & QR Scanner",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Live CameraX Video Feed & Fast Component Detection",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Torch Button if camera active
                        if (hasCameraPermission && cameraInstance != null) {
                            IconButton(
                                onClick = {
                                    val nextState = !isTorchOn
                                    cameraInstance?.cameraControl?.enableTorch(nextState)
                                    isTorchOn = nextState
                                }
                            ) {
                                Icon(
                                    imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                    contentDescription = "Flashlight",
                                    tint = if (isTorchOn) FurnaceAmber else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Camera Viewfinder Box
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                border = BorderStroke(2.dp, CyanPrimary.copy(alpha = 0.7f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (hasCameraPermission) {
                        // Live CameraX Preview
                        AndroidView(
                            factory = { ctx ->
                                val previewView = PreviewView(ctx).apply {
                                    scaleType = PreviewView.ScaleType.FILL_CENTER
                                }

                                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                cameraProviderFuture.addListener({
                                    try {
                                        val cameraProvider = cameraProviderFuture.get()
                                        val preview = Preview.Builder().build().also {
                                            it.surfaceProvider = previewView.surfaceProvider
                                        }

                                        val imageAnalysis = ImageAnalysis.Builder()
                                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                            .build()

                                        imageAnalysis.setAnalyzer(
                                            ContextCompat.getMainExecutor(ctx),
                                            com.example.util.QrCodeAnalyzer { scannedCode ->
                                                handleScannedCode(scannedCode)
                                            }
                                        )

                                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                                        cameraProvider.unbindAll()
                                        val camera = cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            cameraSelector,
                                            preview,
                                            imageAnalysis
                                        )
                                        cameraInstance = camera
                                        cameraError = null
                                    } catch (exc: Exception) {
                                        Log.e("QrScanner", "Camera binding failed", exc)
                                        cameraError = if (isAr)
                                            "تعذر تشغيل الكاميرا على هذا الجهاز/المحاكي: ${exc.localizedMessage ?: "خطأ في عتاد الكاميرا"}"
                                        else
                                            "Camera failed to bind: ${exc.localizedMessage}"
                                    }
                                }, ContextCompat.getMainExecutor(ctx))

                                previewView
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // If permission is missing or error
                    if (!hasCameraPermission || cameraError != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF0F172A).copy(alpha = 0.92f))
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (!hasCameraPermission) Icons.Default.CameraAlt else Icons.Default.VideocamOff,
                                contentDescription = null,
                                tint = FurnaceAmber,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (!hasCameraPermission)
                                    (if (isAr) "إذن الكاميرا مطلوب لتشغيل الماسح" else "Camera Permission Required")
                                else
                                    (cameraError ?: "Camera unavailable"),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (!hasCameraPermission)
                                    (if (isAr) "انقر على الزر أدناه لمنح الصلاحية وتشغيل بث الكاميرا المباشر"
                                    else "Tap below to allow camera access and start live scanning")
                                else
                                    (if (isAr) "يمكنك اختيار كود القطعة من القائمة السريعة أدناه للمتابعة بدون كاميرا"
                                    else "You can select quick component tags below for instant offline lookup"),
                                color = Color.LightGray,
                                fontSize = 11.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            if (!hasCameraPermission) {
                                Button(
                                    onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isAr) "تشغيل الكاميرا الآن" else "Enable Camera Now", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Reticle overlay
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .border(2.dp, CyanPrimary, RoundedCornerShape(12.dp))
                    ) {
                        // Animated Scanning Laser line
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(2.dp)
                                .offset(y = laserOffsetY.dp)
                                .background(CyanPrimary)
                        )
                    }

                    // Live Scanned Success Pill Overlay
                    if (scannedSuccessBanner != null) {
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = StatusGreen,
                            contentColor = Color.Black,
                            shadowElevation = 6.dp,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = scannedSuccessBanner.orEmpty(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Bottom helper label in camera frame
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 10.dp)
                            .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = if (hasCameraPermission && cameraError == null) StatusGreen else FurnaceAmber,
                                modifier = Modifier.size(8.dp)
                            ) {}
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (hasCameraPermission && cameraError == null)
                                    (if (isAr) "الكاميرا نشطة • وجّه نحو ملصق القطعة" else "Camera Active • Point at Part Label")
                                else
                                    (if (isAr) "وضع المسح اليدوي والبديل متاح" else "Manual & Tag Simulation Ready"),
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Actions Bar: Scan from Photo / Open Peer Data Exchange
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        galleryPickerLauncher.launch(
                            androidx.activity.result.PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) "مسح صورة من المعرض" else "Scan from Photo",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { viewModel.navigateTo(NavigationScreen.DATA_EXCHANGE) },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Sync, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) "تبادل البيانات P2P" else "Peer Sync",
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Quick Component Tags for Instant Testing
        item {
            Text(
                text = if (isAr) "أكواد ورموز سريعة للفحص الميداني:" else "Quick Test Furnace Component Codes:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            val sampleTags = listOf("6GQZ10", "182BFL", "Leybold SV630B", "WAU2001", "NHS 35", "LoVVa1", "HiVVa1", "152QAC", "ACT1")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(sampleTags) { tag ->
                    val isSelected = manualInputTag.contains(tag)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) CyanPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, if (isSelected) CyanPrimary else Color.Transparent),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                manualInputTag = tag
                                matchedComponent = FurnaceRepository.components.find {
                                    it.tag.contains(tag, ignoreCase = true) || it.nameEn.contains(tag, ignoreCase = true)
                                }
                            }
                    ) {
                        Text(
                            text = tag,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) CyanPrimary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Manual Tag Lookup Field
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = manualInputTag,
                    onValueChange = {
                        manualInputTag = it
                        matchedComponent = FurnaceRepository.components.find { comp ->
                            comp.tag.contains(it, ignoreCase = true) || comp.nameEn.contains(it, ignoreCase = true)
                        }
                    },
                    label = { Text(if (isAr) "إدخال كود القطعة يدوياً" else "Manual Tag / Code Input") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Button(
                    onClick = {
                        matchedComponent = FurnaceRepository.components.find { comp ->
                            comp.tag.contains(manualInputTag, ignoreCase = true) || comp.nameEn.contains(manualInputTag, ignoreCase = true)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Lookup", tint = Color.Black)
                }
            }
        }

        // Matched Component Result Card
        item {
            val comp = matchedComponent
            if (comp != null) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = CyanPrimary,
                                contentColor = Color.Black
                            ) {
                                Text(
                                    text = comp.tag,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = FurnaceAmber.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = comp.subsystem.name,
                                    color = FurnaceAmber,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (isAr) comp.nameAr else comp.nameEn,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Model: ${comp.manufacturerModel}",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Location: ${if (isAr && comp.locationAr.isNotBlank()) comp.locationAr else comp.locationEn} (${comp.electricalCabinet})",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Drawing / Schematic: Page ${comp.schematicsPage}",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = CyanPrimary
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.selectComponent(comp)
                                    viewModel.navigateTo(NavigationScreen.COMPONENTS)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isAr) "تفاصيل القطعة" else "Part Specs", color = Color.Black, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { viewModel.navigateTo(NavigationScreen.CIRCUITS) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Build, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isAr) "فتح المخطط" else "Open Circuit")
                            }
                        }
                    }
                }
            } else {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isAr) "لم يتم العثور على قطعة مطابقة لهذا الرمز" else "No matching equipment found for this tag",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isAr) "جرّب الأكواد السريعة أعلاه مثل 6GQZ10 أو WAU2001" else "Try sample tags like 6GQZ10 or WAU2001",
                            fontSize = 11.sp,
                            color = CyanPrimary
                        )
                    }
                }
            }
        }
    }

    // P2P Shift Log Sync Import Confirmation Dialog
    if (showP2pImportDialog && incomingP2pLog != null) {
        val log = incomingP2pLog!!
        AlertDialog(
            onDismissRequest = { showP2pImportDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isAr) "مزامنة شحنة من هاتف آخر (P2P QR)" else "P2P Shift Log Received", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (isAr)
                            "تم مسح بيانات الشحنة رقم [${log.batchNumber}] بنجاح من هاتف زميلك. هل ترغب في إضافتها وحفظها في قاعدة بيانات سجل الوردية على هذا الهاتف؟"
                        else
                            "Batch [${log.batchNumber}] received from peer device. Import and save to your local database?",
                        fontSize = 12.sp
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("• Batch: ${log.batchNumber}  |  Operator: ${log.operatorName}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("• Process: ${log.processType}  |  Load: ${log.loadWeightKg} kg", fontSize = 11.sp)
                            Text("• Temp: ${log.targetTempC}°C  |  Quench: ${log.quenchPressureBar} bar", fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addShiftLog(log)
                        showP2pImportDialog = false
                        android.widget.Toast.makeText(
                            context,
                            if (isAr) "تم استيراد وحفظ الشحنة ${log.batchNumber} بنجاح!" else "Batch imported successfully!",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusGreen)
                ) {
                    Text(if (isAr) "استيراد وحفظ" else "Import & Save", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showP2pImportDialog = false }) {
                    Text(if (isAr) "تخطي" else "Cancel")
                }
            }
        )
    }
}
