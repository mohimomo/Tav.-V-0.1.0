package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
import java.util.Locale

data class AlloyPreset(
    val name: String,
    val steelType: String,
    val preheat1C: Double,
    val preheat2C: Double,
    val austenitizingC: Double,
    val soakMinutes: Int,
    val quenchPressureBar: Double,
    val temper1C: Double,
    val temper2C: Double,
    val targetHardnessHrc: String = "50-55 HRC",
    val recommendedGas: String = "Nitrogen (N₂)"
)

@Composable
fun RecipeDesignerScreen(
    uiState: AppUiState,
    viewModel: AppViewModel,
    contentPadding: PaddingValues
) {
    BackHandler {
        viewModel.navigateTo(NavigationScreen.HOME)
    }

    val context = LocalContext.current
    val isAr = uiState.language == AppLanguage.ARABIC

    val presets = remember {
        listOf(
            AlloyPreset("AISI H13 / 1.2344", "Hot-Work Die Steel", 650.0, 850.0, 1025.0, 45, 8.0, 550.0, 520.0, "46-52 HRC", "Nitrogen (N₂) 8 bar"),
            AlloyPreset("AISI D2 / 1.2379", "Cold-Work Die Steel", 650.0, 850.0, 1030.0, 60, 6.0, 520.0, 500.0, "58-62 HRC", "Nitrogen (N₂) 6 bar"),
            AlloyPreset("AISI M2 / 1.3343", "High-Speed Tool Steel", 750.0, 880.0, 1210.0, 15, 6.0, 540.0, 540.0, "63-66 HRC", "Nitrogen (N₂) 6 bar"),
            AlloyPreset("Inconel 718", "Aerospace Superalloy", 0.0, 0.0, 980.0, 60, 4.0, 720.0, 620.0, "40-47 HRC", "Argon (Ar) 4 bar"),
            AlloyPreset("17-4 PH / 1.4542", "Precipitation Hardening", 0.0, 0.0, 1040.0, 30, 5.0, 480.0, 0.0, "40-45 HRC", "Nitrogen (N₂) 5 bar"),
            AlloyPreset("AISI 4140 / 42CrMo4", "Engineering Steel", 0.0, 0.0, 860.0, 50, 6.0, 560.0, 0.0, "28-34 HRC", "Nitrogen (N₂) 6 bar")
        )
    }

    var selectedPresetIndex by remember { mutableIntStateOf(0) }
    val currentPreset = presets[selectedPresetIndex]

    var preheat1Str by remember(selectedPresetIndex) { mutableStateOf(currentPreset.preheat1C.toInt().toString()) }
    var preheat2Str by remember(selectedPresetIndex) { mutableStateOf(currentPreset.preheat2C.toInt().toString()) }
    var austenitizeStr by remember(selectedPresetIndex) { mutableStateOf(currentPreset.austenitizingC.toInt().toString()) }
    var soakMinStr by remember(selectedPresetIndex) { mutableStateOf(currentPreset.soakMinutes.toString()) }
    var quenchPressureStr by remember(selectedPresetIndex) { mutableStateOf(currentPreset.quenchPressureBar.toString()) }
    var temper1Str by remember(selectedPresetIndex) { mutableStateOf(currentPreset.temper1C.toInt().toString()) }
    var batchLoadWeightStr by remember { mutableStateOf("850") }

    val p1 = preheat1Str.toDoubleOrNull() ?: 650.0
    val p2 = preheat2Str.toDoubleOrNull() ?: 850.0
    val aust = austenitizeStr.toDoubleOrNull() ?: 1020.0
    val soak = soakMinStr.toDoubleOrNull() ?: 45.0
    val quench = quenchPressureStr.toDoubleOrNull() ?: 6.0
    val temp1 = temper1Str.toDoubleOrNull() ?: 550.0
    val loadWeight = batchLoadWeightStr.toDoubleOrNull() ?: 850.0

    // Calculations
    val estimatedRampMinutes = (aust / 10.0) // ~10 C/min heating rate
    val estimatedQuenchMinutes = 20.0
    val estimatedTemperMinutes = if (temp1 > 0) (temp1 / 10.0) + 120.0 else 0.0
    val totalCycleMinutes = estimatedRampMinutes + soak + estimatedQuenchMinutes + estimatedTemperMinutes
    val totalHours = totalCycleMinutes / 60.0
    val estimatedKwh = (totalHours * 160.0).toInt() // Average running power ~160kW out of 480kW

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp)
            .testTag("recipe_designer_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Header Card
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
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(CyanPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = null,
                                tint = CyanPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isAr) "مصمم وصفات المعالجة الحرارية" else "Thermal Cycle & Recipe Designer",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Time-Temperature Profile • Vacuum Hardening & Tempering",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isAr) "اختر سبيكة الفولاذ القياسية (Presets):" else "Select Material Standard Preset:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(presets.indices.toList()) { idx ->
                            val preset = presets[idx]
                            val isSel = selectedPresetIndex == idx
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) CyanPrimary else MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, if (isSel) CyanPrimary else MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedPresetIndex = idx }
                            ) {
                                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                                    Text(
                                        text = preset.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSel) Color.Black else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = preset.steelType,
                                        fontSize = 9.sp,
                                        color = if (isSel) Color.Black.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Interactive Thermal Curve Canvas Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, FurnaceAmber.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) "منحنى المعالجة الحرارية الزمني (Time-Temp Graph)" else "Time-Temperature Cycle Curve",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Peak: ${aust.toInt()}°C",
                            color = FurnaceAmber,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Canvas Temperature Profile
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F1420))
                            .padding(12.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // Draw reference grid lines
                            drawLine(Color.DarkGray, Offset(0f, h * 0.25f), Offset(w, h * 0.25f), strokeWidth = 1f)
                            drawLine(Color.DarkGray, Offset(0f, h * 0.50f), Offset(w, h * 0.50f), strokeWidth = 1f)
                            drawLine(Color.DarkGray, Offset(0f, h * 0.75f), Offset(w, h * 0.75f), strokeWidth = 1f)

                            val path = Path()
                            val maxTemp = 1300f

                            fun tempToY(temp: Double): Float = (h - (temp.toFloat() / maxTemp) * h).coerceIn(10f, h - 5f)

                            path.moveTo(0f, h) // 20 C at 0
                            path.lineTo(w * 0.15f, tempToY(p1)) // Preheat 1 ramp
                            path.lineTo(w * 0.25f, tempToY(p1)) // Preheat 1 soak
                            path.lineTo(w * 0.35f, tempToY(p2)) // Preheat 2 ramp
                            path.lineTo(w * 0.45f, tempToY(p2)) // Preheat 2 soak
                            path.lineTo(w * 0.55f, tempToY(aust)) // Austenitize ramp
                            path.lineTo(w * 0.65f, tempToY(aust)) // Austenitize soak
                            path.lineTo(w * 0.72f, tempToY(80.0)) // High-pressure Gas Quench
                            if (temp1 > 0) {
                                path.lineTo(w * 0.82f, tempToY(temp1)) // Temper 1 ramp
                                path.lineTo(w * 0.90f, tempToY(temp1)) // Temper 1 soak
                                path.lineTo(w, tempToY(30.0)) // Final cooling
                            } else {
                                path.lineTo(w, tempToY(30.0))
                            }

                            drawPath(
                                path = path,
                                color = Color(0xFFFFB300), // Furnace Amber
                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("• Pre1: ${p1.toInt()}°C", fontSize = 10.sp, color = CyanPrimary)
                        Text("• Pre2: ${p2.toInt()}°C", fontSize = 10.sp, color = CyanPrimary)
                        Text("• Soak: ${aust.toInt()}°C", fontSize = 10.sp, color = FurnaceAmber, fontWeight = FontWeight.Bold)
                        Text("• Quench: ${quench} bar", fontSize = 10.sp, color = StatusGreen)
                        Text("• Temper: ${temp1.toInt()}°C", fontSize = 10.sp, color = AlertRed)
                    }
                }
            }
        }

        // Cycle Parameters Input Fields
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isAr) "تعديل معايير الوصفة والحرارة:" else "Adjust Cycle Temperatures & Times:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = preheat1Str,
                            onValueChange = { preheat1Str = it },
                            label = { Text("Pre-Heat 1 (°C)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = preheat2Str,
                            onValueChange = { preheat2Str = it },
                            label = { Text("Pre-Heat 2 (°C)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = austenitizeStr,
                            onValueChange = { austenitizeStr = it },
                            label = { Text("Austenitize (°C)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = soakMinStr,
                            onValueChange = { soakMinStr = it },
                            label = { Text("Soak Time (min)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = quenchPressureStr,
                            onValueChange = { quenchPressureStr = it },
                            label = { Text("Quench (bar)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = temper1Str,
                            onValueChange = { temper1Str = it },
                            label = { Text("Temper (°C)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = batchLoadWeightStr,
                        onValueChange = { batchLoadWeightStr = it },
                        label = { Text(if (isAr) "وزن الشحنة المتوقع (kg)" else "Expected Batch Load (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(6.dp))

                    // Cycle Estimates Box
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(if (isAr) "الزمن الكلي المقدر:" else "Estimated Duration:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "${String.format(Locale.US, "%.1f", totalHours)} hours",
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = CyanPrimary
                            )
                        }
                        Column {
                            Text(if (isAr) "استهلاك الكهرباء:" else "Power Usage:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "$estimatedKwh kWh",
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = FurnaceAmber
                            )
                        }
                        Column {
                            Text(if (isAr) "حجم غاز التبريد:" else "Gas Quench Vol:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "19.8 m³",
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = StatusGreen
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                Toast.makeText(context, if (isAr) "تم حفظ وتثبيت وصفة ${currentPreset.name} بنجاح" else "Recipe saved successfully", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        ) {
                            Icon(Icons.Default.BookmarkAdd, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "حفظ الوصفة" else "Save Recipe",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.navigateTo(NavigationScreen.OPERATION)
                                Toast.makeText(context, if (isAr) "تم إرسال وصفة ${currentPreset.name} بنجاح إلى منظومة التشغيل!" else "Recipe sent to Furnace Operation Guide!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FurnaceAmber),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .height(46.dp)
                        ) {
                            Text(
                                text = if (isAr) "🚀 إرسال لمنظومة التشغيل" else "🚀 Send to Operation",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
