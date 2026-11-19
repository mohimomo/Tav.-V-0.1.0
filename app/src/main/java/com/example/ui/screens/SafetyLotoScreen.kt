package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppLanguage
import com.example.ui.AppUiState
import com.example.ui.AppViewModel
import com.example.ui.NavigationScreen
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.FurnaceAmber
import com.example.ui.theme.StatusGreen
import com.example.util.LotoPermitData
import com.example.util.LotoPermitGenerator
import com.example.util.PdfReportGenerator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SafetyLotoScreen(
    uiState: AppUiState,
    viewModel: AppViewModel,
    contentPadding: PaddingValues
) {
    BackHandler {
        viewModel.navigateTo(NavigationScreen.HOME)
    }

    val context = LocalContext.current
    val isAr = uiState.language == AppLanguage.ARABIC

    var permitNumber by remember {
        mutableStateOf("LOTO-${SimpleDateFormat("yyMMdd", Locale.getDefault()).format(Date())}-01")
    }
    var technicianName by remember { mutableStateOf("Mohamed / GEAT Tech") }
    var supervisorName by remember { mutableStateOf("Safety Engineer / Plant Head") }
    var padlockNumber by remember { mutableStateOf("PAD-RED-44") }
    var workDescription by remember {
        mutableStateOf("استبدال عوازل عناصر التسخين الجرافيتية وتنظيف مصيدة العادم الفراغية")
    }

    // 5 Isolation checklist steps
    var isElectricalIsolated by remember { mutableStateOf(true) }
    var isPneumaticIsolated by remember { mutableStateOf(true) }
    var isGasIsolated by remember { mutableStateOf(true) }
    var isZeroEnergyVerified by remember { mutableStateOf(true) }
    var isChamberAtmosphereVerified by remember { mutableStateOf(false) }

    val checkedCount = listOf(
        isElectricalIsolated,
        isPneumaticIsolated,
        isGasIsolated,
        isZeroEnergyVerified,
        isChamberAtmosphereVerified
    ).count { it }

    val allStepsVerified = checkedCount == 5

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp)
            .testTag("safety_loto_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Header Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.5.dp, AlertRed.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(AlertRed.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = AlertRed,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isAr) "تصاريح العزل والسلامة الصناعية (LOTO)" else "Lockout / Tagout & Safe Entry (LOTO)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Zero Energy Verification & Confined Space Permit",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isAr)
                            "إجراء إلزامي قبل الدخول لحجرة الفرن أو فتح لوحات القدرة 480kW (QS1 1250A). يجب التحقق من كافة خطوات العزل الخمس لتوليد تصريح العمل الرسمي وتوثيقه."
                        else
                            "Mandatory protocol before entering the vacuum chamber or servicing 480kW cabinets (QS1 1250A). Verify all 5 energy isolation steps to issue certified permit.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress Bar
                    LinearProgressIndicator(
                        progress = { checkedCount / 5f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (allStepsVerified) StatusGreen else AlertRed,
                        trackColor = MaterialTheme.colorScheme.surface
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Verification: $checkedCount / 5 Steps",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (allStepsVerified) StatusGreen else AlertRed
                        )
                        Text(
                            text = if (allStepsVerified) "SAFE FOR ENTRY (آمن للدخول)" else "LOCKOUT PENDING (العزل غير مكتمل)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (allStepsVerified) StatusGreen else AlertRed
                        )
                    }
                }
            }
        }

        // Section: 5 Verification Steps
        item {
            Text(
                text = if (isAr) "خطوات التحقق من العزل الإلزامي (5 خطوات):" else "Mandatory 5-Step Isolation Checklist:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        val stepList = listOf(
            Triple(
                if (isAr) "1. عزل الطاقة الكهربائية الرئيسية (QS1 1250A)" else "1. Electrical Main Disconnector (QS1 1250A)",
                if (isAr) "فصل القاطع الرئيسي MiCa في لوحة التوزيع، تعليق القفل الأحمر، وتثبيت بطاقة العزل" else "Turn main switch OFF, apply red master padlock & lockout warning tag",
                isElectricalIsolated
            ),
            Triple(
                if (isAr) "2. عزل وتفريغ الهواء المضغوط الهوائي (6 bar)" else "2. Pneumatic Compressed Air Supply (6.0 bar)",
                if (isAr) "إغلاق صمام الإمداد الهوائي الرئيسي، تفريغ الخزان ومسار الصمامات الهوائية" else "Close shut-off ball valve, dump residual line pressure to 0 bar",
                isPneumaticIsolated
            ),
            Triple(
                if (isAr) "3. عزل أسطوانات غاز التبريد النيتروجين والأرجون" else "3. Quench Gas Supply Lines (N2 / Argon)",
                if (isAr) "إغلاق محابس أسطوانات الغاز 6-8 بار وتفريغ الخط نحو مصيدة العادم" else "Shut manifold high-pressure valves & bleed residual pressure",
                isGasIsolated
            ),
            Triple(
                if (isAr) "4. التحقق من انعدام الطاقة والجهد (Zero Energy State)" else "4. Zero-Energy State Verification (< 5V / 0 bar)",
                if (isAr) "قياس الجهد بالميجر/المتر والتأكد من انعدام الجهد وتفريغ المكثفات" else "Confirm with calibrated multimeter < 5V AC/DC across busbars",
                isZeroEnergyVerified
            ),
            Triple(
                if (isAr) "5. فحص بيئة الحجرة الفراغية (دخول الأماكن المغلقة)" else "5. Confined Space Atmosphere & Temperature",
                if (isAr) "معايرة نسبة الأكسجين O2 > 19.5% وانخفاض حرارة الحجرة دون 40°م" else "Verify O2 > 19.5%, zero toxic gas, and chamber temperature < 40°C",
                isChamberAtmosphereVerified
            )
        )

        items(stepList.size) { idx ->
            val (title, desc, isChecked) = stepList[idx]
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, if (isChecked) StatusGreen.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        when (idx) {
                            0 -> isElectricalIsolated = !isElectricalIsolated
                            1 -> isPneumaticIsolated = !isPneumaticIsolated
                            2 -> isGasIsolated = !isGasIsolated
                            3 -> isZeroEnergyVerified = !isZeroEnergyVerified
                            4 -> isChamberAtmosphereVerified = !isChamberAtmosphereVerified
                        }
                    }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = {
                            when (idx) {
                                0 -> isElectricalIsolated = it
                                1 -> isPneumaticIsolated = it
                                2 -> isGasIsolated = it
                                3 -> isZeroEnergyVerified = it
                                4 -> isChamberAtmosphereVerified = it
                            }
                        },
                        colors = CheckboxDefaults.colors(checkedColor = StatusGreen, checkmarkColor = Color.Black)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Section: Form Details
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isAr) "بيانات تصريح العمل والرقم التسلسلي:" else "Permit Details & Padlock Number:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = permitNumber,
                            onValueChange = { permitNumber = it },
                            label = { Text("Permit No") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = padlockNumber,
                            onValueChange = { padlockNumber = it },
                            label = { Text("Padlock ID") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = technicianName,
                        onValueChange = { technicianName = it },
                        label = { Text(if (isAr) "الفني القائم بالعزل" else "LOTO Technician") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = supervisorName,
                        onValueChange = { supervisorName = it },
                        label = { Text(if (isAr) "مشرف السلامة والاعتماد" else "Safety Supervisor") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = workDescription,
                        onValueChange = { workDescription = it },
                        label = { Text(if (isAr) "وصف أعمال الصيانة" else "Work Scope Description") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // PDF Export Button
                    Button(
                        onClick = {
                            val data = LotoPermitData(
                                permitNumber = permitNumber,
                                technicianName = technicianName,
                                supervisorName = supervisorName,
                                workDescription = workDescription,
                                padlockNumber = padlockNumber,
                                isElectricalIsolated = isElectricalIsolated,
                                isPneumaticIsolated = isPneumaticIsolated,
                                isGasIsolated = isGasIsolated,
                                isZeroEnergyVerified = isZeroEnergyVerified,
                                isChamberAtmosphereVerified = isChamberAtmosphereVerified,
                                estimatedDurationHours = 4.0
                            )
                            val pdf = LotoPermitGenerator.generateLotoPermitPdf(context, data)
                            if (pdf != null) {
                                PdfReportGenerator.sharePdf(context, pdf)
                            } else {
                                Toast.makeText(context, "Error generating LOTO permit PDF", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (allStepsVerified) AlertRed else FurnaceAmber),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "تصدير تصريح السلامة المعتمد PDF (LOTO Permit)" else "Export Certified LOTO Safety Permit PDF",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
