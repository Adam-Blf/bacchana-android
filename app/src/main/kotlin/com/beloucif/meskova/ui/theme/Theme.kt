package com.beloucif.meskova.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Clair/sombre/systeme. Mirrors `ThemePreference` from src/stores/themeStore.ts on the web -
 * `SYSTEM` follows the OS setting live, an explicit choice is persisted on the device (see
 * `com.beloucif.meskova.data.ThemeStore`).
 */
enum class ThemePreference { SYSTEM, LIGHT, DARK }

/** Resolves a preference to the concrete theme actually rendered. */
@Composable
fun ThemePreference.resolve(): Boolean = when (this) {
    ThemePreference.SYSTEM -> isSystemInDarkTheme()
    ThemePreference.LIGHT -> false
    ThemePreference.DARK -> true
}

// onPrimary/onPrimaryContainer/onSecondary use TileInk, never CardFace: Neon/NeonDeep/Premium
// are accent fills that stay readable-with-dark-text in both themes (see MeskovaColors.TileInk
// and MeskovaColors.OnStatus KDoc) - CardFace (white) on Neon measured only 3.28:1 in light
// theme and 2.60:1 in dark theme, both below the 4.5:1 AA floor. This is the Material default
// every plain `Button(onClick = ...)` call site relies on (WelcomeScreen, SettingsScreen,
// PaywallScreen, RecapScreen, ConsentBanner, BorderlandScreen).
private val MeskovaLightMaterialColors = lightColorScheme(
    primary = LightMeskovaColors.Neon,
    onPrimary = LightMeskovaColors.TileInk,
    primaryContainer = LightMeskovaColors.NeonDeep,
    onPrimaryContainer = LightMeskovaColors.TileInk,
    secondary = LightMeskovaColors.Premium,
    onSecondary = LightMeskovaColors.OnStatus,
    background = LightMeskovaColors.Bg,
    onBackground = LightMeskovaColors.Ink,
    surface = LightMeskovaColors.Surface,
    onSurface = LightMeskovaColors.Ink,
    surfaceVariant = LightMeskovaColors.SurfaceElevated,
    onSurfaceVariant = LightMeskovaColors.InkSecondary,
    error = LightMeskovaColors.CardRed,
    onError = LightMeskovaColors.CardFace,
    outline = LightMeskovaColors.Border,
    outlineVariant = LightMeskovaColors.BorderStrong,
)

private val MeskovaDarkMaterialColors = darkColorScheme(
    primary = DarkMeskovaColors.Neon,
    onPrimary = DarkMeskovaColors.TileInk,
    primaryContainer = DarkMeskovaColors.NeonDeep,
    onPrimaryContainer = DarkMeskovaColors.TileInk,
    secondary = DarkMeskovaColors.Premium,
    onSecondary = DarkMeskovaColors.OnStatus,
    background = DarkMeskovaColors.Bg,
    onBackground = DarkMeskovaColors.Ink,
    surface = DarkMeskovaColors.Surface,
    onSurface = DarkMeskovaColors.Ink,
    surfaceVariant = DarkMeskovaColors.SurfaceElevated,
    onSurfaceVariant = DarkMeskovaColors.InkSecondary,
    error = DarkMeskovaColors.CardRed,
    onError = DarkMeskovaColors.CardFace,
    outline = DarkMeskovaColors.Border,
    outlineVariant = DarkMeskovaColors.BorderStrong,
)

/**
 * Tavern neobrutalist theme: light by default (cream paper, ink text, orange accent), with a
 * "pop" dark variant on neutral ink (never brown/wood, see [DarkMeskovaColors]). Neobrutalist
 * shapes (hard borders/shadows) live in each screen, only the color source changes here.
 */
@Composable
fun MeskovaTheme(
    themePreference: ThemePreference = ThemePreference.SYSTEM,
    content: @Composable () -> Unit,
) {
    val darkTheme = themePreference.resolve()
    val palette = if (darkTheme) DarkMeskovaColors else LightMeskovaColors
    val materialColors = if (darkTheme) MeskovaDarkMaterialColors else MeskovaLightMaterialColors

    CompositionLocalProvider(LocalMeskovaColors provides palette) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = MeskovaTypography,
            content = content,
        )
    }
}
