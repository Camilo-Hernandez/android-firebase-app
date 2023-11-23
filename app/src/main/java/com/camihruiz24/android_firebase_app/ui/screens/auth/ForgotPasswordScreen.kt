package com.camihruiz24.android_firebase_app.ui.screens.auth

import android.content.Context
import android.widget.Toast
import com.camihruiz24.android_firebase_app.ui.navigation.Routes
import com.camihruiz24.android_firebase_app.ui.theme.Purple40
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.camihruiz24.android_firebase_app.utils.AnalyticsManager
import com.camihruiz24.android_firebase_app.utils.AuthorizationResult
import com.camihruiz24.android_firebase_app.utils.AuthenticationManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(analytics: AnalyticsManager, authManager: AuthenticationManager, navigation: NavController) {
    analytics.logScreenView(screenName = Routes.ForgotPassword.name)

    val context = LocalContext.current
    var email by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Olvidó su contraseña",
            style = TextStyle(fontSize = 40.sp, color = Purple40)
        )
        Spacer(modifier = Modifier.height(50.dp))
        TextField(
            label = { Text(text = "Correo electrónico") },
            value = email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            onValueChange = { email = it })

        Spacer(modifier = Modifier.height(30.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Button(
                onClick = {
                          scope.launch { resetPassword(email, authManager,analytics, context, navigation) }
                },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Recuperar contraseña")
            }
        }
    }
}

suspend fun resetPassword(email: String, authManager: AuthenticationManager, analytics: AnalyticsManager,  context: Context, navigation: NavController) {
    when(val result = authManager.resetPassword(email)) {
        is AuthorizationResult.Success -> {
            analytics.logButtonClicked("Click: envío de reinicio de contraseña para $email")
            Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
            navigation.popBackStack()
        }
        is AuthorizationResult.Error -> {
            analytics.logError(result.errorMessage)
            Toast.makeText(context, "Error al enviar reinicio de contraseña", Toast.LENGTH_SHORT).show()
        }
    }
}
