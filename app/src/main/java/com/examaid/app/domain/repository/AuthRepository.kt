package com.examaid.app.domain.repository

import com.examaid.app.core.util.Resource
import com.examaid.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    
    suspend fun loginWithEmail(email: String, password: String): Resource<User>
    
    suspend fun registerWithEmail(
        email: String,
        password: String,
        displayName: String
    ): Resource<User>
    
    suspend fun loginWithGoogle(idToken: String): Resource<User>
    
    suspend fun logout()
    
    suspend fun getCurrentUser(): User?
    
    fun isUserLoggedIn(): Boolean
    
    suspend fun sendPasswordResetEmail(email: String): Resource<Unit>
    
    fun observeAuthState(): Flow<User?>
}

