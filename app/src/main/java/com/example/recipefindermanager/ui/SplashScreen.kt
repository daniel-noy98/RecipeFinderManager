package com.example.recipefindermanager.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.recipefindermanager.ui.components.AppLogoWithName

@Composable
fun AppSplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F4F0)),
        contentAlignment = Alignment.Center
    ) {
        AppLogoWithName(logoSize = 120.dp)
    }
}
