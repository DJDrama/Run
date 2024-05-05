package com.dj.auth.presentation.register

import com.dj.core.presentation.ui.UiText

sealed interface RegisterEvent{
    data object RegistrationSuccess: RegisterEvent
    data class Error(val error: UiText): RegisterEvent
}