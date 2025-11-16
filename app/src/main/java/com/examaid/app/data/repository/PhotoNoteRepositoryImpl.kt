package com.examaid.app.data.repository

import com.examaid.app.core.util.Resource
import com.examaid.app.data.local.dao.PhotoNoteDao
import com.examaid.app.data.local.entity.PhotoNoteEntity
import com.examaid.app.domain.model.PhotoNote
import com.examaid.app.domain.model.PhotoReason
import com.examaid.app.domain.repository.PhotoNoteRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

@Singleton
class PhotoNoteRepositoryImpl @Inject constructor(
    private val photoNoteDao: PhotoNoteDao
) : PhotoNoteRepository {

    override suspend fun savePhotoNote(
        imageUri: String,
        subject: String,
        reason: PhotoReason,
        note: String?
    ): Resource<PhotoNote> {
        return try {
            val entity = PhotoNoteEntity(
                imageUri = imageUri,
                subject = subject,
                reason = reason.name,
                note = note?.ifBlank { null },
                createdAt = System.currentTimeMillis()
            )
            val id = photoNoteDao.insert(entity)
            val saved = entity.copy(id = id)
            Resource.Success(saved.toDomain())
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Fotoğraf kaydedilirken hata oluştu.")
        }
    }

    override fun observePhotoNotes(): Flow<List<PhotoNote>> {
        return photoNoteDao.observeNotes().map { list ->
            list.map { it.toDomain() }
        }
    }

    private fun PhotoNoteEntity.toDomain(): PhotoNote {
        return PhotoNote(
            id = id,
            imageUri = imageUri,
            subject = subject,
            reason = runCatching { PhotoReason.valueOf(reason) }.getOrDefault(PhotoReason.NEW_LEARNING),
            note = note,
            createdAt = Instant.ofEpochMilli(createdAt)
        )
    }
}
