package com.sam.myapplication.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppTheme {
    RED, BLUE, GREEN, PURPLE, DARK
}

private val RedColorScheme = lightColorScheme(
    primary = ChowkingRed,
    secondary = ChowkingYellow,
    tertiary = ChowkingDarkRed,
    background = Color(0xFFFFFBFB),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

private val BlueColorScheme = lightColorScheme(
    primary = ProfessionalBlue,
    secondary = LightBlue,
    tertiary = DarkBlue,
    background = Color(0xFFF8FBFF),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFF0D47A1),
    onSurface = Color(0xFF0D47A1)
)

private val GreenColorScheme = lightColorScheme(
    primary = ProfessionalGreen,
    secondary = LightGreen,
    tertiary = DarkGreen,
    background = Color(0xFFF8FFF8),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFF1B5E20),
    onSurface = Color(0xFF1B5E20)
)

private val PurpleColorScheme = lightColorScheme(
    primary = ProfessionalPurple,
    secondary = LightPurple,
    tertiary = DarkPurple,
    background = Color(0xFFFBF8FF),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFF4A148C),
    onSurface = Color(0xFF4A148C)
)

private val DarkColorScheme = darkColorScheme(
    primary = SoftRed,
    secondary = SoftYellow,
    tertiary = ChowkingYellow,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkOnSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = Color(0xFF2F3135),
    outline = Color(0xFF43474E)
)

@Composable
fun MyApplicationTheme(
    appTheme: AppTheme = AppTheme.DARK,
    content: @Composable () -> Unit
) {
    val colorScheme = when (appTheme) {
        AppTheme.RED -> RedColorScheme
        AppTheme.BLUE -> BlueColorScheme
        AppTheme.GREEN -> GreenColorScheme
        AppTheme.PURPLE -> PurpleColorScheme
        AppTheme.DARK -> DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
