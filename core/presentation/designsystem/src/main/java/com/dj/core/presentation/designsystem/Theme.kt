package com.dj.core.presentation.designsystem

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val DarkColorScheme = darkColorScheme(
    primary = RunGreen,
    background = RunBlack,
    surface = RunDarkGray, // background of dialogs
    secondary = RunWhite,
    tertiary = RunWhite,
    primaryContainer = RunGreen30,
    onPrimary = RunBlack,
    onBackground = RunWhite,
    onSurface = RunWhite,
    onSurfaceVariant = RunGray,
    error = RunDarkRed,
    errorContainer = RunDarkRed5
)

@Composable
fun RunTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme (
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}