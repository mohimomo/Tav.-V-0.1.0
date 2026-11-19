package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.AppLanguage
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.FurnaceAmber
import com.example.ui.theme.StatusGreen

@Composable
fun AboutAppDialog(
    language: AppLanguage,
    onDismissRequest: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .border(1.dp, CyanPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .testTag("about_app_dialog"),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(CyanPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "About App",
                                tint = CyanPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isAr) "حول التطبيق والمعدات" else "About App & Equipment",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isAr) "دليل فرن التفريغ TAV TPH 120" else "TAV Vacuum Furnace Guide",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.testTag("close_about_dialog_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: General App Information
                InfoSectionCard(
                    title = if (isAr) "١. معلومات عامة حول التطبيق" else "1. General Application Information",
                    icon = Icons.Default.Info,
                    accentColor = CyanPrimary
                ) {
                    Text(
                        text = if (isAr)
                            "تطبيق هندسي وفني متقدم مخصص لمشغلي ومهندسي صيانة أفران المعالجة الحرارية الفراغية الصناعية من طراز TAV TPH 120/120/230 (الرقم التسلسلي 911 - سنة الصنع 2023)."
                        else
                            "Advanced technical and operational guide designed for operators and maintenance engineers of industrial vacuum heat-treating furnace model TAV TPH 120/120/230 (Serial No. 911 - Year 2023).",
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    KeyDetailRow(label = if (isAr) "الإصدار:" else "Version:", value = "v1.2.0 (Offline Edition)")
                    KeyDetailRow(label = if (isAr) "قاعدة البيانات:" else "Local Storage:", value = "Room SQLite Database (Full Offline)")
                    KeyDetailRow(label = if (isAr) "محرك البحث الذكي:" else "Smart Engine:", value = "Multi-Token (Tags, Breakers, P&ID, Arabic/En)")
                    KeyDetailRow(label = if (isAr) "التوافق:" else "Platforms:", value = "Android (Material 3) & iOS Compatible Architecture")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Section 2: Manufacturer Contact (الشركة المصنعة)
                InfoSectionCard(
                    title = if (isAr) "٢. الشركة المصنعة للمعدات" else "2. Furnace Manufacturer",
                    icon = Icons.Default.Info,
                    accentColor = FurnaceAmber
                ) {
                    Text(
                        text = "TAV VACUUM FURNACES S.p.A.",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = FurnaceAmber
                    )
                    Text(
                        text = "Via Dell'Industria, 11 - 24043 Caravaggio (BG), Italy",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    KeyDetailRow(
                        label = if (isAr) "شهادة المطابقة:" else "Certification:",
                        value = "CE Mark (Directive 2006/42/CE Annex I)"
                    )
                    KeyDetailRow(
                        label = if (isAr) "نوع الفرن باللوحة:" else "Furnace Type:",
                        value = "TAV H12 (TPH 120/120/230)"
                    )
                    KeyDetailRow(
                        label = if (isAr) "الهاتف:" else "Phone:",
                        value = "+39 0363 355711",
                        isActionable = true,
                        actionIcon = Icons.Default.Phone,
                        onAction = {
                            safeLaunchIntent(context, Intent(Intent.ACTION_DIAL, Uri.parse("tel:+390363355711")))
                        }
                    )
                    KeyDetailRow(
                        label = if (isAr) "البريد الإلكتروني:" else "Email:",
                        value = "service@tav-vacuumfurnaces.com",
                        isActionable = true,
                        actionIcon = Icons.Default.Email,
                        onAction = {
                            safeLaunchIntent(context, Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:service@tav-vacuumfurnaces.com")))
                        }
                    )
                    KeyDetailRow(
                        label = if (isAr) "الموقع الإلكتروني:" else "Website:",
                        value = "www.tav-vacuumfurnaces.com",
                        isActionable = true,
                        actionIcon = Icons.Default.Share,
                        onAction = {
                            safeLaunchIntent(context, Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tav-vacuumfurnaces.com")))
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Section 3: Consumer Company (الشركة المستهلكة / العميل)
                InfoSectionCard(
                    title = if (isAr) "٣. الشركة المستهلكة (العميل المشغل)" else "3. Consumer Company (Operating Client)",
                    icon = Icons.Default.Person,
                    accentColor = StatusGreen
                ) {
                    Text(
                        text = "GEAT",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = StatusGreen
                    )
                    Text(
                        text = if (isAr)
                            "العميل المعتمد لفرن التفريغ الصناعي TAV بموجب وثائق التوريد الرسمية ولوحة المطابقة (CE Plate S/N 911)."
                        else
                            "Official client and operating company for the TAV vacuum furnace as designated on the CE Plate (S/N 911, 2023).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    KeyDetailRow(label = if (isAr) "رقم الوحدة المشغلة:" else "Operating Unit:", value = "Unit TPH 120 #911 (480 kW)")
                    KeyDetailRow(label = if (isAr) "طبيعة التشغيل:" else "Operation Field:", value = if (isAr) "المعالجة الحرارية الفراغية والتبريد بالغاز" else "Vacuum Heat Treatment & Gas Quenching (6 bar Ar)")
                    KeyDetailRow(label = if (isAr) "سنة التدشين:" else "Commissioning Year:", value = "2023")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Section 4: Developer Information & Contact (معلومات المطور)
                InfoSectionCard(
                    title = if (isAr) "٤. معلومات المطور والاتصال" else "4. Developer Information & Contact",
                    icon = Icons.Default.Person,
                    accentColor = CyanPrimary
                ) {
                    Text(
                        text = "Mohamed Bouzidi",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = CyanPrimary
                    )
                    Text(
                        text = if (isAr) "مهندس برمجيات وتطبيقات صناعية وهندسية" else "Industrial Software & Engineering Solutions Developer",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    KeyDetailRow(
                        label = if (isAr) "البريد الإلكتروني:" else "Developer Email:",
                        value = "mohamedbouzidi333@gmail.com",
                        isActionable = true,
                        actionIcon = Icons.Default.Email,
                        onAction = {
                            safeLaunchIntent(context, Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:mohamedbouzidi333@gmail.com")))
                        }
                    )
                    KeyDetailRow(
                        label = if (isAr) "التقنيات المستخدمة:" else "Tech Stack:",
                        value = "Kotlin, Jetpack Compose, Room SQLite, Coroutines, Flow"
                    )
                    KeyDetailRow(
                        label = if (isAr) "البيئة:" else "Platform:",
                        value = "Google AI Studio Cloud Architecture"
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString("mohamedbouzidi333@gmail.com"))
                            Toast.makeText(context, if (isAr) "تم نسخ بريد المطور" else "Developer email copied", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isAr) "نسخ بريد المطور للتواصل" else "Copy Developer Email",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismissRequest,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isAr) "إغلاق" else "Close",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoSectionCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun KeyDetailRow(
    label: String,
    value: String,
    isActionable: Boolean = false,
    actionIcon: ImageVector? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = if (isActionable && onAction != null) {
                Modifier.clickable { onAction() }
            } else {
                Modifier
            }
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (isActionable) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isActionable) CyanPrimary else MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.SansSerif
            )
            if (isActionable && actionIcon != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = actionIcon,
                    contentDescription = null,
                    tint = CyanPrimary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

private fun safeLaunchIntent(context: Context, intent: Intent) {
    try {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No app available to handle this request", Toast.LENGTH_SHORT).show()
    }
}
