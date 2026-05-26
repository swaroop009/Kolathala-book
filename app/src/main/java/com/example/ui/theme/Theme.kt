package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = TailorPrimary,
    onPrimary = Color.White,
    secondary = TailorSecondary,
    onSecondary = Color.White,
    tertiary = TailorTertiary,
    background = TailorBackground,
    onBackground = TailorTertiary,
    surface = TailorSurface,
    onSurface = TailorTertiary,
    surfaceVariant = TailorLightHover,
    onSurfaceVariant = TailorSecondary,
    outline = TailorBorder,
    error = TailorError,
    onError = Color.White,
    primaryContainer = SleekPurpleLight,
    onPrimaryContainer = SleekPurpleDark,
    errorContainer = TailorErrorContainer,
    onErrorContainer = TailorError
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set to false by default to ensure the elegant Slate styling is applied:
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> LightColorScheme // Focused on light, clean theme as requested
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
