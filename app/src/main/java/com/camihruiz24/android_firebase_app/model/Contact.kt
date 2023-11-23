package com.camihruiz24.android_firebase_app.model

data class Contact (
    var key: String? = null, // Contact's key
    val userId: String = "", // user's id
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
)
