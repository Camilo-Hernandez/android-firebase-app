package io.devexpert.android_firebase.ui.screens.auth

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.camihruiz24.android_firebase_app.R
import com.camihruiz24.android_firebase_app.ui.navigation.Routes
import com.camihruiz24.android_firebase_app.utils.AnalyticsManager
import com.camihruiz24.android_firebase_app.utils.AuthenticationManager
import com.camihruiz24.android_firebase_app.utils.AuthorizationResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private suspend fun incognitoSignIn(
    authManager: AuthenticationManager,
    analytics: AnalyticsManager,
    context: Context,
    navigation: NavController
) {
    when (val result = authManager.signInAnonymously()) {
        is AuthorizationResult.Success -> {
            analytics.logButtonClicked("Click: Continuar como invitado")
            navigation.navigate(Routes.Home.name) {
                popUpTo(Routes.Login.name) { inclusive = true }
            }
        }

        is AuthorizationResult.Error -> {
            analytics.logError("Error SignIn Incognito: ${result.errorMessage}")
            Toast.makeText(context, "Error accediendo como invitado", Toast.LENGTH_SHORT).show()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    analytics: AnalyticsManager,
    authManager: AuthenticationManager,
    navigation: NavController,
) {
    analytics.logScreenView(screenName = Routes.Login.name)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val scope: CoroutineScope = rememberCoroutineScope()
    val context: Context = LocalContext.current

    /** Configuración del launcher para el inicio de sesión con Google */
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        when (val account = authManager.handleSignInResult(GoogleSignIn.getSignedInAccountFromIntent(result.data))) {
            is AuthorizationResult.Success -> {
                val credential: AuthCredential = GoogleAuthProvider.getCredential(account.data.idToken, null)
                scope.launch {
                    authManager.signInWithGoogleCredential(credential)?.let { _: AuthorizationResult<FirebaseUser> ->
                        with(context.getString(R.string.click_success_login)) {
                            analytics.logButtonClicked(this)
                            Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
                        }
                        navigation.navigate(Routes.Home.name) {
                            popUpTo(Routes.Login.name) { inclusive = true }
                        }
                    }
                }
            }

            is AuthorizationResult.Error -> {
                analytics.logError("Error SignIn: ${account.errorMessage}")
                Toast.makeText(context, "Error: ${account.errorMessage}", Toast.LENGTH_SHORT).show()
            }

            null -> Toast.makeText(context, "Error: cuenta nula", Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ClickableText(
            text = AnnotatedString("¿No tienes una cuenta? Regístrate"),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(40.dp),
            onClick = {
                navigation.navigate(Routes.SignUp.name)
                analytics.logButtonClicked("Click: No tienes una cuenta? Regístrate")
            },
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Default,
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_firebase),
            contentDescription = "Firebase",
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Firebase Android",
            style = TextStyle(fontSize = 30.sp)
        )
        Spacer(modifier = Modifier.height(30.dp))
        TextField(
            label = { Text(text = "Correo electrónico") },
            value = email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            onValueChange = { email = it })
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            label = { Text(text = "Contraseña") },
            value = password,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { password = it })
        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Button(
                onClick = {
                    scope.launch {
                        signInWithEmailAndPassword(email, password, authManager, analytics, context, navigation)
                    }
                },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Iniciar Sesión".uppercase())
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        ClickableText(
            text = AnnotatedString("¿Olvidaste tu contraseña?"),
            onClick = {
                navigation.navigate(Routes.ForgotPassword.name) {
                    popUpTo(Routes.Login.name) { inclusive = false }
                }
                analytics.logButtonClicked("Click: ¿Olvidaste tu contraseña?")
            },
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Default,
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(modifier = Modifier.height(25.dp))
        Text(text = "-------- o --------", style = TextStyle(color = Color.Gray))
        Spacer(modifier = Modifier.height(25.dp))
        SocialMediaButton(
            onClick = {
                scope.launch {
                    incognitoSignIn(authManager, analytics, context, navigation)
                }
            },
            text = "Continuar como invitado",
            icon = R.drawable.ic_incognito,
            color = Color(0xFF363636)
        )
        Spacer(modifier = Modifier.height(15.dp))
        SocialMediaButton(
            onClick = {
                authManager.signInWithGoogle(googleSignInLauncher)
            },
            text = "Continuar con Google",
            icon = R.drawable.ic_google,
            color = Color(0xFFF1F1F1)
        )
    }
}

private suspend fun signInWithEmailAndPassword(
    email: String,
    password: String,
    authManager: AuthenticationManager,
    analytics: AnalyticsManager,
    context: Context,
    navigation: NavController,
) {
    if (email.trim().isNotBlank() && password.isNotEmpty()) {
        when (val result = authManager.signInWithEmailAndPassword(email, password)) {
            is AuthorizationResult.Success -> {
                analytics.logButtonClicked("Click: Inicio de sesión exitoso")
                navigation.navigate(Routes.Home.name) {
                    popUpTo(Routes.Login.name) { inclusive = true }
                }
            }

            is AuthorizationResult.Error -> {
                analytics.logButtonClicked("Error SignUp: ${result.errorMessage}")
                Toast.makeText(
                    context,
                    "Error SignUp: ${result.errorMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    } else {
        Toast.makeText(
            context,
            "Existen campos vacíos",
            Toast.LENGTH_SHORT
        ).show()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialMediaButton(onClick: () -> Unit, text: String, icon: Int, color: Color) {
    var click by remember { mutableStateOf(false) }
    Surface(
        onClick = onClick,
        modifier = Modifier
            .padding(start = 40.dp, end = 40.dp)
            .clickable { click = !click },
        shape = RoundedCornerShape(50),
        border = BorderStroke(width = 1.dp, color = if (icon == R.drawable.ic_incognito) color else Color.Gray),
        color = color
    ) {
        Row(
            modifier = Modifier
                .padding(start = 12.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                modifier = Modifier.size(24.dp),
                contentDescription = text,
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, color = if (icon == R.drawable.ic_incognito) Color.White else Color.Black)
            click = true
        }
    }
}
