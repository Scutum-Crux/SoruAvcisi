package com.examaid.app.presentation.upload

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examaid.app.core.util.Resource
import com.examaid.app.domain.model.PhotoNote
import com.examaid.app.domain.model.PhotoReason
import com.examaid.app.domain.usecase.photo.SavePhotoNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class UploadPhotoViewModel @Inject constructor(
    private val savePhotoNoteUseCase: SavePhotoNoteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(UploadPhotoUiState())
    val state: StateFlow<UploadPhotoUiState> = _state.asStateFlow()

    fun selectSubject(subject: String) {
        _state.update { it.copy(selectedSubject = subject, errorMessage = null) }
    }

    fun selectReason(reason: PhotoReason) {
        _state.update { it.copy(selectedReason = reason, errorMessage = null) }
    }

    fun updateNote(note: String) {
        val trimmed = note.take(MAX_NOTE_LENGTH)
        _state.update { it.copy(note = trimmed) }
    }

    fun resetAfterImageChange() {
        _state.update {
            it.copy(
                selectedSubject = null,
                selectedReason = null,
                note = "",
                savedNote = null,
                errorMessage = null,
                isSaving = false
            )
        }
    }

    fun savePhotoNote(imageUri: Uri) {
        val subject = state.value.selectedSubject
        val reason = state.value.selectedReason

        if (subject == null || reason == null) {
            _state.update {
                it.copy(errorMessage = "Lütfen ders ve sebep seçin.")
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, errorMessage = null, savedNote = null) }
            when (val result = savePhotoNoteUseCase(
                imageUri = imageUri,
                subject = subject,
                reason = reason,
                note = state.value.note
            )) {
                is Resource.Success -> {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            savedNote = result.data
                        )
                    }
                }
                is Resource.Error -> {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = result.message ?: "Fotoğraf kaydedilemedi."
                        )
                    }
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun consumeError() {
        _state.update { it.copy(errorMessage = null) }
    }

    companion object {
        const val MAX_NOTE_LENGTH = 50
        val SUBJECTS = listOf(
            "Matematik",
            "Geometri",
            "Türkçe",
            "Edebiyat",
            "Tarih",
            "Coğrafya",
            "Felsefe",
            "Din Kültürü",
            "Fizik",
            "Kimya",
            "Biyoloji"
        )
        val REASONS = PhotoReason.values().toList()
    }
}

data class UploadPhotoUiState(
    val subjects: List<String> = UploadPhotoViewModel.SUBJECTS,
    val selectedSubject: String? = null,
    val selectedReason: PhotoReason? = null,
    val reasons: List<PhotoReason> = UploadPhotoViewModel.REASONS,
    val note: String = "",
    val isSaving: Boolean = false,
    val savedNote: PhotoNote? = null,
    val errorMessage: String? = null
)
