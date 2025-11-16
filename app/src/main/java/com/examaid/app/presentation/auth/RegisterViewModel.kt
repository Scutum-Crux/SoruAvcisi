package com.examaid.app.presentation.auth

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examaid.app.core.navigation.Screen
import com.examaid.app.core.util.Resource
import com.examaid.app.core.util.UiEvent
import com.examaid.app.domain.usecase.auth.LoginWithGoogleUseCase
import com.examaid.app.domain.usecase.auth.RegisterWithEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerWithEmailUseCase: RegisterWithEmailUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase
) : ViewModel() {
    
    private val logTag = "RegisterViewModel"
    
    private val _state = mutableStateOf(RegisterState())
    val state: State<RegisterState> = _state
    
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var lastRegisterAt = 0L
    private var lastGoogleAt = 0L
    private val cooldownMillis = 2_000L
    
    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.DisplayNameChanged -> {
                if (event.displayName.length <= 48) {
                    _state.value = _state.value.copy(displayName = event.displayName)
                }
            }
            is RegisterEvent.EmailChanged -> {
                if (event.email.length <= 96) {
                    _state.value = _state.value.copy(email = event.email)
                }
            }
            is RegisterEvent.PasswordChanged -> {
                if (event.password.length <= 64) {
                    _state.value = _state.value.copy(
                        password = event.password,
                        passwordError = null
                    )
                }
            }
            is RegisterEvent.ConfirmPasswordChanged -> {
                if (event.confirmPassword.length <= 64) {
                    _state.value = _state.value.copy(
                        confirmPassword = event.confirmPassword,
                        confirmPasswordError = null
                    )
                }
            }
            is RegisterEvent.TogglePasswordVisibility -> {
                _state.value = _state.value.copy(
                    isPasswordVisible = !_state.value.isPasswordVisible
                )
            }
            is RegisterEvent.ToggleConfirmPasswordVisibility -> {
                _state.value = _state.value.copy(
                    isConfirmPasswordVisible = !_state.value.isConfirmPasswordVisible
                )
            }
            is RegisterEvent.Register -> {
                register()
            }
            is RegisterEvent.LoginWithGoogle -> {
                loginWithGoogle(event.idToken)
            }
            is RegisterEvent.NavigateToLogin -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.NavigateUp)
                }
            }
        }
    }

    private fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            if (idToken.isBlank()) {
                _uiEvent.send(UiEvent.ShowSnackbar("Google girişi başarısız."))
                println("$logTag: loginWithGoogle called with blank idToken")
                return@launch
            }

            val now = System.currentTimeMillis()
            if (now - lastGoogleAt < cooldownMillis) {
                val waitSeconds = (cooldownMillis - (now - lastGoogleAt)) / 1000
                _uiEvent.send(UiEvent.ShowSnackbar("Lütfen $waitSeconds saniye sonra tekrar deneyin."))
                println("$logTag: loginWithGoogle throttled, waitSeconds=$waitSeconds")
                return@launch
            }
            lastGoogleAt = now

            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = try {
                loginWithGoogleUseCase(idToken)
            } catch (e: Exception) {
                Log.e(logTag, "loginWithGoogleUseCase threw", e)
                println("$logTag: loginWithGoogleUseCase exception=${e.localizedMessage}")
                Resource.Error(e.localizedMessage ?: "Beklenmedik bir hata oluştu.")
            }
            when (result) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(isLoading = false)
                    Log.d(logTag, "loginWithGoogle success")
                     println("$logTag: loginWithGoogle success")
                    _uiEvent.send(UiEvent.Navigate(Screen.Dashboard.route))
                }
                is Resource.Error -> {
                    lastGoogleAt = 0L
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                    _uiEvent.send(UiEvent.ShowSnackbar(result.message ?: "Google girişi başarısız"))
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }
    
    private fun register() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            if (now - lastRegisterAt < cooldownMillis) {
                val waitSeconds = (cooldownMillis - (now - lastRegisterAt)) / 1000
                _uiEvent.send(UiEvent.ShowSnackbar("Lütfen $waitSeconds saniye sonra tekrar deneyin."))
                return@launch
            }
            lastRegisterAt = now

            _state.value = _state.value.copy(
                isLoading = true,
                error = null,
                passwordError = null,
                confirmPasswordError = null
            )

            val password = _state.value.password
            val confirmPassword = _state.value.confirmPassword
            val passwordErrors = mutableListOf<String>()

            if (password.length < 6) {
                passwordErrors.add("Şifre en az 6 karakter olmalıdır.")
            }
            if (password.none { it.isUpperCase() }) {
                passwordErrors.add("Şifre en az bir büyük harf içermelidir.")
            }

            val passwordError = passwordErrors.joinToString("\n").takeIf { it.isNotBlank() }
            val confirmError = if (password != confirmPassword) "Şifreler eşleşmiyor." else null

            if (passwordError != null || confirmError != null) {
                _state.value = _state.value.copy(
                    passwordError = passwordError,
                    confirmPasswordError = confirmError
                )
                _state.value = _state.value.copy(isLoading = false)
                lastRegisterAt = 0L
                Log.w(
                    logTag,
                    "register validation failed passwordError=$passwordError confirmError=$confirmError"
                )
                println("$logTag: register validation failed passwordError=$passwordError confirmError=$confirmError")
                _uiEvent.send(
                    UiEvent.ShowSnackbar(
                        confirmError ?: passwordError ?: "Şifre doğrulaması başarısız."
                    )
                )
                return@launch
            }

            val result = try {
                registerWithEmailUseCase(
                    email = _state.value.email.trim(),
                    password = _state.value.password,
                    confirmPassword = _state.value.confirmPassword,
                    displayName = _state.value.displayName.trim()
                )
            } catch (e: Exception) {
                Log.e(logTag, "registerWithEmailUseCase threw", e)
                println("$logTag: registerWithEmailUseCase exception=${e.localizedMessage}")
                Resource.Error(e.localizedMessage ?: "Beklenmedik bir hata oluştu.")
            }

            _state.value = _state.value.copy(isLoading = false)

            when (result) {
                is Resource.Success -> {
                    Log.d(logTag, "register success for ${_state.value.email}")
                    println("$logTag: register success for ${_state.value.email}")
                    _uiEvent.send(
                        UiEvent.ShowSnackbar("Kayıt başarılı! Lütfen e-posta kutunu kontrol et.")
                    )
                    _uiEvent.send(UiEvent.Navigate(Screen.Dashboard.route))
                }
                is Resource.Error -> {
                    lastRegisterAt = 0L
                    Log.w(logTag, "register failed: ${result.message}")
                    println("$logTag: register failed: ${result.message}")
                    _state.value = _state.value.copy(error = result.message)
                    _uiEvent.send(UiEvent.ShowSnackbar(result.message ?: "Kayıt başarısız"))
                }
                is Resource.Loading -> Unit
            }
        }
    }
}

data class RegisterState(
    val displayName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)

sealed class RegisterEvent {
    data class DisplayNameChanged(val displayName: String) : RegisterEvent()
    data class EmailChanged(val email: String) : RegisterEvent()
    data class PasswordChanged(val password: String) : RegisterEvent()
    data class ConfirmPasswordChanged(val confirmPassword: String) : RegisterEvent()
    data object TogglePasswordVisibility : RegisterEvent()
    data object ToggleConfirmPasswordVisibility : RegisterEvent()
    data object Register : RegisterEvent()
    data class LoginWithGoogle(val idToken: String) : RegisterEvent()
    data object NavigateToLogin : RegisterEvent()
}

