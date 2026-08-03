package com.beloucif.lataverne.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Clair/sombre/systeme. Mirrors `ThemePreference` from src/stores/themeStore.ts on the web -
 * `SYSTEM` follows the OS setting live, an explicit choice is persisted on the device (see
 * `com.beloucif.lataverne.data.ThemeStore`).
 */
enum class ThemePreference { SYSTEM, LIGHT, DARK }

/** Resolves a preference to the concrete theme actually rendered. */
@Composable
fun ThemePreference.resolve(): Boolean = when (this) {
    ThemePreference.SYSTEM -> isSystemInDarkTheme()
    ThemePreference.LIGHT -> false
    ThemePreference.DARK -> true
}

private val LaTaverneLightMaterialColors = lightColorScheme(
    primary = LightLaTaverneColors.Neon,
    onPrimary = LightLaTaverneColors.CardFace,
    primaryContainer = LightLaTaverneColors.NeonDeep,
    onPrimaryContainer = LightLaTaverneColors.CardFace,
    secondary = LightLaTaverneColors.Premium,
    onSecondary = LightLaTaverneColors.CardFace,
    background = LightLaTaverneColors.Bg,
    onBackground = LightLaTaverneColors.Ink,
    surface = LightLaTaverneColors.Surface,
    onSurface = LightLaTaverneColors.Ink,
    surfaceVariant = LightLaTaverneColors.SurfaceElevated,
    onSurfaceVariant = LightLaTaverneColors.InkSecondary,
    error = LightLaTaverneColors.CardRed,
    onError = LightLaTaverneColors.CardFace,
    outline = LightLaTaverneColors.Border,
    outlineVariant = LightLaTaverneColors.BorderStrong,
)

private val LaTaverneDarkMaterialColors = darkColorScheme(
    primary = DarkLaTaverneColors.Neon,
    onPrimary = DarkLaTaverneColors.CardInk,
    primaryContainer = DarkLaTaverneColors.NeonDeep,
    onPrimaryContainer = DarkLaTaverneColors.Ink,
    secondary = DarkLaTaverneColors.Premium,
    onSecondary = DarkLaTaverneColors.CardInk,
    background = DarkLaTaverneColors.Bg,
    onBackground = DarkLaTaverneColors.Ink,
    surface = DarkLaTaverneColors.Surface,
    onSurface = DarkLaTaverneColors.Ink,
    surfaceVariant = DarkLaTaverneColors.SurfaceElevated,
    onSurfaceVariant = DarkLaTaverneColors.InkSecondary,
    error = DarkLaTaverneColors.CardRed,
    onError = DarkLaTaverneColors.CardFace,
    outline = DarkLaTaverneColors.Border,
    outlineVariant = DarkLaTaverneColors.BorderStrong,
)

/**
 * Tavern neobrutalist theme: light by default (cream paper, ink text, orange accent), with a
 * "pop" dark variant on neutral ink (never brown/wood, see [DarkLaTaverneColors]). Neobrutalist
 * shapes (hard borders/shadows) live in each screen, only the color source changes here.
 */
@Composable
fun LaTaverneTheme(
    themePreference: ThemePreference = ThemePreference.SYSTEM,
    content: @Composable () -> Unit,
) {
    val darkTheme = themePreference.resolve()
    val palette = if (darkTheme) DarkLaTaverneColors else LightLaTaverneColors
    val materialColors = if (darkTheme) LaTaverneDarkMaterialColors else LaTaverneLightMaterialColors

    CompositionLocalProvider(LocalLaTaverneColors provides palette) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = LaTaverneTypography,
            content = content,
        )
    }
}
