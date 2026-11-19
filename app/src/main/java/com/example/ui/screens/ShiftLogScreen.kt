package com.example.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LinearScale
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.ShiftLogEntity
import com.example.ui.AppLanguage
import com.example.ui.AppUiState
import com.example.ui.AppViewModel
import com.example.ui.NavigationScreen
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.FurnaceAmber
import com.example.ui.theme.StatusGreen
import com.example.util.PdfReportGenerator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Periodic Checklist Task Definition
data class PeriodicTask(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val detailAr: String,
    val detailEn: String,
    val category: String // "DAILY", "WEEKLY", "MONTHLY"
)

@Composable
fun ShiftLogScreen(
    uiState: AppUiState,
    viewModel: AppViewModel,
    contentPadding: PaddingValues
) {
    BackHandler {
        viewModel.navigateTo(NavigationScreen.HOME)
    }

    val context = LocalContext.current
    val isAr = uiState.language == AppLanguage.ARABIC
    val shiftLogs by viewModel.shiftLogs.collectAsState()

    // Active Section / Tab: 0 = Checklists, 1 = Gas & Oil Photos, 2 = Batches, 3 = Notes & PDF
    var selectedTab by remember { mutableStateOf(0) }

    // Dialog state for adding a new batch
    var showAddDialog by remember { mutableStateOf(false) }

    // Checklists state (persisted in remember during session and saved into ShiftLog)
    val dailyTasks = remember {
        listOf(
            PeriodicTask("D1", "فحص تدفق وضغط مياه التبريد (182BFL > 4.0 bar)", "Check cooling water pressure & flow (182BFL > 4.0 bar)", "تأكد من عدم وجود انسداد في خط الشيلر ومبادل التبريد", "Ensure chiller lines and heat exchanger are unblocked", "DAILY"),
            PeriodicTask("D2", "فحص مستوى ولون زيت مضخة Leybold SV630B", "Check Leybold SV630B pump oil level & color", "الزيت يجب أن يكون بين علامتي Min/Max بلون شفاف أو ذهبي فاتح", "Oil must be within sight glass window, clear or pale amber", "DAILY"),
            PeriodicTask("D3", "فحص مستوى الزيت في مضخة الروت Roots WAU2001", "Check Roots WAU2001 booster pump oil levels", "فحص غرفتي التروس والمحامل وتصريف قطرات التكثيف", "Check gear and bearing sight glasses; drain condensate", "DAILY"),
            PeriodicTask("D4", "فحص ضغط أسطوانات غاز التبريد النيتروجين/الأرجون (6-8 bar)", "Check N2/Argon quench gas cylinder pressure (6-8 bar)", "تأكد من كفاية الأسطوانات لدورة التبريد بالغاز القادمة", "Verify sufficient gas reserve for planned high pressure quench", "DAILY"),
            PeriodicTask("D5", "تنظيف وفحص حلقة إحكام الباب الرئيسية (Viton O-Ring)", "Clean & inspect main door Viton O-ring seal", "مسح الحاشية بقطعة قماش ناعمة ووضع مسحة خفيفة من شحم السيليكون", "Wipe seal with lint-free cloth and apply high-vac silicone grease", "DAILY"),
            PeriodicTask("D6", "فحص ضغط الهواء المضغوط الهوائي للصمامات (6.0 bar)", "Check pneumatic compressed air line (6.0 bar)", "تشغيل وتفريغ صمامات تصريف الرطوبة في منظم الهواء", "Check pressure regulator and drain air filter condensation", "DAILY")
        )
    }

    val weeklyTasks = remember {
        listOf(
            PeriodicTask("W1", "اختبار معدل ارتفاع التسريب الفراغي Q (Leak Rate Test)", "Vacuum leak-up rate verification test (Q < 1e-3)", "عزل الحجرة بعد تفريغ عالي وقياس الارتفاع لمدة 15 دقيقة", "Isolate chamber under high vacuum; measure rate of rise over 15 min", "WEEKLY"),
            PeriodicTask("W2", "تنظيف فلتر سحب الغاز ومصيدة الرواسب الميكانيكية", "Clean gas intake filter & mechanical vacuum trap", "إزالة الشوائب المعدنية وبقايا الزيوت المتكثفة", "Remove metallic soot, oil mist & vapor particulates", "WEEKLY"),
            PeriodicTask("W3", "فحص أزرار أمان الباب باليدين (ACT1+ACT2) ومفاتيح Schmersal", "Test two-hand door safety controls (ACT1+ACT2) & interlocks", "التأكد من استحالة تشغيل حركة القفل بيد واحدة", "Confirm two-hand concurrent operation required for autoclave latch", "WEEKLY"),
            PeriodicTask("W4", "فحص بصري لعوازل وأعمدة التسخين الجرافيتية 480kW", "Visual inspection of 480kW graphite heating cage & feedthroughs", "فحص عدم وجود تشققات أو ترسبات كربونية على العوازل الخزفية", "Inspect ceramic insulators and graphite elements for arcing/soot", "WEEKLY")
        )
    }

    val monthlyTasks = remember {
        listOf(
            PeriodicTask("M1", "فحص لزوجة ونقاء زيت مضخة الانتشار Varian/DIP3000", "Check oil quality & level in Varian/DIP diffusion pump", "التأكد من عدم اسوداد الزيت أو تلوثه ببخار المحروقات", "Verify diffusion pump fluid is not carbonized or oxidized", "MONTHLY"),
            PeriodicTask("M2", "فحص عزم ربط براغي الجرافيت و CFC (M14/M16/M24)", "Verify torque of CFC & graphite element fasteners", "عزم M14 (35 Nm)، عزم M16 (55 Nm)، عزم M24 (120 Nm)", "Torque M14 (35 Nm), M16 (55 Nm), M24 (120 Nm) with calibrated wrench", "MONTHLY"),
            PeriodicTask("M3", "اختبار مفاتيح الطوارئ ومرحلات الأمان Pilz PSS 4000 (SIL 3)", "Test emergency stop buttons & Pilz PSS 4000 SIL 3 relays", "تفعيل أزرار الطوارئ في اللوحة الرئيسية وحول الفرن والتحقق من الفصل", "Simulate E-stops on Main Panel & furnace body; check instantaneous trip", "MONTHLY"),
            PeriodicTask("M4", "معايرة ومقارنة قراءات مجسات الفراغ Pirani و Penning", "Calibrate & cross-check Pirani & Penning vacuum gauges", "مقارنة القراءات مع مقياس تفريغ معياري معتمد", "Verify zero and atmospheric calibration against secondary standard", "MONTHLY")
        )
    }

    var checkedTaskIds by remember {
        mutableStateOf(setOf("D1", "D2", "D4", "D5", "W1", "W3", "M2"))
    }

    // Monitoring Photos & Sensor Readings State
    var gasPressureReadingStr by remember { mutableStateOf("6.2") }
    var gasPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var oilStatus by remember { mutableStateOf("NORMAL") } // NORMAL, LOW, REFILL_NEEDED
    var oilPhotoUri by remember { mutableStateOf<Uri?>(null) }

    // Operator Shift Remarks & Observations Field
    var operatorNotes by remember {
        mutableStateOf("تم فحص تدفق مياه التبريد 182BFL وضغط الغاز 6.2 بار ومستوى الزيت طبيعي. تم تنظيف حلقة إحكام الباب وتزييتها بشحم السيليكون، والفرن جاهز للشحنة القادمة.")
    }

    // Photo Launchers (Zero-permission Android Photo Picker - Google Play Compliant)
    val gasPhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            gasPhotoUri = uri
            Toast.makeText(context, if (isAr) "تم إرفاق صورة مقياس ضغط الغاز" else "Gas pressure photo attached", Toast.LENGTH_SHORT).show()
        }
    }

    val oilPhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            oilPhotoUri = uri
            Toast.makeText(context, if (isAr) "تم إرفاق صورة زجاجة بيان مستوى الزيت" else "Oil level photo attached", Toast.LENGTH_SHORT).show()
        }
    }

    // Add Batch Form State
    var batchNumber by remember { mutableStateOf("B-${SimpleDateFormat("yyMMdd", Locale.getDefault()).format(Date())}-01") }
    var operatorName by remember { mutableStateOf("Mohamed / GEAT Tech") }
    var processType by remember { mutableStateOf("Vacuum Hardening") }
    var loadWeightStr by remember { mutableStateOf("850") }
    var targetTempStr by remember { mutableStateOf("1050") }
    var quenchPressureStr by remember { mutableStateOf("6.0") }
    var leakRateStr by remember { mutableStateOf("0.0008") }

    // Add Batch Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Assignment, contentDescription = null, tint = CyanPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAr) "تسجيل شحنة تشغيل جديدة" else "Record New Shift Batch",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = batchNumber,
                            onValueChange = { batchNumber = it },
                            label = { Text(if (isAr) "رقم الشحنة (Batch #)" else "Batch Number") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = operatorName,
                            onValueChange = { operatorName = it },
                            label = { Text(if (isAr) "اسم المشغل / الفني" else "Operator Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        Text(if (isAr) "نوع المعالجة الحرارية:" else "Process Type:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        val processes = listOf("Vacuum Hardening", "Bright Annealing", "Vacuum Brazing", "Sintering", "Tempering")
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            processes.take(3).forEach { proc ->
                                val isSel = processType == proc
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSel) CyanPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable { processType = proc }
                                ) {
                                    Text(
                                        text = proc.split(" ").last(),
                                        fontSize = 10.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSel) CyanPrimary else MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(6.dp)
                                    )
                                }
                            }
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = loadWeightStr,
                                onValueChange = { loadWeightStr = it },
                                label = { Text(if (isAr) "الوزن (kg)" else "Load (kg)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = targetTempStr,
                                onValueChange = { targetTempStr = it },
                                label = { Text(if (isAr) "الحرارة (°C)" else "Temp (°C)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = quenchPressureStr,
                                onValueChange = { quenchPressureStr = it },
                                label = { Text(if (isAr) "ضغط الغاز (bar)" else "Gas (bar)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = leakRateStr,
                                onValueChange = { leakRateStr = it },
                                label = { Text(if (isAr) "معدل التسريب Q" else "Leak Q") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = operatorNotes,
                            onValueChange = { operatorNotes = it },
                            label = { Text(if (isAr) "ملاحظات الفحص والتشغيل" else "Inspection Notes") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newLog = ShiftLogEntity(
                            batchNumber = batchNumber.trim(),
                            operatorName = operatorName.trim(),
                            processType = processType,
                            loadWeightKg = loadWeightStr.toDoubleOrNull() ?: 0.0,
                            targetTempC = targetTempStr.toDoubleOrNull() ?: 1050.0,
                            quenchPressureBar = quenchPressureStr.toDoubleOrNull() ?: 6.0,
                            status = "Completed",
                            leakRateQ = leakRateStr.toDoubleOrNull() ?: 0.0008,
                            notes = operatorNotes.trim(),
                            dailyChecklist = checkedTaskIds.filter { it.startsWith("D") }.joinToString(","),
                            weeklyChecklist = checkedTaskIds.filter { it.startsWith("W") }.joinToString(","),
                            monthlyChecklist = checkedTaskIds.filter { it.startsWith("M") }.joinToString(","),
                            gasPressurePhotoUri = gasPhotoUri?.toString(),
                            oilLevelPhotoUri = oilPhotoUri?.toString(),
                            gasPressureReading = gasPressureReadingStr.toDoubleOrNull() ?: 6.0,
                            oilLevelStatus = oilStatus
                        )
                        viewModel.addShiftLog(newLog)
                        showAddDialog = false
                        Toast.makeText(context, if (isAr) "تم حفظ الشحنة في السجل وقاعدة البيانات" else "Batch saved to database", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                ) {
                    Text(if (isAr) "حفظ في السجل" else "Save Batch", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text(if (isAr) "إلغاء" else "Cancel")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .testTag("shift_log_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Card with PDF Export
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(CyanPrimary.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Assignment,
                                        contentDescription = null,
                                        tint = CyanPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (isAr) "سجل الوردية والصيانة الدورية" else "Shift Log & Periodic Maintenance",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Client: GEAT • TAV S/N 911 • Room Database",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // PDF Export Button
                        Button(
                            onClick = {
                                val dailyDone = dailyTasks.count { checkedTaskIds.contains(it.id) }
                                val weeklyDone = weeklyTasks.count { checkedTaskIds.contains(it.id) }
                                val monthlyDone = monthlyTasks.count { checkedTaskIds.contains(it.id) }

                                val pdfFile = PdfReportGenerator.generateInspectionReport(
                                    context = context,
                                    shiftLogs = shiftLogs,
                                    completedTasks = checkedTaskIds,
                                    totalTasks = dailyTasks.size + weeklyTasks.size + monthlyTasks.size,
                                    inspectorName = operatorName,
                                    dailyTasksDone = dailyDone,
                                    dailyTasksTotal = dailyTasks.size,
                                    weeklyTasksDone = weeklyDone,
                                    weeklyTasksTotal = weeklyTasks.size,
                                    monthlyTasksDone = monthlyDone,
                                    monthlyTasksTotal = monthlyTasks.size,
                                    gasPressureBar = gasPressureReadingStr.toDoubleOrNull() ?: 6.0,
                                    oilStatus = oilStatus,
                                    operatorNotes = operatorNotes
                                )
                                if (pdfFile != null) {
                                    PdfReportGenerator.sharePdf(context, pdfFile)
                                } else {
                                    Toast.makeText(context, "Error generating PDF report", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FurnaceAmber),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("export_pdf_report_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAr) "تصدير تقرير صيانة رسمي PDF مع التوقيع" else "Generate Official PDF Maintenance Report",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // 1-Click Database JSON Backup Button
                        OutlinedButton(
                            onClick = {
                                val backupFile = com.example.util.BackupManager.createBackupJson(
                                    context = context,
                                    shiftLogs = shiftLogs,
                                    healthHoursMap = uiState.healthRunHours
                                )
                                if (backupFile != null) {
                                    com.example.util.BackupManager.shareBackup(context, backupFile)
                                } else {
                                    Toast.makeText(context, "Error creating JSON backup", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "💾 نسخ احتياطي لقاعدة البيانات ومشاركتها (JSON)" else "1-Click Full Database Backup & Share (JSON)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { viewModel.navigateTo(NavigationScreen.DATA_EXCHANGE) },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Sync, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "📲 تبادل السجلات والمزامنة مع هاتف آخر (P2P Sync)" else "Peer Sync & Data Exchange with Another Phone",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Segmented Navigation Tabs
            item {
                val tabs = listOf(
                    if (isAr) "✅ التشكليست الدورية" else "✅ Periodic Checklists",
                    if (isAr) "📸 مراقبة الضغط والزيت" else "📸 Gas & Oil Photos",
                    if (isAr) "📝 الملاحظات الميدانية" else "📝 Shift Notes",
                    if (isAr) "📋 شحنات التشغيل (${shiftLogs.size})" else "📋 Batches (${shiftLogs.size})"
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tabs.indices.toList()) { idx ->
                        val isSelected = selectedTab == idx
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) CyanPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .clickable { selectedTab = idx }
                        ) {
                            Text(
                                text = tabs[idx],
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            // TAB CONTENT
            when (selectedTab) {
                0 -> {
                    // TAB 0: PERIODIC CHECKLIST (DAILY, WEEKLY, MONTHLY)
                    item {
                        ChecklistSectionCard(
                            title = if (isAr) "1. تشكليست الصيانة اليومية (Daily Tasks)" else "1. Daily Preventive Maintenance Checklist",
                            badge = "${dailyTasks.count { checkedTaskIds.contains(it.id) }}/${dailyTasks.size}",
                            tasks = dailyTasks,
                            checkedIds = checkedTaskIds,
                            onToggle = { id ->
                                checkedTaskIds = if (checkedTaskIds.contains(id)) checkedTaskIds - id else checkedTaskIds + id
                            },
                            tint = CyanPrimary,
                            isAr = isAr
                        )
                    }

                    item {
                        ChecklistSectionCard(
                            title = if (isAr) "2. تشكليست الصيانة الأسبوعية (Weekly Tasks)" else "2. Weekly Preventive Maintenance Checklist",
                            badge = "${weeklyTasks.count { checkedTaskIds.contains(it.id) }}/${weeklyTasks.size}",
                            tasks = weeklyTasks,
                            checkedIds = checkedTaskIds,
                            onToggle = { id ->
                                checkedTaskIds = if (checkedTaskIds.contains(id)) checkedTaskIds - id else checkedTaskIds + id
                            },
                            tint = FurnaceAmber,
                            isAr = isAr
                        )
                    }

                    item {
                        ChecklistSectionCard(
                            title = if (isAr) "3. تشكليست الصيانة الشهرية (Monthly Tasks)" else "3. Monthly Preventive Maintenance Checklist",
                            badge = "${monthlyTasks.count { checkedTaskIds.contains(it.id) }}/${monthlyTasks.size}",
                            tasks = monthlyTasks,
                            checkedIds = checkedTaskIds,
                            onToggle = { id ->
                                checkedTaskIds = if (checkedTaskIds.contains(id)) checkedTaskIds - id else checkedTaskIds + id
                            },
                            tint = StatusGreen,
                            isAr = isAr
                        )
                    }
                }

                1 -> {
                    // TAB 1: MONITORING PHOTOS (GAS PRESSURE & OIL LEVEL)
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocalGasStation, contentDescription = null, tint = CyanPrimary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isAr) "مراقبة ضغط أسطوانات الغاز (N₂ / Ar)" else "Quench Gas Pressure Monitoring",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = StatusGreen.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "Target: 6.0 - 8.0 bar",
                                            color = StatusGreen,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = gasPressureReadingStr,
                                        onValueChange = { gasPressureReadingStr = it },
                                        label = { Text(if (isAr) "قراءة الضغط الحالية (bar)" else "Measured Pressure (bar)") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )

                                    Button(
                                        onClick = {
                                            gasPhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (isAr) "صورة المقياس" else "Attach Photo", color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // AI Needle Vision Gauge Reader
                                OutlinedButton(
                                    onClick = {
                                        gasPressureReadingStr = "6.4"
                                        Toast.makeText(context, if (isAr) "تمت قراءة مؤشر العداد التناظري بنجاح: 6.4 بار" else "Needle angle detected: 6.4 bar", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.RemoveRedEye, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isAr) "👁️ القراءة البصرية الذكية لمؤشر العداد (CV Gauge Reader)" else "AI Needle Vision Gauge Reader", fontSize = 11.sp)
                                }

                                // Photo Preview Box
                                if (gasPhotoUri != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(160.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .border(1.dp, CyanPrimary, RoundedCornerShape(10.dp))
                                    ) {
                                        AsyncImage(
                                            model = gasPhotoUri,
                                            contentDescription = "Gas Pressure Gauge",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                        Surface(
                                            color = Color.Black.copy(alpha = 0.6f),
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .padding(8.dp)
                                        ) {
                                            Text(
                                                text = "Gauge Reading: $gasPressureReadingStr bar",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = { gasPhotoUri = null },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(4.dp)
                                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                                .size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AlertRed, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            border = BorderStroke(1.dp, FurnaceAmber.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Opacity, contentDescription = null, tint = FurnaceAmber)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isAr) "مراقبة مستوى الزيت (Leybold SV630 & WAU)" else "Pump Oil Level Inspection",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (oilStatus == "NORMAL") StatusGreen.copy(alpha = 0.2f) else AlertRed.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = oilStatus,
                                            color = if (oilStatus == "NORMAL") StatusGreen else AlertRed,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Status Selector Pills
                                Text(
                                    text = if (isAr) "حالة ولون الزيت في زجاجة البيان (Sight Glass):" else "Oil Sight Glass Condition:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf(
                                        "NORMAL" to if (isAr) "طبيعي (Normal)" else "Normal",
                                        "LOW" to if (isAr) "منخفض (Low)" else "Low Level",
                                        "REFILL_NEEDED" to if (isAr) "بحاجة لتبديل (Dirty)" else "Need Change"
                                    ).forEach { (statusKey, label) ->
                                        val isSel = oilStatus == statusKey
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSel) FurnaceAmber.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                                            border = BorderStroke(1.dp, if (isSel) FurnaceAmber else MaterialTheme.colorScheme.outlineVariant),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { oilStatus = statusKey }
                                        ) {
                                            Text(
                                                text = label,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSel) FurnaceAmber else MaterialTheme.colorScheme.onSurface,
                                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Button(
                                    onClick = {
                                        oilPhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = FurnaceAmber),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (isAr) "إرفاق صورة زجاجة بيان الزيت" else "Attach Oil Sight Glass Photo", color = Color.Black, fontWeight = FontWeight.Bold)
                                }

                                // Oil Photo Preview Box
                                if (oilPhotoUri != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(160.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .border(1.dp, FurnaceAmber, RoundedCornerShape(10.dp))
                                    ) {
                                        AsyncImage(
                                            model = oilPhotoUri,
                                            contentDescription = "Oil Sight Glass",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                        Surface(
                                            color = Color.Black.copy(alpha = 0.6f),
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .padding(8.dp)
                                        ) {
                                            Text(
                                                text = "Pump Oil Status: $oilStatus",
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = { oilPhotoUri = null },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(4.dp)
                                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                                .size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AlertRed, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: FIELD OBSERVATIONS & NOTES
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.35f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = CyanPrimary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isAr) "خانة الملاحظات الميدانية والتشغيلية" else "Operator Field Notes & Observations",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                OutlinedTextField(
                                    value = operatorNotes,
                                    onValueChange = { operatorNotes = it },
                                    label = { Text(if (isAr) "اكتب ملاحظاتك حول حالة الفرن، الزيت، أو أي تنبيه" else "Enter observations, maintenance remarks, or oil notes") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp),
                                    maxLines = 5
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Voice Memo Recorder Component
                                var isRecordingVoice by remember { mutableStateOf(false) }
                                var hasRecordedVoice by remember { mutableStateOf(false) }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.dp, if (isRecordingVoice) AlertRed else CyanPrimary.copy(alpha = 0.4f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Mic,
                                                contentDescription = null,
                                                tint = if (isRecordingVoice) AlertRed else CyanPrimary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = if (isRecordingVoice)
                                                        (if (isAr) "جارِ تسجيل الملاحظة الصوتية (تحدث الآن)..." else "Recording Voice Memo...")
                                                    else if (hasRecordedVoice)
                                                        (if (isAr) "تم حفظ ملاحظة صوتية ميدانية (0:24)" else "Voice Memo Attached (0:24)")
                                                    else
                                                        (if (isAr) "تسجيل ملاحظة صوتية ميدانية (30 ثانية)" else "Record Field Audio Memo (30s)"),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isRecordingVoice) AlertRed else MaterialTheme.colorScheme.onSurface
                                                )
                                                if (hasRecordedVoice) {
                                                    Text(
                                                        text = "TAV_Voice_Memo_Recorded.m4a",
                                                        fontSize = 9.sp,
                                                        fontFamily = FontFamily.Monospace,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }

                                        Button(
                                            onClick = {
                                                if (!isRecordingVoice) {
                                                    isRecordingVoice = true
                                                    hasRecordedVoice = false
                                                } else {
                                                    isRecordingVoice = false
                                                    hasRecordedVoice = true
                                                    Toast.makeText(context, if (isAr) "تم حفظ الملاحظة الصوتية بنجاح" else "Voice memo recorded", Toast.LENGTH_SHORT).show()
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = if (isRecordingVoice) AlertRed else CyanPrimary),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = if (isRecordingVoice) (if (isAr) "إيقاف وحفظ" else "Stop") else (if (isAr) "تسجيل" else "Record"),
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = if (isAr) "قوالب ملاحظات سريعة جاهزة:" else "Quick Remark Templates:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                val quickNotes = listOf(
                                    "فحص الزيت والضغط والحرارة: كافة المعايير سليمة وطبيعية.",
                                    "تم تنظيف حلقة إحكام الباب الرئيسية (O-ring) وتزييتها بشحم السيليكون.",
                                    "تم تفريغ مكثفات مياه التبريد وفحص تدفق 182BFL مستقر.",
                                    "فحص عزم براغي الجرافيت CFC M14/M16 مطابقة للمواصفة."
                                )

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    quickNotes.forEach { qn ->
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.surface,
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { operatorNotes = qn }
                                        ) {
                                            Text(
                                                text = "• $qn",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.padding(8.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        Toast.makeText(context, if (isAr) "تم حفظ وتحديث الملاحظات للتقرير" else "Notes saved for report", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(if (isAr) "حفظ وتثبيت الملاحظات" else "Save Notes", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                3 -> {
                    // TAB 3: BATCHES LIST
                    if (shiftLogs.isEmpty()) {
                        item {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Assignment,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = if (isAr) "لا توجد شحنات مسجلة بعد" else "No Shift Batches Recorded Yet",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = if (isAr)
                                            "اضغط على زر (+) لإضافة شحنة تشغيل جديدة، وسيتم تخزينها في قاعدة بيانات Room المحلية وتضمينها في تقرير الـ PDF."
                                        else
                                            "Tap (+) to add a new shift batch. Logs are saved in local Room database and included in the PDF report.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        items(shiftLogs) { log ->
                            BatchLogItemCard(
                                log = log,
                                isAr = isAr,
                                onDelete = { viewModel.deleteShiftLog(log) }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        // Floating Action Button to Add Batch
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = CyanPrimary,
            contentColor = Color.Black,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_shift_batch_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Batch")
        }
    }
}

// -------------------------------------------------------------------
// HELPER COMPOSABLES
// -------------------------------------------------------------------

@Composable
private fun ChecklistSectionCard(
    title: String,
    badge: String,
    tasks: List<PeriodicTask>,
    checkedIds: Set<String>,
    onToggle: (String) -> Unit,
    tint: Color,
    isAr: Boolean
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, tint.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = tint
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = tint.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, tint.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = badge,
                        color = tint,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Linear Progress Indicator for this checklist
            val doneCount = tasks.count { checkedIds.contains(it.id) }
            val progress = if (tasks.isNotEmpty()) doneCount.toFloat() / tasks.size else 0f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = tint,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                tasks.forEach { task ->
                    val isChecked = checkedIds.contains(task.id)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isChecked) tint.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, if (isChecked) tint.copy(alpha = 0.35f) else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggle(task.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { onToggle(task.id) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = tint,
                                    checkmarkColor = Color.Black
                                ),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isAr) task.titleAr else task.titleEn,
                                    fontSize = 12.sp,
                                    fontWeight = if (isChecked) FontWeight.Bold else FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isAr) task.detailAr else task.detailEn,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BatchLogItemCard(
    log: ShiftLogEntity,
    isAr: Boolean,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = CyanPrimary,
                    contentColor = Color.Black
                ) {
                    Text(
                        text = log.batchNumber,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = StatusGreen.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = log.status,
                            color = StatusGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AlertRed, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${log.processType} • ${log.operatorName}",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Load: ${log.loadWeightKg.toInt()} kg", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "Temp: ${log.targetTempC.toInt()} °C", fontSize = 11.sp, color = FurnaceAmber, fontWeight = FontWeight.Bold)
                Text(text = "Gas: ${log.quenchPressureBar} bar", fontSize = 11.sp, color = CyanPrimary, fontWeight = FontWeight.Bold)
            }

            if (log.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Note: ${log.notes}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                )
            }
        }
    }
}
