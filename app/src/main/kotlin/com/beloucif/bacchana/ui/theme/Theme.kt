package com.beloucif.bacchana.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Clair/sombre/systeme. Mirrors `ThemePreference` from src/stores/themeStore.ts on the web -
 * `SYSTEM` follows the OS setting live, an explicit choice is persisted on the device (see
 * `com.beloucif.bacchana.data.ThemeStore`).
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
// are accent fills that stay readable-with-dark-text in both themes (see BacchanaColors.TileInk
// and BacchanaColors.OnStatus KDoc) - CardFace (white) on Neon measured only 3.28:1 in light
// theme and 2.60:1 in dark theme, both below the 4.5:1 AA floor. This is the Material default
// every plain `Button(onClick = ...)` call site relies on (WelcomeScreen, SettingsScreen,
// PaywallScreen, RecapScreen, ConsentBanner, BorderlandScreen).
private val BacchanaLightMaterialColors = lightColorScheme(
    primary = LightBacchanaColors.Neon,
    onPrimary = LightBacchanaColors.TileInk,
    primaryContainer = LightBacchanaColors.NeonDeep,
    onPrimaryContainer = LightBacchanaColors.TileInk,
    secondary = LightBacchanaColors.Premium,
    onSecondary = LightBacchanaColors.OnStatus,
    background = LightBacchanaColors.Bg,
    onBackground = LightBacchanaColors.Ink,
    surface = LightBacchanaColors.Surface,
    onSurface = LightBacchanaColors.Ink,
    surfaceVariant = LightBacchanaColors.SurfaceElevated,
    onSurfaceVariant = LightBacchanaColors.InkSecondary,
    error = LightBacchanaColors.CardRed,
    onError = LightBacchanaColors.CardFace,
    outline = LightBacchanaColors.Border,
    outlineVariant = LightBacchanaColors.BorderStrong,
)

private val BacchanaDarkMaterialColors = darkColorScheme(
    primary = DarkBacchanaColors.Neon,
    onPrimary = DarkBacchanaColors.TileInk,
    primaryContainer = DarkBacchanaColors.NeonDeep,
    onPrimaryContainer = DarkBacchanaColors.TileInk,
    secondary = DarkBacchanaColors.Premium,
    onSecondary = DarkBacchanaColors.OnStatus,
    background = DarkBacchanaColors.Bg,
    onBackground = DarkBacchanaColors.Ink,
    surface = DarkBacchanaColors.Surface,
    onSurface = DarkBacchanaColors.Ink,
    surfaceVariant = DarkBacchanaColors.SurfaceElevated,
    onSurfaceVariant = DarkBacchanaColors.InkSecondary,
    error = DarkBacchanaColors.CardRed,
    onError = DarkBacchanaColors.CardFace,
    outline = DarkBacchanaColors.Border,
    outlineVariant = DarkBacchanaColors.BorderStrong,
)

/**
 * Tavern neobrutalist theme: light by default (cream paper, ink text, orange accent), with a
 * "pop" dark variant on neutral ink (never brown/wood, see [DarkBacchanaColors]). Neobrutalist
 * shapes (hard borders/shadows) live in each screen, only the color source changes here.
 */
@Composable
fun BacchanaTheme(
    themePreference: ThemePreference = ThemePreference.SYSTEM,
    content: @Composable () -> Unit,
) {
    val darkTheme = themePreference.resolve()
    val palette = if (darkTheme) DarkBacchanaColors else LightBacchanaColors
    val materialColors = if (darkTheme) BacchanaDarkMaterialColors else BacchanaLightMaterialColors

    CompositionLocalProvider(LocalBacchanaColors provides palette) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = BacchanaTypography,
            content = content,
        )
    }
}
