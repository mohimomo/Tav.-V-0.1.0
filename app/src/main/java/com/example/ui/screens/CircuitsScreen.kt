package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.CircuitsData
import com.example.model.CircuitDiagram
import com.example.model.CircuitDomain
import com.example.model.CircuitNode
import com.example.model.FlowType
import com.example.model.NodeSymbolType
import com.example.model.TraceablePath
import com.example.ui.AppLanguage
import com.example.ui.AppUiState
import com.example.ui.AppViewModel
import com.example.ui.NavigationScreen
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.FurnaceAmber
import com.example.ui.theme.StatusGreen

@Composable
fun CircuitsScreen(
    uiState: AppUiState,
    viewModel: AppViewModel,
    contentPadding: PaddingValues
) {
    BackHandler {
        viewModel.navigateTo(NavigationScreen.HOME)
    }

    val isAr = uiState.language == AppLanguage.ARABIC
    val currentDiagram = CircuitsData.getDiagramByDomain(uiState.selectedCircuitDomain)

    val activePath: TraceablePath? = currentDiagram.traceablePaths.firstOrNull {
        it.id == uiState.activeTracePathId
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 14.dp)
            .testTag("circuits_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Header Banner & Domain Selector Tabs
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(CyanPrimary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = CyanPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isAr) "مخططات الدارات الهندسية القابلة للتتبع" else "Traceable Engineering Schematics",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isAr) "تتبع تدفق الطاقة الكهربائية، المياه، الغاز، والدارات الهيدروليكية" else "Trace electrical power, water cooling, gas, and hydraulic circuits",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Simulation toggle
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (uiState.isSimulationFlowActive) StatusGreen.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.toggleCircuitSimulation() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (uiState.isSimulationFlowActive) StatusGreen else MaterialTheme.colorScheme.outline)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (uiState.isSimulationFlowActive)
                                        (if (isAr) "تدفق حي" else "Live Flow")
                                    else
                                        (if (isAr) "ثابت" else "Paused"),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (uiState.isSimulationFlowActive) StatusGreen else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Domain Selector Tabs (Electrical, Water, Gas, Hydraulic)
                    val domains = CircuitDomain.values()
                    val selectedIndex = domains.indexOf(uiState.selectedCircuitDomain)

                    ScrollableTabRow(
                        selectedTabIndex = selectedIndex,
                        containerColor = Color.Transparent,
                        edgePadding = 0.dp,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedIndex]),
                                color = CyanPrimary,
                                height = 3.dp
                            )
                        },
                        divider = {}
                    ) {
                        domains.forEachIndexed { index, domain ->
                            Tab(
                                selected = selectedIndex == index,
                                onClick = { viewModel.selectCircuitDomain(domain) },
                                text = {
                                    Text(
                                        text = if (isAr) domain.titleAr else domain.titleEn,
                                        fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedIndex == index) CyanPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp
                                    )
                                },
                                modifier = Modifier.testTag("circuit_domain_tab_${domain.name}")
                            )
                        }
                    }
                }
            }
        }

        // 2. Active Diagram Title & Reference
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isAr) currentDiagram.titleAr else currentDiagram.titleEn,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isAr) currentDiagram.subtitleAr else currentDiagram.subtitleEn,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = currentDiagram.schematicsRef,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = CyanPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // 3. Traceable Paths Selector Bar
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) "المسارات القابلة للتتبع (انقر للتتبع):" else "Traceable Paths (Tap to trace flow):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = CyanPrimary
                    )
                    if (activePath != null) {
                        Text(
                            text = if (isAr) "مسار نشط ومُضاء" else "Path Active",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(activePath.flowColorHex)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(currentDiagram.traceablePaths) { path ->
                        val isPathActive = uiState.activeTracePathId == path.id
                        val pathColor = Color(path.flowColorHex)

                        FilterChip(
                            selected = isPathActive,
                            onClick = { viewModel.toggleTracePath(path.id) },
                            label = {
                                Text(
                                    text = if (isAr) path.nameAr else path.nameEn,
                                    fontSize = 11.sp,
                                    fontWeight = if (isPathActive) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(pathColor)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = pathColor.copy(alpha = 0.25f),
                                selectedLabelColor = pathColor
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.testTag("trace_path_chip_${path.id}")
                        )
                    }
                }

                if (activePath != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(activePath.flowColorHex).copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "ℹ  ${if (isAr) activePath.descriptionAr else activePath.descriptionEn}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // 4. Interactive Schematic Diagram Canvas / Traceable Graph
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) "مخطط الدارة التفاعلي (انقر على أي عنصر للفحص)" else "Interactive Schematic (Tap any node to inspect)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${currentDiagram.nodes.size} Nodes • ${currentDiagram.connections.size} Links",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Schematic Graphical Flow Canvas View
                    SchematicFlowCanvas(
                        diagram = currentDiagram,
                        activePath = activePath,
                        selectedNode = uiState.selectedCircuitNode,
                        isFlowActive = uiState.isSimulationFlowActive,
                        onNodeClick = { viewModel.selectCircuitNode(it) },
                        isAr = isAr
                    )
                }
            }
        }

        // 5. Component Breakdown & Quick Inspector List
        item {
            Text(
                text = if (isAr) "عناصر ومكونات الدارة (${currentDiagram.nodes.size} عنصر):" else "Circuit Components & Specifications (${currentDiagram.nodes.size}):",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(currentDiagram.nodes) { node ->
            val isInActivePath = activePath?.nodeIds?.contains(node.id) == true
            val isSelected = uiState.selectedCircuitNode?.id == node.id

            CircuitNodeListItem(
                node = node,
                isInActivePath = isInActivePath,
                isSelected = isSelected,
                isAr = isAr,
                pathColor = if (activePath != null) Color(activePath.flowColorHex) else CyanPrimary,
                onClick = { viewModel.selectCircuitNode(node) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Node Detail Inspection Dialog
    if (uiState.selectedCircuitNode != null) {
        CircuitNodeDetailDialog(
            node = uiState.selectedCircuitNode,
            isAr = isAr,
            onDismiss = { viewModel.selectCircuitNode(null) },
            onQuickSearchAlarm = { alarmCode ->
                viewModel.selectCircuitNode(null)
                viewModel.quickSearch(alarmCode)
            }
        )
    }
}

/**
 * Custom Canvas drawing connecting lines, flow arrows, and pulse animations
 * between schematic nodes.
 */
@Composable
private fun SchematicFlowCanvas(
    diagram: CircuitDiagram,
    activePath: TraceablePath?,
    selectedNode: CircuitNode?,
    isFlowActive: Boolean,
    onNodeClick: (CircuitNode) -> Unit,
    isAr: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "schematic_flow")
    val phaseOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isFlowActive) 40f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase_offset"
    )

    // Horizontal scrollable container for wide schematic viewing
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F172A))
            .horizontalScroll(rememberScrollState())
            .width(960.dp)
    ) {
        // Draw Connecting Lines
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            diagram.connections.forEach { conn ->
                val fromNode = diagram.nodes.firstOrNull { it.id == conn.fromNodeId }
                val toNode = diagram.nodes.firstOrNull { it.id == conn.toNodeId }

                if (fromNode != null && toNode != null) {
                    val startX = (fromNode.x / 1000f) * canvasWidth
                    val startY = (fromNode.y / 600f) * canvasHeight
                    val endX = (toNode.x / 1000f) * canvasWidth
                    val endY = (toNode.y / 600f) * canvasHeight

                    val isConnectionInPath = activePath != null &&
                            activePath.nodeIds.contains(fromNode.id) &&
                            activePath.nodeIds.contains(toNode.id)

                    val lineColor = if (isConnectionInPath) {
                        Color(activePath.flowColorHex)
                    } else {
                        when (conn.flowType) {
                            FlowType.ELECTRIC_3PHASE -> Color(0xFFFF5722).copy(alpha = 0.5f)
                            FlowType.ELECTRIC_CONTROL -> Color(0xFF00E5FF).copy(alpha = 0.5f)
                            FlowType.WATER_SUPPLY -> Color(0xFF03A9F4).copy(alpha = 0.5f)
                            FlowType.WATER_RETURN -> Color(0xFF3F51B5).copy(alpha = 0.5f)
                            FlowType.WATER_EMERGENCY -> Color(0xFFFF9800).copy(alpha = 0.6f)
                            FlowType.GAS_HIGH_PRESSURE -> Color(0xFF00E5FF).copy(alpha = 0.5f)
                            FlowType.PNEUMATIC_AIR -> Color(0xFFFFC107).copy(alpha = 0.5f)
                            FlowType.HYDRAULIC_FLUID -> Color(0xFF9C27B0).copy(alpha = 0.5f)
                            FlowType.SAFETY_SIGNAL -> Color(0xFF4CAF50).copy(alpha = 0.6f)
                        }
                    }

                    val strokeWidth = if (isConnectionInPath) 4.dp.toPx() else 2.dp.toPx()

                    val pathEffect = if (isConnectionInPath && isFlowActive) {
                        PathEffect.dashPathEffect(floatArrayOf(16f, 12f), phaseOffset)
                    } else {
                        null
                    }

                    // Orthogonal connection path
                    val path = Path().apply {
                        moveTo(startX, startY)
                        val midX = (startX + endX) / 2
                        cubicTo(midX, startY, midX, endY, endX, endY)
                    }

                    drawPath(
                        path = path,
                        color = lineColor,
                        style = Stroke(
                            width = strokeWidth,
                            cap = StrokeCap.Round,
                            pathEffect = pathEffect
                        )
                    )

                    // Draw flow direction pulse circle
                    if (isConnectionInPath && isFlowActive) {
                        val pulseT = (phaseOffset % 40f) / 40f
                        val pulseX = startX + (endX - startX) * pulseT
                        val pulseY = startY + (endY - startY) * pulseT
                        drawCircle(
                            color = Color.White,
                            radius = 4.dp.toPx(),
                            center = Offset(pulseX, pulseY)
                        )
                    }
                }
            }
        }

        // Render Schematic Node Cards overlaying the Canvas
        diagram.nodes.forEach { node ->
            val isInActivePath = activePath?.nodeIds?.contains(node.id) == true
            val isSelected = selectedNode?.id == node.id

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Compute coordinates relative to 960x340 container
                val nodeLeft = (node.x / 1000f) * 960f - 75f
                val nodeTop = (node.y / 600f) * 340f - 38f

                SchematicNodeCard(
                    node = node,
                    isInActivePath = isInActivePath,
                    isSelected = isSelected,
                    pathColor = if (activePath != null) Color(activePath.flowColorHex) else CyanPrimary,
                    isAr = isAr,
                    modifier = Modifier
                        .padding(start = nodeLeft.coerceAtLeast(0f).dp, top = nodeTop.coerceAtLeast(0f).dp)
                        .clickable { onNodeClick(node) }
                )
            }
        }
    }
}

