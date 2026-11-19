package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.FurnaceRepository
import com.example.data.local.ComponentPhotoEntity
import java.io.File
import com.example.model.ComponentItem
import com.example.model.ComponentSchematicDetail
import com.example.model.PidSymbolItem
import com.example.model.SchematicDrawingType
import com.example.model.SubsystemCategory
import com.example.data.ComponentSchematicsData
import com.example.ui.AppLanguage
import com.example.ui.AppUiState
import com.example.ui.AppViewModel
import com.example.ui.components.ComponentDetailDialog
import com.example.ui.components.SchematicSummaryCard
import androidx.compose.material.icons.filled.Architecture
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.FurnaceAmber

enum class PhotoFilter {
    ALL,
    WITH_CUSTOM_PHOTO,
    MISSING_PHOTO
}

@Composable
fun ComponentsScreen(
    uiState: AppUiState,
    viewModel: AppViewModel,
    contentPadding: PaddingValues
) {
    val isAr = uiState.language == AppLanguage.ARABIC
    var internalQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(SubsystemCategory.ALL) }
    var selectedTab by remember { mutableStateOf(0) } // 0: Components, 1: Schematics & Blueprints, 2: P&ID Dictionary
    var photoFilter by remember { mutableStateOf(PhotoFilter.ALL) }
    var selectedSchematicType by remember { mutableStateOf<SchematicDrawingType?>(null) }

    val allComponents = FurnaceRepository.components
    val allSchematics = ComponentSchematicsData.list
    val allPidSymbols = FurnaceRepository.pidSymbols
    val customPhotos by viewModel.customComponentPhotos.collectAsState()

    val displayedComponents = remember(internalQuery, selectedCategory, customPhotos, photoFilter) {
        allComponents.filter { comp ->
            val matchesCategory = if (selectedCategory == SubsystemCategory.ALL) true else comp.subsystem == selectedCategory
            val matchesPhoto = when (photoFilter) {
                PhotoFilter.ALL -> true
                PhotoFilter.WITH_CUSTOM_PHOTO -> customPhotos.containsKey(comp.id)
                PhotoFilter.MISSING_PHOTO -> !customPhotos.containsKey(comp.id)
            }
            val matchesQuery = if (internalQuery.isBlank()) true else {
                val q = internalQuery.trim().lowercase()
                comp.tag.lowercase().contains(q) ||
                comp.pidCode.lowercase().contains(q) ||
                comp.nameEn.lowercase().contains(q) ||
                comp.nameAr.lowercase().contains(q) ||
                comp.manufacturerModel.lowercase().contains(q) ||
                comp.electricalCabinet.lowercase().contains(q) ||
                comp.specifications.lowercase().contains(q)
            }
            matchesCategory && matchesPhoto && matchesQuery
        }
    }

    val displayedSchematics = remember(internalQuery, selectedSchematicType) {
        allSchematics.filter { sch ->
            val matchesType = selectedSchematicType == null || sch.schematicType == selectedSchematicType
            val matchesQuery = if (internalQuery.isBlank()) true else {
                val q = internalQuery.trim().lowercase()
                sch.tag.lowercase().contains(q) ||
                sch.pidCode.lowercase().contains(q) ||
                sch.drawingCode.lowercase().contains(q) ||
                sch.drawingTitleAr.lowercase().contains(q) ||
                sch.drawingTitleEn.lowercase().contains(q) ||
                sch.schematicSheetPage.lowercase().contains(q) ||
                sch.cabinet.lowercase().contains(q) ||
                sch.terminalBlock.lowercase().contains(q) ||
                sch.plcAddressTag.lowercase().contains(q) ||
                sch.mechanicalMounting.lowercase().contains(q) ||
                sch.pneumaticOrFluidLine.lowercase().contains(q)
            }
            matchesType && matchesQuery
        }
    }

    val displayedPidSymbols = remember(internalQuery) {
        if (internalQuery.isBlank()) allPidSymbols else {
            val q = internalQuery.trim().lowercase()
            allPidSymbols.filter {
                it.symbol.lowercase().contains(q) ||
                it.italian.lowercase().contains(q) ||
                it.english.lowercase().contains(q) ||
                it.arabic.lowercase().contains(q)
            }
        }
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
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (isAr) "دليل القطع والمخططات ورموز P&ID" else "Components, Schematics & P&ID Directory",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isAr) "تحديد مواقع الأجهزة واللوحات الكهربائية وأرقام الصفحات الرسمية" else "Locate equipment, electrical cabinets & schematics page numbers",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = internalQuery,
                onValueChange = { internalQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("components_search_input"),
                placeholder = {
                    Text(
                        text = if (isAr) "ابحث عن قطعة: 6GQZ10, Leybold, كونتكتور, صمام..." else "Search part: 6GQZ10, Leybold, valve, sensor...",
                        fontSize = 12.sp
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

        // Tabs: Components Catalog vs Engineering Schematics & Blueprints vs P&ID Acronym Dictionary
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        text = if (isAr) "القطع والمعدات (${allComponents.size})" else "Equipment (${allComponents.size})",
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
                            imageVector = Icons.Default.Architecture,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = CyanPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isAr) "المخططات والرسومات (${allSchematics.size})" else "Schematics & DWG (${allSchematics.size})",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = FurnaceAmber
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isAr) "رموز P&ID (${allPidSymbols.size})" else "P&ID Terms (${allPidSymbols.size})",
                            fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            )
        }

        when (selectedTab) {
            0 -> {
                // Category filter chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(SubsystemCategory.values()) { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = {
                                Text(
                                    text = if (isAr) category.titleAr else category.titleEn,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedLabelColor = CyanPrimary
                            )
                        )
                    }
                }

                // Photo Filter Chips (All vs With Custom Photos vs Missing Photos)
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = photoFilter == PhotoFilter.ALL,
                            onClick = { photoFilter = PhotoFilter.ALL },
                            label = {
                                Text(
                                    text = if (isAr) "جميع القطع (${allComponents.size})" else "All Parts (${allComponents.size})",
                                    fontSize = 11.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(selectedLabelColor = CyanPrimary)
                        )
                    }
                    item {
                        FilterChip(
                            selected = photoFilter == PhotoFilter.WITH_CUSTOM_PHOTO,
                            onClick = { photoFilter = PhotoFilter.WITH_CUSTOM_PHOTO },
                            leadingIcon = {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF00E676))
                            },
                            label = {
                                Text(
                                    text = if (isAr) "📷 صور حقيقية محفوظة (${customPhotos.size})" else "📷 Custom Photos (${customPhotos.size})",
                                    fontSize = 11.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(selectedLabelColor = Color(0xFF00E676))
                        )
                    }
                    item {
                        val missingCount = (allComponents.size - customPhotos.size).coerceAtLeast(0)
                        FilterChip(
                            selected = photoFilter == PhotoFilter.MISSING_PHOTO,
                            onClick = { photoFilter = PhotoFilter.MISSING_PHOTO },
                            leadingIcon = {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(14.dp), tint = FurnaceAmber)
                            },
                            label = {
                                Text(
                                    text = if (isAr) "⚠️ بحاجة لصورة حقيقية ($missingCount)" else "⚠️ Need Photo ($missingCount)",
                                    fontSize = 11.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(selectedLabelColor = FurnaceAmber)
                        )
                    }
                }

                // Components List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayedComponents) { comp ->
                        ComponentCard(
                            component = comp,
                            customPhoto = customPhotos[comp.id],
                            language = uiState.language,
                            onClick = { viewModel.selectComponent(comp) },
                            onPhotoPicked = { uri -> viewModel.saveComponentPhoto(comp.id, uri) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
            1 -> {
                // Drawing Type Filter Chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedSchematicType == null,
                            onClick = { selectedSchematicType = null },
                            label = {
                                Text(
                                    text = if (isAr) "جميع المخططات (${allSchematics.size})" else "All Drawings (${allSchematics.size})",
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedSchematicType == null) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(selectedLabelColor = CyanPrimary)
                        )
                    }
                    items(SchematicDrawingType.values()) { type ->
                        val isSelected = selectedSchematicType == type
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedSchematicType = type },
                            label = {
                                Text(
                                    text = if (isAr) type.titleAr else type.titleEn,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(selectedLabelColor = CyanPrimary)
                        )
                    }
                }

                // Count Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) "المخططات الهندسية المعروضة (${displayedSchematics.size}):" else "Schematic Blueprints (${displayedSchematics.size}):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (isAr) "مربوطة بكل قطعة وتوصيلاتها" else "Linked to every part & pinout",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Schematics List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(displayedSchematics) { sch ->
                        val linkedComp = allComponents.find { it.id == sch.componentId }
                        SchematicSummaryCard(
                            schematic = sch,
                            component = linkedComp,
                            isArabic = isAr,
                            onOpenComponent = { comp -> viewModel.selectComponent(comp) },
                            onOpenCircuit = { domain, nodeId -> viewModel.openCircuitForComponent(domain, nodeId) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
            else -> {
                // P&ID Acronym Dictionary List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(displayedPidSymbols) { pid ->
                        PidSymbolCard(item = pid, language = uiState.language)
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun ComponentCard(
    component: ComponentItem,
    customPhoto: ComponentPhotoEntity? = null,
    language: AppLanguage,
    onClick: () -> Unit,
    onPhotoPicked: (Uri) -> Unit = {}
) {
    val isAr = language == AppLanguage.ARABIC
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onPhotoPicked(uri)
        }
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("component_card_${component.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Main Top Row: Image / Visual Thumbnail + Core Names + Tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Component Image / Icon Preview (Custom Real Photo or Default Vector Graphic)
                if (customPhoto != null && File(customPhoto.localFilePath).exists()) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = File(customPhoto.localFilePath),
                            contentDescription = component.nameEn,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF00E676),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(3.dp)
                                .size(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                } else if (component.imageResId != null) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = component.imageResId),
                            contentDescription = component.nameEn,
                            modifier = Modifier
                                .size(64.dp)
                                .padding(2.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                } else {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CyanPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = CyanPrimary,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }

                // Title & Subtitle & Code Badges
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) component.nameAr else component.nameEn,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        // Tag Badge
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

                    Text(
                        text = if (isAr) component.nameEn else component.nameAr,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = FurnaceAmber.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "P&ID: ${component.pidCode}",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = FurnaceAmber,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = component.manufacturerModel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Physical Location in Furnace (مكان التواجد)
            if (component.locationAr.isNotBlank() || component.locationEn.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Location",
                            tint = FurnaceAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = if (isAr) "مكان التواجد والموقع الفيزيائي:" else "Location & Physical Mount:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = FurnaceAmber
                            )
                            Text(
                                text = if (isAr) component.locationAr else component.locationEn,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Role and Purpose in Furnace (دور القطعة ووظيفتها)
            if (component.roleAr.isNotBlank() || component.roleEn.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CyanPrimary.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Role",
                            tint = CyanPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = if (isAr) "الدور والوظيفة في دورة الفرن:" else "Furnace Role & Function:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanPrimary
                            )
                            Text(
                                text = if (isAr) component.roleAr else component.roleEn,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // Bottom Schematics reference, Photo attach button & Details trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = component.electricalCabinet,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f, fill = false)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(
                            imageVector = if (customPhoto != null) Icons.Default.CameraAlt else Icons.Default.AddPhotoAlternate,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = if (customPhoto != null) Color(0xFF00E676) else CyanPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (customPhoto != null) (if (isAr) "تغيير" else "Change") else (if (isAr) "📷 صورة" else "📷 Photo"),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (customPhoto != null) Color(0xFF00E676) else CyanPrimary
                        )
                    }

                    Text(
                        text = if (isAr) "التفاصيل ➔" else "Details ➔",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PidSymbolCard(
    item: PidSymbolItem,
    language: AppLanguage
) {
    val isAr = language == AppLanguage.ARABIC

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CyanPrimary.copy(alpha = 0.15f),
                    modifier = Modifier.width(54.dp)
                ) {
                    Text(
                        text = item.symbol,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = CyanPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = if (isAr) item.arabic else item.english,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "EN: ${item.english}  •  IT: ${item.italian}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = item.category,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
