package com.beloucif.latournee.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

/**
 * Clair/sombre/systeme. Mirrors `ThemePreference` from src/stores/themeStore.ts on the web -
 * `SYSTEM` follows the OS setting live, an explicit choice is persisted on the device (see
 * `com.beloucif.latournee.data.ThemeStore`).
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
// are accent fills that stay readable-with-dark-text in both themes (see LaTourneeColors.TileInk
// and LaTourneeColors.OnStatus KDoc) - CardFace (white) on Neon measured only 3.28:1 in light
// theme and 2.60:1 in dark theme, both below the 4.5:1 AA floor. This is the Material default
// every plain `Button(onClick = ...)` call site relies on (WelcomeScreen, SettingsScreen,
// PaywallScreen, RecapScreen, ConsentBanner, BorderlandScreen).
private val LaTourneeLightMaterialColors = lightColorScheme(
    primary = LightLaTourneeColors.Neon,
    onPrimary = LightLaTourneeColors.TileInk,
    primaryContainer = LightLaTourneeColors.NeonDeep,
    onPrimaryContainer = LightLaTourneeColors.TileInk,
    secondary = LightLaTourneeColors.Premium,
    onSecondary = LightLaTourneeColors.OnStatus,
    background = LightLaTourneeColors.Bg,
    onBackground = LightLaTourneeColors.Ink,
    surface = LightLaTourneeColors.Surface,
    onSurface = LightLaTourneeColors.Ink,
    surfaceVariant = LightLaTourneeColors.SurfaceElevated,
    onSurfaceVariant = LightLaTourneeColors.InkSecondary,
    error = LightLaTourneeColors.CardRed,
    onError = LightLaTourneeColors.CardFace,
    outline = LightLaTourneeColors.Border,
    outlineVariant = LightLaTourneeColors.BorderStrong,
)

private val LaTourneeDarkMaterialColors = darkColorScheme(
    primary = DarkLaTourneeColors.Neon,
    onPrimary = DarkLaTourneeColors.TileInk,
    primaryContainer = DarkLaTourneeColors.NeonDeep,
    onPrimaryContainer = DarkLaTourneeColors.TileInk,
    secondary = DarkLaTourneeColors.Premium,
    onSecondary = DarkLaTourneeColors.OnStatus,
    background = DarkLaTourneeColors.Bg,
    onBackground = DarkLaTourneeColors.Ink,
    surface = DarkLaTourneeColors.Surface,
    onSurface = DarkLaTourneeColors.Ink,
    surfaceVariant = DarkLaTourneeColors.SurfaceElevated,
    onSurfaceVariant = DarkLaTourneeColors.InkSecondary,
    error = DarkLaTourneeColors.CardRed,
    onError = DarkLaTourneeColors.CardFace,
    outline = DarkLaTourneeColors.Border,
    outlineVariant = DarkLaTourneeColors.BorderStrong,
)

/**
 * Tavern neobrutalist theme: light by default (cream paper, ink text, orange accent), with a
 * "pop" dark variant on neutral ink (never brown/wood, see [DarkLaTourneeColors]). Neobrutalist
 * shapes (hard borders/shadows) live in each screen, only the color source changes here.
 */
@Composable
fun LaTourneeTheme(
    themePreference: ThemePreference = ThemePreference.SYSTEM,
    content: @Composable () -> Unit,
) {
    val darkTheme = themePreference.resolve()
    val palette = if (darkTheme) DarkLaTourneeColors else LightLaTourneeColors
    val materialColors = if (darkTheme) LaTourneeDarkMaterialColors else LaTourneeLightMaterialColors

    CompositionLocalProvider(LocalLaTourneeColors provides palette) {
        MaterialTheme(
            colorScheme = materialColors,
            typography = LaTourneeTypography,
            content = content,
        )
    }
}
