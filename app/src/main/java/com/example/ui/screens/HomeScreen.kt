package com.example.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.data.FurnaceSpecsData
import com.example.model.SubsystemCategory
import com.example.ui.AppLanguage
import com.example.ui.AppUiState
import com.example.ui.AppViewModel
import com.example.ui.NavigationScreen
import com.example.ui.tr
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.FurnaceAmber
import com.example.ui.theme.StatusGreen

@Composable
fun HomeScreen(
    uiState: AppUiState,
    viewModel: AppViewModel,
    contentPadding: PaddingValues
) {
    val isAr = uiState.language == AppLanguage.ARABIC
    var expandedSpecs by remember { mutableStateOf(false) }
    var showCePlate by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Top Industrial Hero Header
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        Brush.horizontalGradient(listOf(CyanPrimary, FurnaceAmber)),
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = CyanPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "TAV VACUUM FURNACES",
                                color = CyanPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                letterSpacing = 1.sp
                            )
                        }

                        // Machine Certification Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = FurnaceAmber.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, FurnaceAmber.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "CE 2023 • S/N 911",
                                color = FurnaceAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Model: ${FurnaceSpecsData.MODEL}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Serial No: ${FurnaceSpecsData.SERIAL_NUMBER}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = FurnaceAmber,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "•  Year: ${FurnaceSpecsData.YEAR}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "•  Customer: ${FurnaceSpecsData.CUSTOMER}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Simulated Realtime Status Bar
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(StatusGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isAr) "حالة الفرن: جاهز للتشغيل" else "Status: Ready / Standby",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = StatusGreen
                                )
                            }
                            Text(
                                text = "10⁻⁴ torr • 24°C • 5.6 bar",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // 2. Smart Quick Search Launcher
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(NavigationScreen.SEARCH) }
                    .testTag("home_search_launcher_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = CyanPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isAr) "البحث الذكي (اسم القطعة، كود الخطأ، الرمز...)" else "Smart Search (Part name, error code, tag...)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isAr) "مثال: 6GQZ10, LoVVa1, SV630, NHS 35, تسريب, فرط حرارة" else "e.g. 6GQZ10, LoVVa1, SV630, NHS 35, leak, overtemperature",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Quick Tag Suggestions
        item {
            Column {
                Text(
                    text = if (isAr) "عمليات بحث سريعة شائعة:" else "Quick Search Tags:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                val quickTags = listOf(
                    "6GQZ10", "SV630B", "RoPu1", "NHS 35", "LoVVa1", "HiVVa1",
                    "Vacuum Interlock", "Water overtemperature", "M14 CFC", "S7-1513", "EPC3016"
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quickTags) { tag ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clickable { viewModel.quickSearch(tag) }
                                .testTag("quick_tag_$tag")
                        ) {
                            Text(
                                text = tag,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 3. Key Specifications Cards Grid
        item {
            Text(
                text = if (isAr) "المواصفات الأساسية للفرن TPH 120/120/230" else "Core Specifications (TPH 120/120/230)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SpecMiniCard(
                    title = if (isAr) "أقصى حرارة" else "Max Temp",
                    value = "1300 °C",
                    sub = if (isAr) "تشغيل 1280°م" else "Oper: 1280°C",
                    icon = Icons.Default.Warning,
                    tint = FurnaceAmber,
                    modifier = Modifier.weight(1f)
                )
                SpecMiniCard(
                    title = if (isAr) "مستوى الفراغ" else "Ultimate Vac",
                    value = "10⁻⁶ torr",
                    sub = if (isAr) "تشغيل 10⁻⁴/10⁻⁵" else "10⁻⁴ / 10⁻⁵",
                    icon = Icons.Default.Info,
                    tint = CyanPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SpecMiniCard(
                    title = if (isAr) "قدرة التسخين" else "Heating Power",
                    value = "480 kW",
                    sub = if (isAr) "حمل 750 kVA" else "Load: 750 kVA",
                    icon = Icons.Default.Settings,
                    tint = AlertRed,
                    modifier = Modifier.weight(1f)
                )
                SpecMiniCard(
                    title = if (isAr) "الحجم والشحنة" else "Chamber & Load",
                    value = "3312 dm³",
                    sub = if (isAr) "أقصى حمولة 2000kg" else "Max: 2000 kg",
                    icon = Icons.Default.Build,
                    tint = StatusGreen,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 4. Quick Nav Feature Cards
        item {
            Text(
                text = if (isAr) "أقسام الدليل المعتمد" else "Manual Sections",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                NavFeatureRow(
                    title = uiState.language.tr("العرض التقديمي الشامل للمنظومة (Executive Slide Deck)", "Executive System Presentation Deck", "Présentation exécutive complète"),
                    subtitle = uiState.language.tr("عرض تفاعلي يشرح كامل خصائص التطبيق، فوائده، مستويات الحماية IP54/SIL 3، ومصادر المعلومات المعتمدة", "Interactive slides covering app capabilities, ROI benefits, IP54/SIL 3 safety levels & authorized OEM sources", "Diaporama complet expliquant les fonctionnalités, le ROI, les niveaux de sécurité et les sources officielles"),
                    icon = Icons.Default.AutoAwesome,
                    tint = CyanPrimary,
                    onClick = { viewModel.navigateTo(NavigationScreen.PRESENTATION) }
                )
                NavFeatureRow(
                    title = uiState.language.tr("سجل الوردية والتشغيل وتصدير تقارير PDF الرسمية", "Digital Shift Logbook & Official PDF Reports", "Journal de quart et rapports officiels PDF"),
                    subtitle = uiState.language.tr("توثيق الشحنات وحرارة المعالجة وتصدير تقرير فحص رسمي PDF مع التوقيع", "Batch logging, process parameters & instant export of certified PDF maintenance report", "Suivi des charges, paramètres et exportation de rapport d'inspection officiel PDF"),
                    icon = Icons.Default.Assignment,
                    tint = FurnaceAmber,
                    onClick = { viewModel.navigateTo(NavigationScreen.SHIFT_LOG) }
                )
                NavFeatureRow(
                    title = uiState.language.tr("الحاسبات الهندسية (معدل التسريب Q • غاز التبريد • الطاقة 480kW)", "Engineering Calculators (Leak Rate Q • Quench Gas • Power)", "Calculateurs d'ingénierie (Taux de fuite Q • Gaz • Énergie 480kW)"),
                    subtitle = uiState.language.tr("حساب زمن التفريغ ومعدل التسريب واستهلاك أسطوانات الأرجون وتكلفة الكهرباء", "Calculate vacuum leak-up rate, argon/nitrogen cylinders & cycle electrical cost", "Calcul du taux de remontée de vide, bouteilles d'argon et coût électrique"),
                    icon = Icons.Default.Calculate,
                    tint = CyanPrimary,
                    onClick = { viewModel.navigateTo(NavigationScreen.CALCULATORS) }
                )
                NavFeatureRow(
                    title = uiState.language.tr("محاكي لوحة المراقبة التفاعلية (Virtual SCADA Telemetry)", "Interactive Virtual SCADA Telemetry Monitor", "Moniteur de télémétrie SCADA interactif"),
                    subtitle = uiState.language.tr("مراقبة حية لمحاكاة الفراغ ودرجات الحرارة 4 مناطق وتدفق المياه ومفاتيح الطوارئ", "Live simulated telemetry: multi-zone heat, vacuum levels, water flow & Pilz SIL 3 E-Stop", "Télémétrie en direct: vide, températures multi-zones, débit d'eau et arrêt d'urgence"),
                    icon = Icons.Default.Speed,
                    tint = StatusGreen,
                    onClick = { viewModel.navigateTo(NavigationScreen.TELEMETRY) }
                )
                NavFeatureRow(
                    title = uiState.language.tr("قارئ رموز QR والباركود للقطع والمكونات", "Furnace Parts QR & Barcode Scanner", "Lecteur de codes QR et codes-barres des composants"),
                    subtitle = uiState.language.tr("مسح فوري لأكواد المضخات والصمامات والمجسات للوصول لبطاقتها الفنية والمخطط", "Instant camera scanning for pumps, valves & breakers with direct schematic lookup", "Scan immédiat des pompes, vannes et disjoncteurs avec accès aux schémas"),
                    icon = Icons.Default.QrCodeScanner,
                    tint = FurnaceAmber,
                    onClick = { viewModel.navigateTo(NavigationScreen.SCANNER) }
                )
                NavFeatureRow(
                    title = uiState.language.tr("مساعد الذكاء الاصطناعي الصناعي (Gemini Industrial AI)", "Gemini Industrial AI Assistant", "Assistant IA Industriel Gemini"),
                    subtitle = uiState.language.tr("محادثة ذكية متعددة الأدوار، تشخيص فوري، بحث الويب الموثق، وتوليد رسوم هندسية", "Multi-turn AI diagnostics, search grounding, reasoning & technical diagram generation", "Diagnostic intelligent, raisonnement technique, recherche web et schémas"),
                    icon = Icons.Default.Psychology,
                    tint = CyanPrimary,
                    onClick = { viewModel.navigateTo(NavigationScreen.AI_CHAT) }
                )
                NavFeatureRow(
                    title = uiState.language.tr("مخططات الدارات القابلة للتتبع (كهربائية • مائية • غازية • هيدروليكية)", "Traceable Circuit Schematics (Electrical • Water • Gas • Hydraulic)", "Schémas de circuits traçables (Électrique • Eau • Gaz • Hydraulique)"),
                    subtitle = uiState.language.tr("مخططات هندسية تفاعلية لتتبع مسارات 480kW، التبريد، الغاز 6 بار، وأمان الباب", "Interactive schematics tracing 480kW power loops, cooling, 6 bar gas & door safety", "Schémas interactifs traçant la puissance 480kW, le refroidissement et la sécurité"),
                    icon = Icons.Default.Build,
                    tint = FurnaceAmber,
                    onClick = { viewModel.navigateTo(NavigationScreen.CIRCUITS) }
                )
                NavFeatureRow(
                    title = uiState.language.tr("الدليل الميداني وملاحظات الفنيين (Room DB)", "Offline Field Manual & Notes (Room DB)", "Manuel de terrain hors-ligne et notes (Room)"),
                    subtitle = uiState.language.tr("تخزين محلي لكافة نصوص الدليل والصفحات وملاحظات الصيانة بدون إنترنت", "Full local storage of all PDF manual text, page indices & technician notes", "Stockage local complet des manuels, pages et notes de maintenance sans internet"),
                    icon = Icons.Default.Info,
                    tint = CyanPrimary,
                    onClick = { viewModel.navigateTo(NavigationScreen.MANUAL) }
                )
                NavFeatureRow(
                    title = uiState.language.tr("مؤشر صحة القطع وساعات التشغيل التنبؤية (Health Index)", "Predictive Component Health & Run-Hours", "Indice de santé des composants et heures de marche"),
                    subtitle = uiState.language.tr("تتبع ساعات عمل زيوت المضخات (SV630B/WAU)، دورات أختام VAT، وحرارة عناصر 480kW", "Track roughing oil hours, booster bearings, VAT valve cycles & 480kW hot zone life", "Suivi des heures d'huile, roulements, cycles de vannes et zone chaude"),
                    icon = Icons.Default.Speed,
                    tint = StatusGreen,
                    onClick = { viewModel.navigateTo(NavigationScreen.PREDICTIVE_HEALTH) }
                )
                NavFeatureRow(
                    title = uiState.language.tr("مولد ملصقات QR الصناعية القابلة للطباعة (A4 Stickers)", "Printable Industrial QR Label Sheets (A4)", "Générateur d'étiquettes QR industrielles imprimables"),
                    subtitle = uiState.language.tr("تصدير وطباعة ملصقات A4 لاصقة لكل مضخة وصمام ولوحة لتثبيتها في ورشة العمل", "Export printable adhesive QR sticker sheets for physical pumps, valves & panels", "Exportez des planches d'autocollants QR pour étiqueter le four"),
                    icon = Icons.Default.QrCodeScanner,
                    tint = CyanPrimary,
                    onClick = { viewModel.navigateTo(NavigationScreen.QR_LABELS) }
                )
                NavFeatureRow(
                    title = uiState.language.tr("معايرة المجسات الحرارية (AMS 2750G / SAT & TUS Pyrometry)", "Pyrometry Compliance Helper (AMS 2750G / Nadcap)", "Conformité pyrométrique AMS 2750G (SAT & TUS)"),
                    subtitle = uiState.language.tr("حساب دقة النظام SAT ومسح التماثل الحراري 9 نقاط ومطابقة معايير الطيران Nadcap", "System Accuracy Test (SAT) calculation & 9-point TUS chamber uniformity survey", "Calculs SAT et cartographie d'homogénéité thermique TUS 9 points"),
                    icon = Icons.Default.Build,
                    tint = FurnaceAmber,
                    onClick = { viewModel.navigateTo(NavigationScreen.PYROMETRY) }
                )
                NavFeatureRow(
                    title = uiState.language.tr("فحص اهتزاز المضخات بمستشعر الهاتف (Vibration ISO 10816)", "Vibration FFT Analyzer (ISO 10816)", "Analyseur de vibrations des pompes (ISO 10816)"),
                    subtitle = uiState.language.tr("قياس سرعة الاهتزاز mm/s RMS بمستشعر التسارع وتقييم المحامل في 5 ثوانٍ", "Measure vibration velocity RMS (mm/s) using phone accelerometer to assess bearings", "Mesurez la vitesse RMS des vibrations pour évaluer l'état des roulements"),
                    icon = Icons.Default.Speed,
                    tint = CyanPrimary,
                    onClick = { viewModel.navigateTo(NavigationScreen.VIBRATION_ANALYSIS) }
                )
                NavFeatureRow(
                    title = uiState.language.tr("تصاريح العزل والسلامة الصناعية (LOTO & Safe Entry)", "Lockout / Tagout & Safe Entry Permits (LOTO)", "Consignation et permis de sécurité LOTO"),
                    subtitle = uiState.language.tr("تحقق إلزامي من 5 خطوات لعزل 480kW وتفريغ الغاز وتصدير تصريح رسمي PDF", "Mandatory 5-step energy isolation, zero-energy check & certified PDF permit export", "Vérification en 5 étapes d'isolement énergétique et permis officiel PDF"),
                    icon = Icons.Default.Security,
                    tint = AlertRed,
                    onClick = { viewModel.navigateTo(NavigationScreen.SAFETY_LOTO) }
                )
                NavFeatureRow(
                    title = uiState.language.tr("مصمم ومنشئ وصفات المعالجة الحرارية (Thermal Cycles)", "Thermal Cycle & Recipe Designer", "Concepteur de cycles thermiques et recettes"),
                    subtitle = uiState.language.tr("منحنى حراري تفاعلي لسبائك H13/D2/M2 مع حساب استهلاك الغاز 6 بار والطاقة", "Interactive time-temp profile for H13/D2/M2 with gas quench & power estimation", "Profil temps-température pour H13/D2 avec calcul de gaz 6 bar et énergie"),
                    icon = Icons.Default.Speed,
                    tint = FurnaceAmber,
                    onClick = { viewModel.navigateTo(NavigationScreen.RECIPE_DESIGNER) }
                )
                NavFeatureRow(
                    title = uiState.language.tr("استكشاف الأعطال الشجري التفاعلي (Fault-Tree Wizard)", "Guided Fault-Tree Diagnostic Wizard", "Assistant de diagnostic arborescent (Fault-Tree)"),
                    subtitle = uiState.language.tr("تشخيص موجه خطوة بخطوة بالأسئلة للوصول للسبب الجذري للأعطال في 60 ثانية", "Interactive step-by-step branching questions identifying root cause in 60 seconds", "Diagnostic guidé étape par étape pour identifier la cause racine en 60s"),
                    icon = Icons.Default.Build,
                    tint = StatusGreen,
                    onClick = { viewModel.navigateTo(NavigationScreen.FAULT_TREE) }
                )
                NavFeatureRow(
                    title = uiState.language.tr("بروتوكول تسليم واستلام الورديات (Shift Handover)", "Digital Shift Handover Protocol", "Protocole de transmission de quart (Handover)"),
                    subtitle = uiState.language.tr("نقل المسؤولية بين الورديتين ومتابعة المهام العالقة والتوقيع الرقمي المعتمد", "Seamless shift handover, pending action items tracking & digital sign-off", "Passation de consignes entre équipes, actions en attente et signature"),
                    icon = Icons.Default.Assignment,
                    tint = CyanPrimary,
                    onClick = { viewModel.navigateTo(NavigationScreen.SHIFT_HANDOVER) }
                )
                NavFeatureRow(
                    title = uiState.language.tr("مركز تبادل البيانات والمزامنة الميدانية (Peer Sync)", "Industrial Peer Data Exchange & Sync", "Centre d'échange et synchronisation P2P"),
                    subtitle = uiState.language.tr("مزامنة الشحنات والسجلات وساعات التشغيل بين الهواتف عبر QR وبلوتوث و Wi-Fi محلي", "Direct zero-internet sync of shift logs & run-hours via QR Beam, Bluetooth & local Wi-Fi", "Synchronisation directe des lots et heures via QR, Bluetooth et Wi-Fi local"),
                    icon = Icons.Default.Sync,
                    tint = StatusGreen,
                    onClick = { viewModel.navigateTo(NavigationScreen.DATA_EXCHANGE) }
                )
                NavFeatureRow(
                    title = uiState.language.tr("إجراءات التشغيل والسلامة (تحكم الباب باليدين)", "Operation Guide & Two-Hand Door Safety", "Guide d'exploitation et sécurité porte bimanuelle"),
                    subtitle = uiState.language.tr("تشغيل وإيقاف الفرن، أزرار ACT1+ACT2، المزلاج اليدوي والأمان", "Startup/shutdown procedures, ACT1+ACT2 pendant & manual latch", "Procédures de démarrage, boutons ACT1+ACT2 et verrouillage de porte"),
                    icon = Icons.Default.Security,
                    tint = StatusGreen,
                    onClick = { viewModel.navigateTo(NavigationScreen.OPERATION) }
                )
            }
        }

        // 5. Expandable Full Technical Specifications
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedSpecs = !expandedSpecs },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) "جدول البيانات الفنية التفصيلي (Data Sheet)" else "Full Technical Data Sheets",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = if (expandedSpecs) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = "Toggle"
                        )
                    }

                    AnimatedVisibility(visible = expandedSpecs) {
                        Column(
                            modifier = Modifier.padding(top = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FurnaceSpecsData.groups.forEach { group ->
                                Column {
                                    Text(
                                        text = if (isAr) group.titleAr else group.titleEn,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = CyanPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    group.items.forEach { item ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 3.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = if (isAr) item.labelAr else item.labelEn,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "${item.value} ${item.unit}",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.SemiBold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. CE Nameplate Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showCePlate = !showCePlate },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = CyanPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAr) "لوحة بيانات المصنع الرسمية (CE Plate)" else "Official CE Plate & Certification",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(
                            imageVector = if (showCePlate) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = "Toggle"
                        )
                    }

                    AnimatedVisibility(visible = showCePlate) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                                .border(1.dp, CyanPrimary.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "TAV VACUUM FURNACES S.p.A. - CE",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Via Dell'Industria 11, 24043 Caravaggio (BG), Italy",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))
                            CePlateRow("MODEL / INSIEME", FurnaceSpecsData.MODEL)
                            CePlateRow("SERIAL NUMBER", FurnaceSpecsData.SERIAL_NUMBER)
                            CePlateRow("YEAR OF BUILD", FurnaceSpecsData.YEAR)
                            CePlateRow("TYPE", "TAV H12")
                            CePlateRow("HEATING POWER", "480 kW")
                            CePlateRow("CONNECTED LOAD", "750 kVA")
                            CePlateRow("LINE VOLTAGE", "400 V / 50 Hz")
                            CePlateRow("MAX LOAD", "2000 kg")
                            CePlateRow("FLUID GROUPS", "Gr.2 (N2), Gr.2 (Ar), Gr.2 (H2O), Gr.2 (H2)")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SpecMiniCard(
    title: String,
    value: String,
    sub: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = sub,
                fontSize = 11.sp,
                color = tint,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun NavFeatureRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = tint.copy(alpha = 0.15f),
                shape = CircleShape,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun CePlateRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
