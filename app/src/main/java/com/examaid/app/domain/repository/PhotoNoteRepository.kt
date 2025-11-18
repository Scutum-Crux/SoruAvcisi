package com.examaid.app.domain.repository

import com.examaid.app.core.util.Resource
import com.examaid.app.domain.model.PhotoNote
import com.examaid.app.domain.model.PhotoReason
import kotlinx.coroutines.flow.Flow

interface PhotoNoteRepository {
    suspend fun savePhotoNote(
        imageUri: String,
        subject: String,
        reason: PhotoReason,
        note: String?
    ): Resource<PhotoNote>

    fun observePhotoNotes(): Flow<List<PhotoNote>>
}




