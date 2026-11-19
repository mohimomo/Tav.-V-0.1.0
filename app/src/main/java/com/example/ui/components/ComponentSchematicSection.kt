package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Architecture
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CircuitDomain
import com.example.model.ComponentItem
import com.example.model.ComponentSchematicDetail
import com.example.model.SchematicDrawingType
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.testTag
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.FurnaceAmber

@Composable
fun ComponentSchematicSection(
    schematic: ComponentSchematicDetail,
    isArabic: Boolean,
    onNavigateToCircuit: ((CircuitDomain, String?) -> Unit)? = null
) {
    var isExpanded by remember { mutableStateOf(true) }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header: Blueprint Badge & Title & Expand/Collapse Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = CyanPrimary.copy(alpha = 0.2f),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Architecture,
                                contentDescription = null,
                                tint = CyanPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isArabic) "المخطط والرسم الهندسي المعتمد" else "Certified Engineering Drawing",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = CyanPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = FurnaceAmber.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = schematic.drawingCode,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = FurnaceAmber,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isArabic) schematic.drawingTitleAr else schematic.drawingTitleEn,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Surface(
                    onClick = { isExpanded = !isExpanded },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                    modifier = Modifier.size(30.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. Technical Blueprint Canvas Preview
                    SchematicBlueprintCanvas(schematic = schematic, isArabic = isArabic)

                    // 2. Blueprint Reference & Sheet Pages
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = FurnaceAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isArabic) "رقم الصفحة الرسمية في مخططات الفرن:" else "Official Furnace Blueprint Page:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FurnaceAmber
                                )
                                Text(
                                    text = schematic.schematicSheetPage,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // 3. Electrical Terminal Block & Wiring Loop
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Cable,
                                    contentDescription = null,
                                    tint = CyanPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isArabic) "التوصيلات الطرفية ومسار الأسلاك (Terminals):" else "Terminal Block Pinout & Wiring:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanPrimary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = schematic.terminalBlock,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // 4. PLC Address & Automation Signal Loop
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.DeveloperBoard,
                                    contentDescription = null,
                                    tint = Color(0xFF00E676),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isArabic) "عنوان الـ PLC وإشارة الأتمتة (Siemens S7-1500):" else "PLC I/O Channel & Safety Loop:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00E676)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = schematic.plcAddressTag,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // 5. Mechanical Flange & Mounting Specs
                    if (schematic.mechanicalMounting.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = if (isArabic) "المواصفات الميكانيكية وشفة التثبيت:" else "Mechanical Flange & Sealing Specs:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = schematic.mechanicalMounting,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // 6. Safety Interlocks & Permissives
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = FurnaceAmber.copy(alpha = 0.1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, FurnaceAmber.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = FurnaceAmber,
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(top = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isArabic) "شروط الأمان والتعشيق (Safety Interlocks):" else "Safety Permissive & Interlocks:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FurnaceAmber
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isArabic) schematic.interlockPermissiveAr else schematic.interlockPermissiveEn,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // 7. Signal & Fluid Flow Path
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = if (isArabic) "مسار السريان والتدفق في الدورة:" else "Signal & Flow Direction Path:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isArabic) schematic.flowDirectionDescriptionAr else schematic.flowDirectionDescriptionEn,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // 8. Key Blueprint Symbols
                    if (schematic.keySymbols.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            schematic.keySymbols.forEach { sym ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surface
                                ) {
                                    Text(
                                        text = sym,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 9. Jump to Interactive Circuit Diagram Action Button
                    if (schematic.circuitDomain != null && onNavigateToCircuit != null) {
                        Button(
                            onClick = {
                                onNavigateToCircuit(schematic.circuitDomain, schematic.circuitNodeId)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "🔍 فتح وتتبع المخطط التفاعلي الكامل في قسم الدارات" else "🔍 Open & Trace in Interactive Circuit Diagram",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * High-Contrast Engineering Schematic Blueprint Canvas
 * Generates an architectural blueprint visualization of the component's circuit node and flow.
 */
@Composable
fun SchematicBlueprintCanvas(
    schematic: ComponentSchematicDetail,
    isArabic: Boolean
) {
    val transition = rememberInfiniteTransition(label = "blueprint_pulse")
    val pulsePhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulsePhase"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF001E2E)) // Industrial Blueprint Navy Blue
            .border(1.dp, Color(0xFF004D73), RoundedCornerShape(10.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxWidth().height(130.dp)) {
            val width = size.width
            val height = size.height

            // 1. Blueprint Grid Lines (Engineering Millimeter Grid)
            val gridSpacing = 20.dp.toPx()
            var x = 0f
            while (x <= width) {
                drawLine(
                    color = Color(0xFF003852),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 0.6f
                )
                x += gridSpacing
            }
            var y = 0f
            while (y <= height) {
                drawLine(
                    color = Color(0xFF003852),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 0.6f
                )
                y += gridSpacing
            }

            // 2. Main Signal / Power / Vacuum Flow Line
            val startPoint = Offset(30.dp.toPx(), height / 2)
            val componentCenter = Offset(width / 2, height / 2)
            val endPoint = Offset(width - 30.dp.toPx(), height / 2)

            // Upstream Line
            drawLine(
                color = Color(0xFF00E5FF),
                start = startPoint,
                end = Offset(componentCenter.x - 36.dp.toPx(), height / 2),
                strokeWidth = 3f,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )

            // Downstream Line
            drawLine(
                color = Color(0xFF00E5FF),
                start = Offset(componentCenter.x + 36.dp.toPx(), height / 2),
                end = endPoint,
                strokeWidth = 3f,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )

            // Animated Moving Particles along Flow Line
            val flowX1 = startPoint.x + (componentCenter.x - 36.dp.toPx() - startPoint.x) * pulsePhase
            drawCircle(
                color = Color(0xFF00E676),
                radius = 3.5.dp.toPx(),
                center = Offset(flowX1, height / 2)
            )

            val flowX2 = (componentCenter.x + 36.dp.toPx()) + (endPoint.x - (componentCenter.x + 36.dp.toPx())) * pulsePhase
            drawCircle(
                color = Color(0xFF00E676),
                radius = 3.5.dp.toPx(),
                center = Offset(flowX2, height / 2)
            )

            // Input / Output Terminals
            drawCircle(
                color = Color(0xFF00E5FF),
                radius = 4.dp.toPx(),
                center = startPoint,
                style = Stroke(width = 2f)
            )
            drawCircle(
                color = Color(0xFF00E5FF),
                radius = 4.dp.toPx(),
                center = endPoint,
                style = Stroke(width = 2f)
            )

            // Central Component Symbol Box
            val boxWidth = 72.dp.toPx()
            val boxHeight = 52.dp.toPx()
            val boxLeft = componentCenter.x - boxWidth / 2
            val boxTop = componentCenter.y - boxHeight / 2

            drawRoundRect(
                color = Color(0xFF003852),
                topLeft = Offset(boxLeft, boxTop),
                size = androidx.compose.ui.geometry.Size(boxWidth, boxHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
            )
            drawRoundRect(
                color = Color(0xFF00E5FF),
                topLeft = Offset(boxLeft, boxTop),
                size = androidx.compose.ui.geometry.Size(boxWidth, boxHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                style = Stroke(width = 2f)
            )
        }

        // Overlay Text Inside Component Symbol Box
        Box(
            modifier = Modifier.fillMaxWidth().height(130.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = schematic.tag,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = Color(0xFF00E5FF)
                )
                Text(
                    text = schematic.pidCode,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = FurnaceAmber
                )
            }
        }

        // Left Label (Input Port / Feed)
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 6.dp)
        ) {
            Text(
                text = "IN [T1]",
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF80D8FF)
            )
        }

        // Right Label (Output Port / Drain)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 6.dp)
        ) {
            Text(
                text = "OUT [T2]",
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF80D8FF)
            )
        }

        // Top Right Drawing Tag
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = Color.Black.copy(alpha = 0.6f)
            ) {
                Text(
                    text = schematic.schematicType.titleEn,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00E676),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}

/**
 * Summary card for an engineering drawing/schematic linked directly to a furnace piece/part.
 * Used in ComponentsScreen (Schematics tab) and SearchScreen (Schematic results).
 */
@Composable
fun SchematicSummaryCard(
    schematic: ComponentSchematicDetail,
    component: ComponentItem? = null,
    isArabic: Boolean,
    onOpenComponent: ((ComponentItem) -> Unit)? = null,
    onOpenCircuit: ((CircuitDomain, String?) -> Unit)? = null,
    matchHighlight: String? = null
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("schematic_card_${schematic.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: DWG Code Badge + Drawing Type Badge + Optional Match Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = CyanPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = schematic.drawingCode,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = CyanPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (matchHighlight != null) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = FurnaceAmber.copy(alpha = 0.2f),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Text(
                                text = if (isArabic) "مطابقة: $matchHighlight" else "Match: $matchHighlight",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = FurnaceAmber,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = if (isArabic) schematic.schematicType.titleAr else schematic.schematicType.titleEn,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Drawing Title (Arabic & English)
            Text(
                text = if (isArabic) schematic.drawingTitleAr else schematic.drawingTitleEn,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (isArabic) schematic.drawingTitleEn else schematic.drawingTitleAr,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Linked Component / Equipment Section
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = FurnaceAmber.copy(alpha = 0.2f),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "⚙",
                                    fontSize = 13.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = schematic.tag,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = FurnaceAmber
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• ${schematic.pidCode}",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (component != null) {
                                Text(
                                    text = if (isArabic) component.nameAr else component.nameEn,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    if (component != null && onOpenComponent != null) {
                        OutlinedButton(
                            onClick = { onOpenComponent(component) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text(
                                text = if (isArabic) "تفاصيل القطعة" else "Part Specs",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Technical Layout Information (Sheet Page, Terminal Strip & PLC)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.15f))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isArabic) "📄 الصفحة واللوحة:" else "📄 Sheet & Cabinet:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanPrimary,
                        modifier = Modifier.width(105.dp)
                    )
                    Text(
                        text = "${schematic.schematicSheetPage} • ${schematic.cabinet}",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isArabic) "🔌 روزيتة التوصيل:" else "🔌 Terminal Block:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanPrimary,
                        modifier = Modifier.width(105.dp)
                    )
                    Text(
                        text = schematic.terminalBlock,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF00E676)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isArabic) "💻 عنوان PLC I/O:" else "💻 PLC Address:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanPrimary,
                        modifier = Modifier.width(105.dp)
                    )
                    Text(
                        text = schematic.plcAddressTag,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = FurnaceAmber
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isArabic) "🔒 شرط الأمان:" else "🔒 Safety Permissive:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyanPrimary,
                        modifier = Modifier.width(105.dp)
                    )
                    Text(
                        text = if (isArabic) schematic.interlockPermissiveAr else schematic.interlockPermissiveEn,
                        fontSize = 10.sp,
                        maxLines = 2,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Architectural Blueprint Canvas Drawing
            SchematicBlueprintCanvas(schematic = schematic, isArabic = isArabic)

            Spacer(modifier = Modifier.height(10.dp))

            // Action: Jump to Full Interactive Circuit Screen
            if (onOpenCircuit != null && schematic.circuitDomain != null) {
                val domain = schematic.circuitDomain
                Button(
                    onClick = { onOpenCircuit(domain, schematic.circuitNodeId) },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanPrimary,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth().height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Architecture,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) "فتح المخطط التفاعلي للدارة (${domain.titleAr})" else "Open Interactive Circuit (${domain.titleEn})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
