package com.dj.auth.presentation.login

import com.dj.core.presentation.ui.UiText

sealed interface LoginEvent {
    data class Error(val error: UiText): LoginEvent

    data object LoginSuccess: LoginEvent
}