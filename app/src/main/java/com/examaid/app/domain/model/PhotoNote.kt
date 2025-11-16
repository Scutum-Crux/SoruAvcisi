package com.examaid.app.domain.model

import java.time.Instant

/**
 * Represents a saved question photo with metadata supplied by the user.
 */
data class PhotoNote(
    val id: Long,
    val imageUri: String,
    val subject: String,
    val reason: PhotoReason,
    val note: String?,
    val createdAt: Instant
)

