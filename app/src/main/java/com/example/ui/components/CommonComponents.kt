package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

// 1. Beautiful Gradient Background
@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            DarkBackground,
            Color(0xFF14141E), // Sophisticated deep dark slate mid
            DarkBackground
        )
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradientBrush),
        content = content
    )
}

// 2. Glassmorphic Card Container
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = Color.White.copy(alpha = 0.05f),
    backgroundColor: Color = DarkSurface.copy(alpha = 0.85f),
    elevation: Dp = 0.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    var modifierWithClick = modifier
        .clip(RoundedCornerShape(24.dp))
        .background(backgroundColor)
        .border(1.dp, borderColor, RoundedCornerShape(24.dp))

    if (onClick != null) {
        modifierWithClick = modifierWithClick.clickable(onClick = onClick)
    }

    Card(
        modifier = modifierWithClick,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

// 3. Stat Card
@Composable
fun AnimatedStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    GlassCard(
        modifier = modifier.fillMaxWidth(),
        borderColor = accentColor.copy(alpha = 0.25f),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSilver
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextWhite
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

// 4. Beautiful Custom Canvas Line/Area Sales Graph
@Composable
fun SalesLineChart(
    salesData: List<Double>, // Monthly sales list, e.g. [1200, 2400, 1800, 4200, ...]
    months: List<String>,    // e.g. ["Jan", "Feb", "Mar", ...]
    modifier: Modifier = Modifier,
    accentColor: Color = ElectricBlue,
    secondaryColor: Color = NeonPurple
) {
    if (salesData.isEmpty()) return

    val maxVal = (salesData.maxOrNull() ?: 1.0).coerceAtLeast(1.0)

    GlassCard(modifier = modifier) {
        Text(
            text = "Monthly Performance Analytics",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextWhite
        )
        Text(
            text = "Aggregated sales trend from records",
            style = MaterialTheme.typography.bodySmall,
            color = TextSilver
        )
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val paddingLeft = 50f
                val paddingBottom = 40f
                val chartWidth = width - paddingLeft
                val chartHeight = height - paddingBottom

                // Draw background grid lines
                val gridLines = 4
                for (i in 0..gridLines) {
                    val y = chartHeight - (chartHeight / gridLines) * i
                    drawLine(
                        color = Color.White.copy(alpha = 0.05f),
                        start = Offset(paddingLeft, y),
                        end = Offset(width, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Draw path points
                val stepX = chartWidth / (salesData.size - 1).coerceAtLeast(1)
                val points = salesData.mapIndexed { index, value ->
                    val x = paddingLeft + index * stepX
                    val ratio = value / maxVal
                    val y = chartHeight - (chartHeight * ratio * 0.85).toFloat()
                    Offset(x, y)
                }

                // Draw filled gradient under the path
                val fillPath = Path().apply {
                    moveTo(paddingLeft, chartHeight)
                    points.forEach { lineTo(it.x, it.y) }
                    lineTo(paddingLeft + (salesData.size - 1) * stepX, chartHeight)
                    close()
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(accentColor.copy(alpha = 0.25f), Color.Transparent)
                    )
                )

                // Draw actual sleek path line
                val strokePath = Path().apply {
                    points.forEachIndexed { index, point ->
                        if (index == 0) moveTo(point.x, point.y) else lineTo(point.x, point.y)
                    }
                }

                drawPath(
                    path = strokePath,
                    brush = Brush.horizontalGradient(
                        colors = listOf(accentColor, secondaryColor)
                    ),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw interactive dots
                points.forEach { point ->
                    drawCircle(
                        color = Color.White,
                        radius = 4.dp.toPx(),
                        center = point
                    )
                    drawCircle(
                        color = accentColor,
                        radius = 6.dp.toPx(),
                        center = point,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        // Month Labels Row
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            months.forEach { month ->
                Text(
                    text = month,
                    fontSize = 11.sp,
                    color = TextSilver,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// 5. Loading Skeleton Placeholder
@Composable
fun LoadingSkeleton(
    modifier: Modifier = Modifier,
    height: Dp = 64.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Skeleton")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInCirc),
            repeatMode = RepeatMode.Reverse
        ),
        label = "SkeletonAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceVariant.copy(alpha = alpha))
    )
}

// 6. Styled Empty State
@Composable
fun EmptyState(
    message: String,
    tip: String = "Try expanding filters or add a new record.",
    icon: ImageVector = Icons.Default.Info,
    modifier: Modifier = Modifier,
    actionButton: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(ElectricBlue.copy(alpha = 0.12f), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ElectricBlue,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = TextWhite,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = tip,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSilver,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        if (actionButton != null) {
            Spacer(modifier = Modifier.height(16.dp))
            actionButton()
        }
    }
}

// 7. Glowing Gradient Floating Action Button / Button
@Composable
fun CustomGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(ElectricBlue, NeonPurple),
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(),
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.horizontalGradient(colors),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            color = Color.White
        )
    }
}
