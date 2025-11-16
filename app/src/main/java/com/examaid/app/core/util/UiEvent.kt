package com.examaid.app.core.util

sealed class UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent()
    data class Navigate(val route: String) : UiEvent()
    data object NavigateUp : UiEvent()
}

