package com.camihruiz24.android_firebase_app.ui.screens.home

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.camihruiz24.android_firebase_app.model.Contact
import com.camihruiz24.android_firebase_app.data.AuthenticationManager
import com.camihruiz24.android_firebase_app.data.contacts.RealtimeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(realtimeManager: RealtimeManager, authManager: AuthenticationManager) {
    var showAddContactDialog by remember { mutableStateOf(false) }
    var editContactDialogInfo by remember { mutableStateOf(Pair(false, Contact())) }

    val contacts: List<Contact> by realtimeManager.getAllContacts().collectAsState(initial = emptyList())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddContactDialog = true
                },
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Contact")
            }

            if (showAddContactDialog) {
                AddContactDialog(
                    onContactAdded = {
                        realtimeManager.addContact(it)
                        showAddContactDialog = false
                    },
                    onDialogDismissed = { showAddContactDialog = false },
                    authManager = authManager,
                    inputKey = editContactDialogInfo.second.key
                )
            }
            if (editContactDialogInfo.first) {
                AddContactDialog(
                    onContactAdded = { contact ->
                        contact.key?.let { key ->
                            realtimeManager.updateContact(key, contact)
                        }
                        editContactDialogInfo = Pair(false, Contact())
                    },
                    onDialogDismissed = { editContactDialogInfo = Pair(false, Contact()) },
                    authManager = authManager,
                    inputKey = editContactDialogInfo.second.key,
                    inputName = editContactDialogInfo.second.name,
                    inputPhoneNumber = editContactDialogInfo.second.phoneNumber,
                    inputEmail = editContactDialogInfo.second.email
                )
            }
        }
    ) { paddingValues ->
        if (contacts.isNotEmpty()) {
            LazyColumn(
                Modifier
                    .padding(paddingValues)
                    .padding(start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(0.dp)) }
                items(contacts) {
                    ContactItem(
                        contact = it,
                        realtimeManager = realtimeManager,
                        Modifier.clickable { editContactDialogInfo = Pair(true, it) }
                    )
                }
                item { Spacer(modifier = Modifier.height(0.dp)) }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(100.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No se encontraron \nContactos",
                    fontSize = 18.sp, fontWeight = FontWeight.Thin, textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ContactItem(contact: Contact, realtimeManager: RealtimeManager, modifier: Modifier) {
    var showDeleteContactDialog by remember { mutableStateOf(false) }

    val onDeleteContactConfirmed: () -> Unit = {
        realtimeManager.deleteContact(contactId = contact.key ?: "")
    }

    if (showDeleteContactDialog) {
        DeleteContactDialog(
            onConfirmDelete = {
                onDeleteContactConfirmed()
                showDeleteContactDialog = false
            },
            onDismiss = {
                showDeleteContactDialog = false
            }
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
    )
    {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(3f)) {
                Text(
                    text = contact.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = contact.phoneNumber,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = contact.email,
                    fontWeight = FontWeight.Thin,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center,
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    IconButton(
                        onClick = {
                            showDeleteContactDialog = true
                        },
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Icon")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactDialog(
    onContactAdded: (Contact) -> Unit,
    onDialogDismissed: () -> Unit,
    authManager: AuthenticationManager,
    inputKey: String?,
    inputName: String = "",
    inputPhoneNumber: String = "",
    inputEmail: String = "",
) {
    var name by remember { mutableStateOf(inputName) }
    var phoneNumber by remember { mutableStateOf(inputPhoneNumber) }
    var email by remember { mutableStateOf(inputEmail) }
    val uid = authManager.getCurrentUser()?.uid

    val focusRequester: FocusRequester by remember { mutableStateOf(FocusRequester()) }

    LaunchedEffect(key1 = Unit, block = { focusRequester.requestFocus() })

    val onSaveContact = {
        onContactAdded(
            Contact(
                userId = uid.toString(),
                name = name.trim(),
                phoneNumber = phoneNumber,
                email = email,
            ).apply {
                // Se necesita cuando se quiere editar el contacto en lugar de crearlo, para conservar la key
                inputKey?.let {
                    key = it
                }
            }
        )
        name = inputName
        phoneNumber = inputPhoneNumber
        email = inputEmail
    }

    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = "Agregar Contacto") },
        confirmButton = {
            Button(
                enabled = Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                        phoneNumber.length == 10 &&
                        name.isNotBlank(),
                onClick = {
                    onSaveContact()
                }
            ) {
                Text(text = "Agregar")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDialogDismissed()
                }
            ) {
                Text(text = "Cancelar")
            }
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    label = { Text(text = "Nombre") },
                    maxLines = 1,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor =
                        if (name.isNotBlank()) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error,
                    ),
                    modifier = Modifier.focusRequester(focusRequester),
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        if (!it.contains(Regex("""[^0-9]""")))
                            phoneNumber = it
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    label = { Text(text = "Teléfono") },
                    maxLines = 1,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor =
                        if (phoneNumber.length == 10) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error,
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { onSaveContact() }),
                    label = { Text(text = "Correo electrónico") },
                    maxLines = 1,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor =
                        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error,
                    )
                )
            }
        }
    )
}

@Composable
fun DeleteContactDialog(onConfirmDelete: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Eliminar contacto") },
        text = { Text("¿Estás seguro que deseas eliminar el contacto?") },
        confirmButton = {
            Button(
                onClick = onConfirmDelete
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}
