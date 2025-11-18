package com.examaid.app.presentation.auth

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examaid.app.core.navigation.Screen
import com.examaid.app.core.util.Resource
import com.examaid.app.core.util.UiEvent
import com.examaid.app.data.local.preferences.UserPreferences
import com.examaid.app.domain.usecase.auth.LoginWithEmailUseCase
import com.examaid.app.domain.usecase.auth.LoginWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginWithEmailUseCase: LoginWithEmailUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val userPreferences: UserPreferences
) : ViewModel() {
    
    private val logTag = "LoginViewModel"
    
    private val _state = mutableStateOf(LoginState())
    val state: State<LoginState> = _state
    
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var lastEmailLoginAt = 0L
    private var lastGoogleLoginAt = 0L
    private val cooldownMillis = 2_000L
    
    init {
        loadSavedCredentials()
    }
    
    private fun loadSavedCredentials() {
        viewModelScope.launch {
            val rememberMe = userPreferences.rememberMe.first()
            if (rememberMe) {
                val email = userPreferences.savedEmail.first()
                val password = userPreferences.savedPassword.first()
                _state.value = _state.value.copy(
                    email = email ?: "",
                    password = password ?: "",
                    rememberMe = true
                )
            }
        }
    }
    
    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                if (event.email.length <= 96) {
                    _state.value = _state.value.copy(email = event.email)
                }
            }
            is LoginEvent.PasswordChanged -> {
                if (event.password.length <= 64) {
                    _state.value = _state.value.copy(password = event.password)
                }
            }
            is LoginEvent.TogglePasswordVisibility -> {
                _state.value = _state.value.copy(
                    isPasswordVisible = !_state.value.isPasswordVisible
                )
            }
            is LoginEvent.ToggleRememberMe -> {
                _state.value = _state.value.copy(
                    rememberMe = !_state.value.rememberMe
                )
            }
            is LoginEvent.Login -> {
                loginWithEmail()
            }
            is LoginEvent.LoginWithGoogle -> {
                loginWithGoogle(event.idToken)
            }
            is LoginEvent.NavigateToRegister -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.Navigate(Screen.Register.route))
                }
            }
            is LoginEvent.ForgotPassword -> {
                viewModelScope.launch {
                    _uiEvent.send(UiEvent.Navigate(Screen.ForgotPassword.route))
                }
            }
        }
    }
    
    private fun loginWithEmail() {
        viewModelScope.launch {
            Log.d(logTag, "loginWithEmail() called with email=${_state.value.email}")
            println("$logTag: loginWithEmail() called with email=${_state.value.email}")
            val now = System.currentTimeMillis()
            if (now - lastEmailLoginAt < cooldownMillis) {
                val waitSeconds = (cooldownMillis - (now - lastEmailLoginAt)) / 1000
                _uiEvent.send(UiEvent.ShowSnackbar("Lütfen $waitSeconds saniye sonra tekrar deneyin."))
                return@launch
            }
            lastEmailLoginAt = now

            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = try {
                loginWithEmailUseCase(
                    email = _state.value.email.trim(),
                    password = _state.value.password
                )
            } catch (e: Exception) {
                Log.e(logTag, "loginWithEmailUseCase threw an exception", e)
                println("$logTag: loginWithEmailUseCase exception=${e.localizedMessage}")
                Resource.Error(e.localizedMessage ?: "Beklenmedik bir hata oluştu.")
            }

            _state.value = _state.value.copy(isLoading = false)
            Log.d(logTag, "loginWithEmail result=${result::class.simpleName}, message=${result.message}")
            println("$logTag: loginWithEmail result=${result::class.simpleName}, message=${result.message}")

            when (result) {
                is Resource.Success -> {
                    Log.d(logTag, "loginWithEmail successful for ${_state.value.email}")
                    println("$logTag: loginWithEmail success for ${_state.value.email}")
                    
                    // Beni hatırla seçiliyse kimlik bilgilerini kaydet
                    if (_state.value.rememberMe) {
                        userPreferences.saveCredentials(
                            email = _state.value.email,
                            password = _state.value.password,
                            remember = true
                        )
                    } else {
                        userPreferences.clearCredentials()
                    }
                    
                    _uiEvent.send(UiEvent.Navigate(Screen.Dashboard.route))
                }
                is Resource.Error -> {
                    Log.w(logTag, "loginWithEmail failed: ${result.message}")
                    println("$logTag: loginWithEmail failed: ${result.message}")
                    lastEmailLoginAt = 0L
                    _state.value = _state.value.copy(error = result.message)
                    _uiEvent.send(UiEvent.ShowSnackbar(result.message ?: "Giriş başarısız"))
                }
                is Resource.Loading -> Unit
            }
        }
    }
    
    private fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            if (idToken.isBlank()) {
                Log.w(logTag, "loginWithGoogle called with blank idToken")
                println("$logTag: loginWithGoogle called with blank idToken")
                _uiEvent.send(UiEvent.ShowSnackbar("Google girişi başarısız."))
                return@launch
            }

            val now = System.currentTimeMillis()
            if (now - lastGoogleLoginAt < cooldownMillis) {
                val waitSeconds = (cooldownMillis - (now - lastGoogleLoginAt)) / 1000
                _uiEvent.send(UiEvent.ShowSnackbar("Lütfen $waitSeconds saniye sonra tekrar deneyin."))
                println("$logTag: loginWithGoogle throttled, waitSeconds=$waitSeconds")
                return@launch
            }
            lastGoogleLoginAt = now

            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = try {
                loginWithGoogleUseCase(idToken)
            } catch (e: Exception) {
                Log.e(logTag, "loginWithGoogleUseCase threw an exception", e)
                println("$logTag: loginWithGoogleUseCase exception=${e.localizedMessage}")
                Resource.Error(e.localizedMessage ?: "Beklenmedik bir hata oluştu.")
            }

            _state.value = _state.value.copy(isLoading = false)

            when (result) {
                is Resource.Success -> {
                    Log.d(logTag, "loginWithGoogle successful")
                    println("$logTag: loginWithGoogle success")
                    _uiEvent.send(UiEvent.Navigate(Screen.Dashboard.route))
                }
                is Resource.Error -> {
                    Log.w(logTag, "loginWithGoogle failed: ${result.message}")
                    println("$logTag: loginWithGoogle failed: ${result.message}")
                    lastGoogleLoginAt = 0L
                    _state.value = _state.value.copy(error = result.message)
                    _uiEvent.send(UiEvent.ShowSnackbar(result.message ?: "Google girişi başarısız"))
                }
                is Resource.Loading -> Unit
            }
        }
    }
}

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val rememberMe: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class LoginEvent {
    data class EmailChanged(val email: String) : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()
    data object TogglePasswordVisibility : LoginEvent()
    data object ToggleRememberMe : LoginEvent()
    data object Login : LoginEvent()
    data class LoginWithGoogle(val idToken: String) : LoginEvent()
    data object NavigateToRegister : LoginEvent()
    data object ForgotPassword : LoginEvent()
}

