package com.camihruiz24.android_firebase_app.utils

import android.content.Context
import com.camihruiz24.android_firebase_app.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.security.auth.login.LoginException

sealed interface AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>
    data class Error(val errorMessage: String) : AuthResult<Nothing>
}

class AuthenticationManager(val context: Context) {
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    // FirebaseUser es un contenedor del usuario que se autentica sin importar el proveedor de autenticación
    suspend fun signInAnonymously(): AuthResult<FirebaseUser> =
        try {
            auth.signInAnonymously().await().let { result ->
                AuthResult.Success(result.user ?: throw LoginException(context.getString(R.string.login_error)))
            }
        } catch (e: LoginException) {
            AuthResult.Error(e.message ?: context.getString(R.string.login_error))
        }

    suspend fun createUserWithEmailAndPassword(email: String, password: String,): AuthResult<FirebaseUser?> =
        try {
            auth.createUserWithEmailAndPassword(email, password).await().let {authResult ->
                AuthResult.Success(authResult.user)
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: context.getString(R.string.login_error))
        }

    suspend fun resetPassword(email: String) : AuthResult<Unit> =
        try {
            auth.sendPasswordResetEmail(email).await()
            AuthResult.Success(Unit)
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: context.getString(R.string.password_recovery_error))
        }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult<FirebaseUser?> =
        try {
            auth.signInWithEmailAndPassword(email, password).await().let {
                AuthResult.Success(it.user)
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Error iniciando sesión")
        }

}
