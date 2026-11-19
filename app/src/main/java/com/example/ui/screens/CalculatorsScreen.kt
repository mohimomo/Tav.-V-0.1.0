package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import java.util.Locale

@Composable
fun CalculatorsScreen(
    uiState: AppUiState,
    viewModel: AppViewModel,
    contentPadding: PaddingValues
) {
    BackHandler {
        viewModel.navigateTo(NavigationScreen.HOME)
    }

    val isAr = uiState.language == AppLanguage.ARABIC
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .testTag("calculators_screen")
    ) {
        // Header
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CyanPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = CyanPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isAr) "الحاسبات الهندسية التشغيلية للفرن" else "Operational Engineering Calculators",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "TAV TPH 120/120/230 • Volume: 3312 dm³ • Max Power: 480 kW",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = CyanPrimary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text(if (isAr) "معدل التسريب Q" else "Leak Rate Q", fontSize = 11.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text(if (isAr) "غاز التبريد" else "Quench Gas", fontSize = 11.sp) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text(if (isAr) "طاقة 480kW" else "Power Cost", fontSize = 11.sp) }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { Text(if (isAr) "منحنى الفراغ" else "Pump Curve", fontSize = 11.sp) }
                    )
                }
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> LeakRateCalculatorTab(isAr = isAr)
            1 -> GasConsumptionCalculatorTab(isAr = isAr)
            2 -> PowerCostCalculatorTab(isAr = isAr)
            3 -> PumpDownBenchmarkTab(isAr = isAr)
        }
    }
}

