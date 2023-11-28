package com.camihruiz24.android_firebase_app.data

import android.content.Context
import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudStorageManager @Inject constructor(context: Context) {
    private val storageReference: StorageReference = Firebase.storage.reference
    private val authManager = AuthenticationManager(context)
    private val userId = authManager.getCurrentUser()?.uid
    private val userPhotosReference: StorageReference = storageReference.child("photos").child(userId ?: "")

    suspend fun uploadFile(fileName: String, filePath: Uri) {
        val fileRef = userPhotosReference.child(fileName)
        fileRef.putFile(filePath).await()
    }

    suspend fun getUserImages(): List<String> = userPhotosReference
        .listAll()
        .await()
        .items
        .map {
            it.downloadUrl.await().toString()
        }
}