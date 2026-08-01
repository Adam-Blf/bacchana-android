package com.beloucif.lataverne.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Neo-Tokyo Borderland palette. Mirrors la-taverne-content/tokens/tokens.json exactly -
 * do not hand-tune a value here without updating the source of truth first.
 */
object LaTaverneColors {
    val Bg = Color(0xFF09090B)
    val BgRaised = Color(0xFF0E0E12)
    val Surface = Color(0xFF15151A)
    val SurfaceElevated = Color(0xFF1C1C23)

    val Ink = Color(0xFFFAFAF7)
    val InkSecondary = Color(0xFFA1A1AA)
    val InkMuted = Color(0xFF63636B)

    val Neon = Color(0xFFFF3B41)
    val NeonDeep = Color(0xFFDC2626)
    val NeonSoft = Color(0xFFFF6B6E)

    val CardFace = Color(0xFFF7F5F0)
    val CardInk = Color(0xFF111114)
    val CardRed = Color(0xFFE5323E)

    val Premium = Color(0xFFD4A437)
    val Success = Color(0xFF10B981)
    val Warning = Color(0xFFF59E0B)

    val Border = Color(0x14FAFAF7) // rgba(250, 250, 247, 0.08)
    val BorderStrong = Color(0x29FAFAF7) // rgba(250, 250, 247, 0.16)
}
