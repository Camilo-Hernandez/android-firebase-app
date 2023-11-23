package com.camihruiz24.android_firebase_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.camihruiz24.android_firebase_app.ui.navigation.Navigation
import com.camihruiz24.android_firebase_app.ui.theme.AndroidFirebaseAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            AndroidFirebaseAppTheme {
                    Navigation(this)
            }
        }
    }
}
