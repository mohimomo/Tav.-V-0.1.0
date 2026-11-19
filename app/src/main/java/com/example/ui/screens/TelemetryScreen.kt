package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
import kotlinx.coroutines.delay
import java.util.Locale

enum class SimulationPhase(val titleEn: String, val titleAr: String) {
    STANDBY("Standby & Door Closed", "وضع الاستعداد وإغلاق الباب"),
    ROUGHING_VACUUM("Roughing Vacuum (SV630B + WAU2001)", "التفريغ الأولي (مضخة ميكانيكية + رووتس)"),
    HIGH_VACUUM("High Vacuum (Varian NHS 35 Diffusion)", "التفريغ العالي الفائق (مضخة انتشارية)"),
    HEATING_SOAK("Heating Ramp & Thermal Soak (480 kW)", "التسخين وتثبيت الحرارة 1050°C"),
    GAS_QUENCH("High Pressure Gas Quench (5.5 bar)", "التبريد السريع بالغاز المضغوط 5.5 بار")
}

@Composable
fun TelemetryScreen(
    uiState: AppUiState,
    viewModel: AppViewModel,
    contentPadding: PaddingValues
) {
    BackHandler {
        viewModel.navigateTo(NavigationScreen.HOME)
    }

    val isAr = uiState.language == AppLanguage.ARABIC
    var currentPhase by remember { mutableStateOf(SimulationPhase.HEATING_SOAK) }
    var isRunning by remember { mutableStateOf(true) }
    var eStopTriggered by remember { mutableStateOf(false) }

    // Dynamic telemetry values
    var tempZone1 by remember { mutableFloatStateOf(1048f) } // T1 heating elements
    var tempZone2 by remember { mutableFloatStateOf(1042f) } // T2 workload center
    var vacuumExponent by remember { mutableFloatStateOf(-5.2f) } // 10^-5.2 torr
    var gasPressureBar by remember { mutableFloatStateOf(0.02f) }
    var waterFlowRate by remember { mutableFloatStateOf(18.4f) } // m3/h normal

    // Simulation loop
    LaunchedEffect(isRunning, currentPhase, eStopTriggered) {
        while (isRunning && !eStopTriggered) {
            delay(1000)
            when (currentPhase) {
                SimulationPhase.STANDBY -> {
                    tempZone1 = 25f + (Math.random() * 2).toFloat()
                    tempZone2 = 24f
                    vacuumExponent = 0f // 760 torr
                    gasPressureBar = 1.0f
                }
                SimulationPhase.ROUGHING_VACUUM -> {
                    vacuumExponent = (vacuumExponent - 0.3f).coerceAtLeast(-2.5f)
                    tempZone1 = 30f
                }
                SimulationPhase.HIGH_VACUUM -> {
                    vacuumExponent = (vacuumExponent - 0.4f).coerceAtLeast(-5.8f)
                    tempZone1 = 45f
                }
                SimulationPhase.HEATING_SOAK -> {
                    vacuumExponent = -5.4f + (Math.random() * 0.2f - 0.1f).toFloat()
                    tempZone1 = 1045f + (Math.random() * 6f).toFloat()
                    tempZone2 = 1038f + (Math.random() * 5f).toFloat()
                    gasPressureBar = 0.01f
                }
                SimulationPhase.GAS_QUENCH -> {
                    gasPressureBar = 5.4f + (Math.random() * 0.2f).toFloat()
                    tempZone1 = (tempZone1 - 65f).coerceAtLeast(80f)
                    tempZone2 = (tempZone2 - 50f).coerceAtLeast(95f)
                    vacuumExponent = 0.8f
                }
            }
        }
    }

    // Glow Animation for heating phase
    val infiniteTransition = rememberInfiniteTransition(label = "furnace_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp)
            .testTag("telemetry_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Top Header & Control Bar
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
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
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(CyanPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = CyanPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isAr) "محاكي لوحة المراقبة التفاعلية (SCADA Telemetry)" else "Interactive SCADA Telemetry Monitor",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Siemens S7-1513 PLC • Pilz PSS 4000 SIL 3 Live Interlocks",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { isRunning = !isRunning },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isRunning) FurnaceAmber else CyanPrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = if (isRunning) Icons.Default.Stop else Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isRunning) (if (isAr) "إيقاف مؤقت" else "Pause") else (if (isAr) "تشغيل" else "Resume"), color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                eStopTriggered = !eStopTriggered
                                if (eStopTriggered) {
                                    isRunning = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (eStopTriggered) (if (isAr) "إلغاء الطوارئ" else "Reset E-Stop") else (if (isAr) "زر الطوارئ" else "E-STOP"), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 2. Simulated Furnace Chamber Visual
        item {
            val chamberGlowColor = when (currentPhase) {
                SimulationPhase.HEATING_SOAK -> AlertRed.copy(alpha = glowAlpha)
                SimulationPhase.GAS_QUENCH -> CyanPrimary.copy(alpha = 0.6f)
                else -> Color.DarkGray
            }

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                border = androidx.compose.foundation.BorderStroke(2.dp, chamberGlowColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "TAV VACUUM CHAMBER (1200x1200x2300 mm)",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.Gray
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (eStopTriggered) AlertRed else StatusGreen
                            ) {
                                Text(
                                    text = if (eStopTriggered) "PILZ SIL 3 TRIPPED" else "INTERLOCKS OK",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Chamber Center Visual with Status
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = when (currentPhase) {
                                    SimulationPhase.HEATING_SOAK -> Icons.Default.LocalFireDepartment
                                    SimulationPhase.GAS_QUENCH -> Icons.Default.Air
                                    else -> Icons.Default.Speed
                                },
                                contentDescription = null,
                                tint = chamberGlowColor,
                                modifier = Modifier.size(46.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isAr) currentPhase.titleAr else currentPhase.titleEn,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Graphite CFC HeCh Elements • Contactor 152QAC: ${if (currentPhase == SimulationPhase.HEATING_SOAK && !eStopTriggered) "ON (TC1 Active)" else "OFF"}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.LightGray
                            )
                        }

                        // Bottom phase selector chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            SimulationPhase.values().forEach { phase ->
                                val isSelected = currentPhase == phase
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (isSelected) CyanPrimary else Color.DarkGray)
                                        .clickable { currentPhase = phase }
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = phase.name.take(6),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.Black else Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Live Gauges Telemetry Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Temperature Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.LocalFireDepartment, contentDescription = null, tint = AlertRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isAr) "الحرارة T1 / T2" else "Temp T1 / T2", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${tempZone1.toInt()} °C",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = AlertRed
                        )
                        Text(
                            text = "Core T2: ${tempZone2.toInt()} °C",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Vacuum Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Speed, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isAr) "مستوى الفراغ" else "Vacuum Level", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (vacuumExponent <= -1f) "10^${String.format(Locale.US, "%.1f", vacuumExponent)} torr" else "Atmosphere",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = CyanPrimary
                        )
                        Text(
                            text = "Penning & Pirani OK",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Quench Gas Pressure
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Air, contentDescription = null, tint = FurnaceAmber, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isAr) "ضغط الغاز" else "Gas Pressure", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${String.format(Locale.US, "%.2f", gasPressureBar)} bar",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = FurnaceAmber
                        )
                        Text(
                            text = "Safety relief: 6.6 bar",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Water Cooling Flow
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.WaterDrop, contentDescription = null, tint = StatusGreen, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isAr) "تدفق مياه 182BFL" else "Cooling Water", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${String.format(Locale.US, "%.1f", waterFlowRate)} m³/h",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = StatusGreen
                        )
                        Text(
                            text = "In: 18°C • Out: 25°C",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 4. Valve States Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isAr) "حالة الصمامات الرئيسية والقواطع (P&ID Status)" else "Main Valves & Interlocks State (P&ID)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val valves = listOf(
                        Triple("LoVVa1", "Roughing Valve", currentPhase == SimulationPhase.ROUGHING_VACUUM),
                        Triple("HiVVa1", "High Vacuum Valve", currentPhase == SimulationPhase.HIGH_VACUUM || currentPhase == SimulationPhase.HEATING_SOAK),
                        Triple("GaInVa1", "Gas Quench Inlet", currentPhase == SimulationPhase.GAS_QUENCH),
                        Triple("152QAC", "Heating Contactor", currentPhase == SimulationPhase.HEATING_SOAK && !eStopTriggered)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        valves.forEach { (tag, desc, isOpen) ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(if (isOpen) StatusGreen else Color.DarkGray)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(tag, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                Text(if (isOpen) "OPEN" else "CLOSED", fontSize = 9.sp, color = if (isOpen) StatusGreen else Color.Gray, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
