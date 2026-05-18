package com.cyberguardian

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.cyberguardian.navigation.CyberGuardianNavHost
import com.cyberguardian.ui.theme.CyberColors
import com.cyberguardian.ui.theme.CyberGuardianTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CyberGuardianTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CyberColors.DeepBlack
                ) {
                    CyberGuardianNavHost()
                }
            }
        }
    }
}
