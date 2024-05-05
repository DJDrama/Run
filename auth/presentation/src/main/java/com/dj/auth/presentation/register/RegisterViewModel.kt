package com.dj.auth.presentation.register

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.textAsFlow
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dj.auth.domain.UserDataValidator
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalFoundationApi::class)
class RegisterViewModel
constructor(
    private val userDataValidator: UserDataValidator
) : ViewModel() {

    var state by mutableStateOf(value = RegisterState())
        private set

    init {
        state.email.textAsFlow()
            .onEach { email ->
                val isValidEmail = userDataValidator.isValidEmail(email = email.toString())
                state =
                    state.copy(
                        isEmailValid = isValidEmail,
                        canRegister = isValidEmail && state.passwordValidationState.isValidPassword && !state.isRegistering
                    )
            }
            .catch { }
            .launchIn(viewModelScope)

        state.password.textAsFlow()
            .onEach { password ->
                val passwordValidationState = userDataValidator.validatePassword(password = password.toString())
                state = state.copy(
                    passwordValidationState = passwordValidationState,
                    canRegister = state.isEmailValid && state.passwordValidationState.isValidPassword && !state.isRegistering
                )
            }
            .catch { }
            .launchIn(viewModelScope)
    }

    fun onAction(action: RegisterAction) {

    }
}