package com.camihruiz24.android_firebase_app.model

import com.camihruiz24.android_firebase_app.data.notes.local.NoteEntity

data class NoteUiModel(
    val id: String,
    val userId: String,
    val title: String,
    val content: String,
)

fun NoteUiModel.toNoteEntity() = NoteEntity(id, userId, title, content)