@Composable
private fun SchematicNodeCard(
    node: CircuitNode,
    isInActivePath: Boolean,
    isSelected: Boolean,
    pathColor: Color,
    isAr: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        isSelected -> Color.White
        isInActivePath -> pathColor
        node.isPrimarySource -> CyanPrimary
        node.isEndpoint -> StatusGreen
        else -> Color(0xFF334155)
    }

    val backgroundColor = when {
        isSelected -> Color(0xFF1E293B)
        isInActivePath -> pathColor.copy(alpha = 0.2f)
        else -> Color(0xFF1E293B).copy(alpha = 0.85f)
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = backgroundColor,
        border = BorderStroke(if (isInActivePath || isSelected) 2.dp else 1.dp, borderColor),
        shadowElevation = if (isInActivePath) 6.dp else 2.dp,
        modifier = modifier
            .width(150.dp)
            .height(76.dp)
            .testTag("node_${node.id}")
    ) {
        Column(
            modifier = Modifier
                .padding(6.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = node.componentTag,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (isInActivePath) pathColor else CyanPrimary,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isInActivePath) pathColor else StatusGreen)
                )
            }

            Text(
                text = if (isAr) node.labelAr else node.labelEn,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1
            )

            Text(
                text = if (isAr) node.defaultStatusAr else node.defaultStatusEn,
                fontSize = 9.sp,
                color = Color(0xFF94A3B8),
                maxLines = 1
            )
        }
    }
}