@Composable
fun LeakRateCalculatorTab(isAr: Boolean) {
    val chamberVolumeLiters = 3312.0 // Chamber useful volume
    var pInitialStr by remember { mutableStateOf("0.0001") } // 10^-4 mbar
    var pFinalStr by remember { mutableStateOf("0.0045") }   // 4.5*10^-3 mbar
    var timeMinutesStr by remember { mutableStateOf("30") }  // 30 min duration

    val pInitial = pInitialStr.toDoubleOrNull() ?: 0.0001
    val pFinal = pFinalStr.toDoubleOrNull() ?: 0.0045
    val timeMinutes = timeMinutesStr.toDoubleOrNull() ?: 30.0

    val timeSeconds = (timeMinutes * 60.0).coerceAtLeast(1.0)
    val deltaP = (pFinal - pInitial).coerceAtLeast(0.0)
    // Q = (V * deltaP) / deltaT  in mbar * l / s
    val qLeakRate = (chamberVolumeLiters * deltaP) / timeSeconds
    val deltaPPerHour = deltaP * (60.0 / timeMinutes.coerceAtLeast(1.0))

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isAr) "معادلة فحص معدل التسريب القياسية (Vacuum Leak Rate Test)" else "Standard Vacuum Leak-Up Rate Test",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Q = (V × ΔP) / Δt  [mbar · l/s]",
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        color = CyanPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isAr)
                            "يتم قياس الضغط بعد عزل الغرفة بإغلاق صمام التفريغ الرئيسي HiVVa1 وصمام التفريغ الخشن LoVVa1 ومراقبة ارتفاع الضغط."
                        else
                            "Measure pressure rise after isolating chamber by closing high vacuum valve HiVVa1 and roughing valve LoVVa1.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isAr) "معاملات الاختبار الميداني" else "Test Parameters",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )

                    OutlinedTextField(
                        value = pInitialStr,
                        onValueChange = { pInitialStr = it },
                        label = { Text(if (isAr) "الضغط الأولي P1 (mbar)" else "Initial Pressure P1 (mbar)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = pFinalStr,
                        onValueChange = { pFinalStr = it },
                        label = { Text(if (isAr) "الضغط النهائي P2 (mbar)" else "Final Pressure P2 (mbar)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = timeMinutesStr,
                        onValueChange = { timeMinutesStr = it },
                        label = { Text(if (isAr) "مدة الاختبار (بالدقائق)" else "Test Duration (Minutes)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(if (isAr) "حجم الحجرة الفعلي (V):" else "Chamber Volume (V):", fontSize = 11.sp)
                            Text("3312 dm³ (3312 Liters)", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        // Results Card
        item {
            val isExcellent = qLeakRate < 1.0e-3
            val isAcceptable = qLeakRate in 1.0e-3..5.0e-3
            val badgeColor = when {
                isExcellent -> StatusGreen
                isAcceptable -> FurnaceAmber
                else -> AlertRed
            }
            val verdictText = when {
                isExcellent -> if (isAr) "ممتاز - مؤهل للتفريغ العالي واللحام (Titanium / Brazing OK)" else "EXCELLENT - Ultra-High Vacuum Qualified"
                isAcceptable -> if (isAr) "مقبول - صالح لعمليات التقسية والتلدين العادية (Standard Hardening OK)" else "ACCEPTABLE - Suitable for Standard Hardening"
                else -> if (isAr) "تحذير: معدل تسريب مرتفع! (LEAK DETECTED - INSPECT SEALS)" else "WARNING: HIGH LEAK DETECTED - INSPECTION REQUIRED"
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = badgeColor.copy(alpha = 0.12f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) "النتيجة والتقييم الهندسي" else "Diagnostic Result",
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                        Icon(
                            imageVector = if (qLeakRate <= 5.0e-3) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = badgeColor
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Q = ${String.format(Locale.US, "%.2e", qLeakRate)} mbar · l/s",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = badgeColor
                    )

                    Text(
                        text = "ΔP/h = ${String.format(Locale.US, "%.4f", deltaPPerHour)} mbar/hour",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = verdictText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = badgeColor
                    )

                    if (qLeakRate > 5.0e-3) {
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isAr)
                                "قائمة الإجراءات الفورية:\n1. فحص حلقة السيليكون O-ring على شفة الباب الرئيسي وتنظيفها وتشحيمها بشحم السيليكون Apiezon.\n2. التحقق من إحكام صمام التفريغ الرئيسي HiVVa1 والقرص النحاسي.\n3. فحص مجسات الفراغ Penning و Pirani والتأكد من إحكام الفلنجات KF40/ISO-K."
                            else
                                "Action Checklist:\n1. Inspect main door O-ring seal, clean and re-grease with vacuum silicone lubricant.\n2. Check high vacuum valve HiVVa1 seat and seal plate.\n3. Verify Penning & Pirani gauge flange connections (KF40/ISO-K).",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GasConsumptionCalculatorTab(isAr: Boolean) {
    val chamberVolumeM3 = 3.312 // 3312 dm3 = 3.312 m3
    var quenchPressureBar by remember { mutableFloatStateOf(4.5f) }
    var selectedGasType by remember { mutableStateOf("Argon") }
    var gasCostPerM3Str by remember { mutableStateOf("8.5") }

    val gasCostPerM3 = gasCostPerM3Str.toDoubleOrNull() ?: 8.5
    // Required STP gas volume = V_chamber * (P_abs)  approximately
    val totalGasM3 = chamberVolumeM3 * quenchPressureBar
    // Standard 50L bottle at 200 bar = 10 m3 STP gas
    val bottlesNeeded = Math.ceil(totalGasM3 / 10.0).toInt()
    val totalCost = totalGasM3 * gasCostPerM3

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isAr) "حاسبة استهلاك غاز التبريد الفائق (6.0 bar Gas Quenching)" else "6.0 bar Gas Quenching Consumption",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isAr)
                            "الفرن مصمم للحقن بغاز الأرجون (Argon) أو النيتروجين (Nitrogen) حتى ضغط مطلق 6.0 بار لتبريد المعادن بسرعة فائقة مع صمام أمان 192PVS1 (6.6 bar)."
                        else
                            "Calculates pressurized gas volume to backfill 3312 dm³ vessel up to 6.0 bar absolute for high-speed gas quenching.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (isAr) "ضغط التبريد المطلوب:" else "Target Quench Pressure:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(
                            text = "${String.format(Locale.US, "%.1f", quenchPressureBar)} bar abs",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = CyanPrimary
                        )
                    }

                    Slider(
                        value = quenchPressureBar,
                        onValueChange = { quenchPressureBar = it },
                        valueRange = 1.0f..6.0f,
                        steps = 9
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Argon (Ar 99.999%)", "Nitrogen (N2)").forEach { gas ->
                            val isSelected = selectedGasType == gas
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) CyanPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) CyanPrimary else Color.Transparent),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = gas,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) CyanPrimary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = gasCostPerM3Str,
                        onValueChange = { gasCostPerM3Str = it },
                        label = { Text(if (isAr) "سعر المتر المكعب للغاز ($ / m³)" else "Gas Price ($ / m³)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
        }

        // Outputs
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isAr) "ملخص استهلاك الغاز والتكلفة المقدرة" else "Gas Consumption & Cost Summary",
                        fontWeight = FontWeight.Bold,
                        color = CyanPrimary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (isAr) "كمية الغاز المطلوبة بالحجرة:" else "Gas Volume Required:", fontSize = 12.sp)
                        Text(
                            text = "${String.format(Locale.US, "%.2f", totalGasM3)} m³ STP",
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (isAr) "عدد أسطوانات 50L (200 bar) المعادلة:" else "Equivalent 50L Cylinders:", fontSize = 12.sp)
                        Text(
                            text = "$bottlesNeeded ${if (isAr) "أسطوانة" else "Cylinders"}",
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = FurnaceAmber
                        )
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (isAr) "التكلفة التقديرية للشحنة:" else "Estimated Gas Cost per Batch:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(
                            text = "$ ${String.format(Locale.US, "%.2f", totalCost)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = StatusGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PowerCostCalculatorTab(isAr: Boolean) {
    var heatingHoursStr by remember { mutableStateOf("1.5") }
    var avgHeatingKwStr by remember { mutableStateOf("280") } // out of 480 kW
    var soakHoursStr by remember { mutableStateOf("2.0") }
    var soakKwStr by remember { mutableStateOf("95") }
    var quenchHoursStr by remember { mutableStateOf("0.5") }
    var electricityTariffStr by remember { mutableStateOf("0.12") }

    val heatingHours = heatingHoursStr.toDoubleOrNull() ?: 1.5
    val avgHeatingKw = avgHeatingKwStr.toDoubleOrNull() ?: 280.0
    val soakHours = soakHoursStr.toDoubleOrNull() ?: 2.0
    val soakKw = soakKwStr.toDoubleOrNull() ?: 95.0
    val quenchHours = quenchHoursStr.toDoubleOrNull() ?: 0.5
    val quenchBlowerKw = 120.0 // Gas quench blower motor
    val pumpSkidKw = 25.0      // Leybold SV630B + Roots WAU2001
    val electricityTariff = electricityTariffStr.toDoubleOrNull() ?: 0.12

    val totalHeatingKwh = (heatingHours * avgHeatingKw) + (soakHours * soakKw)
    val totalQuenchKwh = (quenchHours * quenchBlowerKw)
    val totalPumpsKwh = (heatingHours + soakHours + quenchHours) * pumpSkidKw
    val totalCycleKwh = totalHeatingKwh + totalQuenchKwh + totalPumpsKwh
    val totalCycleCost = totalCycleKwh * electricityTariff

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isAr) "حاسبة الطاقة الكهربائية وتكلفة الدورة الحرارية" else "Electrical Power & Cycle Energy Cost",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Heating: 480 kW max (Tr1 Eurotherm TC1) • Pumps: 25 kW • Blower: 120 kW",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = FurnaceAmber
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(if (isAr) "بارامترات الدورة الحرارية" else "Heat Cycle Parameters", fontWeight = FontWeight.Bold, fontSize = 12.sp)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = heatingHoursStr,
                            onValueChange = { heatingHoursStr = it },
                            label = { Text(if (isAr) "ساعات التسخين (h)" else "Ramp Time (h)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = avgHeatingKwStr,
                            onValueChange = { avgHeatingKwStr = it },
                            label = { Text(if (isAr) "متوسط القدرة (kW)" else "Avg Power (kW)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = soakHoursStr,
                            onValueChange = { soakHoursStr = it },
                            label = { Text(if (isAr) "ساعات التثبيت (h)" else "Soak Time (h)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = soakKwStr,
                            onValueChange = { soakKwStr = it },
                            label = { Text(if (isAr) "قدرة التثبيت (kW)" else "Soak Power (kW)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = quenchHoursStr,
                            onValueChange = { quenchHoursStr = it },
                            label = { Text(if (isAr) "مدة التبريد (h)" else "Quench Time (h)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = electricityTariffStr,
                            onValueChange = { electricityTariffStr = it },
                            label = { Text(if (isAr) "سعر الكهرباء ($/kWh)" else "Tariff ($/kWh)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // Summary
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, FurnaceAmber.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isAr) "إجمالي استهلاك الكهرباء والتكلفة" else "Total Electrical Energy & Cost",
                        fontWeight = FontWeight.Bold,
                        color = FurnaceAmber
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (isAr) "طاقة التسخين والتثبيت:" else "Heating & Soak Energy:", fontSize = 12.sp)
                        Text("${String.format(Locale.US, "%.1f", totalHeatingKwh)} kWh", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (isAr) "طاقة مروحة التبريد 120kW ومضخات الفراغ:" else "Quench Blower & Vacuum Skid:", fontSize = 12.sp)
                        Text("${String.format(Locale.US, "%.1f", totalQuenchKwh + totalPumpsKwh)} kWh", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (isAr) "إجمالي استهلاك الدورة الكاملة:" else "Total Cycle Consumption:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("${String.format(Locale.US, "%.1f", totalCycleKwh)} kWh", fontWeight = FontWeight.Black, fontSize = 15.sp, fontFamily = FontFamily.Monospace, color = CyanPrimary)
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (isAr) "التكلفة الكهربائية المقدرة:" else "Estimated Electricity Cost:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(
                            text = "$ ${String.format(Locale.US, "%.2f", totalCycleCost)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = StatusGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PumpDownBenchmarkTab(isAr: Boolean) {
    var measuredMinutesStr by remember { mutableStateOf("16") }
    var targetVacuumType by remember { mutableIntStateOf(2) } // 0 = 1 mbar, 1 = 1e-2 mbar, 2 = 1e-4 mbar, 3 = 1e-5 mbar

    val measuredMin = measuredMinutesStr.toDoubleOrNull() ?: 16.0
    val expectedMin = when (targetVacuumType) {
        0 -> 8.0
        1 -> 14.0
        2 -> 22.0
        else -> 30.0
    }

    val ratio = measuredMin / expectedMin
    val (statusLabel, statusColor, diagnosisAr, diagnosisEn) = when {
        ratio <= 1.15 -> CalcFourTuple(
            "OPTIMAL & DRY",
            StatusGreen,
            "أداء منظومة التفريغ مثالي والحجرة جافة وخالية من التسريب والرطوبة.",
            "Vacuum skid performance is optimal. Chamber is clean and moisture-free."
        )
        ratio <= 1.40 -> CalcFourTuple(
            "ACCEPTABLE / OUTGASSING",
            FurnaceAmber,
            "سرعة التفريغ مقبولة ولكن أبطأ قليلاً؛ غالباً بسبب رطوبة على سطح الشحنة أو زيوت تشحيم متبقية.",
            "Slightly sluggish; typical for surface moisture or minor hydrocarbon outgassing."
        )
        else -> CalcFourTuple(
            "SLUGGISH / SUSPECTED LEAK",
            AlertRed,
            "تأخر ملحوظ في الوصول للفراغ! يُنصح بفحص حلقة الباب Viton وصمامات التبريد ومعدل التسريب Q.",
            "Significant delay! Check door Viton O-ring seal, roughing valve LoVVa1, and leak rate Q."
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isAr) "مقارنة منحنى التفريغ المعياري للفرن" else "OEM Vacuum Pump-Down Benchmark",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = statusColor,
                            contentColor = Color.Black
                        ) {
                            Text(
                                text = statusLabel,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isAr) "اختر مستوى الفراغ المستهدف للمقارنة:" else "Select Target Vacuum Level to Benchmark:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val vacuumSteps = listOf(
                        "1 mbar" to "Roughing",
                        "10⁻² mbar" to "Roots",
                        "10⁻⁴ mbar" to "Diffusion",
                        "10⁻⁵ mbar" to "Ultra-Vac"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        vacuumSteps.forEachIndexed { index, (vac, label) ->
                            val isSel = targetVacuumType == index
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) CyanPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) CyanPrimary else Color.Transparent),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { targetVacuumType = index }
                            ) {
                                Column(
                                    modifier = Modifier.padding(6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = vac,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) CyanPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = label,
                                        fontSize = 9.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = measuredMinutesStr,
                        onValueChange = { measuredMinutesStr = it },
                        label = { Text(if (isAr) "الزمن الفعلي المستغرق في الدورة (بالدقائق)" else "Actual Elapsed Cycle Time (Minutes)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(if (isAr) "الزمن المعياري لـ TAV:" else "TAV OEM Standard:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "$expectedMin min",
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = CyanPrimary
                            )
                        }
                        Column {
                            Text(if (isAr) "الزمن الفعلي المقاس:" else "Actual Measured Time:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "$measuredMin min",
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        }
                        Column {
                            Text(if (isAr) "الانحراف عن المعيار:" else "Deviation Ratio:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            val devPercent = ((ratio - 1.0) * 100).toInt()
                            val devStr = if (devPercent >= 0) "+$devPercent%" else "$devPercent%"
                            Text(
                                text = devStr,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = statusColor, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAr) diagnosisAr else diagnosisEn,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class CalcFourTuple<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

