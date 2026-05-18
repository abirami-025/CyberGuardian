package com.cyberguardian.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cyberguardian.ui.screens.advisor.AdvisorChatScreen
import com.cyberguardian.ui.screens.breach.BreachAlertScreen
import com.cyberguardian.ui.screens.dashboard.DashboardScreen
import com.cyberguardian.ui.screens.emergency.EmergencyScreen
import com.cyberguardian.ui.screens.scanner.ScannerScreen
import com.cyberguardian.ui.screens.settings.SettingsScreen
import com.cyberguardian.ui.screens.splash.SplashScreen
import com.cyberguardian.ui.screens.threats.ThreatHistoryScreen

object Routes {
    const val SPLASH = "splash"
    const val DASHBOARD = "dashboard"
    const val THREAT_HISTORY = "threat_history"
    const val ADVISOR = "advisor"
    const val SCANNER = "scanner"
    const val BREACH = "breach"
    const val EMERGENCY = "emergency"
    const val SETTINGS = "settings"
}

@Composable
fun CyberGuardianNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH,
        enterTransition = {
            fadeIn(animationSpec = tween(400)) + slideInHorizontally(
                initialOffsetX = { it / 4 },
                animationSpec = tween(400)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                targetOffsetX = { -it / 4 },
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(400)) + slideInHorizontally(
                initialOffsetX = { -it / 4 },
                animationSpec = tween(400)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                targetOffsetX = { it / 4 },
                animationSpec = tween(300)
            )
        }
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToDashboard = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                onNavigateToThreats = { navController.navigate(Routes.THREAT_HISTORY) },
                onNavigateToAdvisor = { navController.navigate(Routes.ADVISOR) },
                onNavigateToScanner = { navController.navigate(Routes.SCANNER) },
                onNavigateToBreach = { navController.navigate(Routes.BREACH) },
                onNavigateToEmergency = { navController.navigate(Routes.EMERGENCY) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(Routes.THREAT_HISTORY) {
            ThreatHistoryScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.ADVISOR) {
            AdvisorChatScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.SCANNER) {
            ScannerScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.BREACH) {
            BreachAlertScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.EMERGENCY) {
            EmergencyScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
