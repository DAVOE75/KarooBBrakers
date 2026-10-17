package com.davoe.karoobbrakers.ui.theme

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextGeometricTransform
import com.davoe.karoobbrakers.R

val DarkCarbon = Color(0xFF0F1318)
val DarkSurface = Color(0xFF181D24)
val DarkCard = Color(0xFF212833)
val AccentOrange = Color(0xFFFF6D00)
val AccentCyan = Color(0xFF00E5FF)
val AccentRed = Color(0xFFFF1744)
val AccentGreen = Color(0xFF00E676)
val TextPrimary = Color(0xFFECEFF1)
val TextSecondary = Color(0xFF90A4AE)

val BarlowCondensed = FontFamily(
    Font(R.font.barlow_condensed_regular, FontWeight.Normal),
    Font(R.font.barlow_condensed_medium, FontWeight.Medium),
    Font(R.font.barlow_condensed_semibold, FontWeight.SemiBold),
    Font(R.font.barlow_condensed_bold, FontWeight.Bold),
    Font(R.font.barlow_condensed_bold, FontWeight.ExtraBold),
)

// Tipografía Barlow Condensed con grosor 700 (Bold) para pantallas de datos
val DataScreenStyleBold700 = TextStyle(
    fontFamily = BarlowCondensed,
    fontWeight = FontWeight.Bold,
)

// Tipografía Barlow Condensed con grosor 700 (Bold) y ancho 50% para cifras numéricas
val DataScreenNumberStyle50Width = TextStyle(
    fontFamily = BarlowCondensed,
    fontWeight = FontWeight.Bold,
    textGeometricTransform = TextGeometricTransform(scaleX = 0.5f),
)

// Contenedor temático que inyecta Barlow Condensed Bold 700 exclusivamente en las pantallas de datos
@Composable
fun DataScreenContainer(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = BarlowCondensed,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
        )
    ) {
        content()
    }
}

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

/**
 * Tema principal para el panel de control de la extensión.
 * Mantiene la tipografía estándar del sistema (Roboto) para una legibilidad
 * limpia y nativa en menús, ajustes, selectores, guías y configuración.
 */
@Composable
fun KarooBBrakersTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content,
    )
}


