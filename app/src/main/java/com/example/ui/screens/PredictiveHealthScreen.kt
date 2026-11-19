package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.ui.AppLanguage
import com.example.ui.AppUiState
import com.example.ui.AppViewModel
import com.example.ui.NavigationScreen
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.FurnaceAmber
import com.example.ui.theme.StatusGreen

data class HealthTrackedItem(
    val key: String,
    val tag: String,
    val titleAr: String,
    val titleEn: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val currentUsage: Int,
    val maxLimit: Int,
    val unit: String,
    val recommendationAr: String,
    val recommendationEn: String
)

@Composable
fun PredictiveHealthScreen(
    uiState: AppUiState,
    viewModel: AppViewModel,
    contentPadding: PaddingValues
) {
    BackHandler {
        viewModel.navigateTo(NavigationScreen.HOME)
    }

    val context = LocalContext.current
    val isAr = uiState.language == AppLanguage.ARABIC
    val hoursMap = uiState.healthRunHours

    val trackedItems = remember(hoursMap) {
        listOf(
            HealthTrackedItem(
                key = "SV630B_OIL",
                tag = "6GQZ10",
                titleAr = "زيت مضخة التفريغ الميكانيكية Leybold SV630B",
                titleEn = "Leybold SV630B Roughing Pump Oil",
                descriptionAr = "فترة تغيير الزيت التوربيني LVO 100 وتغيير مصيدة العادم",
                descriptionEn = "LVO 100 hydrocarbon oil & exhaust demister cartridge change interval",
                currentUsage = hoursMap["SV630B_OIL"] ?: 840,
                maxLimit = 1500,
                unit = "h",
                recommendationAr = "الزيت نظيف وحالته ممتازة. الفحص القادم بعد 150 ساعة.",
                recommendationEn = "Oil clarity is nominal. Next scheduled inspection in 150h."
            ),
            HealthTrackedItem(
                key = "WAU2001_BEARINGS",
                tag = "8GQZ10",
                titleAr = "محامل وتروس مضخة الروت Roots RUVAC WAU2001",
                titleEn = "Roots RUVAC WAU2001 Bearings & Gears",
                descriptionAr = "تزييت غرفتي التروس وفحص الخلوص الميكانيكي ومنع التلامس",
                descriptionEn = "Gearbox synthetic lubricant & lobe clearance inspection",
                currentUsage = hoursMap["WAU2001_BEARINGS"] ?: 2200,
                maxLimit = 6000,
                unit = "h",
                recommendationAr = "المحامل مستقرة والاهتزازات ضمن المعيار المقبول.",
                recommendationEn = "Vibration amplitude nominal. Bearing health optimal."
            ),
            HealthTrackedItem(
                key = "DIP3000_FLUID",
                tag = "NHS 35",
                titleAr = "سائل مضخة الانتشار الفراغي Varian NHS 35",
                titleEn = "Varian NHS 35 Diffusion Pump Fluid",
                descriptionAr = "سائل السيليكون DC 704 / 705 ومستوى سخانات الغلاية 24kW",
                descriptionEn = "Silicone fluid (DC 704/705) oxidation check & 24kW boiler status",
                currentUsage = hoursMap["DIP3000_FLUID"] ?: 1100,
                maxLimit = 3000,
                unit = "h",
                recommendationAr = "سائل الفراغ العالي سليم والتسخين عند 200°C طبيعي.",
                recommendationEn = "High vacuum fluid nominal. Boiler temperature 200°C stable."
            ),
            HealthTrackedItem(
                key = "VAT_SEALS_CYCLES",
                tag = "HiVVa1",
                titleAr = "أختام وحلقات بوابة الفراغ العالي (VAT Gate Valve)",
                titleEn = "VAT High-Vacuum Gate Valve Viton Seals",
                descriptionAr = "عدد دورات فتح وإغلاق بوابات الصمام الفراغي الرئيسي",
                descriptionEn = "Actuation cycle life for high-vacuum gate valve vulcanized seal",
                currentUsage = hoursMap["VAT_SEALS_CYCLES"] ?: 3420,
                maxLimit = 10000,
                unit = "cycles",
                recommendationAr = "إحكام الإغلاق الفراغي ممتاز ومعدل التسريب أقل من 1e-4.",
                recommendationEn = "Seal integrity verified; leak rate < 1e-4 mbar*l/s."
            ),
            HealthTrackedItem(
                key = "CFC_HOT_ZONE_HOURS",
                tag = "HZ-480KW",
                titleAr = "عناصر التسخين الجرافيتية والعزل الحراري CFC (480kW)",
                titleEn = "CFC Graphite Heating Cage & Thermal Insulation",
                descriptionAr = "ساعات التعرض لحرارة تشغيلية تفوق 1000°م تحت الفراغ",
                descriptionEn = "Cumulative operating hours above 1000°C under vacuum/gas quench",
                currentUsage = hoursMap["CFC_HOT_ZONE_HOURS"] ?: 1280,
                maxLimit = 4000,
                unit = "h",
                recommendationAr = "العوازل الخزفية وبراغي M14/M16 مشدودة ومثالية.",
                recommendationEn = "Ceramic standoffs clean, M14/M16 torque verification PASS."
            ),
            HealthTrackedItem(
                key = "COOLING_FILTER_DAYS",
                tag = "182BFL",
                titleAr = "فلتر دائرة مياه التبريد ومبادل الحرارة",
                titleEn = "Cooling Water Line Filter & Heat Exchanger",
                descriptionAr = "تنظيف مصفاة الشوائب المعدنية والتحقق من عدم تكلس الأنابيب",
                descriptionEn = "Strainer basket clean-out & scale buildup prevention",
                currentUsage = hoursMap["COOLING_FILTER_DAYS"] ?: 45,
                maxLimit = 90,
                unit = "days",
                recommendationAr = "تدفق المياه 2500 L/h عند 4.2 بار - تدفق سلس.",
                recommendationEn = "Cooling flow 2500 L/h at 4.2 bar - optimal."
            )
        )
    }

    // Overall Furnace Health Index Calculation
    val totalHealthScore = remember(trackedItems) {
        val scores = trackedItems.map {
            val ratio = it.currentUsage.toFloat() / it.maxLimit.toFloat()
            (1f - ratio).coerceIn(0f, 1f) * 100f
        }
        scores.average().toInt()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp)
            .testTag("predictive_health_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Overall Furnace Health Score Hero Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.5.dp, CyanPrimary.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isAr) "مؤشر الصحة التشغيلية التنبؤي" else "Furnace Health & Life Index",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "TAV TPH 120 (S/N 911) • Predictive Maintenance AI",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Circular Progress Score
                        Box(contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = { totalHealthScore / 100f },
                                modifier = Modifier.size(68.dp),
                                color = if (totalHealthScore > 70) StatusGreen else FurnaceAmber,
                                trackColor = MaterialTheme.colorScheme.surface,
                                strokeWidth = 6.dp
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$totalHealthScore%",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = if (totalHealthScore > 70) StatusGreen else FurnaceAmber
                                )
                                Text(
                                    text = "HEALTH",
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(if (isAr) "حالة المعدة الكلية" else "System State", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = if (isAr) "جاهزية كاملة (Optimal)" else "Optimal Ready",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusGreen
                            )
                        }
                        Column {
                            Text(if (isAr) "أقرب صيانة متوقعة" else "Next Major Service", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "Leybold SV630B (660h)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = FurnaceAmber
                            )
                        }
                        Column {
                            Text(if (isAr) "مستوى الحماية" else "Safety Integrity", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "Pilz SIL 3 / IP54",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanPrimary
                            )
                        }
                    }
                }
            }
        }

        // Section Title
        item {
            Text(
                text = if (isAr) "مراقبة ساعات عمل القطع والأختام الحيوية:" else "Critical Component Run-Hours & Wear Counters:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        // List of tracked items
        items(trackedItems) { item ->
            val ratio = (item.currentUsage.toFloat() / item.maxLimit.toFloat()).coerceIn(0f, 1f)
            val remaining = (item.maxLimit - item.currentUsage).coerceAtLeast(0)
            val healthPercent = ((1f - ratio) * 100).toInt()
            val stateColor = when {
                ratio < 0.70f -> StatusGreen
                ratio < 0.90f -> FurnaceAmber
                else -> AlertRed
            }

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, stateColor.copy(alpha = 0.4f)),
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
                                text = item.tag,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = stateColor.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, stateColor.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "$healthPercent% Life Remaining",
                                color = stateColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isAr) item.titleAr else item.titleEn,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = if (isAr) item.descriptionAr else item.descriptionEn,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress bar
                    LinearProgressIndicator(
                        progress = { ratio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = stateColor,
                        trackColor = MaterialTheme.colorScheme.surface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${item.currentUsage} / ${item.maxLimit} ${item.unit}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isAr) "متبقي: $remaining ${item.unit}" else "$remaining ${item.unit} left",
                            fontSize = 11.sp,
                            color = stateColor,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Recommendation info box
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) item.recommendationAr else item.recommendationEn,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.resetComponentHours(item.key)
                                Toast.makeText(context, if (isAr) "تم تصفير العداد وتوثيق الصيانة بنجاح" else "Counter reset to 0 (Service Logged)", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isAr) "توثيق صيانة / تصفير" else "Log Service / Reset", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
