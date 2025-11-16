package com.examaid.app.domain.usecase.auth

import com.examaid.app.core.util.Resource
import com.examaid.app.domain.model.User
import com.examaid.app.domain.repository.AuthRepository
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Resource<User> {
        if (idToken.isBlank()) {
            return Resource.Error("Google oturum açma başarısız")
        }
        
        return authRepository.loginWithGoogle(idToken)
    }
}

