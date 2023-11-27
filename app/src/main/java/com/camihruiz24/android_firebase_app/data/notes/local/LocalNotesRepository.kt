package com.camihruiz24.android_firebase_app.data.notes.local

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

/**
 * Repository that provides insert, update, delete, and retrieve of [NoteEntity] from a given data source.
 */
interface LocalNotesRepository {

    suspend fun syncLocalDbWithRemoteDb()
    /**
     * Retrieve all the [NoteEntity]s from local data source.
     */
    fun getAllNotesStream(): Flow<List<NoteEntity>>

    /**
     * Retrieves a single [NoteEntity] from local data source if it exists
     */
    suspend fun getNoteById(noteId: String): NoteEntity

    /**
     * Insert [NoteEntity] in both local and remote data sources
     */
    suspend fun insertNote(noteEntity: NoteEntity)

    /**
     * Delete [NoteEntity] from the local data source
     */
    suspend fun deleteNotes(vararg noteEntities: NoteEntity)

    /**
     * Update [NoteEntity] in the data source
     */
    suspend fun updateNote(noteEntity: NoteEntity)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNotesRepository(
        repositoryImpl: NotesRepository
    ): LocalNotesRepository

}

