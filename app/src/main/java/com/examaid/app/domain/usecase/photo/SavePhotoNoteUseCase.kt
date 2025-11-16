package com.examaid.app.domain.usecase.photo

import android.net.Uri
import com.examaid.app.core.util.Resource
import com.examaid.app.domain.model.PhotoNote
import com.examaid.app.domain.model.PhotoReason
import com.examaid.app.domain.repository.PhotoNoteRepository
import javax.inject.Inject

class SavePhotoNoteUseCase @Inject constructor(
    private val repository: PhotoNoteRepository
) {
    suspend operator fun invoke(
        imageUri: Uri,
        subject: String,
        reason: PhotoReason,
        note: String?
    ): Resource<PhotoNote> {
        if (subject.isBlank()) {
            return Resource.Error("Lütfen bir ders seçin.")
        }

        return repository.savePhotoNote(
            imageUri = imageUri.toString(),
            subject = subject,
            reason = reason,
            note = note?.trim().takeIf { !it.isNullOrEmpty() }
        )
    }
}

