package com.examaid.app.domain.usecase.auth

import com.examaid.app.core.util.Resource
import android.util.Log
import com.examaid.app.domain.model.User
import com.examaid.app.domain.repository.AuthRepository
import javax.inject.Inject

class LoginWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Resource<User> {
        println("LoginWithEmailUseCase: START")
        Log.d("LoginWithEmailUseCase", "START")
        
        if (email.isBlank()) {
            println("LoginWithEmailUseCase: email blank")
            return Resource.Error("E-posta adresi boş olamaz")
        }
        
        if (password.isBlank()) {
            println("LoginWithEmailUseCase: password blank")
            return Resource.Error("Şifre boş olamaz")
        }
        
        if (password.length < 6) {
            println("LoginWithEmailUseCase: password too short")
            return Resource.Error("Şifre en az 6 karakter olmalıdır")
        }
        
        println("LoginWithEmailUseCase: calling repository")
        val trimmedEmail = email.trim()
        val result = authRepository.loginWithEmail(trimmedEmail, password)
        println("LoginWithEmailUseCase: GOT RESULT type=${result::class.simpleName}")
        Log.d("LoginWithEmailUseCase", "GOT RESULT type=${result::class.simpleName}")
        return result
    }
}