private fun BorderStroke(width: androidx.compose.ui.unit.Dp, color: Color) =
    androidx.compose.foundation.BorderStroke(width, color)

@Composable
private fun CircuitNodeListItem(
    node: CircuitNode,
    isInActivePath: Boolean,
    isSelected: Boolean,
    isAr: Boolean,
    pathColor: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isInActivePath) 2.dp else 1.dp,
                color = if (isInActivePath) pathColor else MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .testTag("circuit_list_item_${node.id}")
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (isInActivePath) pathColor.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getNodeIcon(node.symbolType),
                        contentDescription = null,
                        tint = if (isInActivePath) pathColor else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = node.componentTag,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = CyanPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAr) node.labelAr else node.labelEn,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${node.cabinet} • ${node.rating}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = StatusGreen.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (isAr) node.defaultStatusAr else node.defaultStatusEn,
                        fontSize = 10.sp,
                        color = StatusGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }

                if (node.relatedAlarms.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${node.relatedAlarms.size} Alarms",
                        fontSize = 10.sp,
                        color = AlertRed,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun CircuitNodeDetailDialog(
    node: CircuitNode,
    isAr: Boolean,
    onDismiss: () -> Unit,
    onQuickSearchAlarm: (String) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .border(1.dp, CyanPrimary.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .testTag("circuit_node_detail_dialog"),
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
                                imageVector = getNodeIcon(node.symbolType),
                                contentDescription = null,
                                tint = CyanPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = "Tag: ${node.componentTag}",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = if (isAr) node.labelAr else node.labelEn,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(14.dp))

                // Detail Items
                DetailInfoRow(
                    label = if (isAr) "الموقع واللوحة:" else "Cabinet / Location:",
                    value = node.cabinet
                )
                DetailInfoRow(
                    label = if (isAr) "المواصفات والقدرة:" else "Rating / Capacity:",
                    value = node.rating
                )
                DetailInfoRow(
                    label = if (isAr) "صفحة المخطط الرسمي:" else "Schematic Ref Page:",
                    value = node.schematicsPage
                )
                DetailInfoRow(
                    label = if (isAr) "الحالة المقننة للدارة:" else "Operating State:",
                    value = if (isAr) node.defaultStatusAr else node.defaultStatusEn,
                    valueColor = StatusGreen
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Technical Description
                Text(
                    text = if (isAr) "الوصف الهندسي ودور العنصر في الدارة:" else "Engineering Role in Circuit:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = CyanPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isAr) node.descriptionAr else node.descriptionEn,
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                // Related Alarms section if any
                if (node.relatedAlarms.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isAr) "أعطال وإنذارات مرتبطة بهذا العنصر:" else "Related Circuit Alarms & Faults:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AlertRed
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    node.relatedAlarms.forEach { alarmTitle ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AlertRed.copy(alpha = 0.1f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clickable { onQuickSearchAlarm(alarmTitle) }
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = AlertRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = alarmTitle,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = CyanPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isAr) "إغلاق نافذة الفحص" else "Close Inspector",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailInfoRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
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
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

private fun getNodeIcon(type: NodeSymbolType): ImageVector {
    return when (type) {
        NodeSymbolType.POWER_SOURCE, NodeSymbolType.TRANSFORMER, NodeSymbolType.THYRISTOR -> Icons.Default.Build
        NodeSymbolType.BREAKER, NodeSymbolType.CONTACTOR -> Icons.Default.Settings
        NodeSymbolType.MOTOR_PUMP -> Icons.Default.Settings
        NodeSymbolType.VALVE, NodeSymbolType.HEAT_EXCHANGER, NodeSymbolType.TANK -> Icons.Default.Info
        NodeSymbolType.VESSEL, NodeSymbolType.HEATING_ELEMENT -> Icons.Default.Build
        NodeSymbolType.SENSOR -> Icons.Default.Info
        NodeSymbolType.SAFETY_STATION -> Icons.Default.Warning
        NodeSymbolType.PISTON -> Icons.Default.Build
        NodeSymbolType.PLC_MODULE -> Icons.Default.Settings
    }
}
