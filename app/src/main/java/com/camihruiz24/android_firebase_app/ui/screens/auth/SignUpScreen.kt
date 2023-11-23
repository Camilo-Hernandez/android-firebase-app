package com.camihruiz24.android_firebase_app.ui.screens.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.camihruiz24.android_firebase_app.ui.navigation.Routes
import com.camihruiz24.android_firebase_app.ui.theme.Purple40
import com.camihruiz24.android_firebase_app.utils.AnalyticsManager
import com.camihruiz24.android_firebase_app.utils.AuthorizationResult
import com.camihruiz24.android_firebase_app.utils.AuthenticationManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(analytics: AnalyticsManager, authManager: AuthenticationManager, navigation: NavController) {
    analytics.logScreenView(screenName = Routes.SignUp.name)

    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crear Cuenta",
            style = TextStyle(fontSize = 40.sp, color = Purple40)
        )
        Spacer(modifier = Modifier.height(50.dp))
        TextField(
            label = { Text(text = "Correo electrónico") },
            value = email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            onValueChange = { email = it })

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = { Text(text = "Contraseña") },
            value = password,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { password = it })

        Spacer(modifier = Modifier.height(30.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Button(
                onClick = {
                    scope.launch {
                        signUp(email, password, authManager, analytics, context, navigation)
                    }
                },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Registrarse")
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
        ClickableText(
            text = AnnotatedString("¿Ya tienes cuenta? Inicia sesión"),
            onClick = {
                navigation.popBackStack()
            },
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Default,
                textDecoration = TextDecoration.Underline,
                color = Purple40
            )
        )
    }
}

suspend fun signUp(
    email: String,
    password: String,
    authManager: AuthenticationManager,
    analytics: AnalyticsManager,
    context: Context,
    navigation: NavController
) {
    if (email.trim().isNotBlank() && password.isNotEmpty()) {
        when (val result = authManager.createUserWithEmailAndPassword(email, password)) {
            is AuthorizationResult.Success -> {
                analytics.logButtonClicked("Botón de Registro realizado")
                Toast.makeText(
                    context,
                    "Usuario registrado",
                    Toast.LENGTH_SHORT
                ).show()
                navigation.popBackStack()
            }

            is AuthorizationResult.Error -> {
                analytics.logButtonClicked("Error SignUp: ${result.errorMessage}")
                analytics.logError(result.errorMessage)
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
            "Correo o contraseña no válidos. El correo no debe tener espacios en blanco y la contraseña no puede ser vacía",
            Toast.LENGTH_SHORT
        ).show()
    }

}
