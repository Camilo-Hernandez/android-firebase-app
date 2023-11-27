package com.camihruiz24.android_firebase_app.data.notes.local

import android.security.keystore.UserNotAuthenticatedException
import com.camihruiz24.android_firebase_app.data.AuthenticationManager
import com.camihruiz24.android_firebase_app.data.notes.remote.NetworkNote
import com.camihruiz24.android_firebase_app.data.notes.remote.NetworkNotesRemoteRepository
import com.camihruiz24.android_firebase_app.data.notes.remote.toNoteEntity
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class NotesRepository @Inject constructor(
    private val noteEntityDao: NoteEntityDao,
    private val networkNotesRemoteRepository: NetworkNotesRemoteRepository,
    authenticationManager: AuthenticationManager,
) : LocalNotesRepository {

    private val userId: String? = authenticationManager.getCurrentUser()?.uid

    /** Populates the local notes table with the remote notes DB */
    override suspend fun syncLocalDbWithRemoteDb() {
        coroutineScope {
            noteEntityDao.deleteNotes(
                *noteEntityDao.getAllNotes().first().toTypedArray()
            )
        }

        coroutineScope {
            networkNotesRemoteRepository.getAllNotes().first().also { it: List<NetworkNote> ->
                noteEntityDao.upsertNotes(
                    *it.map(NetworkNote::toNoteEntity).toTypedArray()
                )
            }
        }
    }

    /** Returns a flow of a list of all existing notes in the local DB since it's the single source of truth */
    override fun getAllNotesStream(): Flow<List<NoteEntity>> = noteEntityDao.getAllNotes()

    /** Retrieves a note given its id */
    override suspend fun getNoteById(noteId: String): NoteEntity =
        noteEntityDao.getNoteById(noteId)

    /** Inserts one single note in both local and remote databases */
    override suspend fun insertNote(noteEntity: NoteEntity) {
        userId?.let {
            val noteId: String = networkNotesRemoteRepository.addNote(noteEntity.toNetworkNote())
            noteEntityDao.upsertNotes(noteEntity.copy(id = noteId, userId = userId))
        } ?: throw UserNotAuthenticatedException("User not logged")
    }

    override suspend fun deleteNotes(vararg noteEntities: NoteEntity) {
        noteEntityDao.deleteNotes(*noteEntities)
        noteEntities.forEach {
            networkNotesRemoteRepository.deleteNote(it.id)
        }
    }

    override suspend fun updateNote(noteEntity: NoteEntity) {
        noteEntityDao.upsertNotes(noteEntity)
        networkNotesRemoteRepository.updateNote(noteEntity.toNetworkNote())
    }

}
