package com.davoe.karoobbrakers.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkCarbon = Color(0xFF0F1318)
val DarkSurface = Color(0xFF181D24)
val DarkCard = Color(0xFF212833)
val AccentOrange = Color(0xFFFF6D00)
val AccentCyan = Color(0xFF00E5FF)
val AccentRed = Color(0xFFFF1744)
val AccentGreen = Color(0xFF00E676)
val TextPrimary = Color(0xFFECEFF1)
val TextSecondary = Color(0xFF90A4AE)

private val DarkColorScheme = darkColorScheme(
    primary = AccentOrange,
    secondary = AccentCyan,
    tertiary = AccentGreen,
    background = DarkCarbon,
    surface = DarkSurface,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
)

@Composable
fun KarooBBrakersTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content,
    )
}
