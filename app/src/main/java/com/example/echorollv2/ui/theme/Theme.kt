package com.example.echorollv2.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppColors(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val border: Color,
    val cardBackground: Color
)

val DarkAppColors = AppColors(
    isDark = true,
    background = Color(0xFF000000),
    surface = Color(0xFF121212),
    surfaceVariant = Color(0xFF1A1A1A),
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFF8E8E93),
    border = Color(0xFF2C2C2E),
    cardBackground = Color(0xFF1E1E1E)
)

val LightAppColors = AppColors(
    isDark = false,
    background = Color(0xFFF2F2F7),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE5E5EA),
    textPrimary = Color(0xFF1C1C1E),
    textSecondary = Color(0xFF636366),
    border = Color(0xFFD1D1D6),
    cardBackground = Color(0xFFFFFFFF)
)

val LocalAppColors = staticCompositionLocalOf { DarkAppColors }

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = PrimaryGreen,
    tertiary = PrimaryOrange,
    background = DarkBackground,
    surface = SurfaceColor,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = TextWhite,
    onSurface = TextWhite
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = PrimaryGreen,
    tertiary = PrimaryOrange,
    background = Color(0xFFF2F2F7),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1C1E),
    onSurface = Color(0xFF1C1C1E)
)

@Composable
fun EchoRollV2Theme(
    isDark: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = if (isDark) DarkAppColors else LightAppColors
    val colorScheme = if (isDark) DarkColorScheme else LightColorScheme
    
    CompositionLocalProvider(LocalAppColors provides colors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}