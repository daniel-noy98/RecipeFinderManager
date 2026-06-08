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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun SignUpScreen(
    loading: Boolean,
    error: String?,
    onSignUp: (String, String) -> Unit,
    onGoToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    AuthLayout(
        title = "Create account",
        subtitle = "Join Recipe Finder & Manager and start sharing recipes"
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

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        if (error != null) {
            Spacer(Modifier.height(12.dp))
            Text(error, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))

        AuthPrimaryButton(
            text = "Create account",
            loading = loading,
            onClick = { onSignUp(email, pass) }
        )

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = onGoToLogin,
            enabled = !loading,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Already have an account? Login", color = Color(0xFF374151))
        }
    }
}
