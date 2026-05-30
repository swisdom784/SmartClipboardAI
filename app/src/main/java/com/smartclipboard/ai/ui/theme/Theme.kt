package com.smartclipboard.ai.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = SamsungBlue,
    onPrimary = AppSurface,
    primaryContainer = BlueSoft,
    onPrimaryContainer = SamsungBlueDark,
    background = AppBackground,
    onBackground = AppText,
    surface = AppSurface,
    onSurface = AppText,
    outline = AppOutline
)

private val DarkColorScheme = darkColorScheme(
    primary = SamsungBlueDarkMode
)

@Composable
fun SmartClipboardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
