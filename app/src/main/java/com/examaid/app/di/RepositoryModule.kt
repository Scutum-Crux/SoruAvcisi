package com.examaid.app.di

import com.examaid.app.data.repository.AuthRepositoryImpl
import com.examaid.app.data.repository.PhotoNoteRepositoryImpl
import com.examaid.app.domain.repository.AuthRepository
import com.examaid.app.domain.repository.PhotoNoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindPhotoNoteRepository(
        photoNoteRepositoryImpl: PhotoNoteRepositoryImpl
    ): PhotoNoteRepository
}

