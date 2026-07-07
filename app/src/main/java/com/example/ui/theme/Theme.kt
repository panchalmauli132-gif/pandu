package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    secondary = NeonPurple,
    tertiary = BrightCyan,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = TextWhite,
    onSurface = TextWhite,
    onSurfaceVariant = TextSilver,
    error = NeonPink
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF3F51B5),
    secondary = Color(0xFF7E57C2),
    tertiary = Color(0xFF00ACC1),
    background = Color(0xFFF5F6FC),
    surface = Color.White,
    surfaceVariant = Color(0xFFECEFF1),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1D24),
    onSurface = Color(0xFF1C1D24),
    onSurfaceVariant = Color(0xFF546E7A),
    error = Color(0xFFD32F2F)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme by default as requested
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
