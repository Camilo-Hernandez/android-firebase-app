package com.camihruiz24.android_firebase_app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.camihruiz24.android_firebase_app.data.notes.local.LocalNotesRepository
import com.camihruiz24.android_firebase_app.data.notes.local.NoteEntity
import com.camihruiz24.android_firebase_app.data.notes.local.toUiModel
import com.camihruiz24.android_firebase_app.model.NoteUiModel
import com.camihruiz24.android_firebase_app.model.toNoteEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface UiState<out T> {
    data class Success<T>(val data: T) : UiState<T>
    data object Loading : UiState<Nothing>
}

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val notesRepository: LocalNotesRepository
) : ViewModel() {

    val uiState: StateFlow<UiState<List<NoteUiModel>>> = notesRepository.getAllNotesStream()
        .onEach {
            UiState.Loading
        }
        .map { noteEntities ->
            noteEntities
                .map(NoteEntity::toUiModel)
                .let { noteUiModels ->
                    UiState.Success(noteUiModels)
                }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, UiState.Loading)

    init {
        viewModelScope.launch { notesRepository.syncLocalDbWithRemoteDb() }
    }

    fun addNote(note: NoteUiModel) {
        viewModelScope.launch { notesRepository.insertNote(note.toNoteEntity()) }
    }

    fun updateNote(note: NoteUiModel) {
        viewModelScope.launch { notesRepository.updateNote(note.toNoteEntity()) }
    }

    fun deleteNote(vararg notes: NoteUiModel) {
        viewModelScope.launch { notesRepository.deleteNotes(*notes.map(NoteUiModel::toNoteEntity).toTypedArray()) }
    }

}
