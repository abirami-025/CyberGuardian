package com.cyberguardian.ui.animations

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.cyberguardian.ui.theme.CyberColors
import kotlin.math.*
import kotlin.random.Random

data class Particle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var alpha: Float,
    var radius: Float,
    var color: Color,
    var life: Float = 1f,
    var decay: Float = 0.005f
)

/**
 * Always-on background particle field with floating code particles
 * and energy streams that intensify during scans.
 */
@Composable
fun ParticleField(
    modifier: Modifier = Modifier,
    isScanning: Boolean = false,
    particleCount: Int = 40
) {
    val particles = remember {
        mutableStateListOf<Particle>().apply {
            repeat(particleCount) {
                add(createParticle(800f, 1600f))
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "particleField")
    val tick by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(16, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "tick"
    )

    // Use tick to drive particle updates implicitly
    @Suppress("UNUSED_VARIABLE")
    val ignored = tick

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        particles.forEachIndexed { index, particle ->
            // Update position
            val speedMult = if (isScanning) 3f else 1f
            particle.x += particle.vx * speedMult
            particle.y += particle.vy * speedMult
            particle.life -= particle.decay

            // Respawn if dead or out of bounds
            if (particle.life <= 0f || particle.x < -20f || particle.x > w + 20f ||
                particle.y < -20f || particle.y > h + 20f) {
                val newP = createParticle(w, h)
                particle.x = newP.x
                particle.y = newP.y
                particle.vx = newP.vx
                particle.vy = newP.vy
                particle.alpha = newP.alpha
                particle.radius = newP.radius
                particle.color = newP.color
                particle.life = 1f
            }

            val currentAlpha = (particle.alpha * particle.life).coerceIn(0f, 1f)

            // Glow
            drawCircle(
                color = particle.color.copy(alpha = currentAlpha * 0.3f),
                radius = particle.radius * 3f,
                center = Offset(particle.x, particle.y)
            )
            // Core
            drawCircle(
                color = particle.color.copy(alpha = currentAlpha),
                radius = particle.radius,
                center = Offset(particle.x, particle.y)
            )
        }

        // Connection lines between nearby particles
        for (i in particles.indices) {
            for (j in i + 1 until particles.size) {
                val dx = particles[i].x - particles[j].x
                val dy = particles[i].y - particles[j].y
                val dist = sqrt(dx * dx + dy * dy)
                if (dist < 120f) {
                    val lineAlpha = ((1f - dist / 120f) * 0.15f).coerceIn(0f, 1f)
                    drawLine(
                        color = CyberColors.CyberCyan.copy(alpha = lineAlpha),
                        start = Offset(particles[i].x, particles[i].y),
                        end = Offset(particles[j].x, particles[j].y),
                        strokeWidth = 0.5f
                    )
                }
            }
        }
    }
}

private fun createParticle(maxW: Float, maxH: Float): Particle {
    val colors = listOf(
        CyberColors.CyberCyan,
        CyberColors.ElectricBlue,
        CyberColors.NeonMagenta,
        CyberColors.NeonPurple,
        CyberColors.NeonGreen
    )
    return Particle(
        x = Random.nextFloat() * maxW,
        y = Random.nextFloat() * maxH,
        vx = (Random.nextFloat() - 0.5f) * 0.8f,
        vy = -Random.nextFloat() * 0.6f - 0.1f,  // Mostly upward drift
        alpha = Random.nextFloat() * 0.5f + 0.1f,
        radius = Random.nextFloat() * 2f + 0.5f,
        color = colors.random(),
        decay = Random.nextFloat() * 0.003f + 0.001f
    )
}

/**
 * Explosion particle burst effect for threat detection.
 * Short-lived burst, then particles fade out.
 */
@Composable
fun ThreatBurstEffect(
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    burstColor: Color = CyberColors.NeonRed
) {
    var burstParticles by remember { mutableStateOf<List<Particle>>(emptyList()) }

    LaunchedEffect(isActive) {
        if (isActive) {
            burstParticles = List(60) {
                val angle = Random.nextFloat() * 2f * PI.toFloat()
                val speed = Random.nextFloat() * 6f + 2f
                Particle(
                    x = 0f, y = 0f,
                    vx = cos(angle) * speed,
                    vy = sin(angle) * speed,
                    alpha = 1f,
                    radius = Random.nextFloat() * 3f + 1f,
                    color = listOf(burstColor, CyberColors.NeonMagenta, CyberColors.CyberCyan).random(),
                    decay = Random.nextFloat() * 0.015f + 0.01f
                )
            }
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "burst")
    val tick by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(16, easing = LinearEasing), RepeatMode.Restart),
        label = "burstTick"
    )

    @Suppress("UNUSED_VARIABLE")
    val ignored = tick

    Canvas(modifier = modifier.fillMaxSize()) {
        val cx = size.width / 2
        val cy = size.height / 2

        burstParticles.forEach { p ->
            p.x += p.vx
            p.y += p.vy
            p.life -= p.decay
            p.vx *= 0.98f
            p.vy *= 0.98f

            if (p.life > 0f) {
                val a = (p.alpha * p.life).coerceIn(0f, 1f)
                drawCircle(
                    color = p.color.copy(alpha = a * 0.4f),
                    radius = p.radius * 4f,
                    center = Offset(cx + p.x, cy + p.y)
                )
                drawCircle(
                    color = p.color.copy(alpha = a),
                    radius = p.radius,
                    center = Offset(cx + p.x, cy + p.y)
                )
            }
        }
    }
}
