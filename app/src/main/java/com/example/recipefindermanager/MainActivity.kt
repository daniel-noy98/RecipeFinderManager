package com.example.recipefindermanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.recipefindermanager.ui.AppSplashScreen
import com.example.recipefindermanager.ui.AuthGate
import com.example.recipefindermanager.ui.theme.RecipeFinderManagerTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RecipeFinderManagerTheme {
                var showSplash by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    delay(1600)
                    showSplash = false
                }

                if (showSplash) {
                    AppSplashScreen()
                } else {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        AuthGate()
                    }
                }
            }
        }
    }
}