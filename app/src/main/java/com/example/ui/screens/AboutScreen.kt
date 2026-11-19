package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.AppLanguage
import com.example.ui.AppUiState
import com.example.ui.AppViewModel
import com.example.ui.NavigationScreen
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.FurnaceAmber
import com.example.ui.theme.StatusGreen

@Composable
fun AboutScreen(
    uiState: AppUiState,
    viewModel: AppViewModel,
    contentPadding: PaddingValues
) {
    BackHandler {
        viewModel.navigateTo(NavigationScreen.HOME)
    }

    val isAr = uiState.language == AppLanguage.ARABIC
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 16.dp)
            .testTag("about_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. App Hero Card
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(CyanPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "App Icon",
                            tint = CyanPrimary,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isAr) "دليل فرن التفريغ TAV TPH 120" else "TAV Vacuum Furnace Guide",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = "Model: TPH 120/120/230 • S/N: 911 • Year: 2023",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = FurnaceAmber,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isAr)
                            "تطبيق هندسي وفني متقدم يوفر دليلاً شاملاً لكافة مفاصل الفرن الصناعي، محرك بحث ذكي للأعطال والقطع، وتخزين محلي لكافة نصوص الدليل عبر Room SQLite للعمل دون اتصال بالإنترنت."
                        else
                            "Advanced technical and engineering guide for industrial vacuum furnaces, featuring smart error/part search, and full offline manual access via local Room SQLite database.",
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = StatusGreen.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "v1.2.0 Offline",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusGreen,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CyanPrimary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "Android & iOS Ready",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Manufacturer Info (الشركة المصنعة)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(FurnaceAmber.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = FurnaceAmber,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isAr) "معلومات الشركة المصنعة (Manufacturer)" else "Furnace Manufacturer Information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = FurnaceAmber
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "TAV VACUUM FURNACES S.p.A.",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Via Dell'Industria, 11 - 24043 Caravaggio (BG), Italy",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AboutDetailRow(
                        label = if (isAr) "التوجيه الأوروبي والمعايير:" else "Directive & Standards:",
                        value = "2006/42/CE • EN 60204-1 • EN 13849-1"
                    )
                    AboutDetailRow(
                        label = if (isAr) "نوع الطراز المعتمد:" else "CE Plate Type:",
                        value = "TAV H12 (TPH 120/120/230)"
                    )
                    AboutDetailRow(
                        label = if (isAr) "هاتف الدعم الفني والمصنع:" else "Factory & Technical Phone:",
                        value = "+39 0363 355711",
                        isActionable = true,
                        actionIcon = Icons.Default.Phone,
                        onAction = {
                            safeLaunchIntent(context, Intent(Intent.ACTION_DIAL, Uri.parse("tel:+390363355711")))
                        }
                    )
                    AboutDetailRow(
                        label = if (isAr) "بريد خدمة العملاء:" else "Customer Service Email:",
                        value = "service@tav-vacuumfurnaces.com",
                        isActionable = true,
                        actionIcon = Icons.Default.Email,
                        onAction = {
                            safeLaunchIntent(context, Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:service@tav-vacuumfurnaces.com")))
                        }
                    )
                    AboutDetailRow(
                        label = if (isAr) "الموقع الإلكتروني الرسمي:" else "Official Website:",
                        value = "www.tav-vacuumfurnaces.com",
                        isActionable = true,
                        actionIcon = Icons.Default.Share,
                        onAction = {
                            safeLaunchIntent(context, Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tav-vacuumfurnaces.com")))
                        }
                    )
                }
            }
        }

        // 3. Consumer / Client Company (الشركة المستهلكة)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(StatusGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = StatusGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isAr) "الشركة المستهلكة والمشغلة (Client / Consumer)" else "Consumer Company (Operating Client)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = StatusGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "GEAT",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isAr)
                            "العميل والجهة المستهلكة المسجلة رسمياً على لوحة المواصفات الأوروبية (CE Plate) ووثائق الفرن الفنية."
                        else
                            "Registered client and operating consumer designated on the official CE Nameplate and furnace technical documentation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AboutDetailRow(
                        label = if (isAr) "رقم تعريف الوحدة:" else "Operating Equipment:",
                        value = "TAV Vacuum Furnace TPH 120 (S/N 911)"
                    )
                    AboutDetailRow(
                        label = if (isAr) "تطبيقات التشغيل:" else "Operational Application:",
                        value = if (isAr) "معالجة حرارية فراغية دقيقة وتبريد فجائي بالآرجون" else "Precision vacuum thermal treatment & Argon quenching (6 bar)"
                    )
                    AboutDetailRow(
                        label = if (isAr) "قدرة الفرن الإجمالية:" else "Connected Power Rating:",
                        value = "480 kW Heating • 750 kVA Load"
                    )
                }
            }
        }

        // 4. Developer Info & Contact (معلومات المطور)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyanPrimary.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(CyanPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = CyanPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isAr) "معلومات المطور والاتصال (Developer)" else "Developer Information & Contact",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CyanPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Mohamed Bouzidi",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isAr) "مهندس برمجيات وتطبيقات صناعية وهندسية" else "Industrial Software & Engineering Solutions Developer",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AboutDetailRow(
                        label = if (isAr) "البريد الإلكتروني للمطور:" else "Developer Email:",
                        value = "mohamedbouzidi333@gmail.com",
                        isActionable = true,
                        actionIcon = Icons.Default.Email,
                        onAction = {
                            safeLaunchIntent(context, Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:mohamedbouzidi333@gmail.com")))
                        }
                    )
                    AboutDetailRow(
                        label = if (isAr) "بيئة التطوير والمنصة:" else "Development Environment:",
                        value = "Google AI Studio Android Platform"
                    )
                    AboutDetailRow(
                        label = if (isAr) "الحزمة البرمجية:" else "Architecture Stack:",
                        value = "Kotlin • Jetpack Compose M3 • Room SQLite • Coroutines"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString("mohamedbouzidi333@gmail.com"))
                                Toast.makeText(context, if (isAr) "تم نسخ بريد المطور" else "Developer email copied", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (isAr) "نسخ البريد" else "Copy Email",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Button(
                            onClick = {
                                safeLaunchIntent(context, Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:mohamedbouzidi333@gmail.com")))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAr) "إرسال رسالة" else "Send Email",
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AboutDetailRow(
    label: String,
    value: String,
    isActionable: Boolean = false,
    actionIcon: ImageVector? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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
                color = if (isActionable) CyanPrimary else MaterialTheme.colorScheme.onSurface
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
        Toast.makeText(context, "No application found to handle this action", Toast.LENGTH_SHORT).show()
    }
}
