package com.beloucif.lataverne.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Tavern neobrutalist palette, mirrors src/styles/tokens.css on the web (source of truth) -
 * do not hand-tune a value here without updating tokens.css first. Light is the default
 * theme: cream paper background, ink text, orange accent, hard black shadows (no glow).
 * Dark is the "pop" candlelit variant: neutral ink background (never brown/wood), the same
 * festive pops and orange accent lightened to clear WCAG on a dark surface.
 */
data class LaTaverneColorScheme(
    val Bg: Color,
    val BgRaised: Color,
    val Surface: Color,
    val SurfaceElevated: Color,

    val Ink: Color,
    val InkSecondary: Color,
    val InkMuted: Color,

    val Neon: Color,
    val NeonDeep: Color,
    val NeonSoft: Color,
    /** Orange used as TEXT (links, small labels < 18px, non-bold): darker/lighter than
     * Neon/NeonDeep so it clears AA normal text contrast (4.5:1) on its own background. */
    val OrangeInk: Color,

    /** Festive flat pops used on mode tiles / accents. */
    val PopYellow: Color,
    val PopPink: Color,
    val PopBlue: Color,
    val PopLime: Color,

    /** Fixed in both themes: real playing cards do not invert with the room's lighting. */
    val CardFace: Color,
    val CardInk: Color,
    val CardRed: Color,

    val Premium: Color,
    val Success: Color,
    val Warning: Color,

    val Border: Color,
    val BorderStrong: Color,
)

val LightLaTaverneColors = LaTaverneColorScheme(
    Bg = Color(0xFFFFF9F0),
    BgRaised = Color(0xFFFFF3E0),
    Surface = Color(0xFFFFFFFF),
    SurfaceElevated = Color(0xFFFFEFD6),

    Ink = Color(0xFF111111),
    InkSecondary = Color(0xFF44444A),
    InkMuted = Color(0xFF6B6B70),

    Neon = Color(0xFFFA5600),
    NeonDeep = Color(0xFFE24E00),
    NeonSoft = Color(0xFFFF8A3D),
    OrangeInk = Color(0xFFC74300),

    PopYellow = Color(0xFFFFD029),
    PopPink = Color(0xFFFF6FB2),
    PopBlue = Color(0xFF6E9BFF),
    PopLime = Color(0xFF9BE94C),

    CardFace = Color(0xFFFFFFFF),
    CardInk = Color(0xFF111111),
    CardRed = Color(0xFFC71F2D),

    Premium = Color(0xFF855C12),
    Success = Color(0xFF177C50),
    Warning = Color(0xFFB45309),

    Border = Color(0x26111111), // rgba(17, 17, 17, 0.15)
    BorderStrong = Color(0xFF111111),
)

/**
 * "La taverne pop" - neutral ink background (never brown/wood), pops and the orange accent
 * lightened to clear WCAG AA on the dark surface. Values mirror the `[data-theme='dark']`
 * block of tokens.css (ratios computed there against #141216, all >= 4.5:1).
 */
val DarkLaTaverneColors = LaTaverneColorScheme(
    Bg = Color(0xFF141216),
    BgRaised = Color(0xFF1D1B20),
    Surface = Color(0xFF1D1B20),
    SurfaceElevated = Color(0xFF26232B),

    Ink = Color(0xFFF4EFE6),
    InkSecondary = Color(0xFFA39DB0),
    InkMuted = Color(0xFF837D8F),

    Neon = Color(0xFFFF7A2E),
    NeonDeep = Color(0xFFE86014),
    NeonSoft = Color(0xFFFF9E5C),
    OrangeInk = Color(0xFFFF7A2E),

    PopYellow = Color(0xFFFFD84D),
    PopPink = Color(0xFFFF7FBE),
    PopBlue = Color(0xFF7FB0FF),
    PopLime = Color(0xFFA6F05A),

    CardFace = Color(0xFFFFFFFF),
    CardInk = Color(0xFF111111),
    CardRed = Color(0xFFC71F2D),

    Premium = Color(0xFFD9A441),
    Success = Color(0xFF3EA876),
    Warning = Color(0xFFD67428),

    Border = Color(0x33F4EFE6), // rgba(244, 239, 230, 0.2)
    BorderStrong = Color(0xFFF4EFE6),
)

val LocalLaTaverneColors = staticCompositionLocalOf { LightLaTaverneColors }

/**
 * Theme-aware accessor kept as an object so every existing `LaTaverneColors.Bg`-style call
 * site (300+ across the screens) keeps compiling unchanged: each property is a `@Composable`
 * getter reading [LocalLaTaverneColors], exactly like `MaterialTheme.colorScheme` does.
 */
object LaTaverneColors {
    val Bg: Color @Composable get() = LocalLaTaverneColors.current.Bg
    val BgRaised: Color @Composable get() = LocalLaTaverneColors.current.BgRaised
    val Surface: Color @Composable get() = LocalLaTaverneColors.current.Surface
    val SurfaceElevated: Color @Composable get() = LocalLaTaverneColors.current.SurfaceElevated

    val Ink: Color @Composable get() = LocalLaTaverneColors.current.Ink
    val InkSecondary: Color @Composable get() = LocalLaTaverneColors.current.InkSecondary
    val InkMuted: Color @Composable get() = LocalLaTaverneColors.current.InkMuted

    val Neon: Color @Composable get() = LocalLaTaverneColors.current.Neon
    val NeonDeep: Color @Composable get() = LocalLaTaverneColors.current.NeonDeep
    val NeonSoft: Color @Composable get() = LocalLaTaverneColors.current.NeonSoft
    val OrangeInk: Color @Composable get() = LocalLaTaverneColors.current.OrangeInk

    val PopYellow: Color @Composable get() = LocalLaTaverneColors.current.PopYellow
    val PopPink: Color @Composable get() = LocalLaTaverneColors.current.PopPink
    val PopBlue: Color @Composable get() = LocalLaTaverneColors.current.PopBlue
    val PopLime: Color @Composable get() = LocalLaTaverneColors.current.PopLime

    val CardFace: Color @Composable get() = LocalLaTaverneColors.current.CardFace
    val CardInk: Color @Composable get() = LocalLaTaverneColors.current.CardInk
    val CardRed: Color @Composable get() = LocalLaTaverneColors.current.CardRed

    val Premium: Color @Composable get() = LocalLaTaverneColors.current.Premium
    val Success: Color @Composable get() = LocalLaTaverneColors.current.Success
    val Warning: Color @Composable get() = LocalLaTaverneColors.current.Warning

    val Border: Color @Composable get() = LocalLaTaverneColors.current.Border
    val BorderStrong: Color @Composable get() = LocalLaTaverneColors.current.BorderStrong
}
