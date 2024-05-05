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
                state =
                    state.copy(
                        isEmailValid = userDataValidator.isValidEmail(email = email.toString())
                    )
            }
            .catch { }
            .launchIn(viewModelScope)

        state.password.textAsFlow()
            .onEach { password ->
                state = state.copy(
                    passwordValidationState = userDataValidator.validatePassword(password = password.toString())
                )
            }
            .catch { }
            .launchIn(viewModelScope)
    }

    fun onAction(action: RegisterAction) {

    }
}