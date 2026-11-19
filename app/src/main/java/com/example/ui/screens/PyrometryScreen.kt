package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import kotlin.math.abs
import kotlin.math.max

@Composable
fun PyrometryScreen(
    uiState: AppUiState,
    viewModel: AppViewModel,
    contentPadding: PaddingValues
) {
    BackHandler {
        viewModel.navigateTo(NavigationScreen.HOME)
    }

    val isAr = uiState.language == AppLanguage.ARABIC
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = SAT, 1 = TUS

    // SAT State
    var setpointStr by remember { mutableStateOf("1050") }
    var controlTempStr by remember { mutableStateOf("1050.8") }
    var testRefTempStr by remember { mutableStateOf("1050.2") }
    var correctionFactorStr by remember { mutableStateOf("0.3") }

    // TUS State (9 thermocouples)
    var tusSetpointStr by remember { mutableStateOf("1050") }
    var tusTcValues by remember {
        mutableStateOf(
            listOf(
                "1049.2", "1051.4", "1050.6", "1048.8",
                "1052.1", "1050.3", "1049.7", "1051.0",
                "1050.0" // Center
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp)
            .testTag("pyrometry_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(FurnaceAmber.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Thermostat,
                                contentDescription = null,
                                tint = FurnaceAmber,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isAr) "معايرة المجسات الحرارية (AMS 2750G / Nadcap)" else "Pyrometry Compliance (AMS 2750G)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "System Accuracy Test (SAT) & Temperature Uniformity (TUS)",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = CyanPrimary
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text(if (isAr) "اختبار دقة النظام (SAT)" else "SAT Calculator") }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text(if (isAr) "التماثل الحراري (TUS)" else "TUS 9-Point Survey") }
                        )
                    }
                }
            }
        }

        if (selectedTab == 0) {
            // TAB 0: SAT TEST
            item {
                val setpoint = setpointStr.toDoubleOrNull() ?: 1050.0
                val ctrlTemp = controlTempStr.toDoubleOrNull() ?: 1050.8
                val refTemp = testRefTempStr.toDoubleOrNull() ?: 1050.2
                val corr = correctionFactorStr.toDoubleOrNull() ?: 0.3

                val correctedRef = refTemp + corr
                val satDifference = abs(ctrlTemp - correctedRef)

                // AMS 2750G Table 14: Type B/C Instrumentation limit: max(1.1°C, 0.4% of temperature)
                val maxAllowableError = max(1.1, setpoint * 0.004)
                val isPass = satDifference <= maxAllowableError

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, if (isPass) StatusGreen.copy(alpha = 0.5f) else AlertRed.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isAr) "نتائج اختبار SAT الميداني:" else "SAT Evaluation Result:",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isPass) StatusGreen else AlertRed,
                                contentColor = Color.Black
                            ) {
                                Text(
                                    text = if (isPass) "PASS (مقبول)" else "FAIL (تجاوز)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = setpointStr,
                                onValueChange = { setpointStr = it },
                                label = { Text("Setpoint (°C)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = controlTempStr,
                                onValueChange = { controlTempStr = it },
                                label = { Text(if (isAr) "قراءة الكنترولر T_ctrl" else "Controller (°C)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = testRefTempStr,
                                onValueChange = { testRefTempStr = it },
                                label = { Text(if (isAr) "قراءة الحساس المرجعي T_ref" else "Reference (°C)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = correctionFactorStr,
                                onValueChange = { correctionFactorStr = it },
                                label = { Text(if (isAr) "معامل التصحيح Corr" else "TC Corr (°C)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Results breakdown
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Corrected Reference Temp: ${String.format(Locale.US, "%.2f", correctedRef)} °C",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "SAT Difference (|T_ctrl - T_corr|): ${String.format(Locale.US, "%.2f", satDifference)} °C",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = if (isPass) StatusGreen else AlertRed,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "AMS 2750G Max Allowable Tolerance: ±${String.format(Locale.US, "%.2f", maxAllowableError)} °C",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        } else {
            // TAB 1: TUS 9-POINT SURVEY
            item {
                val setpoint = tusSetpointStr.toDoubleOrNull() ?: 1050.0
                val parsedTemps = tusTcValues.mapNotNull { it.toDoubleOrNull() }
                val minTemp = if (parsedTemps.isNotEmpty()) parsedTemps.minOrNull() ?: 1050.0 else 1050.0
                val maxTemp = if (parsedTemps.isNotEmpty()) parsedTemps.maxOrNull() ?: 1050.0 else 1050.0
                val maxDeviation = max(abs(maxTemp - setpoint), abs(minTemp - setpoint))
                // AMS 2750G Class 2 tolerance: +/- 6.0 °C
                val isClass2Pass = maxDeviation <= 6.0

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, if (isClass2Pass) StatusGreen.copy(alpha = 0.5f) else AlertRed.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isAr) "مسح التماثل الحراري للحجرة (TUS):" else "TUS Survey Verification:",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isClass2Pass) StatusGreen else AlertRed,
                                contentColor = Color.Black
                            ) {
                                Text(
                                    text = if (isClass2Pass) "Class 2 PASS (±6°C)" else "Class 2 FAIL",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = tusSetpointStr,
                            onValueChange = { tusSetpointStr = it },
                            label = { Text("Survey Setpoint Temperature (°C)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isAr) "قراءات المجسات الـ 9 في أركان ومركز الحجرة:" else "9 Thermocouple Readouts (Corners + Center):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // 3x3 Grid input for thermocouples
                        for (row in 0 until 3) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                for (col in 0 until 3) {
                                    val idx = row * 3 + col
                                    val label = if (idx == 8) "TC9 (Ctr)" else "TC${idx + 1}"
                                    OutlinedTextField(
                                        value = tusTcValues.getOrElse(idx) { "" },
                                        onValueChange = { v ->
                                            val newList = tusTcValues.toMutableList()
                                            if (idx < newList.size) newList[idx] = v
                                            tusTcValues = newList
                                        },
                                        label = { Text(label, fontSize = 9.sp) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        modifier = Modifier.weight(1f),
                                        singleLine = true
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Chamber Uniformity Spread: ΔT = ${String.format(Locale.US, "%.1f", maxTemp - minTemp)} °C (Min: ${minTemp}°C, Max: ${maxTemp}°C)",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isClass2Pass) StatusGreen else AlertRed
                        )
                        Text(
                            text = "Peak Deviation from Setpoint: ±${String.format(Locale.US, "%.2f", maxDeviation)} °C (Allowed Class 2: ±6.0 °C)",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
