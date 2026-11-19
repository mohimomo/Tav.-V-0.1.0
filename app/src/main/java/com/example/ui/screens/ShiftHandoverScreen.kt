package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HandoverActionItem(
    val id: String,
    val textAr: String,
    val textEn: String,
    var isDone: Boolean = false
)

@Composable
fun ShiftHandoverScreen(
    uiState: AppUiState,
    viewModel: AppViewModel,
    contentPadding: PaddingValues
) {
    BackHandler {
        viewModel.navigateTo(NavigationScreen.HOME)
    }

    val context = LocalContext.current
    val isAr = uiState.language == AppLanguage.ARABIC

    var outgoingOperator by remember { mutableStateOf("Mohamed (Shift A)") }
    var incomingOperator by remember { mutableStateOf("Karim (Shift B)") }
    var handoverNotes by remember {
        mutableStateOf("الشحنة B-01 اكتملت بنجاح وحرارة الحجرة تراجعت إلى 180°م تحت غاز خامل. تم تنظيف حلقة إحكام الباب وزيت مضخة التفريغ طبيعي.")
    }
    var isHandoverSigned by remember { mutableStateOf(false) }

    val pendingActions = remember {
        mutableStateListOf(
            HandoverActionItem("1", "فحص زجاجة بيان الزيت في مضخة الروت بعد ساعتين", "Check oil sight glass in Roots pump at 14:00", true),
            HandoverActionItem("2", "التأكد من ضغط أسطوانات النيتروجين > 6.0 بار قبل بدء الشحنة التالية", "Verify N2 cylinder pressure > 6.0 bar before next quench cycle", true),
            HandoverActionItem("3", "تفريغ محبس مياه التكثيف في شيلر التبريد", "Drain condensate valve on cooling chiller line", false),
            HandoverActionItem("4", "تسليم مفتاح قفل أمان الباب الميكانيكي ACT1 للمشغل المستلم", "Handover mechanical safety key for door control ACT1 to Shift B", false)
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(16.dp)
            .testTag("shift_handover_screen"),
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
                                imageVector = Icons.Default.SwapHoriz,
                                contentDescription = null,
                                tint = CyanPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isAr) "بروتوكول تسليم واستلام الورديات (Shift Handover)" else "Digital Shift Handover Protocol",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Shift A ➔ Shift B Transfer • Action Items & Machine State",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isAr)
                            "توثيق رسمي يضمن نقل المسؤولية الهندسية بين المشغلين دون أي ثغرات أو نسيان، وتأكيد المهام العالقة وحالة الشحنات النشطة."
                        else
                            "Official digital protocol ensuring seamless transfer of operational responsibility between incoming and outgoing technicians.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Operators Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = outgoingOperator,
                            onValueChange = { outgoingOperator = it },
                            label = { Text(if (isAr) "المشغل المسلّم (الوردية المنتهية)" else "Outgoing Operator") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = incomingOperator,
                            onValueChange = { incomingOperator = it },
                            label = { Text(if (isAr) "المشغل المستلم (الوردية القادمة)" else "Incoming Operator") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    OutlinedTextField(
                        value = handoverNotes,
                        onValueChange = { handoverNotes = it },
                        label = { Text(if (isAr) "ملخص حالة الفرن وملاحظات التسليم" else "Furnace State & Handover Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            }
        }

        // Section: Pending Action Items Checklist
        item {
            Text(
                text = if (isAr) "المهام والتعليمات العالقة للوردية القادمة:" else "Pending Action Items for Incoming Shift:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        items(pendingActions.size) { idx ->
            val item = pendingActions[idx]
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, if (item.isDone) StatusGreen.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        pendingActions[idx] = item.copy(isDone = !item.isDone)
                    }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = item.isDone,
                        onCheckedChange = { isChecked ->
                            pendingActions[idx] = item.copy(isDone = isChecked)
                        },
                        colors = CheckboxDefaults.colors(checkedColor = StatusGreen, checkmarkColor = Color.Black)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isAr) item.textAr else item.textEn,
                        fontSize = 12.sp,
                        fontWeight = if (item.isDone) FontWeight.Bold else FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Digital Sign-Off Button
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, if (isHandoverSigned) StatusGreen else FurnaceAmber),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isAr) "حالة توقيع استلام الوردية:" else "Handover Sign-off Status:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isHandoverSigned) StatusGreen else FurnaceAmber,
                            contentColor = Color.Black
                        ) {
                            Text(
                                text = if (isHandoverSigned) "SIGNED & APPROVED (معتمد)" else "PENDING SIGNATURE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            isHandoverSigned = true
                            Toast.makeText(context, if (isAr) "تم اعتماد وتسجيل تسليم الوردية بنجاح" else "Handover signed and recorded", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isHandoverSigned) StatusGreen else CyanPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Icon(Icons.Default.AssignmentTurnedIn, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isAr) "اعتماد وتوقيع استلام الوردية رقمياً" else "Digitally Sign & Approve Shift Handover",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
