package com.examaid.app.domain.usecase.auth

import com.examaid.app.core.util.Resource
import com.examaid.app.core.util.isValidEmail
import com.examaid.app.domain.model.User
import com.examaid.app.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String,
        displayName: String
    ): Resource<User> {
        if (email.isBlank()) {
            return Resource.Error("E-posta adresi boş olamaz")
        }
        
        if (!email.isValidEmail()) {
            return Resource.Error("Geçersiz e-posta adresi")
        }
        
        if (displayName.isBlank()) {
            return Resource.Error("İsim boş olamaz")
        }
        
        if (password.isBlank()) {
            return Resource.Error("Şifre boş olamaz")
        }
        
        if (password.length < 6) {
            return Resource.Error("Şifre en az 6 karakter olmalıdır")
        }

        if (password.none { it.isUpperCase() }) {
            return Resource.Error("Şifre en az bir büyük harf içermelidir")
        }
        
        if (password != confirmPassword) {
            return Resource.Error("Şifreler eşleşmiyor")
        }
        
        return authRepository.registerWithEmail(email, password, displayName)
    }
}

