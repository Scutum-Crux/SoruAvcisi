package com.examaid.app.di

import android.content.Context
import androidx.room.Room
import com.examaid.app.core.constants.Constants
import com.examaid.app.data.local.database.ExamAidDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideExamAidDatabase(
        @ApplicationContext context: Context
    ): ExamAidDatabase {
        return Room.databaseBuilder(
            context,
            ExamAidDatabase::class.java,
            Constants.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun providePhotoNoteDao(database: ExamAidDatabase) = database.photoNoteDao()

    @Provides
    @Singleton
    fun provideFlashcardDao(database: ExamAidDatabase) = database.flashcardDao()
}

