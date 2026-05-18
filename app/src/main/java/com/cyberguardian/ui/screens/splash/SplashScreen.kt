package com.cyberguardian.ui.screens.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cyberguardian.ui.animations.ParticleField
import com.cyberguardian.ui.theme.CyberColors
import com.cyberguardian.ui.theme.MonoFontFamily
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen(onNavigateToDashboard: () -> Unit) {
    var showShield by remember { mutableStateOf(false) }
    var showTitle by remember { mutableStateOf(false) }
    var showTagline by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        showShield = true
        delay(800)
        showTitle = true
        delay(400)
        showTagline = true
        delay(1500)
        onNavigateToDashboard()
    }

    val shieldScale by animateFloatAsState(
        targetValue = if (showShield) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "shieldScale"
    )

    val shieldAlpha by animateFloatAsState(
        targetValue = if (showShield) 1f else 0f,
        animationSpec = tween(800),
        label = "shieldAlpha"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val shieldRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shieldRotate"
    )

    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowPulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(CyberColors.DashboardGradient)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Background particles
        ParticleField(modifier = Modifier.fillMaxSize(), isScanning = true, particleCount = 30)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated Shield Logo
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .scale(shieldScale)
                    .alpha(shieldAlpha),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.width / 2 * 0.7f

                    // Outer rotating ring
                    val ringPoints = 6
                    for (i in 0 until ringPoints) {
                        val angle = Math.toRadians((shieldRotation + i * 60.0))
                        val x = center.x + radius * 1.1f * cos(angle).toFloat()
                        val y = center.y + radius * 1.1f * sin(angle).toFloat()
                        drawCircle(
                            color = CyberColors.CyberCyan.copy(alpha = glowPulse * 0.6f),
                            radius = 4f,
                            center = Offset(x, y)
                        )
                    }

                    // Shield hexagon glow
                    drawCircle(
                        color = CyberColors.CyberCyan.copy(alpha = glowPulse * 0.1f),
                        radius = radius,
                        center = center
                    )

                    // Shield outline
                    drawCircle(
                        color = CyberColors.CyberCyan.copy(alpha = glowPulse * 0.6f),
                        radius = radius,
                        center = center,
                        style = Stroke(width = 3f, cap = StrokeCap.Round)
                    )

                    // Inner shield
                    drawCircle(
                        color = CyberColors.CyberCyan.copy(alpha = 0.15f),
                        radius = radius * 0.6f,
                        center = center
                    )
                    drawCircle(
                        color = CyberColors.CyberCyan.copy(alpha = glowPulse * 0.4f),
                        radius = radius * 0.6f,
                        center = center,
                        style = Stroke(width = 2f)
                    )

                    // Center core
                    drawCircle(
                        color = CyberColors.CyberCyan.copy(alpha = glowPulse),
                        radius = 8f,
                        center = center
                    )
                }

                // Shield icon text
                Text(
                    text = "🛡️",
                    fontSize = 48.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App name with glitch-in effect
            AnimatedVisibility(
                visible = showTitle,
                enter = fadeIn(tween(600)) + slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(600)
                )
            ) {
                Text(
                    text = "CYBERGUARDIAN",
                    fontFamily = MonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    letterSpacing = 4.sp,
                    color = CyberColors.CyberCyan
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            AnimatedVisibility(
                visible = showTagline,
                enter = fadeIn(tween(800)) + slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(600)
                )
            ) {
                Text(
                    text = "YOUR AI CYBER SHIELD",
                    fontFamily = MonoFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    letterSpacing = 3.sp,
                    color = CyberColors.TextSecondary
                )
            }
        }
    }
}
