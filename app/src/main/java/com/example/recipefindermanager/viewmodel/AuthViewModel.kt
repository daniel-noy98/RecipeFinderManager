package com.example.recipefindermanager.viewmodel

import androidx.lifecycle.ViewModel
import com.example.recipefindermanager.data.FavoritesStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {

    enum class Mode { LOGIN, SIGNUP, FORGOT_PASSWORD }

    private val auth = FirebaseAuth.getInstance()

    private val _user = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val user: StateFlow<FirebaseUser?> = _user

    private val _mode = MutableStateFlow(Mode.LOGIN)
    val mode: StateFlow<Mode> = _mode

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _infoMessage = MutableStateFlow<String?>(null)
    val infoMessage: StateFlow<String?> = _infoMessage

    private val _resetEmail = MutableStateFlow("")
    val resetEmail: StateFlow<String> = _resetEmail

    fun goToSignUp() {
        clearMessages()
        _mode.value = Mode.SIGNUP
    }

    fun goToLogin() {
        clearMessages()
        _mode.value = Mode.LOGIN
    }

    fun goToForgotPassword(email: String = "") {
        clearMessages()
        _resetEmail.value = email
        _mode.value = Mode.FORGOT_PASSWORD
    }

    private fun clearMessages() {
        _error.value = null
        _infoMessage.value = null
    }

    fun login(email: String, password: String) {
        _loading.value = true
        _error.value = null
        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { _user.value = auth.currentUser }
            .addOnFailureListener { e -> _error.value = e.message ?: "Login failed" }
            .addOnCompleteListener { _loading.value = false }
    }

    fun signUp(email: String, password: String) {
        _loading.value = true
        _error.value = null
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnSuccessListener { _user.value = auth.currentUser }
            .addOnFailureListener { e -> _error.value = e.message ?: "Sign up failed" }
            .addOnCompleteListener { _loading.value = false }
    }

    fun resetPassword(email: String) {
        val trimmed = email.trim()
        if (trimmed.isBlank()) {
            _error.value = "Please enter your email"
            return
        }

        _loading.value = true
        _error.value = null
        _infoMessage.value = null

        auth.sendPasswordResetEmail(trimmed)
            .addOnSuccessListener {
                _infoMessage.value = "Password reset email sent. Check your inbox."
            }
            .addOnFailureListener { e ->
                _error.value = e.message ?: "Could not send reset email"
            }
            .addOnCompleteListener { _loading.value = false }
    }

    fun logout() {
        auth.signOut()
        FavoritesStore.clear()
        _user.value = null
        clearMessages()
        _mode.value = Mode.LOGIN
    }
}
