package com.desafiodevspace.countryexplorer.ui.theme

import android.app.Activity
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


// Cores para o Esquema
val PrimaryIndigo = Color(0xFF3F51B5) // Indigo 500
val OnPrimaryWhite = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFE0E0FF) // Um azul claro para fundos de acentuação
val OnPrimaryContainerDark = Color(0xFF00006E)

// Cores Neutras (Fundo, Superfícies, Textos)
val SurfaceLight = Color(0xFFFFFFFF)     // Fundo principal Tema Claro
val OnSurfaceDark = Color(0xFF1C1B1F)     // Texto principal Tema Claro
val SurfaceDark = Color(0xFF1C1B1F)       // Fundo principal Tema Escuro
val OnSurfaceLight = Color(0xFFE0E3E1)    // Texto principal Tema Escuro

// Cores de Ícones Inativos e Contornos
val OutlineGrey = Color(0xFF79747E)       // Contornos e ícones inativos

// Cor de Destaque (Estrela de Favorito)
val ErrorRed = Color(0xFFB00020)          // Padrão para mensagens de erro

// =====================================================================
// Esquemas de Cores M3 (Light / Dark)
// =====================================================================

// O Dark Theme deve usar tons mais claros para 'primary' e escuros para 'surface'
val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = Color(0xFFC7C1FF), // Tom mais claro do Indigo para contraste
    onPrimary = Color(0xFF0C1943),
    primaryContainer = Color(0xFF2E3D6E),
    onPrimaryContainer = Color(0xFFE3E0FF),

    // Cores de Superfície e Fundo
    background = SurfaceDark, // 0xFF1C1B1F
    surface = SurfaceDark,    // Fundo dos cards/listas Dark
    onBackground = OnSurfaceLight, // Texto claro
    onSurface = OnSurfaceLight,    // Texto claro
    surfaceVariant = Color(0xFF47464F),
    onSurfaceVariant = Color(0xFFCAC4D0), // Textos secundários

    outline = Color(0xFF928F99), // Contornos mais claros no Dark
    error = ErrorRed,
    onError = OnPrimaryWhite
)

// O Light Theme usa a cor de semente mais escura para 'primary' e branco para 'surface'
val LightColorScheme = androidx.compose.material3.lightColorScheme(
    primary = PrimaryIndigo,            // 0xFF3F51B5 (O azul principal)
    onPrimary = OnPrimaryWhite,         // 0xFFFFFFFF (Branco)
    primaryContainer = PrimaryContainerLight, // 0xFFE0E0FF (Fundo dos chips ativos)
    onPrimaryContainer = OnPrimaryContainerDark, // 0xFF00006E (Texto sobre chips ativos)

    // Cores de Superfície e Fundo
    background = SurfaceLight,          // 0xFFFFFFFF
    surface = SurfaceLight,             // Fundo dos cards/listas Light
    onBackground = OnSurfaceDark,       // 0xFF1C1B1F (Texto escuro)
    onSurface = OnSurfaceDark,          // Texto escuro
    surfaceVariant = Color(0xFFE7E0EB),
    onSurfaceVariant = OutlineGrey,     // 0xFF79747E (Textos secundários/ícones inativos)

    outline = OutlineGrey,              // Contornos
    error = ErrorRed,
    onError = OnPrimaryWhite
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