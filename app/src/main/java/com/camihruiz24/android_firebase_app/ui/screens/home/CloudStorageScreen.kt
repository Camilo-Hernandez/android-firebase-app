package com.camihruiz24.android_firebase_app.ui.screens.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.camihruiz24.android_firebase_app.data.CloudStorageManager
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudStorageScreen(cloudStorageManager: CloudStorageManager) {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val file = context.createImageFile()
    val uri: Uri? = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        "com.camihruiz24.android_firebase_app" + ".provider", file
    )

    var capturedImageUri by remember { mutableStateOf<Uri?>(Uri.EMPTY) }

    var gallery by remember { mutableStateOf<List<String>>(listOf()) }
    LaunchedEffect(key1 = Unit) {
        gallery = cloudStorageManager.getUserImages()
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) {
        if (it) {
            Toast.makeText(context, "Foto tomada con éxito", Toast.LENGTH_SHORT).show()
            capturedImageUri = uri
            capturedImageUri?.let {
                scope.launch {
                    cloudStorageManager.uploadFile(file.name, it)
                    gallery = cloudStorageManager.getUserImages()
                }
            }
        } else Toast.makeText(context, "La foto no pudo ser tomada", Toast.LENGTH_SHORT).show()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            if (it) {
                Toast.makeText(context, "Permiso de cámara autorizado", Toast.LENGTH_SHORT).show()
                cameraLauncher.launch(uri)
            } else Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)

                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        cameraLauncher.launch(uri)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Photo")
            }
        }
    ) { contentPadding ->
        Box(modifier = Modifier.padding(contentPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                    items(gallery) { imageUrl ->
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageUrl)
                                .crossfade(true)
                                .transformations(RoundedCornersTransformation(20f))
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(RectangleShape)
                                .clip(RoundedCornerShape(20.dp))
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(4.dp),
                        )
                    }
                }

            }
        }
    }
}

private fun Context.createImageFile(): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    return File.createTempFile(imageFileName, ".jpg", externalCacheDir)
}
