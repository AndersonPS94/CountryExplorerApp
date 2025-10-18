package com.desafiodevspace.countryexplorer.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext





val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = DarkAccent,
    onPrimary = DarkTextPrimary,
    primaryContainer = DarkSurface,
    onPrimaryContainer = DarkTextSecondary,
    secondary = DarkAccent,
    onSecondary = DarkTextPrimary,
    secondaryContainer = DarkSurface,
    onSecondaryContainer = DarkTextSecondary,
    surface = DarkSurface,
    background = DarkBackground,
    onSurface = DarkTextPrimary,
    onBackground = DarkTextPrimary,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkTextSecondary
)


val LightColorScheme = androidx.compose.material3.lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    surface = Surface,
    background = Background,
    onSurface = OnSurface,
    onBackground = OnBackground,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline
)

@Composable
fun CountryExplorerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
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