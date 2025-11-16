package com.examaid.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.examaid.app.data.local.entity.PhotoNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoNoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: PhotoNoteEntity): Long

    @Query("SELECT * FROM photo_notes ORDER BY createdAt DESC")
    fun observeNotes(): Flow<List<PhotoNoteEntity>>
}
