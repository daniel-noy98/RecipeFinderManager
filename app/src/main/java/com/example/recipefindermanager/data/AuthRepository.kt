package com.example.recipefindermanager.data

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

object AuthRepository {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    fun currentUid(): String? = auth.currentUser?.uid
    fun isLoggedIn(): Boolean = auth.currentUser != null

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    fun logout() {
        auth.signOut()
    }
}
