package com.example.recipefindermanager.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun ForgotPasswordScreen(
    initialEmail: String,
    loading: Boolean,
    error: String?,
    infoMessage: String?,
    onSendReset: (String) -> Unit,
    onBackToLogin: () -> Unit
) {
    var email by remember(initialEmail) { mutableStateOf(initialEmail) }

    AuthLayout(
        title = "Reset password",
        subtitle = "Enter your email and we'll send you a reset link"
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        if (infoMessage != null) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = infoMessage,
                color = Color(0xFF15803D),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (error != null) {
            Spacer(Modifier.height(12.dp))
            Text(error, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))

        AuthPrimaryButton(
            text = "Send reset link",
            loading = loading,
            onClick = { onSendReset(email) }
        )

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = onBackToLogin,
            enabled = !loading,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Back to login", color = Color(0xFF374151))
        }
    }
}
