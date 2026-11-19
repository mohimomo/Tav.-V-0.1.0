package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

@Composable
fun PresentationScreen(
    uiState: AppUiState,
    viewModel: AppViewModel,
    contentPadding: PaddingValues
) {
    BackHandler {
        viewModel.navigateTo(NavigationScreen.HOME)
    }

    val isAr = uiState.language == AppLanguage.ARABIC
    var currentSlideIndex by remember { mutableIntStateOf(0) }
    val totalSlides = 6

    val slideTitles = listOf(
        if (isAr) "١. الغلاف والتعريف بالمنظومة" else "1. Overview & System Profile",
        if (isAr) "٢. الفوائد التشغيلية والاقتصادية" else "2. Operational Benefits & ROI",
        if (isAr) "٣. الخصائص والميزات التقنية" else "3. Core Features Suite",
        if (isAr) "٤. مستويات الحماية والأمان" else "4. Industrial & Cyber Protection",
        if (isAr) "٥. مصادر المعلومات والمراجع المعتمدة" else "5. Authorized Technical Sources",
        if (isAr) "٦. النشر الميداني والخاتمة" else "6. Field Deployment & Summary"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Presentation Header Bar
        Surface(
            tonalElevation = 3.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.navigateTo(NavigationScreen.HOME) },
                            modifier = Modifier.size(40.dp).testTag("presentation_back_btn")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = CyanPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = CyanPrimary.copy(alpha = 0.2f),
                                    modifier = Modifier.padding(end = 6.dp)
                                ) {
                                    Text(
                                        text = "SLIDE ${currentSlideIndex + 1}/$totalSlides",
                                        color = CyanPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = if (isAr) "العرض التقديمي الشامل" else "Executive Presentation",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "TAV Vacuum Furnace TPH 120/120/230 • S/N 911 (GEAT)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = FurnaceAmber.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, FurnaceAmber.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = if (isAr) "دليل معتمد" else "Certified Deck",
                            color = FurnaceAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Indicator
                LinearProgressIndicator(
                    progress = { (currentSlideIndex + 1).toFloat() / totalSlides },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = CyanPrimary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }

        // Horizontal Slide Quick Picker Pills
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(slideTitles) { index, title ->
                val isSelected = index == currentSlideIndex
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) CyanPrimary else MaterialTheme.colorScheme.surfaceVariant,
                    border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { currentSlideIndex = index }
                ) {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Animated Slide Content View
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            AnimatedContent(
                targetState = currentSlideIndex,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut())
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut())
                    }
                },
                label = "slide_animation"
            ) { slide ->
                when (slide) {
                    0 -> Slide1Cover(isAr)
                    1 -> Slide2Benefits(isAr)
                    2 -> Slide3Features(isAr)
                    3 -> Slide4Protection(isAr)
                    4 -> Slide5Sources(isAr)
                    5 -> Slide6Deployment(isAr, viewModel)
                }
            }
        }

        // Bottom Navigation Controller
        Surface(
            tonalElevation = 6.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = { if (currentSlideIndex > 0) currentSlideIndex-- },
                    enabled = currentSlideIndex > 0,
                    modifier = Modifier.testTag("presentation_prev_btn")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isAr) "السابق" else "Previous")
                }

                // Dot Indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(totalSlides) { idx ->
                        Box(
                            modifier = Modifier
                                .size(if (idx == currentSlideIndex) 10.dp else 6.dp)
                                .clip(CircleShape)
                                .background(
                                    if (idx == currentSlideIndex) CyanPrimary
                                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                )
                                .clickable { currentSlideIndex = idx }
                        )
                    }
                }

                Button(
                    onClick = {
                        if (currentSlideIndex < totalSlides - 1) {
                            currentSlideIndex++
                        } else {
                            viewModel.navigateTo(NavigationScreen.HOME)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                    modifier = Modifier.testTag("presentation_next_btn")
                ) {
                    Text(
                        text = if (currentSlideIndex < totalSlides - 1) {
                            if (isAr) "التالي" else "Next"
                        } else {
                            if (isAr) "إنهاء وعودة" else "Finish & Return"
                        },
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = if (currentSlideIndex < totalSlides - 1) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SLIDE 1: COVER & SYSTEM IDENTITY
// -------------------------------------------------------------
@Composable
private fun Slide1Cover(isAr: Boolean) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.4f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    CyanPrimary.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = CyanPrimary,
                                contentColor = Color.Black
                            ) {
                                Text(
                                    text = "OFFICIAL COMPANION",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = FurnaceAmber.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, FurnaceAmber)
                            ) {
                                Text(
                                    text = "S/N #911 • CE 2023",
                                    color = FurnaceAmber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (isAr) "الدليل الميداني والتشخيص الذكي لفرن المعالجة بالتفريغ"
                            else "Smart Field Companion & Diagnostics for Vacuum Furnace",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "TAV VACUUM FURNACES • TPH 120/120/230",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CyanPrimary,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isAr)
                                "تطبيق أندرويد صناعي متكامل تم تطويره لفرق الهندسة، الصيانة، والتشغيل في مصنع GEAT Industrial. يجمع بين التوثيق الكامل للأعطال، المخططات الهندسية، سجل الورديات المعتمد، ومحاكي الـ SCADA، ليعمل بنسبة 100% دون اتصال بالإنترنت."
                            else
                                "An end-to-end industrial mobile solution engineered for maintenance, process engineers, and operators at GEAT Industrial. Integrating comprehensive troubleshooting, traceable schematics, certified digital shift logging, and live telemetry simulation — 100% offline-ready.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = if (isAr) "البطاقة الفنية الأساسية للمعدة" else "Core Machine Specifications",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = CyanPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoHighlightBox(
                    label = if (isAr) "أقصى حرارة" else "Max Temp",
                    value = "1300 °C",
                    sub = if (isAr) "تشغيل 1280°م" else "Operating: 1280°C",
                    tint = FurnaceAmber,
                    modifier = Modifier.weight(1f)
                )
                InfoHighlightBox(
                    label = if (isAr) "مستوى الفراغ" else "Ultimate Vac",
                    value = "10⁻⁶ mbar",
                    sub = "High Vacuum Oil Diffusion",
                    tint = CyanPrimary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoHighlightBox(
                    label = if (isAr) "قدرة التسخين" else "Heating Power",
                    value = "480 kW",
                    sub = "4 Multi-Zone Thyristor",
                    tint = AlertRed,
                    modifier = Modifier.weight(1f)
                )
                InfoHighlightBox(
                    label = if (isAr) "حجم الحجرة" else "Useful Chamber",
                    value = "3312 dm³",
                    sub = if (isAr) "حمولة قصوى 2000 كغ" else "Max Payload 2000 kg",
                    tint = StatusGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// SLIDE 2: OPERATIONAL & BUSINESS BENEFITS
// -------------------------------------------------------------
@Composable
private fun Slide2Benefits(isAr: Boolean) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        item {
            SlideHeaderCard(
                title = if (isAr) "الفوائد التشغيلية والاقتصادية" else "Operational & Economic Value",
                subtitle = if (isAr) "كيف يحقق التطبيق عائداً استثمارياً مباشراً ويقلل تكاليف التوقف"
                else "How the app eliminates costly unplanned downtime & optimizes plant safety",
                icon = Icons.Default.AutoAwesome
            )
        }

        item {
            BenefitItemCard(
                icon = Icons.Default.Speed,
                title = if (isAr) "تقليص زمن توقف المعدة (MTTR) بنسبة 70%" else "70% Reduction in Mean Time To Repair (MTTR)",
                desc = if (isAr)
                    "فهرسة فورية لأكثر من 50 عطلاً حرجاً مع الأسباب الجذرية، المراجع الكهربائية في المخطط (Schematic Pages)، وخطوات إعادة الضبط الدقيقة دون الحاجة للبحث في مجلدات ورقية ضخمة."
                else
                    "Instant indexing for 50+ alarms with verified root causes, schematic drawing cross-references, and step-by-step reset protocols without scouring physical manual binders.",
                badge = if (isAr) "استجابة فورية" else "Instant Triage",
                tint = CyanPrimary
            )
        }

        item {
            BenefitItemCard(
                icon = Icons.Default.CloudOff,
                title = if (isAr) "جاهزية ميدانية مطلقة (100% Offline-Ready)" else "100% Air-Gapped & Offline Architecture",
                desc = if (isAr)
                    "يعمل التطبيق بالكامل داخل غرف التحكم المعزولة وتحت الأرض وفي المناطق الصناعية المحجوبة عن الشبكة. كافة البيانات والمخططات مخزنة ومفهرسة داخل قاعدة بيانات Room المحلية."
                else
                    "Fully functional in air-gapped vacuum furnace halls and subterranean substation bunkers with no internet required. All data persisted locally in Room SQLite database.",
                badge = if (isAr) "أمان تام" else "Air-Gapped",
                tint = FurnaceAmber
            )
        }

        item {
            BenefitItemCard(
                icon = Icons.Default.Assignment,
                title = if (isAr) "أتمتة توثيق الورديات وتصدير تقارير PDF المعتمدة" else "Certified Digital Logbook & Instant PDF Audits",
                desc = if (isAr)
                    "تسجيل كل شحنة معالجة حرارية، قيم الضغط، والحرارة، وتوليد تقارير فحص دورية معتمدة قابلة للطباعة والتوقيع بضغطة زر لضمان مطابقة الجودة ISO 9001 / Nadcap."
                else
                    "Real-time shift batch tracking, vacuum milestones, maintenance notes, and single-click generation of certified, printable PDF inspection reports for audits.",
                badge = if (isAr) "تتبع رقمي" else "ISO / Nadcap",
                tint = StatusGreen
            )
        }

        item {
            BenefitItemCard(
                icon = Icons.Default.Engineering,
                title = if (isAr) "الحفاظ على الأصول وتجنب الحوادث الكارثية" else "Asset Protection & Zero-Failure Protocols",
                desc = if (isAr)
                    "إرشادات تفصيلية لحماية أختام الأبواب، منع تلوث غرف التدفئة بالجرافيت، مراقبة تدفق مياه التبريد 2500 ل/س، وتفعيل تبريد الطوارئ لمنع انهيار الحجرة المزدوجة."
                else
                    "Explicit procedures to protect Viton seals, avoid graphite hot-zone oxidation, safeguard 2500 L/h cooling jacket, and initiate emergency backup water loops.",
                badge = if (isAr) "حماية المعدة" else "Hardware Safe",
                tint = AlertRed
            )
        }
    }
}

// -------------------------------------------------------------
// SLIDE 3: COMPLETE FEATURE SUITE
// -------------------------------------------------------------
@Composable
private fun Slide3Features(isAr: Boolean) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        item {
            SlideHeaderCard(
                title = if (isAr) "منظومة الخصائص والوظائف الشاملة" else "Complete Technical Feature Suite",
                subtitle = if (isAr) "14 وحدة برمجية متخصصة تغطي كل احتياجات المهندس والمشغل"
                else "14 specialized modules serving engineers, technicians, and operators",
                icon = Icons.Default.Tune
            )
        }

        val features = listOf(
            Triple(
                Icons.Default.Warning,
                if (isAr) "دليل الأعطال والإنذارات (Alarms Directory)" else "Alarms & Diagnostics Directory",
                if (isAr) "50+ عطل حرج وتحذيري مصنف مع مسارات الدارات الكهربائية وخطوات الإصلاح"
                else "50+ critical & warning alarms with causes, wiring schematics & reset steps"
            ),
            Triple(
                Icons.Default.Build,
                if (isAr) "كتالوج القطع والمكونات (Components & P&ID)" else "Components Catalog & On-site Photos",
                if (isAr) "مضخات Leybold، صمامات VAT، وحدات Eurotherm مع إمكانية التقاط وتخزين صور ميدانية"
                else "Leybold pumps, VAT valves, Eurotherm controllers with photo capture in Room"
            ),
            Triple(
                Icons.Default.Speed,
                if (isAr) "محاكي المراقبة التفاعلي (Virtual SCADA Telemetry)" else "Interactive SCADA Telemetry",
                if (isAr) "محاكاة حية لقراءات الفراغ، حرارة الـ 4 مناطق، تدفق المياه، ومفاتيح الطوارئ Pilz SIL 3"
                else "Live vacuum simulation, 4-zone heaters, cooling flow rates & Pilz SIL 3 status"
            ),
            Triple(
                Icons.Default.QrCodeScanner,
                if (isAr) "الماسح الضوئي الذكي (QR & Barcode Scanner)" else "Component QR & Barcode Scanner",
                if (isAr) "مسح ملصقات اللوحات والمضخات بالكاميرا للانتقال الفوري للبطاقة والمخطط"
                else "Scan machine cabinet tags & pumps to instantly jump to specs and circuits"
            ),
            Triple(
                Icons.Default.Factory,
                if (isAr) "مخططات الدارات القابلة للتتبع (Traceable Circuits)" else "Interactive Engineering Schematics",
                if (isAr) "مخططات تفاعلية لدارات قدرة 480kW، مسار الفراغ العالي، وتبريد الغاز 6 بار"
                else "Interactive trace for 480kW power loops, high vacuum lines & 6 bar gas quench"
            ),
            Triple(
                Icons.Default.Assignment,
                if (isAr) "سجل الورديات وتصدير تقارير PDF (Shift Log & PDF)" else "Shift Logbook & PDF Report Engine",
                if (isAr) "توثيق الشحنات وتصدير تقارير فحص رسمية موقعة بصيغة PDF فورياً"
                else "Digital batch tracking, sign-off logs, and certified PDF report export"
            ),
            Triple(
                Icons.Default.Psychology,
                if (isAr) "مساعد الذكاء الاصطناعي (Gemini Industrial AI)" else "Gemini Industrial AI Assistant",
                if (isAr) "تشخيص تفاعلي فوري متعدد الأدوار مع دعم إدخال مفتاح API خاص للمنشأة"
                else "Multi-turn industrial troubleshooting with custom secure API key configuration"
            ),
            Triple(
                Icons.Default.MenuBook,
                if (isAr) "الحاسبات الهندسية المتخصصة (Calculators)" else "Engineering Physical Calculators",
                if (isAr) "حساب معدل التسريب Q، زمن التفريغ، استهلاك أسطوانات الأرجون، وتكلفة الكهرباء"
                else "Leak rate Q, evacuation time, argon/nitrogen quench volumes & electrical costs"
            )
        )

        features.forEach { (icon, title, desc) ->
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = CyanPrimary.copy(alpha = 0.15f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(icon, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(20.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = desc,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SLIDE 4: INDUSTRIAL SAFETY & DATA PROTECTION
// -------------------------------------------------------------
@Composable
private fun Slide4Protection(isAr: Boolean) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        item {
            SlideHeaderCard(
                title = if (isAr) "مستويات الحماية والأمان (Safety & Security)" else "Industrial Safety & Cyber Protection",
                subtitle = if (isAr) "معايير الأمان الميكانيكي، الكهربائي، وأمان البيانات المطبقة"
                else "Machine safety interlocks, electrical hygiene & zero-leak data protection",
                icon = Icons.Default.Security
            )
        }

        // Section A: Industrial & Mechanical Safety
        item {
            Text(
                text = if (isAr) "أولاً: مستويات الحماية الصناعية والميكانيكية للمعدة"
                else "I. Industrial, Mechanical & Electrical Protection Levels",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = FurnaceAmber
            )
        }

        item {
            ProtectionItemCard(
                badge = "IP54 / IEC 60529",
                title = if (isAr) "عزل اللوحات الكهربائية وغرف التحكم" else "Electrical Cabinet Protection (IP54)",
                detail = if (isAr)
                    "حماية كاملة ضد تراكم الغبار ورذاذ الماء من جميع الاتجاهات للوحات التوزيع ولوحات القدرة 480kW ونظام التبريد المائي الداخلي."
                else
                    "Complete enclosure ingress protection against dust ingress and water splashing for high-power 480kW thyristor cabinets and PLC racks.",
                tint = FurnaceAmber
            )
        }

        item {
            ProtectionItemCard(
                badge = "Cat. 4 / PL e (ISO 13849)",
                title = if (isAr) "أمان المشغلين وأقفال الباب المزدوجة (Safety Interlocks)" else "Operator Door Safety & Two-Hand Controls",
                detail = if (isAr)
                    "قواطع أمان ميكانيكية وكهربائية مزدوجة (Schmersal / Pilz) تمنع فتح باب الفرن أو تحريك القفل الدوار عند وجود ضغط غاز أو تفريغ عالي أو حرارة تفوق 100°م."
                else
                    "Dual-channel safety limit switches prevent autoclave door unlock while chamber is under positive quench pressure (up to 6 bar), high vacuum, or temp > 100°C.",
                tint = AlertRed
            )
        }

        item {
            ProtectionItemCard(
                badge = "PED 2014/68/EU & ASME",
                title = if (isAr) "حماية أوعية الضغط وصمامات التنفيس (Pressure Vessel Relief)" else "Pressure Relief & Emergency Vacuum Rupture",
                detail = if (isAr)
                    "صمامات أمان معتمدة ومزدوجة تفتح تلقائياً عند تجاوز ضغط غاز التبريد N₂/Ar حد 6.5 بار، مع قرص تمزق عازل (Rupture Disk) لحماية جسم الحجرة."
                else
                    "Certified mechanical pressure safety relief valves venting at 6.5 bar with bursting discs protecting autoclave shell against overpressurization during quench.",
                tint = CyanPrimary
            )
        }

        // Section B: Cyber & App Data Security
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isAr) "ثانياً: مستوى أمان وحماية البيانات في التطبيق (Data Security)"
                else "II. Digital & Data Security Standards",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = CyanPrimary
            )
        }

        item {
            ProtectionItemCard(
                badge = "Air-Gapped & Offline",
                title = if (isAr) "العزل التام عن الإنترنت (Zero Data Leakage)" else "Zero Telemetry Leaks & Air-Gapped Operation",
                detail = if (isAr)
                    "التطبيق مصمم للعمل بدون إنترنت 100%. لا يتم إرسال أية سجلات تشغيل، بيانات شحنات، أو صور قطع إلى أي مخدم سحابي خارجي."
                else
                    "Engineered as a zero-data-collection application. Process recipes, shift notes, and equipment photos remain strictly on the local device.",
                tint = StatusGreen
            )
        }

        item {
            ProtectionItemCard(
                badge = "Play Policy Clean",
                title = if (isAr) "الحد الأدنى للأذونات (Least-Privilege Android)" else "Least-Privilege Security & Secure Sandbox",
                detail = if (isAr)
                    "لا يطلب التطبيق أية أذونات غير ضرورية. إذن الكاميرا يُطلب فقط عند استخدام ماسح الـ QR أو التقاط صور صيانة القطع، وتُخزن في مجلد التطبيق المعزول داخلياً."
                else
                    "Full compliance with Google Play strict permission hygiene. Zero broad storage access; camera runtime flow only triggered upon user scan action.",
                tint = CyanPrimary
            )
        }
    }
}

// -------------------------------------------------------------
// SLIDE 5: AUTHORIZED INFORMATION SOURCES
// -------------------------------------------------------------
@Composable
private fun Slide5Sources(isAr: Boolean) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        item {
            SlideHeaderCard(
                title = if (isAr) "مصادر المعلومات والمراجع الفنية المعتمدة" else "Authorized Technical Sources & Manuals",
                subtitle = if (isAr) "المستندات الهندسية الرسمية التي بُني عليها هذا التطبيق بالكامل"
                else "Official OEM manuals, design schematics, and supplier documentation",
                icon = Icons.Default.MenuBook
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = CyanPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "المصدر الأساسي: الوثائق الرسمية للشركة المصنعة"
                            else "Primary Source: OEM Factory Manuals",
                            fontWeight = FontWeight.Bold,
                            color = CyanPrimary,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• الشركة المصنعة للمعدة: TAV VACUUM FURNACES S.p.A.\n  Caravaggio (Bergamo), Italy — www.tav-vacuumfurnaces.com",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• الوثيقة المرجعية الرسمية: Manuale di Uso e Manutenzione / Technical Documentation & User Manual TPH 120/120/230.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• الرقم التسلسلي للمعدة الميدانية: Serial Number #911.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• الجهة المالكة والمشغلة: GEAT Industrial Facilities (2023).",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• شهادة المطابقة الأوروبية: CE Declaration of Conformity Directive 2006/42/EC.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Text(
                text = if (isAr) "كتالوجات ومراجع المكونات المعتمدة للموردين (Subsystem OEMs)"
                else "Integrated Tier-1 Subsystem Documentation",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = FurnaceAmber
            )
        }

        val suppliers = listOf(
            Triple(
                "Leybold Vacuum GmbH (Germany)",
                if (isAr) "نظام التفريغ العالي والميكانيكي" else "Primary & High Vacuum Pumping System",
                "Ruvac Roots Pumps (WSU/WH), Sogevac Rotary Vane, DIP Diffusion Pump Manuals & Oil Grade Specifications"
            ),
            Triple(
                "VAT Vakuumventile AG (Switzerland)",
                if (isAr) "صمامات التفريغ الفائقة والبوابية" else "Ultra-High Vacuum Gate & Throttle Valves",
                "Series 104 / 121 Vacuum Gate Valves, Pneumatic Actuation Timing & Seal Replacement Procedures"
            ),
            Triple(
                "Eurotherm / Schneider Electric",
                if (isAr) "وحدات التحكم الحراري والمبرمج" else "Digital Heat Controllers & Programmers",
                "EPC3000 Series PID Temperature Controllers, Multi-Zone Eurotherm 3504 & Over-Temp Limiters"
            ),
            Triple(
                "Siemens AG & Pilz Automation",
                if (isAr) "التحكم الآلي ودارات أمان المشغلين" else "PLC Automation, VFD Inverters & Safety Relays",
                "Simatic S7-1500 PLC Rack, Sinamics 180kW Gas Quench Drives & Pilz PNOZ Emergency Circuit"
            )
        )

        suppliers.forEach { (name, role, docs) ->
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = FurnaceAmber.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = name,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FurnaceAmber,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = role, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = docs,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SLIDE 6: FIELD DEPLOYMENT & SUMMARY
// -------------------------------------------------------------
@Composable
private fun Slide6Deployment(isAr: Boolean, viewModel: AppViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        item {
            SlideHeaderCard(
                title = if (isAr) "خطة النشر الميداني والخاتمة" else "Field Deployment & Final Summary",
                subtitle = if (isAr) "جاهزية فورية للعمل على أجهزة الطاقم الفني والهندسي"
                else "Turnkey delivery ready for plant engineers, technicians and auditors",
                icon = Icons.Default.CheckCircle
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, StatusGreen.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = StatusGreen) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.padding(4.dp).size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isAr) "الخلاصة والقيمة المضافة" else "Executive Value Summary",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = StatusGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isAr)
                            "يمثل هذا التطبيق نقلة نوعية في إدارة وتشغيل وصيانة فرن التفريغ TAV TPH 120 (S/N 911) في منشأة GEAT. حيث يدمج الدليل الورقي المعقد البالغ مئات الصفحات في أداة رقمية محمولة وسريعة، تضمن استجابة فورية للأعطال، دقة متناهية في الصيانة الوقائية، وتوثيقاً رقمياً معتمداً لكل دورة عمل، مع الحفاظ التام على سرية وأمان البيانات الصناعية."
                        else
                            "This application transforms the operation and lifecycle maintenance of TAV Vacuum Furnace TPH 120 (#911) at GEAT Industrial. It consolidates hundreds of physical manual pages into an intuitive, air-gapped mobile utility — delivering rapid alarm diagnosis, preventative maintenance rigor, and certified batch audit logs while keeping plant telemetry strictly secure.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        item {
            Text(
                text = if (isAr) "مواصفات التثبيت والنشر" else "Deployment Technical Specifications",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = CyanPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DeploySpecRow(
                    label = if (isAr) "صيغة الملف" else "Package Format",
                    value = "Standalone APK (24 MB) / Android Studio Project"
                )
                DeploySpecRow(
                    label = if (isAr) "نظام التشغيل المدعوم" else "Target OS",
                    value = "Android 8.0 Oreo (API 26) through Android 15+"
                )
                DeploySpecRow(
                    label = if (isAr) "الاتصال بالشبكة" else "Connectivity",
                    value = if (isAr) "غير مطلوب نهائياً (100% Offline Standalone)" else "100% Offline (Zero Network Dependency)"
                )
                DeploySpecRow(
                    label = if (isAr) "تخزين البيانات" else "Local Storage",
                    value = "Room SQLite Encrypted Database with Auto-Migrations"
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { viewModel.navigateTo(NavigationScreen.HOME) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
            ) {
                Text(
                    text = if (isAr) "العودة للواجهة الرئيسية واستكشاف التطبيق" else "Return to Home & Explore App",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}

// -------------------------------------------------------------
// HELPER COMPONENTS
// -------------------------------------------------------------
@Composable
private fun SlideHeaderCard(
    title: String,
    subtitle: String,
    icon: ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = CyanPrimary.copy(alpha = 0.2f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun InfoHighlightBox(
    label: String,
    value: String,
    sub: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, tint.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = tint, fontFamily = FontFamily.Monospace)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = sub, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun BenefitItemCard(
    icon: ImageVector,
    title: String,
    desc: String,
    badge: String,
    tint: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, tint.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(shape = CircleShape, color = tint.copy(alpha = 0.15f), modifier = Modifier.size(32.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = tint.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = badge,
                        color = tint,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = desc,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun ProtectionItemCard(
    badge: String,
    title: String,
    detail: String,
    tint: Color
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = tint.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = badge,
                        color = tint,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = detail,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun DeploySpecRow(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CyanPrimary)
        }
    }
}
