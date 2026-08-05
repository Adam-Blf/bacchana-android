package com.beloucif.bacchus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Clair/sombre/systeme. Mirrors `ThemePreference` from src/stores/themeStore.ts on the web -
 * `SYSTEM` follows the OS setting live, an explicit choice is persisted on the device (see
 * `com.beloucif.bacchus.data.ThemeStore`).
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
// are accent fills that stay readable-with-dark-text in both themes (see BacchusColors.TileInk
// and BacchusColors.OnStatus KDoc) - CardFace (white) on Neon measured only 3.28:1 in light
// theme and 2.60:1 in dark theme, both below the 4.5:1 AA floor. This is the Material default
// every plain `Button(onClick = ...)` call site relies on (WelcomeScreen, SettingsScreen,
// PaywallScreen, RecapScreen, ConsentBanner, BorderlandScreen).
private val BacchusLightMaterialColors = lightColorScheme(
    primary = LightBacchusColors.Neon,
    onPrimary = LightBacchusColors.TileInk,
    primaryContainer = LightBacchusColors.NeonDeep,
    onPrimaryContainer = LightBacchusColors.TileInk,
    secondary = LightBacchusColors.Premium,
    onSecondary = LightBacchusColors.OnStatus,
    background = LightBacchusColors.Bg,
    onBackground = LightBacchusColors.Ink,
    surface = LightBacchusColors.Surface,
    onSurface = LightBacchusColors.Ink,
    surfaceVariant = LightBacchusColors.SurfaceElevated,
    onSurfaceVariant = LightBacchusColors.InkSecondary,
    error = LightBacchusColors.CardRed,
    onError = LightBacchusColors.CardFace,
    outline = LightBacchusColors.Border,
    outlineVariant = LightBacchusColors.BorderStrong,
)

private val BacchusDarkMaterialColors = darkColorScheme(
    primary = DarkBacchusColors.Neon,
    onPrimary = DarkBacchusColors.TileInk,
    primaryContainer = DarkBacchusColors.NeonDeep,
    onPrimaryContainer = DarkBacchusColors.TileInk,
    secondary = DarkBacchusColors.Premium,
    onSecondary = DarkBacchusColors.OnStatus,
    background = DarkBacchusColors.Bg,
    onBackground = DarkBacchusColors.Ink,
    surface = DarkBacchusColors.Surface,
    onSurface = DarkBacchusColors.Ink,
    surfaceVariant = DarkBacchusColors.SurfaceElevated,
    onSurfaceVariant = DarkBacchusColors.InkSecondary,
    error = DarkBacchusColors.CardRed,
    onError = DarkBacchusColors.CardFace,
    outline = DarkBacchusColors.Border,
    outlineVariant = DarkBacchusColors.BorderStrong,
)

/**
 * Tavern neobrutalist theme: light by default (cream paper, ink text, orange accent), with a
 * "pop" dark variant on neutral ink (never brown/wood, see [DarkBacchusColors]). Neobrutalist
 * shapes (hard borders/shadows) live in each screen, only the color source changes here.
 */
@Composable
fun BacchusTheme(
    themePreference: ThemePreference = ThemePreference.SYSTEM,
    content: @Composable () -> Unit,
) {
    val darkTheme = themePreference.resolve()
    val palette = if (darkTheme) DarkBacchusColors else LightBacchusColors
    val materialColors = if (darkTheme) BacchusDarkMaterialColors else BacchusLightMaterialColors

    CompositionLocalProvider(LocalBacchusColors provides palette) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = BacchusTypography,
            content = content,
        )
    }
}
