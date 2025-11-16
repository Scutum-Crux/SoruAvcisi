package com.examaid.app.presentation.auth

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.examaid.app.core.util.Resource
import com.examaid.app.core.util.UiEvent
import com.examaid.app.domain.usecase.auth.SendPasswordResetEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val sendPasswordResetEmailUseCase: SendPasswordResetEmailUseCase
) : ViewModel() {

    private val logTag = "ForgotPasswordVM"

    private val _state = mutableStateOf(ForgotPasswordState())
    val state: State<ForgotPasswordState> = _state

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    private var lastResetAt = 0L
    private val cooldownMillis = 60_000L

    fun onEvent(event: ForgotPasswordEvent) {
        when (event) {
            is ForgotPasswordEvent.EmailChanged -> {
                if (event.email.length <= 96) {
                    _state.value = _state.value.copy(email = event.email, error = null)
                }
            }
            ForgotPasswordEvent.SendReset -> sendResetEmail()
        }
    }

    private fun sendResetEmail() {
        viewModelScope.launch {
            Log.d(logTag, "sendResetEmail() called with email=${_state.value.email}")
            println("$logTag: sendResetEmail() called with email=${_state.value.email}")
            val now = System.currentTimeMillis()
            if (now - lastResetAt < cooldownMillis) {
                val remaining = (cooldownMillis - (now - lastResetAt)) / 1000
                _uiEvent.send(UiEvent.ShowSnackbar("Lütfen $remaining saniye sonra tekrar deneyin."))
                return@launch
            }

            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = sendPasswordResetEmailUseCase(_state.value.email.trim())) {
                is Resource.Success -> {
                    lastResetAt = now
                    _state.value = _state.value.copy(isLoading = false, error = null)
                    _uiEvent.send(UiEvent.ShowSnackbar("Şifre sıfırlama e-postası gönderildi."))
                    Log.d(logTag, "sendResetEmail success")
                    println("$logTag: sendResetEmail success")
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                    _uiEvent.send(
                        UiEvent.ShowSnackbar(result.message ?: "Şifre sıfırlama e-postası gönderilemedi.")
                    )
                    Log.w(logTag, "sendResetEmail failed: ${result.message}")
                    println("$logTag: sendResetEmail failed: ${result.message}")
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }
}

data class ForgotPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class ForgotPasswordEvent {
    data class EmailChanged(val email: String) : ForgotPasswordEvent()
    data object SendReset : ForgotPasswordEvent()
}

