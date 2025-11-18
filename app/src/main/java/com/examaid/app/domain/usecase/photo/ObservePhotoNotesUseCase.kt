package com.examaid.app.domain.usecase.photo

import com.examaid.app.domain.model.PhotoNote
import com.examaid.app.domain.repository.PhotoNoteRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObservePhotoNotesUseCase @Inject constructor(
    private val repository: PhotoNoteRepository
) {
    operator fun invoke(): Flow<List<PhotoNote>> = repository.observePhotoNotes()
}




