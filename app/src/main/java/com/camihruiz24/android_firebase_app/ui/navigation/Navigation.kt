package com.camihruiz24.android_firebase_app.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.camihruiz24.android_firebase_app.ui.screens.Screen
import com.camihruiz24.android_firebase_app.ui.screens.auth.ForgotPasswordScreen
import com.camihruiz24.android_firebase_app.ui.screens.auth.SignUpScreen
import com.camihruiz24.android_firebase_app.ui.screens.home.HomeScreen
import com.camihruiz24.android_firebase_app.data.AnalyticsManager
import com.camihruiz24.android_firebase_app.data.AuthenticationManager
import com.google.firebase.auth.FirebaseUser
import io.devexpert.android_firebase.ui.screens.auth.LoginScreen

@Composable
fun Navigation(context: Context, navController: NavHostController = rememberNavController()) {
    val analytics = AnalyticsManager(context)
    val authManager = AuthenticationManager(context)
    val user: FirebaseUser? = authManager.getCurrentUser()

    Screen {
        NavHost(
            navController = navController,
            startDestination = user?.let { Routes.Home.name } ?: Routes.Login.name
        ) {

            composable(Routes.Login.name) {
                LoginScreen(
                    analytics = analytics,
                    authManager = authManager,
                    navigation = navController
                )
            }
            composable(Routes.Home.name) {
                HomeScreen(analytics = analytics, authManager = authManager, navigation = navController)
            }
            composable(Routes.SignUp.name) {
                SignUpScreen(
                    analytics = analytics,
                    authManager = authManager,
                    navigation = navController
                )
            }
            composable(Routes.ForgotPassword.name) {
                ForgotPasswordScreen(
                    analytics = analytics,
                    authManager = authManager,
                    navigation = navController
                )
            }
        }
    }
}
