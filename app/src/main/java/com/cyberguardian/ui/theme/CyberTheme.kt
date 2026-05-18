package com.cyberguardian.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val CyberColorScheme = darkColorScheme(
    primary = CyberColors.CyberCyan,
    onPrimary = CyberColors.DeepBlack,
    primaryContainer = CyberColors.ElectricBlue,
    secondary = CyberColors.NeonMagenta,
    onSecondary = CyberColors.DeepBlack,
    secondaryContainer = CyberColors.NeonPurple,
    tertiary = CyberColors.NeonGreen,
    background = CyberColors.DeepBlack,
    onBackground = CyberColors.TextPrimary,
    surface = CyberColors.DarkSurface,
    onSurface = CyberColors.TextPrimary,
    surfaceVariant = CyberColors.CardSurface,
    onSurfaceVariant = CyberColors.TextSecondary,
    outline = CyberColors.GlassBorder,
    error = CyberColors.NeonRed,
    onError = Color.White
)

@Composable
fun CyberGuardianTheme(content: @Composable () -> Unit) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = false
        )
    }

    MaterialTheme(
        colorScheme = CyberColorScheme,
        typography = CyberTypography,
        content = content
    )
}
