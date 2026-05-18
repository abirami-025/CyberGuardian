package com.cyberguardian.ui.theme

import androidx.compose.ui.graphics.Color

object CyberColors {
    // Primary Neon Palette
    val CyberCyan = Color(0xFF00F0FF)
    val NeonMagenta = Color(0xFFFF00E5)
    val ElectricBlue = Color(0xFF0066FF)
    val NeonGreen = Color(0xFF39FF14)
    val NeonPurple = Color(0xFFBF00FF)
    val NeonOrange = Color(0xFFFF6B00)
    val NeonRed = Color(0xFFFF003C)

    // Backgrounds
    val DeepBlack = Color(0xFF0A0A0F)
    val DarkSurface = Color(0xFF12121A)
    val CardSurface = Color(0xFF1A1A2E)
    val CardSurfaceLight = Color(0xFF252540)

    // Glass
    val GlassWhite = Color(0x15FFFFFF)
    val GlassBorder = Color(0x30FFFFFF)
    val GlassHighlight = Color(0x08FFFFFF)

    // Text
    val TextPrimary = Color(0xFFEEEEFF)
    val TextSecondary = Color(0xFF8888AA)
    val TextMuted = Color(0xFF555570)

    // Risk levels
    val RiskLow = NeonGreen
    val RiskMedium = Color(0xFFFFD700)
    val RiskHigh = NeonOrange
    val RiskBlocked = NeonRed

    // Gradients
    val CyanToBlue = listOf(CyberCyan, ElectricBlue)
    val MagentaToPurple = listOf(NeonMagenta, NeonPurple)
    val GreenToCyan = listOf(NeonGreen, CyberCyan)
    val RedToMagenta = listOf(NeonRed, NeonMagenta)
    val DashboardGradient = listOf(
        Color(0xFF0A0A0F),
        Color(0xFF0D0D1A),
        Color(0xFF101025)
    )
}
