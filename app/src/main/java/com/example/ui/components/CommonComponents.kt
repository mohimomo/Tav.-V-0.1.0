package com.example.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import coil.compose.AsyncImage
import com.example.data.local.ComponentPhotoEntity
import java.io.File
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.model.ComponentSchematicDetail
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AlarmItem
import com.example.model.AlarmSeverity
import com.example.model.ComponentItem
import com.example.ui.AppLanguage
import com.example.ui.AppStrings
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertYellow
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.FurnaceAmber
import com.example.ui.theme.StatusGreen

@Composable
fun SeverityBadge(severity: AlarmSeverity, language: AppLanguage) {
    val bgColor: Color
    val textColor: Color
    val label = AppStrings.severityLabel(severity, language)
    when (severity) {
        AlarmSeverity.CRITICAL -> {
            bgColor = AlertRed.copy(alpha = 0.2f)
            textColor = AlertRed
        }
        AlarmSeverity.HIGH -> {
            bgColor = FurnaceAmber.copy(alpha = 0.2f)
            textColor = FurnaceAmber
        }
        AlarmSeverity.WARNING -> {
            bgColor = AlertYellow.copy(alpha = 0.2f)
            textColor = AlertYellow
        }
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AlarmDetailDialog(
    alarm: AlarmItem,
    language: AppLanguage,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onDismiss: () -> Unit,
    onAskAi: (() -> Unit)? = null
) {
    val isAr = language == AppLanguage.ARABIC

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isAr) alarm.nameAr else alarm.nameEn,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isAr) alarm.nameEn else alarm.nameAr,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.testTag("bookmark_alarm_btn")
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) FurnaceAmber else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SeverityBadge(severity = alarm.severity, language = language)
                    if (alarm.pageRef.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = alarm.pageRef,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                DetailSection(
                    title = AppStrings.alarmDescription(language),
                    icon = Icons.Default.Info,
                    content = if (isAr) alarm.descriptionAr else alarm.descriptionEn,
                    subContent = if (isAr) alarm.descriptionEn else alarm.descriptionAr
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Furnace Reaction
                DetailSection(
                    title = AppStrings.furnaceReaction(language),
                    icon = Icons.Default.Warning,
                    tint = AlertRed,
                    content = if (isAr) alarm.reactionAr else alarm.reactionEn,
                    subContent = if (isAr) alarm.reactionEn else alarm.reactionAr
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Causes and Solutions
                DetailSection(
                    title = AppStrings.causesAndRemedies(language),
                    icon = Icons.Default.Build,
                    tint = StatusGreen,
                    content = if (isAr) alarm.causesAndSolutionsAr else alarm.causesAndSolutionsEn,
                    subContent = if (isAr) alarm.causesAndSolutionsEn else alarm.causesAndSolutionsAr
                )

                Spacer(modifier = Modifier.height(10.dp))

                // References & Hardware Tags
                if (alarm.references.isNotEmpty()) {
                    Text(
                        text = AppStrings.schematicsAndBreakers(language),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        alarm.references.forEach { ref ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = CyanPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = ref,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag("close_alarm_dialog_btn")
            ) {
                Text(text = AppStrings.close(language))
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onAskAi?.invoke()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                modifier = Modifier.testTag("ask_ai_alarm_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = AppStrings.askAiAssistant(language),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    )
}

@Composable
fun ComponentDetailDialog(
    component: ComponentItem,
    customPhoto: ComponentPhotoEntity? = null,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSavePhoto: (Uri, String) -> Unit = { _, _ -> },
    onDeletePhoto: () -> Unit = {},
    onNavigateToCircuit: ((com.example.model.CircuitDomain, String?) -> Unit)? = null
) {
    val isAr = language == AppLanguage.ARABIC
    val schematic = remember(component.id) {
        com.example.data.ComponentSchematicsData.getSchematicForComponent(component.id)
    }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onSavePhoto(uri, "")
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isAr) component.nameAr else component.nameEn,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${component.pidCode} • Tag: ${component.tag}",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Component Image Display Banner (Real Photo or Technical Vector Graphic)
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (customPhoto != null && File(customPhoto.localFilePath).exists()) {
                            // Display the user-added real photo from local storage
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = File(customPhoto.localFilePath),
                                    contentDescription = component.nameEn,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF00E676).copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color(0xFF00E676),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isAr) "صورة حقيقية محفوظة محلياً على الجهاز" else "Real Photo Saved Locally on Device",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF00E676)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(if (isAr) "تغيير الصورة" else "Change Photo", fontSize = 11.sp)
                                }
                                TextButton(
                                    onClick = onDeletePhoto,
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text(if (isAr) "حذف" else "Delete", fontSize = 11.sp)
                                }
                            }
                        } else {
                            // Display technical schematic artwork
                            if (component.imageResId != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(150.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = component.imageResId),
                                        contentDescription = component.nameEn,
                                        modifier = Modifier.size(130.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = if (isAr)
                                    "المخطط الفني متوفر. يمكنك إضافة الصورة الحقيقية للقطعة من المعرض أو الكاميرا وتخزينها محلياً."
                                else
                                    "Technical schematic displayed. You can add the real component photo from device and store it locally.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Button(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isAr) "📷 إضافة الصورة الحقيقية وحفظها محلياً" else "📷 Add Real Photo & Save Locally",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                // Physical Location in Furnace (مكان تواجد القطعة)
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = FurnaceAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "مكان التواجد في الفرن والموقع الفيزيائي" else "Physical Location & Mount in Furnace",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FurnaceAmber
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isAr) component.locationAr.ifBlank { component.electricalCabinet } else component.locationEn.ifBlank { component.electricalCabinet },
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Component Role & Function (دور القطعة في دورة الفرن)
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = CyanPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "الدور والوظيفة الأساسية في دورة الفرن" else "Primary Role & Cycle Function",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isAr) component.roleAr.ifBlank { component.descriptionAr } else component.roleEn.ifBlank { component.descriptionEn },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Manufacturer & Model
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (isAr) "الشركة الصانعة والطراز" else "Manufacturer & Model",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = component.manufacturerModel,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Specifications
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (isAr) "المواصفات الفنية والكهربائية" else "Technical & Electrical Specs",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanPrimary
                        )
                        Text(
                            text = component.specifications,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Schematics & Location
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (isAr) "الموقع ولوحة التحكم ومخطط الدائرة" else "Cabinet & Schematic Wiring Location",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = FurnaceAmber
                        )
                        Text(
                            text = "${component.electricalCabinet}\n${component.schematicsPage}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    }
                }

                // 📐 Certified Engineering Schematics & Blueprint Section
                if (schematic != null) {
                    ComponentSchematicSection(
                        schematic = schematic,
                        isArabic = isAr,
                        onNavigateToCircuit = onNavigateToCircuit
                    )
                }

                // Description
                DetailSection(
                    title = if (isAr) "وظيفة القطعة في دورة الفرن" else "Component Purpose & Operation",
                    icon = Icons.Default.Info,
                    content = if (isAr) component.descriptionAr else component.descriptionEn,
                    subContent = if (isAr) component.descriptionEn else component.descriptionAr
                )

                // Related Alarms
                if (component.relatedAlarms.isNotEmpty()) {
                    Column {
                        Text(
                            text = if (isAr) "الإنذارات المرتبطة بهذه القطعة:" else "Related Alarms for this component:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = AlertRed
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        component.relatedAlarms.forEach { alarmTitle ->
                            Text(
                                text = "• $alarmTitle",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag("close_comp_dialog_btn")
            ) {
                Text(text = if (isAr) "تم" else "OK")
            }
        }
    )
}

@Composable
fun DetailSection(
    title: String,
    icon: ImageVector,
    content: String,
    subContent: String = "",
    tint: Color = MaterialTheme.colorScheme.primary
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = tint
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = tint
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subContent.isNotEmpty() && subContent != content) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subContent,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
