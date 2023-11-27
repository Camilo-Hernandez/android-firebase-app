package com.camihruiz24.android_firebase_app.data.notes.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.camihruiz24.android_firebase_app.data.notes.remote.NetworkNote
import com.camihruiz24.android_firebase_app.model.NoteUiModel

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val title: String,
    val content: String,
)

fun NoteEntity.toNetworkNote() = NetworkNote(id, userId, title, content)

fun NoteEntity.toUiModel() = NoteUiModel(id, userId, title, content)
