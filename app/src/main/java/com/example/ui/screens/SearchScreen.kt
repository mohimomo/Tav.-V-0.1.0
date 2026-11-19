package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.data.SearchResultItem
import com.example.model.AlarmItem
import com.example.model.ComponentItem
import com.example.model.MaintenanceTask
import com.example.model.PidSymbolItem
import com.example.model.SubsystemCategory
import com.example.ui.AppLanguage
import com.example.ui.AppUiState
import com.example.ui.AppViewModel
import com.example.ui.components.AlarmDetailDialog
import com.example.ui.components.ComponentDetailDialog
import com.example.ui.components.SchematicSummaryCard
import com.example.ui.components.SeverityBadge
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.FurnaceAmber
import com.example.ui.theme.StatusGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    uiState: AppUiState,
    viewModel: AppViewModel,
    contentPadding: PaddingValues
) {
    val isAr = uiState.language == AppLanguage.ARABIC
    var filterType by remember { mutableStateOf("ALL") } // ALL, SCHEMATICS, COMPONENTS, ALARMS, PID, MAINTENANCE
    val customPhotos by viewModel.customComponentPhotos.collectAsState()

    val filteredResults = remember(uiState.searchResults, filterType) {
        when (filterType) {
            "SCHEMATICS" -> uiState.searchResults.filterIsInstance<SearchResultItem.SchematicResult>()
            "ALARMS" -> uiState.searchResults.filterIsInstance<SearchResultItem.AlarmResult>()
            "COMPONENTS" -> uiState.searchResults.filterIsInstance<SearchResultItem.ComponentResult>()
            "PID" -> uiState.searchResults.filterIsInstance<SearchResultItem.PidSymbolResult>()
            "MAINTENANCE" -> uiState.searchResults.filterIsInstance<SearchResultItem.MaintenanceResult>()
            else -> uiState.searchResults
        }
    }

    // Dialogs
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

    if (uiState.selectedComponent != null) {
        val selected = uiState.selectedComponent
        val photo = customPhotos[selected.id]
        ComponentDetailDialog(
            component = selected,
            customPhoto = photo,
            language = uiState.language,
            onDismiss = { viewModel.selectComponent(null) },
            onSavePhoto = { uri, notes -> viewModel.saveComponentPhoto(selected.id, uri, notes) },
            onDeletePhoto = { viewModel.deleteComponentPhoto(selected.id) },
            onNavigateToCircuit = { domain, nodeId -> viewModel.openCircuitForComponent(domain, nodeId) }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        // Search Input Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = if (isAr) "محرك البحث الذكي للفرن 911" else "Smart Search Engine (S/N 911)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isAr) "ابحث باسم القطعة، الرمز الكهربائي، كود الخطأ، رقم الصفحة أو العطل" else "Search by part tag, P&ID symbol, alarm code, page number, or fault symptom",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("smart_search_input"),
                placeholder = {
                    Text(
                        text = if (isAr) "اكتب: 6GQZ10, LoVVa1, تسريب, NHS 35, حرارة..." else "e.g. 6GQZ10, LoVVa1, leak, NHS 35, overtemperature...",
                        fontSize = 13.sp
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
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.onSearchQueryChanged("") },
                            modifier = Modifier.testTag("clear_search_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedIndicatorColor = CyanPrimary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                ),
                singleLine = true
            )
        }

        // Subsystem Category Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(SubsystemCategory.values()) { category ->
                val isSelected = uiState.selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.onCategoryFilterChanged(category) },
                    label = {
                        Text(
                            text = if (isAr) category.titleAr else category.titleEn,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyanPrimary.copy(alpha = 0.2f),
                        selectedLabelColor = CyanPrimary
                    ),
                    modifier = Modifier.testTag("category_chip_${category.name}")
                )
            }
        }

        // Entity Type Chips (All, Alarms, Parts, P&ID, Maintenance)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val typeFilters = listOf(
                "ALL" to (if (isAr) "الكل" else "All"),
                "SCHEMATICS" to (if (isAr) "المخططات والرسومات" else "Schematics & DWG"),
                "COMPONENTS" to (if (isAr) "القطع والمعدات" else "Components & Tags"),
                "ALARMS" to (if (isAr) "الإنذارات والأعطال" else "Alarms & Faults"),
                "PID" to (if (isAr) "رموز P&ID" else "P&ID Acronyms"),
                "MAINTENANCE" to (if (isAr) "الصيانة الوقائية" else "Maintenance")
            )
            items(typeFilters) { (key, label) ->
                val isSelected = filterType == key
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clickable { filterType = key }
                        .testTag("type_filter_$key")
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Results Count Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isAr) "نتائج البحث (${filteredResults.size}):" else "Search Results (${filteredResults.size}):",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (uiState.searchQuery.isNotEmpty()) {
                Text(
                    text = if (isAr) "تصفية: \"${uiState.searchQuery}\"" else "Filter: \"${uiState.searchQuery}\"",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Results List
        if (filteredResults.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(54.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isAr) "لم يتم العثور على نتائج مطابقة" else "No matching results found",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (isAr) "جرب البحث برمز القطعة مثل 6GQZ10 أو اسم الحساس أو العطل" else "Try searching by tag like 6GQZ10, LoVVa1, or symptom",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredResults) { result ->
                    when (result) {
                        is SearchResultItem.AlarmResult -> {
                            AlarmSearchResultCard(
                                alarm = result.alarm,
                                matchField = result.matchField,
                                language = uiState.language,
                                onClick = { viewModel.selectAlarm(result.alarm) }
                            )
                        }
                        is SearchResultItem.SchematicResult -> {
                            SchematicSummaryCard(
                                schematic = result.schematic,
                                component = result.component,
                                isArabic = isAr,
                                onOpenComponent = { comp -> viewModel.selectComponent(comp) },
                                onOpenCircuit = { domain, nodeId -> viewModel.openCircuitForComponent(domain, nodeId) },
                                matchHighlight = result.matchField
                            )
                        }
                        is SearchResultItem.ComponentResult -> {
                            ComponentSearchResultCard(
                                component = result.component,
                                matchField = result.matchField,
                                language = uiState.language,
                                onClick = { viewModel.selectComponent(result.component) }
                            )
                        }
                        is SearchResultItem.PidSymbolResult -> {
                            PidSearchResultCard(
                                item = result.pidSymbol,
                                language = uiState.language
                            )
                        }
                        is SearchResultItem.MaintenanceResult -> {
                            MaintenanceSearchResultCard(
                                task = result.task,
                                language = uiState.language
                            )
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
fun AlarmSearchResultCard(
    alarm: AlarmItem,
    matchField: String,
    language: AppLanguage,
    onClick: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("alarm_result_${alarm.id}")
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
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = AlertRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) alarm.nameAr else alarm.nameEn,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                SeverityBadge(severity = alarm.severity, language = language)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isAr) alarm.nameEn else alarm.nameAr,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isAr) "استجابة الفرن: ${alarm.reactionAr}" else "Reaction: ${alarm.reactionEn}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

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
                        color = CyanPrimary
                    )
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = if (isAr) "عرض الحل والتشخيص ➔" else "View Diagnosis ➔",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ComponentSearchResultCard(
    component: ComponentItem,
    matchField: String,
    language: AppLanguage,
    onClick: () -> Unit
) {
    val isAr = language == AppLanguage.ARABIC
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("component_result_${component.id}")
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
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = CyanPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) component.nameAr else component.nameEn,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CyanPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = component.tag,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${component.pidCode} • ${component.manufacturerModel}",
                style = MaterialTheme.typography.bodySmall,
                color = FurnaceAmber,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = component.specifications,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = component.electricalCabinet,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = if (isAr) "تفاصيل القطعة ➔" else "Details ➔",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PidSearchResultCard(
    item: PidSymbolItem,
    language: AppLanguage
) {
    val isAr = language == AppLanguage.ARABIC
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = FurnaceAmber.copy(alpha = 0.2f),
                    modifier = Modifier.padding(end = 10.dp)
                ) {
                    Text(
                        text = item.symbol,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        color = FurnaceAmber,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Column {
                    Text(
                        text = if (isAr) item.arabic else item.english,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${item.english} • ${item.italian}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Text(
                    text = item.category,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun MaintenanceSearchResultCard(
    task: MaintenanceTask,
    language: AppLanguage
) {
    val isAr = language == AppLanguage.ARABIC
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAr) task.operationAr else task.operationEn,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = StatusGreen.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (isAr) task.frequencyAr else task.frequencyEn,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusGreen,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isAr) task.remarksAr else task.remarksEn,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (task.torques.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Torques: ${task.torques.joinToString(" • ")}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = FurnaceAmber,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
