package com.example.ui.screens

import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppLanguage
import com.example.ui.AppUiState
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.FurnaceAmber
import com.example.ui.theme.StatusGreen

@Composable
fun OperationGuideScreen(
    uiState: AppUiState,
    contentPadding: PaddingValues
) {
    val isAr = uiState.language == AppLanguage.ARABIC

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isAr) "إجراءات التشغيل والسلامة الرسمية" else "Official Operation & Safety Guide",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isAr) "تعليمات بدء وإيقاف الفرن، تحكم الباب باليدين، وقواعد الأمان الحراري والغازي" else "Startup/shutdown sequences, two-hand door control, and thermal safety rules",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Two-Hand Control Highlight Box (Crucial safety design of TAV 911)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, FurnaceAmber, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = FurnaceAmber,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isAr) "جهاز التحكم بالباب باليدين (ACT1 + ACT2 Remote Pendant)" else "Two-Hand Door Operation (Pilz PIT js2)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = FurnaceAmber
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isAr) "لمنع حوادث انحصار الأطراف أثناء فتح وإغلاق الباب الثقيل:" else "To prevent bodily entrapment during heavy door motion:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isAr) "• إغلاق الباب: أدر مفتاح الاختيار إلى 'CLOSE DOOR'. عند إضاءة مصباح التمكين الأبيض، اضغط واستمر بالضغط متزامناً باليدين على زري ACT1 و ACT2 في الريموت الأصفر Pilz حتى انغلاق حلقات الكبس ومكبس الأمان تلقائياً، ثم أحكم قفل المزلاج اليدوي (Manual Latch).\n• فتح الباب: تأكد أن حرارة الفرن أقل من 90°م واكتمل التبريد. أدر المفتاح إلى 'OPEN DOOR'، واضغط باستمرار على ACT1 + ACT2 لفتح مكبس الأمان والحلقات، ثم اسحب الباب يدوياً حتى يعشق قفل الثبات." else "• Door Closing: Turn selector to 'CLOSE DOOR'. When DOOR ENABLE LAMP lights up, press and hold simultaneously ACT1 and ACT2 on Pilz remote until rings and safety piston close automatically, then lock the manual latch.\n• Door Opening: Ensure furnace is cool (<90°C). Turn selector to 'OPEN DOOR', hold ACT1 + ACT2 simultaneously to release safety piston and rings, then manually slide door until locked.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Startup Procedure Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = StatusGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "خطوات بدء تشغيل الفرن (Furnace Startup)" else "Furnace Startup Sequence",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = StatusGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val startupSteps = if (isAr) listOf(
                        "التأكد من عدم وجود أي أعمال صيانة جارية على الفرن أو ملحقاته.",
                        "فتح جميع الصمامات اليدوية على خط مياه الطوارئ والتحقق من جاهزية خزان المياه وتدفقها.",
                        "التأكد من فتح محابس إعادة ملء خزان المياه وعمل جهاز التحكم بالملء.",
                        "تشغيل القاطع الرئيسي (Main Isolator ON) في اللوحة الرئيسية MiCa.",
                        "التأكد من عدم وجود أي إنذارات نشطة على شاشة SCADA WinCC.",
                        "تشغيل الدوائر المساعدة (Auxiliary Circuits Enable Button) وإضاءة الزر الأزرق.",
                        "إجراء دورة تنظيف وحرق فراغي (Baking / Burn-out) قبل بدء أول تشغيل إنتاجي."
                    ) else listOf(
                        "Ensure no maintenance is being performed on furnace or auxiliary equipment.",
                        "Open all manual valves on emergency water supply and check water availability.",
                        "Ensure water tank refill manual valves are open and refill controller works.",
                        "Switch ON the main isolator on the electrical cabinet.",
                        "Verify no alarms are active on Siemens WinCC SCADA.",
                        "Press Auxiliary Circuits Enable button (blue light illuminates).",
                        "Perform an empty chamber cleaning cycle before initial production batch."
                    )

                    startupSteps.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = StatusGreen.copy(alpha = 0.2f),
                                modifier = Modifier.size(18.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${index + 1}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusGreen
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = step,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Shutdown Procedure Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = null,
                            tint = AlertRed
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "خطوات إيقاف الفرن (Furnace Shutdown)" else "Furnace Shutdown Sequence",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = AlertRed
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val shutdownSteps = if (isAr) listOf(
                        "التأكد من أن حرارة الفرن الداخلية أقل من 100°م تماماً قبل الإيقاف.",
                        "إغلاق باب الفرن وتفريغ الهواء حتى مستوى 0.1 م.بار لمنع تلوث حجرة التسخين بالرطوبة.",
                        "إغلاق الصمام اليدوي على خط مياه الطوارئ لتجنب استهلاك وتفريغ المياه دون داعٍ أثناء التوقف.",
                        "إيقاف الدوائر المساعدة من المفتاح باللوحة الرئيسية.",
                        "فصل القاطع الرئيسي (Main Isolator OFF)."
                    ) else listOf(
                        "Verify furnace internal temperature is cold (below 100°C).",
                        "Close door and pump down to at least 1E-1 mbar to prevent contamination.",
                        "Close manual valve on emergency water line to prevent unnecessary water/gas waste.",
                        "Switch off auxiliary circuits switch on control cabinet.",
                        "Finally switch off the main isolator."
                    )

                    shutdownSteps.forEachIndexed { index, step ->
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = AlertRed.copy(alpha = 0.2f),
                                modifier = Modifier.size(18.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${index + 1}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AlertRed
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = step,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Asphyxiation and Thermal Safety Rules
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isAr) "تحذيرات السلامة الحرارية ومخاطر الاختناق" else "High Temperature & Gas Asphyxiation Warnings",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = AlertRed
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isAr) "1. خطر الاختناق (Asphyxiation): غازات التبريد (Argon و Nitrogen) غازات خانقة عديمة الرائحة تزيح الأكسجين فوراً. يُمنع نهائياً الدخول داخل الغرفة دون تهوية شاملة وقياس نسبة الأكسجين، وتوجيه خطوط العادم خارج المصنع.\n2. خطر الحروق: ارتداء القفازات والنظارات الواقية إلزامي. لا يُفتح الباب والفرن ساخن فوق 90°م لتفادي أكسدة وتلف العوازل الحرارية الجرافيتية.\n3. مضخة الانتشار: يُحظر تماماً فتح مضخة الانتشار للهواء وهي ساخنة (230°م) لتفادي الانفجار الفوري لزيت السيليكون!" else "1. Asphyxiation Risk: Quenching gases (Argon, Nitrogen) displace oxygen instantly. Never enter vessel without ventilation and O2 measurement. Exhausts must vent outdoors.\n2. Thermal Burns: Compulsory use of thermal gloves and eye protection. Never open door above 90°C to prevent chamber oxidation.\n3. Diffusion Pump Explosion Hazard: Never vent diffusion pump while hot (230°C) as silicon oil (DV747) will explode upon air contact!",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
