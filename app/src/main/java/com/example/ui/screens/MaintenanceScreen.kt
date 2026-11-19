package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FurnaceRepository
import com.example.model.MaintenanceTask
import com.example.ui.AppLanguage
import com.example.ui.AppUiState
import com.example.ui.AppViewModel
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.FurnaceAmber
import com.example.ui.theme.StatusGreen

@Composable
fun MaintenanceScreen(
    uiState: AppUiState,
    viewModel: AppViewModel,
    contentPadding: PaddingValues
) {
    val isAr = uiState.language == AppLanguage.ARABIC
    val allTasks = FurnaceRepository.maintenanceTasks
    var selectedFrequency by remember { mutableStateOf("ALL") }

    val frequencies = listOf(
        "ALL" to (if (isAr) "الكل" else "All"),
        "Daily" to (if (isAr) "يومياً" else "Daily"),
        "Weekly" to (if (isAr) "أسبوعياً" else "Weekly"),
        "Monthly" to (if (isAr) "شهرياً" else "Monthly"),
        "Six-Monthly" to (if (isAr) "نصف سنوي" else "6-Monthly"),
        "Yearly" to (if (isAr) "سنوياً" else "Yearly"),
        "Triennial" to (if (isAr) "كل 3 سنوات" else "Triennial")
    )

    val displayedTasks = remember(selectedFrequency) {
        if (selectedFrequency == "ALL") allTasks else {
            allTasks.filter { it.frequencyEn.startsWith(selectedFrequency, ignoreCase = true) }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isAr) "جدول الصيانة الدورية وعزوم الربط المقننة" else "Periodic Maintenance & Tightening Torques",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isAr) "التحقق الدوري، الصيانة الوقائية، وتأكيد عزم مسامير عناصر التسخين" else "Routine inspections, preventive schedule & heating elements torque table",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Safety Warning Banner
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AlertRed.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AlertRed.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lockout",
                        tint = AlertRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isAr) "قاعدة السلامة الصارمة قبل الصيانة (LOTO)" else "Strict Safety Rule (Lockout/Tagout)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = AlertRed
                        )
                        Text(
                            text = if (isAr) "يجب فصل وقفل القاطع الرئيسي (Main Isolator Padlock)، وإفراغ خزان النيوماتيك 50 لتر وإغلاق محابس الغاز قبل فك أي جزء!" else "Main isolator MUST be switched off and padlocked, 50L air tank discharged, and gas valves closed before servicing.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Heating Element Torque Table Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            tint = FurnaceAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "جدول عزوم ربط مسامير الكربون (CFC Tightening Torques)" else "CFC Heating Screws Tightening Torques",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = FurnaceAmber
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isAr) "يتم فحص وتأكيد الربط بمفتاح العزم المقنن (Dynamometric Wrench) أسبوعياً:" else "Check weekly with a calibrated dynamometric wrench:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TorqueBadge(label = "M14 CFC", torque = "2.5 Nm", modifier = Modifier.weight(1f))
                        TorqueBadge(label = "M16 CFC", torque = "3.5 Nm", modifier = Modifier.weight(1f))
                        TorqueBadge(label = "M24 CFC", torque = "8.0 Nm", modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Frequency Selector Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = frequencies.indexOfFirst { it.first == selectedFrequency }.coerceAtLeast(0),
                containerColor = MaterialTheme.colorScheme.surface,
                edgePadding = 0.dp
            ) {
                frequencies.forEach { (key, label) ->
                    Tab(
                        selected = selectedFrequency == key,
                        onClick = { selectedFrequency = key },
                        text = {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = if (selectedFrequency == key) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }

        // Tasks Checklist
        items(displayedTasks) { task ->
            val isCompleted = uiState.completedMaintenanceTasks.contains(task.id)

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.toggleMaintenanceTask(task.id) }
                    .testTag("maintenance_task_${task.id}")
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = "Status",
                                tint = if (isCompleted) StatusGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isAr) task.operationAr else task.operationEn,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = CyanPrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (isAr) task.frequencyAr else task.frequencyEn,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isAr) task.remarksAr else task.remarksEn,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${if (isAr) "العدة:" else "Tools:"} ${if (isAr) task.requiredToolsAr else task.requiredToolsEn}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = task.responsibility,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FurnaceAmber
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun TorqueBadge(label: String, torque: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = CyanPrimary
            )
            Text(
                text = torque,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = FurnaceAmber
            )
        }
    }
}
