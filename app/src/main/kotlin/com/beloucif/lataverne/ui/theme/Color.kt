package com.beloucif.lataverne.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Tavern neobrutalist palette, mirrors tokens.json v2 (la-taverne-content/tokens/tokens.json) -
 * do not hand-tune a value here without updating the source of truth first.
 * Light is the default theme: cream paper background, ink text, orange accent,
 * hard black shadows (no glow).
 */
object LaTaverneColors {
    val Bg = Color(0xFFFFF9F0)
    val BgRaised = Color(0xFFFFFFFF)
    val Surface = Color(0xFFFFFFFF)
    val SurfaceElevated = Color(0xFFFFFFFF)

    val Ink = Color(0xFF111111)
    val InkSecondary = Color(0xFF44444A)
    val InkMuted = Color(0xFF6B6B70)

    val Neon = Color(0xFFFF5C00)
    val NeonDeep = Color(0xFFE24E00)
    val NeonSoft = Color(0xFFFF8A3D)

    val CardFace = Color(0xFFFFFFFF)
    val CardInk = Color(0xFF111111)
    val CardRed = Color(0xFFE5323E)

    val Premium = Color(0xFFA87718)
    val Success = Color(0xFF1B8A5A)
    val Warning = Color(0xFFB45309)

    val Border = Color(0x33111111) // rgba(17, 17, 17, 0.20) - hairline
    val BorderStrong = Color(0xFF111111) // solid ink, brutal border
}
