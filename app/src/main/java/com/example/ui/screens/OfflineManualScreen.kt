package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.local.ManualDocumentEntity
import com.example.ui.AppLanguage
import com.example.ui.AppUiState
import com.example.ui.AppViewModel
import com.example.ui.NavigationScreen
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.FurnaceAmber
import com.example.ui.theme.StatusGreen

@Composable
fun OfflineManualScreen(
    uiState: AppUiState,
    viewModel: AppViewModel,
    contentPadding: PaddingValues
) {
    BackHandler {
        viewModel.navigateTo(NavigationScreen.HOME)
    }

    val isAr = uiState.language == AppLanguage.ARABIC
    val documents by viewModel.manualDocuments.collectAsState()

    val categories = listOf(
        "ALL" to if (isAr) "الكل" else "All",
        "GENERAL" to if (isAr) "عام" else "General",
        "SPECIFICATIONS" to if (isAr) "المواصفات" else "Specs",
        "SAFETY" to if (isAr) "الأمان والسلامة" else "Safety",
        "DESCRIPTION" to if (isAr) "وصف الأجزاء" else "Description",
        "SOFTWARE" to if (isAr) "برمجيات WinCC" else "Software",
        "OPERATION" to if (isAr) "التشغيل" else "Operation",
        "MAINTENANCE" to if (isAr) "الصيانة" else "Maintenance",
        "CE_PLATE" to if (isAr) "لوحة CE" else "CE Plate"
    )

    val filteredDocuments = documents.filter { doc ->
        val matchesCategory = uiState.selectedManualCategory == "ALL" || doc.category.equals(uiState.selectedManualCategory, ignoreCase = true)
        val matchesChapter = uiState.selectedManualChapter == "ALL" || doc.chapterNumber == uiState.selectedManualChapter
        matchesCategory && matchesChapter
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 16.dp)
            .testTag("offline_manual_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Header Banner
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(CyanPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Manual Room DB",
                            tint = CyanPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isAr) "دليل الفرن المخزن محلياً (Room DB)" else "Offline Manual Database (Room)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = StatusGreen.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (isAr) "بدون إنترنت" else "Offline Ready",
                                    fontSize = 10.sp,
                                    color = StatusGreen,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isAr)
                                "نصوص الدليل الرسمي لفرن TPH 120/120/230 مع أرقام الصفحات وملاحظات الفنيين"
                            else
                                "Official manual text for TPH 120/120/230 with PDF page indexing & technician notes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 2. Search Field
        item {
            OutlinedTextField(
                value = uiState.manualSearchQuery,
                onValueChange = { viewModel.onManualSearchQueryChanged(it) },
                placeholder = {
                    Text(
                        if (isAr) "ابحث في نصوص الدليل، أرقام الصفحات، العوازل، أو الملاحظات..."
                        else "Search offline manual text, pages, torques, notes..."
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = CyanPrimary
                    )
                },
                trailingIcon = {
                    if (uiState.manualSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onManualSearchQueryChanged("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("manual_search_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyanPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // 3. Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { (key, label) ->
                    val isSelected = uiState.selectedManualCategory == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.filterManualByCategory(key) },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyanPrimary.copy(alpha = 0.2f),
                            selectedLabelColor = CyanPrimary
                        ),
                        modifier = Modifier.testTag("filter_chip_$key")
                    )
                }
            }
        }

        // 4. Results Count Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAr) "الأقسام المتاحة (${filteredDocuments.size}):" else "Available Sections (${filteredDocuments.size}):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Room SQLite Local DB",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = CyanPrimary
                )
            }
        }

        // 5. Document Cards
        if (filteredDocuments.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isAr) "لا توجد نتائج تطابق البحث في الدليل المحلي" else "No matching sections found in offline manual database",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredDocuments, key = { it.id }) { doc ->
                ManualDocumentCard(
                    document = doc,
                    isAr = isAr,
                    onToggleBookmark = {
                        viewModel.toggleManualBookmark(doc.id, doc.isBookmarked)
                    },
                    onSaveNote = { note ->
                        viewModel.saveTechnicianNote(doc.id, note)
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ManualDocumentCard(
    document: ManualDocumentEntity,
    isAr: Boolean,
    onToggleBookmark: () -> Unit,
    onSaveNote: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isEditingNote by remember { mutableStateOf(false) }
    var noteInput by remember(document.technicianNotes) { mutableStateOf(document.technicianNotes) }

    val categoryColor = when (document.category) {
        "SAFETY" -> AlertRed
        "SPECIFICATIONS" -> CyanPrimary
        "MAINTENANCE" -> FurnaceAmber
        "OPERATION" -> StatusGreen
        else -> MaterialTheme.colorScheme.primary
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = if (document.isBookmarked) CyanPrimary else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .testTag("manual_doc_${document.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Badges and Bookmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = categoryColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = document.category,
                            color = categoryColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "PDF P.${document.pageNumber}",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "Ch.${document.chapterNumber}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (document.isBookmarked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Bookmark",
                        tint = if (document.isBookmarked) AlertRed else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title & Section
            Text(
                text = "${document.sectionNumber} ${if (isAr) document.sectionTitleAr else document.sectionTitleEn}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = if (isAr) document.chapterTitleAr else document.chapterTitleEn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Key Points Chip
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isAr) document.keyPointsAr else document.keyPointsEn,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            // Technician note preview if exists
            if (document.technicianNotes.isNotBlank() && !isEditingNote) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = FurnaceAmber.copy(alpha = 0.15f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = FurnaceAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${if (isAr) "ملاحظة الفني: " else "Tech Note: "}${document.technicianNotes}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Expandable Content
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = if (isAr) "النص الكامل للمعلومات (Full Manual Content):" else "Full Manual Section Content:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = CyanPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isAr) document.contentAr else document.contentEn,
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (isAr) "مقتطف OCR المباشر من وثيقة PDF:" else "Raw PDF OCR Excerpt:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = document.rawPdfOcrExcerpt,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    // Technician Note Editor
                    Spacer(modifier = Modifier.height(10.dp))
                    if (isEditingNote) {
                        OutlinedTextField(
                            value = noteInput,
                            onValueChange = { noteInput = it },
                            label = { Text(if (isAr) "أدخل ملاحظات الصيانة للفني" else "Enter technician maintenance note") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    onSaveNote(noteInput)
                                    isEditingNote = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = "Save", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isAr) "حفظ في قاعدة البيانات" else "Save to Room DB", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { isEditingNote = false },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Text(if (isAr) "إلغاء" else "Cancel", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    } else {
                        Button(
                            onClick = { isEditingNote = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Add Note", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (document.technicianNotes.isBlank())
                                    (if (isAr) "إضافة ملاحظة فنية على هذا القسم" else "Add Technician Note to this section")
                                else
                                    (if (isAr) "تعديل الملاحظة الفنية" else "Edit Technician Note"),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Expand / Collapse Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded)
                        (if (isAr) "إخفاء التفاصيل" else "Hide Details")
                    else
                        (if (isAr) "عرض التفاصيل والنص الكامل" else "View Details & Full Text"),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyanPrimary
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = "Expand",
                    tint = CyanPrimary
                )
            }
        }
    }
}
