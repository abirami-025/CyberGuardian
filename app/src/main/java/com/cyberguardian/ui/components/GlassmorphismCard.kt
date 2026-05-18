package com.cyberguardian.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cyberguardian.ui.theme.CyberColors

/**
 * Glassmorphism card with animated neon border glow,
 * translucent background, and chromatic edge effect.
 */
@Composable
fun GlassmorphismCard(
    modifier: Modifier = Modifier,
    glowColor: Color = CyberColors.CyberCyan,
    cornerRadius: Dp = 20.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glassGlow")

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gradientShift"
    )

    val shape = RoundedCornerShape(cornerRadius)

    val borderBrush = Brush.linearGradient(
        colors = listOf(
            glowColor.copy(alpha = glowAlpha),
            glowColor.copy(alpha = glowAlpha * 0.3f),
            CyberColors.NeonMagenta.copy(alpha = glowAlpha * 0.2f),
            glowColor.copy(alpha = glowAlpha * 0.5f)
        ),
        start = Offset(gradientOffset * 500f, 0f),
        end = Offset(gradientOffset * 500f + 200f, 300f)
    )

    val baseModifier = modifier
        .clip(shape)
        .background(
            Brush.verticalGradient(
                colors = listOf(
                    CyberColors.CardSurface.copy(alpha = 0.85f),
                    CyberColors.CardSurface.copy(alpha = 0.6f)
                )
            ),
            shape
        )
        .border(width = 1.dp, brush = borderBrush, shape = shape)

    val clickModifier = if (onClick != null) {
        baseModifier.clickable(
            indication = null,
            interactionSource = remember { MutableInteractionSource() }
        ) { onClick() }
    } else {
        baseModifier
    }

    Box(
        modifier = clickModifier.padding(16.dp),
        content = content
    )
}

/**
 * Animated neon button with inward ripple, scale bounce, and glow.
 */
@Composable
fun NeonButton(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = CyberColors.CyberCyan,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "buttonScale"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "btnGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "btnGlowAlpha"
    )

    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .scale(scale)
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        color.copy(alpha = 0.2f),
                        color.copy(alpha = 0.1f)
                    )
                ),
                shape
            )
            .border(1.dp, color.copy(alpha = glowAlpha), shape)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                isPressed = true
                onClick()
            }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.material3.Text(
            text = text.uppercase(),
            color = color,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            fontSize = androidx.compose.ui.unit.sp(14),
            letterSpacing = androidx.compose.ui.unit.sp(2)
        )
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(150)
            isPressed = false
        }
    }
}

/**
 * Animated stat card for dashboard quick stats
 */
@Composable
fun StatCard(
    label: String,
    value: String,
    icon: String,
    glowColor: Color = CyberColors.CyberCyan,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    GlassmorphismCard(
        modifier = modifier,
        glowColor = glowColor,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            androidx.compose.material3.Text(
                text = icon,
                fontSize = androidx.compose.ui.unit.sp(28)
            )
            Spacer(modifier = Modifier.height(8.dp))
            androidx.compose.material3.Text(
                text = value,
                color = glowColor,
                fontFamily = com.cyberguardian.ui.theme.MonoFontFamily,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                fontSize = androidx.compose.ui.unit.sp(22)
            )
            Spacer(modifier = Modifier.height(4.dp))
            androidx.compose.material3.Text(
                text = label,
                color = CyberColors.TextSecondary,
                fontSize = androidx.compose.ui.unit.sp(11),
                letterSpacing = androidx.compose.ui.unit.sp(1)
            )
        }
    }
}
