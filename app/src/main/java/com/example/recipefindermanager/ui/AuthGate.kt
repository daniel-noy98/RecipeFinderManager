package com.example.recipefindermanager.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recipefindermanager.ui.auth.ForgotPasswordScreen
import com.example.recipefindermanager.ui.auth.LoginScreen
import com.example.recipefindermanager.ui.auth.SignUpScreen
import com.example.recipefindermanager.viewmodel.AuthViewModel

@Composable
fun AuthGate(vm: AuthViewModel = viewModel()) {
    val user by vm.user.collectAsState()
    val mode by vm.mode.collectAsState()

    if (user == null) {
        when (mode) {
            AuthViewModel.Mode.LOGIN -> LoginScreen(
                loading = vm.loading.collectAsState().value,
                error = vm.error.collectAsState().value,
                onLogin = { email, pass -> vm.login(email, pass) },
                onGoToSignUp = { vm.goToSignUp() },
                onForgotPassword = { email -> vm.goToForgotPassword(email) }
            )
            AuthViewModel.Mode.FORGOT_PASSWORD -> ForgotPasswordScreen(
                initialEmail = vm.resetEmail.collectAsState().value,
                loading = vm.loading.collectAsState().value,
                error = vm.error.collectAsState().value,
                infoMessage = vm.infoMessage.collectAsState().value,
                onSendReset = { email -> vm.resetPassword(email) },
                onBackToLogin = { vm.goToLogin() }
            )
            AuthViewModel.Mode.SIGNUP -> SignUpScreen(
                loading = vm.loading.collectAsState().value,
                error = vm.error.collectAsState().value,
                onSignUp = { email, pass -> vm.signUp(email, pass) },
                onGoToLogin = { vm.goToLogin() }
            )
        }
    } else {
        AppRoot(
            onLogout = { vm.logout() }
        )
    }
}
