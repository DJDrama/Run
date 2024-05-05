package com.dj.www

import androidx.compose.runtime.mutableStateOf

data class MainState(
    val isLoggedIn: Boolean  = false,
    val isCheckingAuth: Boolean = false,
)