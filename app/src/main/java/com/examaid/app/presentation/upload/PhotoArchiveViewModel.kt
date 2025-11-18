package com.examaid.app.presentation.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examaid.app.domain.model.PhotoNote
import com.examaid.app.domain.usecase.photo.ObservePhotoNotesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class PhotoArchiveViewModel @Inject constructor(
    observePhotoNotesUseCase: ObservePhotoNotesUseCase
) : ViewModel() {

    val state: StateFlow<PhotoArchiveUiState> = observePhotoNotesUseCase()
        .map { notes -> PhotoArchiveUiState(items = notes) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PhotoArchiveUiState()
        )
}

data class PhotoArchiveUiState(
    val items: List<PhotoNote> = emptyList()
)




