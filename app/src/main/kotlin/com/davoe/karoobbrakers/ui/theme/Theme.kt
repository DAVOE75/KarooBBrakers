package com.davoe.karoobbrakers.ui.theme

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
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

val KarooTypography = Typography(
    displayLarge = TextStyle(fontFamily = BarlowCondensed, fontWeight = FontWeight.Bold, fontSize = 57.sp),
    displayMedium = TextStyle(fontFamily = BarlowCondensed, fontWeight = FontWeight.Bold, fontSize = 45.sp),
    displaySmall = TextStyle(fontFamily = BarlowCondensed, fontWeight = FontWeight.Bold, fontSize = 36.sp),
    headlineLarge = TextStyle(fontFamily = BarlowCondensed, fontWeight = FontWeight.Bold, fontSize = 32.sp),
    headlineMedium = TextStyle(fontFamily = BarlowCondensed, fontWeight = FontWeight.Bold, fontSize = 28.sp),
    headlineSmall = TextStyle(fontFamily = BarlowCondensed, fontWeight = FontWeight.Bold, fontSize = 24.sp),
    titleLarge = TextStyle(fontFamily = BarlowCondensed, fontWeight = FontWeight.Bold, fontSize = 22.sp),
    titleMedium = TextStyle(fontFamily = BarlowCondensed, fontWeight = FontWeight.Bold, fontSize = 16.sp),
    titleSmall = TextStyle(fontFamily = BarlowCondensed, fontWeight = FontWeight.Bold, fontSize = 14.sp),
    bodyLarge = TextStyle(fontFamily = BarlowCondensed, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = BarlowCondensed, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = BarlowCondensed, fontWeight = FontWeight.Normal, fontSize = 12.sp),
    labelLarge = TextStyle(fontFamily = BarlowCondensed, fontWeight = FontWeight.Bold, fontSize = 14.sp),
    labelMedium = TextStyle(fontFamily = BarlowCondensed, fontWeight = FontWeight.SemiBold, fontSize = 12.sp),
    labelSmall = TextStyle(fontFamily = BarlowCondensed, fontWeight = FontWeight.Medium, fontSize = 11.sp),
)

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
        typography = KarooTypography,
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides TextStyle(
                fontFamily = BarlowCondensed,
                color = TextPrimary,
            )
        ) {
            content()
        }
    }
}

