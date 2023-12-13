package com.camihruiz24.android_firebase_app.ui.screens.home

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.camihruiz24.android_firebase_app.R
import com.camihruiz24.android_firebase_app.data.AnalyticsManager
import com.camihruiz24.android_firebase_app.data.AuthenticationManager
import com.camihruiz24.android_firebase_app.data.CloudStorageManager
import com.camihruiz24.android_firebase_app.data.contacts.RealtimeManager
import com.camihruiz24.android_firebase_app.ui.navigation.Routes
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.crashlytics.setCustomKeys
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.get
import com.google.firebase.remoteconfig.ktx.remoteConfig

private lateinit var firebaseRemoteConfig: FirebaseRemoteConfig
private var welcomeMessage by mutableStateOf("Bienvenidx")
private var isButtonVisible by mutableStateOf(true)
const val IS_BUTTON_VISIBLE = "is_button_visible"
const val WELCOME_MESSAGE = "welcome_message"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    analytics: AnalyticsManager,
    navigation: NavController,
    authManager: AuthenticationManager
) {
    analytics.logScreenView(screenName = Routes.Home.name)

    initRemoteConfig()

    val context = LocalContext.current

    val user: FirebaseUser? = authManager.getCurrentUser()

    val navController = rememberNavController()

    var showDialog by remember { mutableStateOf(false) }

    val onLogoutConfirmed: () -> Unit = {
        authManager.signOut()
        navigation.navigate(Routes.Login.name) {
            popUpTo(Routes.Home.name) { inclusive = true }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        user?.photoUrl?.let { it: Uri ->
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(it)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Imagen de perfil",
                                placeholder = painterResource(id = R.drawable.profile),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(40.dp)
                            )
                        } ?: Image(
                            painterResource(id = R.drawable.profile),
                            "userPhoto",
                            Modifier
                                .padding(end = 8.dp)
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (!user?.displayName.isNullOrBlank()) {
                                    "Hola ${user?.displayName}"
                                } else welcomeMessage,
                                fontSize = 20.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = user?.email ?: "Anónimo",
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(),
                actions = {
                    if (isButtonVisible) {
                        IconButton(
                            onClick = {
                                val crashlytics = Firebase.crashlytics
                                crashlytics.setCustomKey("Login Screen", "Botón Forzar detención")
                                crashlytics.log("Botón Forzar detención")
                                crashlytics.setCustomKeys {
                                    key("string", "botón warning")
                                    key("long", 50L)
                                    key("boolean", true)
                                    key("integer", 10)
                                    key("float", 5.6F)
                                    key("double", 5.6)
                                }
                                crashlytics.setUserId(user?.uid ?: throw RuntimeException("No user id"))
                                throw RuntimeException("Error forzado desde LoginScreen")
                            }
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = "Forzar Error")
                        }
                    }
                    IconButton(
                        onClick = {
                            showDialog = true
                        }
                    ) {
                        Icon(Icons.Outlined.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                }
            )
        },
        bottomBar = {
            BottomBar(navController = navController)
        }
    ) { contentPadding ->
        Box(modifier = Modifier.padding(contentPadding)) {
            if (showDialog) {
                LogoutDialog(onConfirmLogout = {
                    onLogoutConfirmed()
                    showDialog = false
                }, onDismiss = { showDialog = false })
            }

            NavGraph(
                navController = navController,
                context = context,
                authManager = authManager,
            )
        }
    }
}

fun initRemoteConfig() {
    firebaseRemoteConfig = Firebase.remoteConfig
    FirebaseRemoteConfigSettings.Builder()
        .setMinimumFetchIntervalInSeconds(3_600)
        .build().also { configSettings ->
            // Add config settings
            firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        }
    // Add default values to the remote configuration
    firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
    // Add a listener to the remote changes
    firebaseRemoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
        private val TAG = "Home Screen"
        override fun onUpdate(configUpdate: ConfigUpdate) {
            Log.d(TAG, "Updated Keys: ${configUpdate.updatedKeys}")
            if (configUpdate.updatedKeys.contains(IS_BUTTON_VISIBLE) ||
                configUpdate.updatedKeys.contains(WELCOME_MESSAGE)
            ) {
                // When the activation is completed, display a welcome message
                firebaseRemoteConfig.activate().addOnCompleteListener {
                    displayWelcomeMessage()
                }
            }
        }

        private fun displayWelcomeMessage() {
            welcomeMessage = firebaseRemoteConfig[WELCOME_MESSAGE].asString()
            isButtonVisible = firebaseRemoteConfig[IS_BUTTON_VISIBLE].asBoolean()
        }

        override fun onError(error: FirebaseRemoteConfigException) {
            Log.d(TAG, "onError: ${error.localizedMessage}")
        }
    })
    fetchWelcome()
}

fun fetchWelcome() {
    firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
        if (task.isSuccessful) println("Parámetros actualizados: ${task.result}")
        else println("Fetch failed")

    }
}

@Composable
fun LogoutDialog(onConfirmLogout: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cerrar sesión") },
        text = { Text("¿Estás seguro que deseas cerrar sesión?") },
        confirmButton = {
            Button(
                onClick = onConfirmLogout
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun BottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    NavigationBar {
        BottomNavScreen.entries.forEach { screens ->
            if (currentDestination != null) {
                AddItem(
                    screens = screens,
                    currentDestination = currentDestination,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun RowScope.AddItem(screens: BottomNavScreen, currentDestination: NavDestination, navController: NavHostController) {
    NavigationBarItem(
        label = { Text(text = screens.title) },
        icon = { Icon(imageVector = screens.icon, contentDescription = "Icons") },
        selected = currentDestination.hierarchy.any {
            it.route == screens.route
        },
        onClick = {
            navController.navigate(screens.route) {
                popUpTo(navController.graph.id)
                launchSingleTop = true
            }
        }
    )
}

@Composable
fun NavGraph(navController: NavHostController, context: Context, authManager: AuthenticationManager) {
    val realtimeManager = RealtimeManager(context)
    val cloudStorageManager = CloudStorageManager(context)

    NavHost(navController = navController, startDestination = BottomNavScreen.Contact.route) {
        composable(route = BottomNavScreen.Contact.route) {
            ContactsScreen(realtimeManager, authManager)
        }
        composable(route = BottomNavScreen.Note.route) {
            NotesScreen()
        }
        composable(route = BottomNavScreen.Photos.route) {
            CloudStorageScreen(cloudStorageManager)
        }
    }
}

enum class BottomNavScreen(val route: String, val title: String, val icon: ImageVector) {
    Contact(
        route = "contact",
        title = "Contactos",
        icon = Icons.Default.Person
    ),
    Note(
        route = "notes",
        title = "Notas",
        icon = Icons.Default.List
    ),
    Photos(
        route = "photos",
        title = "Fotos",
        icon = Icons.Default.Face
    ),

}