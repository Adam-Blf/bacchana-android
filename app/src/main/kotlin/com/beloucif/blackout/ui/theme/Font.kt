package com.beloucif.blackout.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.beloucif.blackout.R

/**
 * Self-hosted font families (no CDN, no runtime download). Never JetBrains Mono, never
 * IBM Plex Mono - Space Mono is the only monospace family in this app, per BlackOut DA.
 */
object BlackOutFonts {
    val Display = FontFamily(Font(R.font.anton_regular, FontWeight.Normal))

    val Body = FontFamily(
        Font(R.font.space_grotesk_regular, FontWeight.Normal),
        Font(R.font.space_grotesk_medium, FontWeight.Medium),
        Font(R.font.space_grotesk_bold, FontWeight.Bold),
    )

    val Mono = FontFamily(
        Font(R.font.space_mono_regular, FontWeight.Normal),
        Font(R.font.space_mono_bold, FontWeight.Bold),
    )
}
