package com.cyberguardian.ui.screens.threats

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cyberguardian.domain.model.RiskLevel
import com.cyberguardian.domain.model.Threat
import com.cyberguardian.ui.animations.ParticleField
import com.cyberguardian.ui.components.GlassmorphismCard
import com.cyberguardian.ui.theme.CyberColors
import com.cyberguardian.ui.theme.MonoFontFamily
import com.cyberguardian.ui.screens.dashboard.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreatHistoryScreen(
    onBack: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val threats by viewModel.recentThreats.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(CyberColors.DashboardGradient))
        )
        ParticleField(modifier = Modifier.fillMaxSize(), particleCount = 15)

        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            TopAppBar(
                title = {
                    Text(
                        "THREAT HISTORY",
                        fontFamily = MonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = CyberColors.CyberCyan
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = CyberColors.TextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CyberColors.DeepBlack.copy(alpha = 0.8f)
                )
            )

            // Summary bar
            GlassmorphismCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                glowColor = CyberColors.NeonGreen
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${threats.size}", fontFamily = MonoFontFamily, fontWeight = FontWeight.Bold,
                            fontSize = 24.sp, color = CyberColors.CyberCyan)
                        Text("TOTAL", fontFamily = MonoFontFamily, fontSize = 9.sp,
                            color = CyberColors.TextMuted, letterSpacing = 1.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${threats.count { it.isNeutralized }}", fontFamily = MonoFontFamily,
                            fontWeight = FontWeight.Bold, fontSize = 24.sp, color = CyberColors.NeonGreen)
                        Text("BLOCKED", fontFamily = MonoFontFamily, fontSize = 9.sp,
                            color = CyberColors.TextMuted, letterSpacing = 1.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${threats.count { it.riskLevel == RiskLevel.HIGH || it.riskLevel == RiskLevel.BLOCKED }}",
                            fontFamily = MonoFontFamily, fontWeight = FontWeight.Bold,
                            fontSize = 24.sp, color = CyberColors.NeonRed)
                        Text("CRITICAL", fontFamily = MonoFontFamily, fontSize = 9.sp,
                            color = CyberColors.TextMuted, letterSpacing = 1.sp)
                    }
                }
            }

            // Threat timeline
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(threats) { index, threat ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(400, delayMillis = index * 100)) +
                                slideInHorizontally(
                                    initialOffsetX = { it / 3 },
                                    animationSpec = tween(400, delayMillis = index * 100)
                                )
                    ) {
                        ThreatTimelineCard(threat = threat)
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun ThreatTimelineCard(threat: Threat) {
    val riskColor = when (threat.riskLevel) {
        RiskLevel.SAFE -> CyberColors.NeonGreen
        RiskLevel.LOW -> CyberColors.CyberCyan
        RiskLevel.MEDIUM -> CyberColors.RiskMedium
        RiskLevel.HIGH -> CyberColors.NeonOrange
        RiskLevel.BLOCKED -> CyberColors.NeonRed
    }

    var expanded by remember { mutableStateOf(false) }

    GlassmorphismCard(
        modifier = Modifier.fillMaxWidth(),
        glowColor = riskColor,
        cornerRadius = 16.dp,
        onClick = { expanded = !expanded }
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = threat.type.icon, fontSize = 36.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(threat.title, fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp, color = CyberColors.TextPrimary)
                    Text(threat.type.displayName, fontSize = 11.sp,
                        color = CyberColors.TextSecondary)
                    Text(
                        formatTime(threat.timestamp),
                        fontFamily = MonoFontFamily, fontSize = 10.sp,
                        color = CyberColors.TextMuted
                    )
                }
                // Risk badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(riskColor.copy(alpha = 0.15f))
                        .border(1.dp, riskColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        threat.riskLevel.label.uppercase(),
                        fontFamily = MonoFontFamily, fontSize = 9.sp,
                        fontWeight = FontWeight.Bold, color = riskColor,
                        letterSpacing = 1.sp
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Divider(color = CyberColors.GlassBorder, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("EXPLANATION", fontFamily = MonoFontFamily, fontSize = 10.sp,
                        letterSpacing = 1.sp, color = CyberColors.CyberCyan)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(threat.explanation, fontSize = 13.sp,
                        color = CyberColors.TextSecondary, lineHeight = 18.sp)

                    threat.senderInfo?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("SOURCE: $it", fontFamily = MonoFontFamily, fontSize = 10.sp,
                            color = CyberColors.TextMuted)
                    }

                    if (threat.isNeutralized) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "✅ NEUTRALIZED — You're protected",
                            fontFamily = MonoFontFamily, fontSize = 11.sp,
                            fontWeight = FontWeight.Bold, color = CyberColors.NeonGreen,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 3600000 -> "${diff / 60000}m ago"
        diff < 86400000 -> "${diff / 3600000}h ago"
        diff < 604800000 -> "${diff / 86400000}d ago"
        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date(timestamp))
    }
}
