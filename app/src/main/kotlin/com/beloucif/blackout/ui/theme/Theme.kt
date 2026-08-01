package com.beloucif.blackout.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

/**
 * BlackOut is nocturnal by nature: dark theme only in v1, no light scheme is exposed.
 * See docs/DESIGN.md - "A11y: dark only, l'app est nocturne par nature".
 */
private val BlackOutDarkColors = darkColorScheme(
    primary = BlackOutColors.Neon,
    onPrimary = BlackOutColors.Ink,
    primaryContainer = BlackOutColors.NeonDeep,
    onPrimaryContainer = BlackOutColors.Ink,
    secondary = BlackOutColors.Premium,
    onSecondary = BlackOutColors.CardInk,
    background = BlackOutColors.Bg,
    onBackground = BlackOutColors.Ink,
    surface = BlackOutColors.Surface,
    onSurface = BlackOutColors.Ink,
    surfaceVariant = BlackOutColors.SurfaceElevated,
    onSurfaceVariant = BlackOutColors.InkSecondary,
    error = BlackOutColors.CardRed,
    onError = BlackOutColors.Ink,
    outline = BlackOutColors.Border,
    outlineVariant = BlackOutColors.BorderStrong,
)

@Composable
fun BlackOutTheme(content: @Composable () -> Unit) {
    // isSystemInDarkTheme() is read for future-proofing only; v1 always renders dark.
    isSystemInDarkTheme()
    MaterialTheme(
        colorScheme = BlackOutDarkColors,
        typography = BlackOutTypography,
        content = content,
    )
}
