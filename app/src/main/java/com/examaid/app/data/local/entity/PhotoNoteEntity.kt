package com.examaid.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photo_notes")
data class PhotoNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imageUri: String,
    val subject: String,
    val reason: String,
    val note: String?,
    val createdAt: Long
)




