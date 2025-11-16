package com.examaid.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.examaid.app.data.local.dao.PhotoNoteDao
import com.examaid.app.data.local.entity.PhotoNoteEntity

@Database(
    entities = [PhotoNoteEntity::class],
    version = com.examaid.app.core.constants.Constants.DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ExamAidDatabase : RoomDatabase() {
    abstract fun photoNoteDao(): PhotoNoteDao
}

