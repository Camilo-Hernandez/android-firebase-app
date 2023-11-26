package com.camihruiz24.android_firebase_app.utils

import android.content.Context
import android.util.Log
import com.camihruiz24.android_firebase_app.model.Note
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

const val NOTES = "notes"

class FirestoreManager(context: Context) {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val authManager: AuthenticationManager = AuthenticationManager(context)
    private var userId: String? = authManager.getCurrentUser()?.uid

    private val userNotesQuery: Query = firestore.collection(NOTES)
        .whereEqualTo("userId", userId).orderBy("title")

    // Forma 1
    fun getAllNotes(): Flow<List<Note>> = userNotesQuery.snapshots()
        .map { it.toObjects(Note::class.java) }


    // Forma 2
    /*
    fun getAllNotes(): Flow<List<Note>> {
        return callbackFlow {
            val registration: ListenerRegistration = userNotesQuery.addSnapshotListener { snapshot: QuerySnapshot?, _ ->
                snapshot?.let { querySnapshot ->
                    val notes = querySnapshot.toObjects<Note>()
                    // Forma larga
                    /*
                    val notes = mutableListOf<Note>()
                    // Iterar los documentos del query snapshot
                    for (document in querySnapshot.documents) {
                        val note = document.toObject(Note::class.java)
                        note?.id = document.id
                        note?.let {
                            notes.add(it)
                        }
                    }*/
                    // Se envía la lista de notas a través del canal callbackFlow
                    trySend(notes).isSuccess
                }
            }
            // Se elimina el listener para evitar fugas de memoria
            awaitClose { registration.remove() }
        }
    }
    */

    suspend fun addNote(note: Note) {
        with<CollectionReference, Unit>(firestore.collection(NOTES)) {
            val noteId: String = add(note)
                .run {
                    await()
                    result.id
                }

            document(noteId)
                .set(note.copy(id = noteId, userId = userId.toString()))
                .await()
        }
    }

    suspend fun updateNote(note: Note) {
        Log.d("Nota actual", note.toString())
        val noteRef: DocumentReference = firestore.collection(NOTES).document(note.id)
        noteRef.set(note).await()
    }

    suspend fun deleteNote(noteId: String) {
        val noteRef: DocumentReference = firestore.collection(NOTES).document(noteId)
        noteRef.delete().await()
    }

}