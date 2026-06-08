package com.example.recipefindermanager.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.recipefindermanager.R

@Composable
fun AppLogoWithName(
    modifier: Modifier = Modifier,
    logoSize: Dp = 96.dp,
    nameColor: Color = Color(0xFF2F6B73)
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.app_logo),
            contentDescription = "App logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(logoSize)
        )

        Spacer(Modifier.height(14.dp))

        Text(
            text = stringResource(R.string.app_display_name),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = nameColor,
            textAlign = TextAlign.Center
        )
    }
}
