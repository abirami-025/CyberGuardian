package com.cyberguardian.ui.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cyberguardian.domain.model.RiskLevel
import com.cyberguardian.domain.model.Threat
import com.cyberguardian.ui.animations.ParticleField
import com.cyberguardian.ui.animations.RadarSweep
import com.cyberguardian.ui.animations.RiskMeter
import com.cyberguardian.ui.components.GlassmorphismCard
import com.cyberguardian.ui.components.NeonButton
import com.cyberguardian.ui.components.StatCard
import com.cyberguardian.ui.theme.CyberColors
import com.cyberguardian.ui.theme.MonoFontFamily

@Composable
fun DashboardScreen(
    onNavigateToThreats: () -> Unit,
    onNavigateToAdvisor: () -> Unit,
    onNavigateToScanner: () -> Unit,
    onNavigateToBreach: () -> Unit,
    onNavigateToEmergency: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val stats by viewModel.stats.collectAsState()
    val recentThreats by viewModel.recentThreats.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(CyberColors.DashboardGradient))
        )

        // Particle field background
        ParticleField(
            modifier = Modifier.fillMaxSize(),
            isScanning = isScanning,
            particleCount = 25
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 48.dp, bottom = 24.dp)
        ) {
            // ── Header ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CYBERGUARDIAN",
                        fontFamily = MonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        letterSpacing = 3.sp,
                        color = CyberColors.CyberCyan
                    )
                    Text(
                        text = if (stats.isGuardianActive) "● GUARDIAN ACTIVE" else "○ GUARDIAN PAUSED",
                        fontFamily = MonoFontFamily,
                        fontSize = 10.sp,
                        letterSpacing = 2.sp,
                        color = if (stats.isGuardianActive) CyberColors.NeonGreen else CyberColors.TextMuted
                    )
                }
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = CyberColors.TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Radar + Risk Meter Hero Section ──
            GlassmorphismCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(280.dp),
                glowColor = when (stats.currentRiskLevel) {
                    RiskLevel.SAFE, RiskLevel.LOW -> CyberColors.CyberCyan
                    RiskLevel.MEDIUM -> CyberColors.RiskMedium
                    else -> CyberColors.NeonRed
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Radar
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        RadarSweep(
                            modifier = Modifier.fillMaxSize(),
                            threatCount = recentThreats.count { !it.isNeutralized },
                            isActive = stats.isGuardianActive
                        )
                    }

                    // Risk meter
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(8.dp)
                    ) {
                        RiskMeter(
                            riskLevel = stats.currentRiskLevel,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Quick Stats Row ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = "BLOCKED",
                    value = "${stats.threatsBlocked}",
                    icon = "🛡️",
                    glowColor = CyberColors.NeonGreen,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToThreats
                )
                StatCard(
                    label = "SCANNED",
                    value = "${stats.messagesScanned}",
                    icon = "📨",
                    glowColor = CyberColors.CyberCyan,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "TRUST",
                    value = "${stats.trustScore.toInt()}%",
                    icon = "⭐",
                    glowColor = CyberColors.NeonMagenta,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Quick Scan Button ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                NeonButton(
                    text = if (isScanning) "⟳ Scanning…" else "⚡ Quick Scan",
                    color = if (isScanning) CyberColors.NeonMagenta else CyberColors.CyberCyan,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { viewModel.toggleScanning() }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Feature Grid ──
            Text(
                text = "PROTECTION MODULES",
                fontFamily = MonoFontFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                letterSpacing = 2.sp,
                color = CyberColors.TextSecondary,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    FeatureCard("SMS\nShield", "💬", CyberColors.CyberCyan, onClick = onNavigateToScanner)
                }
                item {
                    FeatureCard("Call\nGuard", "📞", CyberColors.NeonGreen, onClick = onNavigateToScanner)
                }
                item {
                    FeatureCard("QR\nScanner", "📱", CyberColors.ElectricBlue, onClick = onNavigateToScanner)
                }
                item {
                    FeatureCard("AI\nAdvisor", "🤖", CyberColors.NeonMagenta, onClick = onNavigateToAdvisor)
                }
                item {
                    FeatureCard("Breach\nCheck", "🔍", CyberColors.NeonOrange, onClick = onNavigateToBreach)
                }
                item {
                    FeatureCard("Emergency\nActions", "🆘", CyberColors.NeonRed, onClick = onNavigateToEmergency)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Recent Threats ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECENT ACTIVITY",
                    fontFamily = MonoFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    letterSpacing = 2.sp,
                    color = CyberColors.TextSecondary
                )
                TextButton(onClick = onNavigateToThreats) {
                    Text(
                        text = "VIEW ALL →",
                        fontFamily = MonoFontFamily,
                        fontSize = 10.sp,
                        letterSpacing = 1.sp,
                        color = CyberColors.CyberCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            recentThreats.take(3).forEachIndexed { index, threat ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(400, delayMillis = index * 150)) +
                            slideInVertically(
                                initialOffsetY = { it / 2 },
                                animationSpec = tween(400, delayMillis = index * 150)
                            )
                ) {
                    ThreatCard(
                        threat = threat,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun FeatureCard(
    label: String,
    icon: String,
    glowColor: Color,
    onClick: () -> Unit
) {
    GlassmorphismCard(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        glowColor = glowColor,
        cornerRadius = 16.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = label,
                fontFamily = MonoFontFamily,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = CyberColors.TextPrimary,
                letterSpacing = 0.5.sp,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun ThreatCard(
    threat: Threat,
    modifier: Modifier = Modifier
) {
    val riskColor = when (threat.riskLevel) {
        RiskLevel.SAFE -> CyberColors.NeonGreen
        RiskLevel.LOW -> CyberColors.CyberCyan
        RiskLevel.MEDIUM -> CyberColors.RiskMedium
        RiskLevel.HIGH -> CyberColors.NeonOrange
        RiskLevel.BLOCKED -> CyberColors.NeonRed
    }

    GlassmorphismCard(
        modifier = modifier.fillMaxWidth(),
        glowColor = riskColor,
        cornerRadius = 14.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Text(text = threat.type.icon, fontSize = 32.sp)

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = threat.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = CyberColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = threat.description,
                    fontSize = 12.sp,
                    color = CyberColors.TextSecondary,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Status badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(riskColor.copy(alpha = 0.15f))
                    .border(1.dp, riskColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (threat.isNeutralized) "SAFE" else threat.riskLevel.label.uppercase(),
                    fontFamily = MonoFontFamily,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (threat.isNeutralized) CyberColors.NeonGreen else riskColor,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
