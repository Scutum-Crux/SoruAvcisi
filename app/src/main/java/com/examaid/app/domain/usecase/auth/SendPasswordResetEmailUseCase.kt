package com.examaid.app.domain.usecase.auth

import com.examaid.app.core.util.Resource
import com.examaid.app.core.util.isValidEmail
import com.examaid.app.domain.repository.AuthRepository
import javax.inject.Inject

class SendPasswordResetEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Resource<Unit> {
        if (email.isBlank()) {
            return Resource.Error("E-posta adresi boş olamaz")
        }

        if (!email.isValidEmail()) {
            return Resource.Error("Geçersiz e-posta adresi")
        }

        return authRepository.sendPasswordResetEmail(email)
    }
}

