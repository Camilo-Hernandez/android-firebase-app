package com.camihruiz24.android_firebase_app.ui.screens.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.camihruiz24.android_firebase_app.model.NoteUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    notesViewModel: NotesViewModel = hiltViewModel()
) {
    val notesUiState by notesViewModel.uiState.collectAsStateWithLifecycle()
    when (notesUiState) {
        is UiState.Loading -> Box(contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        is UiState.Success -> {

            val networkNotes = (notesUiState as UiState.Success<List<NoteUiModel>>).data
            val addNote: (NoteUiModel) -> Unit = notesViewModel::addNote
            val updateNote: (NoteUiModel) -> Unit = notesViewModel::updateNote
            val deleteNote: (NoteUiModel) -> Unit = { notesViewModel.deleteNote(it) }
            NotesListScreen(
                networkNotes, addNote, updateNote, deleteNote
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun NotesListScreen(
    networkNotes: List<NoteUiModel>,
    addNote: (NoteUiModel) -> Unit,
    updateNote: (NoteUiModel) -> Unit,
    deleteNote: (NoteUiModel) -> Unit
) {

    var showAddNoteDialog by remember { mutableStateOf(false) }
    var showEditNoteDialog by remember { mutableStateOf(false) }
    var editingNote by remember { mutableStateOf(NoteUiModel("", "", "", "")) }

    Scaffold(floatingActionButton = {
        FloatingActionButton(
            onClick = {
                showAddNoteDialog = true
            },
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Note")
        }
        if (showAddNoteDialog) {
            AddNoteDialog(
                onAddNote = { note ->
                    addNote(note)
                    showAddNoteDialog = false
                },
                onDialogDismissed = { showAddNoteDialog = false },
            )
        }
        if (showEditNoteDialog) {
            AddNoteDialog(
                onAddNote = { note ->
                    updateNote(note)
                    showEditNoteDialog = false
                },
                onDialogDismissed = { showEditNoteDialog = false },
                currentNote = editingNote,
            )
        }
    }) { paddingValues ->
        if (networkNotes.isNotEmpty()) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2), contentPadding = paddingValues
            ) {
                items(networkNotes) { note ->
                    NoteItem(networkNote = note, deleteNote = deleteNote, modifier = Modifier.clickable {
                        showEditNoteDialog = true
                        editingNote = note
                    })
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(imageVector = Icons.Default.List, contentDescription = null, modifier = Modifier.size(100.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No se encontraron \nNotas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Thin,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun NoteItem(
    networkNote: NoteUiModel, deleteNote: (NoteUiModel) -> Unit, modifier: Modifier = Modifier
) {
    var showDeleteNoteDialog by remember { mutableStateOf(false) }

    val onDeleteNoteConfirmed: () -> Unit = {
        deleteNote(networkNote)
    }

    if (showDeleteNoteDialog) {
        DeleteNoteDialog(onConfirmDelete = {
            onDeleteNoteConfirmed()
            showDeleteNoteDialog = false
        }, onDismiss = {
            showDeleteNoteDialog = false
        })
    }

    Card(
        modifier = modifier.padding(6.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = networkNote.title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = networkNote.content, fontWeight = FontWeight.Thin, fontSize = 13.sp, lineHeight = 15.sp
            )
            IconButton(onClick = { showDeleteNoteDialog = true }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Icon")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteDialog(
    onAddNote: (NoteUiModel) -> Unit,
    onDialogDismissed: () -> Unit,
    currentNote: NoteUiModel = NoteUiModel("","","",""),
) {
    Log.d("AddNoteDialog: Nota actual", currentNote.toString())
    var title by remember { mutableStateOf(currentNote.title) }
    var content by remember { mutableStateOf(currentNote.content) }

    AlertDialog(onDismissRequest = {}, title = { Text(text = "Agregar Nota") }, confirmButton = {
        Button(onClick = {
            onAddNote(
                NoteUiModel(
                    id = currentNote.id, userId = currentNote.userId, title = title, content = content
                )
            )
            title = currentNote.title
            content = currentNote.content
        }) {
            Text(text = "Agregar")
        }
    }, dismissButton = {
        Button(onClick = {
            onDialogDismissed()
        }) {
            Text(text = "Cancelar")
        }
    }, text = {
        Column {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                label = { Text(text = "Título") },
                maxLines = 1,
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
                value = content,
                onValueChange = { content = it },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                maxLines = 4,
                label = { Text(text = "Contenido") })
        }
    })
}

@Composable
fun DeleteNoteDialog(onConfirmDelete: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(onDismissRequest = onDismiss,
        title = { Text("Eliminar Nota") },
        text = { Text("¿Estás seguro que deseas eliminar la nota?") },
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
        })
}
