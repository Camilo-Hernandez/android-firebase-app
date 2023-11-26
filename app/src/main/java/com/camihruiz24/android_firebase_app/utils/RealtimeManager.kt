package com.camihruiz24.android_firebase_app.utils

import android.content.Context
import com.camihruiz24.android_firebase_app.model.Contact
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class RealtimeManager(context: Context) {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    /** Referencia al hijo de la DB ("tabla"). En este caso, contacts, el hijo de la DB global.
     * La DB de Firebase, siendo NoSQL, es anidada */
    private val contactsReference: DatabaseReference = database.reference.child("contacts")

    private val authManager: AuthenticationManager = AuthenticationManager(context)

    fun getAllContacts(): Flow<List<Contact>> {
        val currentUserId: String? = authManager.getCurrentUser()?.uid
        return callbackFlow<List<Contact>> {
            val listener = contactsReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val contacts = snapshot.children.mapNotNull { it: DataSnapshot ->
                        val contact = it.getValue(Contact::class.java)
                        it.key?.let { contact?.copy(key = it) }
                    }
                    trySend(contacts.filter { it.userId == currentUserId })
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException()) // cierra con el posible error que suceda
                }
            })
            // Cuando se cierre el flujo, se elimine el oyente para evitar fugas de datos
            awaitClose { contactsReference.removeEventListener(listener) }
        }
    }

    /**
     * Adds a contact to the user's contacts database
     */
    fun addContact(contact: Contact) {
        val key: String? = contactsReference.push().key
        key?.let {
            contactsReference.child(key).setValue(contact)
        }
    }

    /**
     * Deletes a contact from the user's contacts database using its ID
     */
    fun deleteContact(contactId: String) {
        contactsReference.child(contactId).removeValue()
    }

    /**
     * Updates a contact in the user's contacts database using its ID and the new contact information
     */
    fun updateContact(contactId: String, updatedContact: Contact) {
        contactsReference.child(contactId).setValue(updatedContact)
    }

}
