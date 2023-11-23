package com.camihruiz24.android_firebase_app.utils

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.camihruiz24.android_firebase_app.R
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.security.auth.login.LoginException

sealed interface AuthorizationResult<out T> {
    data class Success<T>(val data: T) : AuthorizationResult<T>
    data class Error(val errorMessage: String) : AuthorizationResult<Nothing>
}

class AuthenticationManager(private val context: Context) {
    private val auth: FirebaseAuth by lazy { Firebase.auth }

    // Se debe obtener la instacia del cliente de google autenticado para cerrar sesión de la cuenta de Google, además
    // de la cuenta de Firebase
    private val signInClient: SignInClient = Identity.getSignInClient(context)

    // FirebaseUser es un contenedor del usuario que se autentica sin importar el proveedor de autenticación
    suspend fun signInAnonymously(): AuthorizationResult<FirebaseUser> =
        try {
            auth.signInAnonymously().await().let { result ->
                AuthorizationResult.Success(result.user ?: throw LoginException(context.getString(R.string.login_error)))
            }
        } catch (e: LoginException) {
            AuthorizationResult.Error(e.message ?: context.getString(R.string.login_error))
        }

    suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthorizationResult<FirebaseUser?> =
        try {
            auth.createUserWithEmailAndPassword(email, password).await().let { authResult ->
                AuthorizationResult.Success(authResult.user)
            }
        } catch (e: Exception) {
            AuthorizationResult.Error(e.message ?: context.getString(R.string.login_error))
        }

    suspend fun resetPassword(email: String): AuthorizationResult<Unit> =
        try {
            auth.sendPasswordResetEmail(email).await()
            AuthorizationResult.Success(Unit)
        } catch (e: Exception) {
            AuthorizationResult.Error(e.message ?: context.getString(R.string.password_recovery_error))
        }

    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthorizationResult<FirebaseUser?> =
        try {
            auth.signInWithEmailAndPassword(email, password).await().let {
                AuthorizationResult.Success(it.user)
            }
        } catch (e: Exception) {
            AuthorizationResult.Error(e.message ?: context.getString(R.string.login_error))
        }

    fun signOut() {
        auth.signOut()
        signInClient.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    fun handleSignInResult(task: Task<GoogleSignInAccount>): AuthorizationResult<GoogleSignInAccount>? =
        try {
            task.getResult(ApiException::class.java).let { account ->
                AuthorizationResult.Success<GoogleSignInAccount>(account)
            }
        } catch (e: ApiException) {
            AuthorizationResult.Error(e.message ?: context.getString(R.string.login_error))
        }

    suspend fun signInWithGoogleCredential(credential: AuthCredential): AuthorizationResult<FirebaseUser>? =
        try {
            val firebaseUser: com.google.firebase.auth.AuthResult = auth.signInWithCredential(credential).await()
            firebaseUser.user?.let{
                AuthorizationResult.Success<FirebaseUser>(it)
            } ?: throw Exception(context.getString(R.string.signin_with_google_failed))
        } catch (e: Exception) {
            AuthorizationResult.Error(e.message ?: context.getString(R.string.signin_with_google_failed))
        }

    private val googleSignInClient: GoogleSignInClient by lazy {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
            .let { GoogleSignIn.getClient(context, it) }
    }

    fun signInWithGoogle(googleSignInLauncher: ActivityResultLauncher<Intent>) {
        val signInIntent: Intent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

}

