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
fun LoginScreen(
    loading: Boolean,
    error: String?,
    onLogin: (String, String) -> Unit,
    onGoToSignUp: () -> Unit,
    onForgotPassword: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    AuthLayout(
        title = "Login",
        subtitle = "Sign in to discover and manage your recipes"
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { onForgotPassword(email.trim()) },
                enabled = !loading
            ) {
                Text("Forgot password?", color = Color(0xFF22C55E))
            }
        }

        if (error != null) {
            Spacer(Modifier.height(8.dp))
            Text(error, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))

        AuthPrimaryButton(
            text = "Sign in",
            loading = loading,
            onClick = { onLogin(email, pass) }
        )

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = onGoToSignUp,
            enabled = !loading,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("No account? Sign up", color = Color(0xFF374151))
        }
    }
}
