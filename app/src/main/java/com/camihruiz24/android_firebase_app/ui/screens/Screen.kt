package com.camihruiz24.android_firebase_app.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.camihruiz24.android_firebase_app.ui.theme.AndroidFirebaseAppTheme

@Composable
fun Screen(content: @Composable () -> Unit) {
    AndroidFirebaseAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}