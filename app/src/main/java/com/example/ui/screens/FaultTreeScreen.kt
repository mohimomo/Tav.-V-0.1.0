package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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

data class FaultTreeNode(
    val id: String,
    val questionAr: String,
    val questionEn: String,
    val detailAr: String,
    val detailEn: String,
    val yesNodeId: String?,
    val noNodeId: String?,
    val rootCauseAr: String? = null,
    val rootCauseEn: String? = null,
    val actionAr: String? = null,
    val actionEn: String? = null,
    val schematicRef: String? = null
)

@Composable
fun FaultTreeScreen(
    uiState: AppUiState,
    viewModel: AppViewModel,
    contentPadding: PaddingValues
) {
    BackHandler {
        viewModel.navigateTo(NavigationScreen.HOME)
    }

    val isAr = uiState.language == AppLanguage.ARABIC

    val faultScenarios = remember {
        listOf(
            "VACUUM_STUCK" to if (isAr) "الفراغ لا ينخفض عن 1 mbar (Roughing Stuck)" else "Chamber Stuck at Rough Vacuum (1 mbar)",
            "HEATING_UNDER_TEMP" to if (isAr) "الحرارة لا تتجاوز 800°م (Under-Temp 480kW)" else "Chamber Cannot Reach > 800°C (480kW Power)",
            "COOLING_182BFL" to if (isAr) "إنذار تدفق مياه التبريد 182BFL مفتوح" else "Cooling Water Flow Alarm 182BFL Active",
            "DOOR_INTERLOCK" to if (isAr) "الباب لا يقفل بأزرار ACT1+ACT2" else "Autoclave Door Fails to Lock with ACT1+ACT2"
        )
    }

    var selectedScenarioIndex by remember { mutableIntStateOf(0) }
    var currentNodeId by remember(selectedScenarioIndex) {
        mutableStateOf(
            when (selectedScenarioIndex) {
                0 -> "V1"
                1 -> "H1"
                2 -> "C1"
                else -> "D1"
            }
        )
    }

    val nodesMap = remember {
        mapOf(
            // Vacuum Tree
            "V1" to FaultTreeNode(
                id = "V1",
                questionAr = "هل مضخة التفريغ Leybold SV630B تعمل وتصدر صوتاً طبيعياً؟",
                questionEn = "Is Leybold SV630B roughing pump running with normal acoustic pitch?",
                detailAr = "افحص قاطع المحرك في لوحة FuCa وزجاجة بيان الزيت",
                detailEn = "Check motor overload breaker in FuCa and oil sight glass window",
                yesNodeId = "V2",
                noNodeId = "V_PUMP_FAIL"
            ),
            "V2" to FaultTreeNode(
                id = "V2",
                questionAr = "هل صمام السحب الأولي LoVVa1 مفتوح ومؤشر الحساس الأخضر مضاء؟",
                questionEn = "Is roughing suction valve LoVVa1 open with limit switch confirmed on SCADA?",
                detailAr = "فحص ضغط الهواء المضغوط 6.0 بار المغذي للبوابة الهوائية",
                detailEn = "Check 6.0 bar pneumatic actuator line and magnetic sensors",
                yesNodeId = "V3",
                noNodeId = "V_VALVE_FAIL"
            ),
            "V3" to FaultTreeNode(
                id = "V3",
                questionAr = "هل تم تنظيف حلقة إحكام الباب Viton O-Ring وإضافة شحم السيليكون اليوم؟",
                questionEn = "Has main door Viton O-ring been cleaned and lubricated with high-vac grease today?",
                detailAr = "أي حبة رمل أو رايش معدني تمنع الوصول لفراغ أقل من 1 بار",
                detailEn = "Any fine metallic swarf or soot across the O-ring groove breaks vacuum sealing",
                yesNodeId = "V_ROOT_CAUSE_OUTGASSING",
                noNodeId = "V_ROOT_CAUSE_DOOR_LEAK"
            ),
            "V_PUMP_FAIL" to FaultTreeNode(
                id = "V_PUMP_FAIL",
                questionAr = "",
                questionEn = "",
                detailAr = "",
                detailEn = "",
                yesNodeId = null,
                noNodeId = null,
                rootCauseAr = "عطل في تغذية مضخة SV630B أو فصل القاطع الحراري QM-SV630",
                rootCauseEn = "SV630B roughing pump motor overload tripped or oil starvation lockout",
                actionAr = "افحص القاطع الحراري QM-SV630 في اللوحة FuCa ومستوى زيت LVO 100",
                actionEn = "Inspect QM-SV630 thermal relay in FuCa cabinet & check oil level in sight glass",
                schematicRef = "Schematic Page 14 (FuCa Cabinet)"
            ),
            "V_VALVE_FAIL" to FaultTreeNode(
                id = "V_VALVE_FAIL",
                questionAr = "",
                questionEn = "",
                detailAr = "",
                detailEn = "",
                yesNodeId = null,
                noNodeId = null,
                rootCauseAr = "انسداد أو انخفاض ضغط الهواء المضغوط للصمام LoVVa1 (< 5 bar)",
                rootCauseEn = "Pneumatic actuator air pressure low (< 5.0 bar) or solenoid 24V coil fault",
                actionAr = "افحص منظم الهواء الرئيسي واضبطه على 6.0 بار، وتأكد من ملف البلوف YV-LoVVa1",
                actionEn = "Adjust main pneumatic regulator to 6.0 bar & test YV-LoVVa1 24VDC solenoid",
                schematicRef = "Schematic Page 28 (Pneumatic Manifold)"
            ),
            "V_ROOT_CAUSE_DOOR_LEAK" to FaultTreeNode(
                id = "V_ROOT_CAUSE_DOOR_LEAK",
                questionAr = "",
                questionEn = "",
                detailAr = "",
                detailEn = "",
                yesNodeId = null,
                noNodeId = null,
                rootCauseAr = "تسريب ميكانيكي عبر حلقة إحكام الباب الرئيسية (Viton O-Ring)",
                rootCauseEn = "Vacuum seal leak at autoclave door Viton O-ring gasket",
                actionAr = "نظف مجرى الحاشية بقطعة قماش خالية من الوبر، أعد وضع شحم سيليكون داوكورنينغ 976V",
                actionEn = "Clean Viton gasket with alcohol & lint-free cloth, apply Dow Corning 976V grease",
                schematicRef = "Mechanical Manual Section 4.2 (Door Flange)"
            ),
            "V_ROOT_CAUSE_OUTGASSING" to FaultTreeNode(
                id = "V_ROOT_CAUSE_OUTGASSING",
                questionAr = "",
                questionEn = "",
                detailAr = "",
                detailEn = "",
                yesNodeId = null,
                noNodeId = null,
                rootCauseAr = "تطاير رطوبة وزيوت من الشحنة (Load Outgassing) تعيق الفراغ الأولي",
                rootCauseEn = "Severe workpiece outgassing (moisture/cutting oil remnants on parts)",
                actionAr = "شغل التطهير بالنيتروجين الخامل (N2 Purge) أو جفف الشحنة قبل إغلاق الفرن",
                actionEn = "Initiate N2 gas purging cycle to flush moisture; ensure dry parts before loading",
                schematicRef = "Operation Guide Section 3.1"
            ),

            // Heating Tree
            "H1" to FaultTreeNode(
                id = "H1",
                questionAr = "هل يظهر إنذار زيادة تيار أو قوس كهربي (Arcing Alarm) على شاشة الثايرستور؟",
                questionEn = "Is thyristor power controller indicating over-current or graphite arcing alarm?",
                detailAr = "فحص مؤشرات Eurotherm EPack / SCR في كابينة MiCa",
                detailEn = "Check Eurotherm EPack SCR alarm LEDs in main electrical cabinet MiCa",
                yesNodeId = "H_ARC_FAIL",
                noNodeId = "H2"
            ),
            "H2" to FaultTreeNode(
                id = "H2",
                questionAr = "هل مستوى الفراغ داخل الحجرة أفضل من 10⁻² mbar؟",
                questionEn = "Is chamber vacuum better than 10⁻² mbar?",
                detailAr = "حماية PLC تمنع تشغيل قدرة 480kW في حال كان الفراغ رديئاً لمنع تفريغ الغاز القوسي",
                detailEn = "PLC interlock blocks high power heating if vacuum is too poor (Glow discharge prevention)",
                yesNodeId = "H_THERMOCOUPLE_FAIL",
                noNodeId = "H_VACUUM_INTERLOCK"
            ),
            "H_ARC_FAIL" to FaultTreeNode(
                id = "H_ARC_FAIL",
                questionAr = "",
                questionEn = "",
                detailAr = "",
                detailEn = "",
                yesNodeId = null,
                noNodeId = null,
                rootCauseAr = "ترسبات كربونية على العوازل الخزفية أو ملامسة عنصر جرافيتي لهيكل الفرن",
                rootCauseEn = "Carbon soot buildup on ceramic feedthroughs or graphite cage mechanical short",
                actionAr = "افصل الطاقة وافحص العوازل ونظف الترسبات الكحولية وافحص عزم براغي M14/M16",
                actionEn = "Perform LOTO, clean ceramic insulators with isopropanol, verify M14/M16 torque",
                schematicRef = "Schematic Page 08 (Heating Elements & Busbars)"
            ),
            "H_VACUUM_INTERLOCK" to FaultTreeNode(
                id = "H_VACUUM_INTERLOCK",
                questionAr = "",
                questionEn = "",
                detailAr = "",
                detailEn = "",
                yesNodeId = null,
                noNodeId = null,
                rootCauseAr = "قفل أمان الفراغ نشط: لا يمكن التسخين فوق 800°م بفراغ أسوأ من 10⁻² mbar",
                rootCauseEn = "PLC vacuum safety interlock active: heating inhibited to prevent glow discharge",
                actionAr = "انتظر وصول الفراغ الفائق بمضخة الانتشار أو افحص أختام الفراغ",
                actionEn = "Wait for diffusion pump to reach high vacuum (< 10⁻² mbar) before auto heating",
                schematicRef = "PLC Logic Interlock Block DB101"
            ),
            "H_THERMOCOUPLE_FAIL" to FaultTreeNode(
                id = "H_THERMOCOUPLE_FAIL",
                questionAr = "",
                questionEn = "",
                detailAr = "",
                detailEn = "",
                yesNodeId = null,
                noNodeId = null,
                rootCauseAr = "انقطاع أو تلف مجس الحرارة الرئيسي نوع S (Type S Thermocouple Break)",
                rootCauseEn = "Type S control thermocouple open-circuit or broken ceramic sheath",
                actionAr = "افحص قراءة القناة في كرت الإدخال الحراري Siemens SM 531 واستبدل المجس",
                actionEn = "Check Siemens SM 531 TC module diagnostics & replace Type S thermocouple",
                schematicRef = "Schematic Page 22 (Temperature Sensors)"
            )
        )
    }

    val currentNode = nodesMap[currentNodeId]

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp)
            .testTag("fault_tree_screen"),
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
                                .background(FurnaceAmber.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountTree,
                                contentDescription = null,
                                tint = FurnaceAmber,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isAr) "استكشاف الأعطال الشجري التفاعلي (Fault-Tree)" else "Guided Fault-Tree Diagnostic Wizard",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Step-by-step root cause analysis in under 60 seconds",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isAr) "اختر العَرَض أو المشكلة التشغيلية:" else "Select Observed Symptom to Diagnose:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        faultScenarios.forEachIndexed { index, (key, label) ->
                            val isSel = selectedScenarioIndex == index
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) CyanPrimary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, if (isSel) CyanPrimary else MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedScenarioIndex = index
                                        currentNodeId = when (index) {
                                            0 -> "V1"
                                            1 -> "H1"
                                            2 -> "C1"
                                            else -> "D1"
                                        }
                                    }
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) CyanPrimary else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Active Question / Solution Step
        if (currentNode != null) {
            val isFinalSolution = currentNode.rootCauseAr != null

            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.5.dp, if (isFinalSolution) StatusGreen else FurnaceAmber),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        if (!isFinalSolution) {
                            // Question Step
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.HelpOutline, contentDescription = null, tint = FurnaceAmber)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAr) "سؤال الفحص الميداني:" else "Diagnostic Verification Question:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = FurnaceAmber
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = if (isAr) currentNode.questionAr else currentNode.questionEn,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                lineHeight = 21.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (isAr) currentNode.detailAr else currentNode.detailEn,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = {
                                        if (currentNode.yesNodeId != null) currentNodeId = currentNode.yesNodeId
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusGreen),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(46.dp)
                                ) {
                                    Text(if (isAr) "نعم (YES)" else "YES", color = Color.Black, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        if (currentNode.noNodeId != null) currentNodeId = currentNode.noNodeId
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f).height(46.dp)
                                ) {
                                    Text(if (isAr) "لا (NO)" else "NO", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            // Final Root Cause & Solution
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusGreen)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAr) "السبب الجذري المحدد وإجراء الإصلاح:" else "Identified Root Cause & Corrective Action:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = StatusGreen
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = if (isAr) "السبب الجذري (Root Cause):" else "Root Cause:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AlertRed
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isAr) currentNode.rootCauseAr.orEmpty() else currentNode.rootCauseEn.orEmpty(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, StatusGreen.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = if (isAr) "إجراء الإصلاح الفني (Corrective Action):" else "Corrective Action:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusGreen
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isAr) currentNode.actionAr.orEmpty() else currentNode.actionEn.orEmpty(),
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (currentNode.schematicRef != null) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Ref: ${currentNode.schematicRef}",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CyanPrimary
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedButton(
                                onClick = {
                                    currentNodeId = when (selectedScenarioIndex) {
                                        0 -> "V1"
                                        1 -> "H1"
                                        2 -> "C1"
                                        else -> "D1"
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isAr) "إعادة التشخيص من البداية" else "Restart Diagnostic Tree")
                            }
                        }
                    }
                }
            }
        }
    }
}
