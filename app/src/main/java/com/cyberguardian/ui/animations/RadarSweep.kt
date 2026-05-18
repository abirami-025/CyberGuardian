package com.cyberguardian.ui.animations

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import com.cyberguardian.ui.theme.CyberColors
import kotlin.math.*
import kotlin.random.Random

/**
 * Infinite rotating radar sweep with gradient arc, particle trails,
 * and detected-threat blips with bloom glow.
 */
@Composable
fun RadarSweep(
    modifier: Modifier = Modifier,
    threatCount: Int = 0,
    isActive: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")

    // Main sweep rotation
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    // Pulse ring expansion
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )

    // Glow intensity pulsation
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    // Generate stable threat blip positions
    val threatPositions = remember(threatCount) {
        List(threatCount.coerceAtMost(8)) {
            Pair(
                Random.nextFloat() * 0.6f + 0.15f, // radius ratio
                Random.nextFloat() * 360f            // angle
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val maxRadius = minOf(size.width, size.height) / 2 * 0.9f

        // ── Background grid rings ──
        for (i in 1..4) {
            val ringRadius = maxRadius * (i / 4f)
            drawCircle(
                color = CyberColors.CyberCyan.copy(alpha = 0.08f),
                radius = ringRadius,
                center = center,
                style = Stroke(width = 1f)
            )
        }

        // ── Cross-hair lines ──
        val lineColor = CyberColors.CyberCyan.copy(alpha = 0.1f)
        drawLine(lineColor, Offset(center.x, center.y - maxRadius), Offset(center.x, center.y + maxRadius), 1f)
        drawLine(lineColor, Offset(center.x - maxRadius, center.y), Offset(center.x + maxRadius, center.y), 1f)

        // ── Pulse ring ──
        drawCircle(
            color = CyberColors.CyberCyan.copy(alpha = (1f - pulseRadius) * 0.3f),
            radius = maxRadius * pulseRadius,
            center = center,
            style = Stroke(width = 2f)
        )

        // ── Sweep arc with gradient ──
        if (isActive) {
            val sweepBrush = Brush.sweepGradient(
                0f to Color.Transparent,
                0.7f to Color.Transparent,
                0.85f to CyberColors.CyberCyan.copy(alpha = 0.05f),
                0.95f to CyberColors.CyberCyan.copy(alpha = glowAlpha * 0.4f),
                1f to CyberColors.CyberCyan.copy(alpha = glowAlpha),
                center = center
            )

            rotate(sweepAngle, pivot = center) {
                drawCircle(
                    brush = sweepBrush,
                    radius = maxRadius,
                    center = center
                )

                // Sweep line
                val lineEnd = Offset(
                    center.x + maxRadius * cos(0f),
                    center.y + maxRadius * sin(0f)
                )
                drawLine(
                    color = CyberColors.CyberCyan.copy(alpha = glowAlpha),
                    start = center,
                    end = lineEnd,
                    strokeWidth = 2f
                )
            }
        }

        // ── Center dot with glow ──
        drawCircle(
            color = CyberColors.CyberCyan.copy(alpha = 0.3f),
            radius = 12f,
            center = center
        )
        drawCircle(
            color = CyberColors.CyberCyan,
            radius = 5f,
            center = center
        )

        // ── Threat blips ──
        threatPositions.forEach { (radiusRatio, angle) ->
            val rad = Math.toRadians(angle.toDouble())
            val blipPos = Offset(
                center.x + maxRadius * radiusRatio * cos(rad).toFloat(),
                center.y + maxRadius * radiusRatio * sin(rad).toFloat()
            )

            // Glow
            drawCircle(
                color = CyberColors.NeonRed.copy(alpha = glowAlpha * 0.4f),
                radius = 14f,
                center = blipPos
            )
            // Core
            drawCircle(
                color = CyberColors.NeonRed.copy(alpha = 0.9f),
                radius = 5f,
                center = blipPos
            )
        }
    }
}
