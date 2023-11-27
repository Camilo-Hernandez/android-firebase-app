package com.camihruiz24.android_firebase_app.data.notes.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteEntityDao {
    @Query("SELECT * FROM notes ORDER BY title COLLATE NOCASE")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :noteId LIMIT 1")
    suspend fun getNoteById(noteId: String): NoteEntity

    @Upsert
    suspend fun upsertNotes(vararg noteEntity: NoteEntity)

    @Delete
    suspend fun deleteNotes(vararg noteEntities: NoteEntity)

}