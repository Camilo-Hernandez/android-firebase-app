package com.camihruiz24.android_firebase_app.data.notes.remote

import com.camihruiz24.android_firebase_app.data.notes.local.NoteEntity

// UI Model (also used by Firebase)
data class NetworkNote(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val content: String = "",
)

fun NetworkNote.toNoteEntity() = NoteEntity(id, userId, title, content)