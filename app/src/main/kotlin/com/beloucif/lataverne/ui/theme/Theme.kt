package com.beloucif.lataverne.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * La Taverne is nocturnal by nature: dark theme only in v1, no light scheme is exposed.
 * See docs/DESIGN.md - "A11y: dark only, l'app est nocturne par nature".
 */
private val LaTaverneDarkColors = darkColorScheme(
    primary = LaTaverneColors.Neon,
    onPrimary = LaTaverneColors.Ink,
    primaryContainer = LaTaverneColors.NeonDeep,
    onPrimaryContainer = LaTaverneColors.Ink,
    secondary = LaTaverneColors.Premium,
    onSecondary = LaTaverneColors.CardInk,
    background = LaTaverneColors.Bg,
    onBackground = LaTaverneColors.Ink,
    surface = LaTaverneColors.Surface,
    onSurface = LaTaverneColors.Ink,
    surfaceVariant = LaTaverneColors.SurfaceElevated,
    onSurfaceVariant = LaTaverneColors.InkSecondary,
    error = LaTaverneColors.CardRed,
    onError = LaTaverneColors.Ink,
    outline = LaTaverneColors.Border,
    outlineVariant = LaTaverneColors.BorderStrong,
)

@Composable
fun LaTaverneTheme(content: @Composable () -> Unit) {
    // isSystemInDarkTheme() is read for future-proofing only; v1 always renders dark.
    isSystemInDarkTheme()
    MaterialTheme(
        colorScheme = LaTaverneDarkColors,
        typography = LaTaverneTypography,
        content = content,
    )
}
