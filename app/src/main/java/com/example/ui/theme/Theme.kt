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
    primary = CyanPrimary,
    onPrimary = Color(0xFF082F49),
    primaryContainer = Color(0xFF0369A1),
    onPrimaryContainer = Color(0xFFE0F2FE),
    secondary = FurnaceAmber,
    onSecondary = Color(0xFF451A03),
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = Color(0xFFFEF3C7),
    tertiary = StatusGreen,
    onTertiary = Color(0xFF064E3B),
    background = IndustrialDarkBg,
    onBackground = TextWhite,
    surface = IndustrialDarkSurface,
    onSurface = TextWhite,
    surfaceVariant = IndustrialDarkSurfaceVariant,
    onSurfaceVariant = TextMuted,
    error = AlertRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = IndustrialLightPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBAE6FD),
    onPrimaryContainer = Color(0xFF082F49),
    secondary = FurnaceGlowOrange,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFEDD5),
    onSecondaryContainer = Color(0xFF7C2D12),
    tertiary = StatusGreen,
    onTertiary = Color.White,
    background = IndustrialLightBg,
    onBackground = TextDark,
    surface = IndustrialLightSurface,
    onSurface = TextDark,
    surfaceVariant = IndustrialLightSurfaceVariant,
    onSurfaceVariant = Color(0xFF475569),
    error = AlertRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent industrial palette
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
