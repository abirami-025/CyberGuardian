package com.cyberguardian.ui.animations

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberguardian.domain.model.RiskLevel
import com.cyberguardian.ui.theme.CyberColors
import com.cyberguardian.ui.theme.MonoFontFamily
import kotlin.math.*

/**
 * Pulsating neon risk gauge with smooth segment fill,
 * orbiting data nodes, color gradients, and spark emission.
 */
@Composable
fun RiskMeter(
    riskLevel: RiskLevel,
    modifier: Modifier = Modifier
) {
    val targetProgress = riskLevel.score / 100f

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "riskProgress"
    )

    val riskColor by animateColorAsState(
        targetValue = when (riskLevel) {
            RiskLevel.SAFE -> CyberColors.NeonGreen
            RiskLevel.LOW -> CyberColors.CyberCyan
            RiskLevel.MEDIUM -> CyberColors.RiskMedium
            RiskLevel.HIGH -> CyberColors.NeonOrange
            RiskLevel.BLOCKED -> CyberColors.NeonRed
        },
        animationSpec = tween(600),
        label = "riskColor"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "riskMeter")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (riskLevel) {
                    RiskLevel.BLOCKED -> 400
                    RiskLevel.HIGH -> 600
                    RiskLevel.MEDIUM -> 1000
                    else -> 2000
                },
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val orbitAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = when (riskLevel) {
                    RiskLevel.BLOCKED -> 1500
                    RiskLevel.HIGH -> 2500
                    else -> 5000
                },
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "orbit"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = minOf(size.width, size.height) / 2 * 0.8f
            val strokeWidth = 16f

            // Background arc
            drawArc(
                color = CyberColors.CardSurface,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Filled arc with glow
            val arcBrush = Brush.sweepGradient(
                colors = listOf(
                    riskColor.copy(alpha = 0.3f),
                    riskColor.copy(alpha = glowAlpha),
                    riskColor
                ),
                center = center
            )

            drawArc(
                brush = arcBrush,
                startAngle = 135f,
                sweepAngle = 270f * animatedProgress,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth * pulseScale, cap = StrokeCap.Round)
            )

            // Outer glow ring
            drawArc(
                color = riskColor.copy(alpha = glowAlpha * 0.2f),
                startAngle = 135f,
                sweepAngle = 270f * animatedProgress,
                useCenter = false,
                topLeft = Offset(center.x - radius - 4f, center.y - radius - 4f),
                size = Size(radius * 2 + 8f, radius * 2 + 8f),
                style = Stroke(width = strokeWidth + 8f, cap = StrokeCap.Round)
            )

            // Orbiting data nodes
            val nodeCount = 3
            for (i in 0 until nodeCount) {
                val angle = Math.toRadians((orbitAngle + i * (360f / nodeCount)).toDouble())
                val nodeRadius = radius + 25f
                val nodePos = Offset(
                    center.x + nodeRadius * cos(angle).toFloat(),
                    center.y + nodeRadius * sin(angle).toFloat()
                )
                // Glow
                drawCircle(
                    color = riskColor.copy(alpha = 0.3f),
                    radius = 8f,
                    center = nodePos
                )
                // Core
                drawCircle(
                    color = riskColor,
                    radius = 3f,
                    center = nodePos
                )
            }

            // Center glow
            drawCircle(
                color = riskColor.copy(alpha = glowAlpha * 0.15f),
                radius = radius * 0.5f,
                center = center
            )
        }

        // Risk level text
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(animatedProgress * 100).toInt()}",
                fontFamily = MonoFontFamily,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = riskColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = riskLevel.label.uppercase(),
                fontFamily = MonoFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = riskColor.copy(alpha = 0.8f),
                letterSpacing = 2.sp
            )
        }
    }
}
