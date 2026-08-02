package com.beloucif.lataverne.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * Tavern neobrutalist theme: light by default (cream paper, ink text, orange accent).
 * The candlelit dark variant from tokens.json v2 will land in a later version.
 */
private val LaTaverneLightColors = lightColorScheme(
    primary = LaTaverneColors.Neon,
    onPrimary = LaTaverneColors.CardFace,
    primaryContainer = LaTaverneColors.NeonDeep,
    onPrimaryContainer = LaTaverneColors.CardFace,
    secondary = LaTaverneColors.Premium,
    onSecondary = LaTaverneColors.CardFace,
    background = LaTaverneColors.Bg,
    onBackground = LaTaverneColors.Ink,
    surface = LaTaverneColors.Surface,
    onSurface = LaTaverneColors.Ink,
    surfaceVariant = LaTaverneColors.SurfaceElevated,
    onSurfaceVariant = LaTaverneColors.InkSecondary,
    error = LaTaverneColors.CardRed,
    onError = LaTaverneColors.CardFace,
    outline = LaTaverneColors.Border,
    outlineVariant = LaTaverneColors.BorderStrong,
)

@Composable
fun LaTaverneTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LaTaverneLightColors,
        typography = LaTaverneTypography,
        content = content,
    )
}
