package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import com.example.model.AlarmItem
import com.example.model.AlarmSeverity
import com.example.model.SubsystemCategory
import com.example.ui.AppLanguage
import com.example.ui.AppUiState
import com.example.ui.AppViewModel
import com.example.ui.tr
import com.example.ui.components.AlarmDetailDialog
import com.example.ui.components.SeverityBadge
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.FurnaceAmber

@Composable
fun AlarmsDirectoryScreen(
    uiState: AppUiState,
    viewModel: AppViewModel,
    contentPadding: PaddingValues
) {
    val isAr = uiState.language == AppLanguage.ARABIC
    var internalQuery by remember { mutableStateOf("") }
    var selectedSeverity by remember { mutableStateOf<AlarmSeverity?>(null) }
    var selectedCategory by remember { mutableStateOf(SubsystemCategory.ALL) }
    var selectedTab by remember { mutableStateOf(0) } // 0: All, 1: Bookmarks

    val allAlarms = FurnaceRepository.alarms

    val displayedAlarms = remember(internalQuery, selectedSeverity, selectedCategory, selectedTab, uiState.bookmarkedAlarms) {
        allAlarms.filter { alarm ->
            val matchesTab = if (selectedTab == 1) uiState.bookmarkedAlarms.contains(alarm.id) else true
            val matchesCategory = if (selectedCategory == SubsystemCategory.ALL) true else alarm.subsystem == selectedCategory
            val matchesSeverity = if (selectedSeverity == null) true else alarm.severity == selectedSeverity

            val matchesQuery = if (internalQuery.isBlank()) true else {
                val q = internalQuery.trim().lowercase()
                alarm.id.lowercase().contains(q) ||
                alarm.nameEn.lowercase().contains(q) ||
                alarm.nameAr.lowercase().contains(q) ||
                alarm.descriptionEn.lowercase().contains(q) ||
                alarm.descriptionAr.lowercase().contains(q) ||
                alarm.causesAndSolutionsEn.lowercase().contains(q) ||
                alarm.causesAndSolutionsAr.lowercase().contains(q) ||
                alarm.references.any { it.lowercase().contains(q) }
            }

            matchesTab && matchesCategory && matchesSeverity && matchesQuery
        }
    }

    if (uiState.selectedAlarm != null) {
        AlarmDetailDialog(
            alarm = uiState.selectedAlarm,
            language = uiState.language,
            isBookmarked = uiState.bookmarkedAlarms.contains(uiState.selectedAlarm.id),
            onToggleBookmark = { viewModel.toggleBookmarkAlarm(uiState.selectedAlarm.id) },
            onDismiss = { viewModel.selectAlarm(null) },
            onAskAi = { viewModel.askAiAboutAlarm(uiState.selectedAlarm) }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = uiState.language.tr("سجل الإنذارات وتشخيص الأعطال (70+ إنذار)", "Alarms & Fault Diagnostics (70+ Alarms)", "Répertoire des alarmes et diagnostic (70+ alarmes)"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = uiState.language.tr("استجابة الفرن التلقائية، الأسباب، الخطوات التصحيحية ومراجع القواطع", "Automatic furnace reaction, causes, step-by-step remedies & breakers", "Réaction automatique du four, causes racines, dépannage et disjoncteurs"),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Search in alarms
            OutlinedTextField(
                value = internalQuery,
                onValueChange = { internalQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("alarms_search_input"),
                placeholder = {
                    Text(
                        text = uiState.language.tr("بحث سريع في الإنذارات: رقم أو اسم أو كود...", "Quick search: code, name, breaker...", "Recherche rapide : code, nom, disjoncteur..."),
                        fontSize = 12.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = AlertRed
                    )
                },
                trailingIcon = {
                    if (internalQuery.isNotEmpty()) {
                        IconButton(onClick = { internalQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                singleLine = true
            )
        }

        // Tabs: All vs Bookmarks
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        text = uiState.language.tr("جميع الإنذارات (${allAlarms.size})", "All Alarms (${allAlarms.size})", "Toutes les alarmes (${allAlarms.size})"),
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = FurnaceAmber
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = uiState.language.tr("المفضلة (${uiState.bookmarkedAlarms.size})", "Saved (${uiState.bookmarkedAlarms.size})", "Favoris (${uiState.bookmarkedAlarms.size})"),
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            )
        }

        // Severity Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedSeverity == null,
                    onClick = { selectedSeverity = null },
                    label = { Text(uiState.language.tr("كل المستويات", "All Levels", "Tous les niveaux"), fontSize = 11.sp) }
                )
            }
            item {
                FilterChip(
                    selected = selectedSeverity == AlarmSeverity.CRITICAL,
                    onClick = { selectedSeverity = if (selectedSeverity == AlarmSeverity.CRITICAL) null else AlarmSeverity.CRITICAL },
                    label = { Text(uiState.language.tr("حرجة فقط", "Critical Only", "Critiques uniquement"), fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedLabelColor = AlertRed)
                )
            }
            item {
                FilterChip(
                    selected = selectedSeverity == AlarmSeverity.HIGH,
                    onClick = { selectedSeverity = if (selectedSeverity == AlarmSeverity.HIGH) null else AlarmSeverity.HIGH },
                    label = { Text(uiState.language.tr("عالية", "High", "Élevées"), fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedLabelColor = FurnaceAmber)
                )
            }
            item {
                FilterChip(
                    selected = selectedSeverity == AlarmSeverity.WARNING,
                    onClick = { selectedSeverity = if (selectedSeverity == AlarmSeverity.WARNING) null else AlarmSeverity.WARNING },
                    label = { Text(uiState.language.tr("تحذيرات", "Warnings", "Avertissements"), fontSize = 11.sp) }
                )
            }
        }

        // Alarms List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(displayedAlarms) { alarm ->
                AlarmCard(
                    alarm = alarm,
                    language = uiState.language,
                    isBookmarked = uiState.bookmarkedAlarms.contains(alarm.id),
                    onToggleBookmark = { viewModel.toggleBookmarkAlarm(alarm.id) },
                    onClick = { viewModel.selectAlarm(alarm) }
                )
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun AlarmCard(
    alarm: AlarmItem,
    language: AppLanguage,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit,
    onClick: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("alarm_card_${alarm.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isAr) alarm.nameAr else alarm.nameEn,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isAr) alarm.nameEn else alarm.nameAr,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    SeverityBadge(severity = alarm.severity, language = language)
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save",
                            tint = if (isBookmarked) FurnaceAmber else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = if (isAr) "استجابة الفرن: ${alarm.reactionAr}" else "Reaction: ${alarm.reactionEn}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (alarm.references.isNotEmpty()) {
                    Text(
                        text = alarm.references.first(),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = CyanPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
                Text(
                    text = if (isAr) "الحل والتشخيص ➔" else "Diagnosis ➔",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
