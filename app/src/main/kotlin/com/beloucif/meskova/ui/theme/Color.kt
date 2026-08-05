package com.beloucif.meskova.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.beloucif.meskova.core.theme.MeskovaPalette
import com.beloucif.meskova.core.theme.PaletteColor

/**
 * Tavern neobrutalist palette, mirrors src/styles/tokens.css on the web (source of truth) -
 * do not hand-tune a value here without updating tokens.css first. Light is the default
 * theme: cream paper background, ink text, orange accent, hard black shadows (no glow).
 * Dark is the "pop" candlelit variant: neutral ink background (never brown/wood), the same
 * festive pops and orange accent lightened to clear WCAG on a dark surface.
 *
 * Every field is built from [com.beloucif.meskova.core.theme.MeskovaPalette] (`:core`, pure
 * JVM) via [Color], never re-typed as a hex literal here - the exact same numbers that render
 * the UI are the ones `MeskovaPaletteContrastTest` (`:core:test`) verifies, so the two can
 * never drift out of sync.
 */
data class MeskovaColorScheme(
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

    /**
     * Fixed ink (#111111 in both themes) for any text/icon/border drawn on top of a
     * [PopYellow]/[PopPink]/[PopBlue]/[PopLime]/[Neon]/[NeonDeep]/[NeonSoft] fill: those fills
     * stay light in BOTH themes, so [Ink] (which inverts with the theme) must never be used on
     * top of them - that inversion is exactly the bug reported twice by Adam while playing the
     * app ("du blanc sur du jaune c'est illisible"). See docs/DESIGN_TOKENS.md section 2bis and
     * `core`'s `MeskovaPaletteContrastTest`.
     */
    val TileInk: Color,

    /** Fixed orange (#C74300) for text drawn on [CardFace] (always white): unlike [OrangeInk],
     * which is recalculated per theme, this stays correct against a surface that never changes. */
    val CardAccent: Color,

    val Premium: Color,
    val Success: Color,
    val Warning: Color,
    val Danger: Color,

    /**
     * Ink for text/icons drawn on a solid [Premium]/[Success]/[Warning]/[Danger] fill. Unlike
     * the pop/neon family, these semantic colors get DARKER in light theme and LIGHTER in dark
     * theme - so the correct on-fill ink flips too, computed once per theme here rather than
     * guessed at the call site.
     */
    val OnStatus: Color,

    val Border: Color,
    val BorderStrong: Color,
)

private fun PaletteColor.toColor(): Color = Color(0xFF000000.toInt() or rgbInt)

private fun MeskovaPalette.toColorScheme(borderAlphaByte: Int): MeskovaColorScheme = MeskovaColorScheme(
    Bg = bg.toColor(),
    BgRaised = bgRaised.toColor(),
    Surface = surface.toColor(),
    SurfaceElevated = surfaceElevated.toColor(),

    Ink = ink.toColor(),
    InkSecondary = inkSecondary.toColor(),
    InkMuted = inkMuted.toColor(),

    Neon = neon.toColor(),
    NeonDeep = neonDeep.toColor(),
    NeonSoft = neonSoft.toColor(),
    OrangeInk = orangeInk.toColor(),

    PopYellow = popYellow.toColor(),
    PopPink = popPink.toColor(),
    PopBlue = popBlue.toColor(),
    PopLime = popLime.toColor(),

    CardFace = cardFace.toColor(),
    CardInk = cardInk.toColor(),
    CardRed = cardRed.toColor(),

    TileInk = tileInk.toColor(),
    CardAccent = cardAccent.toColor(),

    Premium = premium.toColor(),
    Success = success.toColor(),
    Warning = warning.toColor(),
    Danger = danger.toColor(),
    OnStatus = onStatus.toColor(),

    Border = Color((borderAlphaByte shl 24) or border.rgbInt),
    BorderStrong = borderStrong.toColor(),
)

// Border alpha bytes: light 0.15 * 255 = 38 (0x26), dark 0.38 * 255 = 97 (0x61) - matches
// MeskovaPalette.borderAlpha exactly (docs/DESIGN_TOKENS.md section 3.4).
val LightMeskovaColors: MeskovaColorScheme = MeskovaPalette.Light.toColorScheme(borderAlphaByte = 0x26)
val DarkMeskovaColors: MeskovaColorScheme = MeskovaPalette.Dark.toColorScheme(borderAlphaByte = 0x61)

val LocalMeskovaColors = staticCompositionLocalOf { LightMeskovaColors }

/**
 * Theme-aware accessor kept as an object so every existing `MeskovaColors.Bg`-style call
 * site (300+ across the screens) keeps compiling unchanged: each property is a `@Composable`
 * getter reading [LocalMeskovaColors], exactly like `MaterialTheme.colorScheme` does.
 */
object MeskovaColors {
    val Bg: Color @Composable get() = LocalMeskovaColors.current.Bg
    val BgRaised: Color @Composable get() = LocalMeskovaColors.current.BgRaised
    val Surface: Color @Composable get() = LocalMeskovaColors.current.Surface
    val SurfaceElevated: Color @Composable get() = LocalMeskovaColors.current.SurfaceElevated

    val Ink: Color @Composable get() = LocalMeskovaColors.current.Ink
    val InkSecondary: Color @Composable get() = LocalMeskovaColors.current.InkSecondary
    val InkMuted: Color @Composable get() = LocalMeskovaColors.current.InkMuted

    val Neon: Color @Composable get() = LocalMeskovaColors.current.Neon
    val NeonDeep: Color @Composable get() = LocalMeskovaColors.current.NeonDeep
    val NeonSoft: Color @Composable get() = LocalMeskovaColors.current.NeonSoft
    val OrangeInk: Color @Composable get() = LocalMeskovaColors.current.OrangeInk

    val PopYellow: Color @Composable get() = LocalMeskovaColors.current.PopYellow
    val PopPink: Color @Composable get() = LocalMeskovaColors.current.PopPink
    val PopBlue: Color @Composable get() = LocalMeskovaColors.current.PopBlue
    val PopLime: Color @Composable get() = LocalMeskovaColors.current.PopLime

    val CardFace: Color @Composable get() = LocalMeskovaColors.current.CardFace
    val CardInk: Color @Composable get() = LocalMeskovaColors.current.CardInk
    val CardRed: Color @Composable get() = LocalMeskovaColors.current.CardRed

    val TileInk: Color @Composable get() = LocalMeskovaColors.current.TileInk
    val CardAccent: Color @Composable get() = LocalMeskovaColors.current.CardAccent

    val Premium: Color @Composable get() = LocalMeskovaColors.current.Premium
    val Success: Color @Composable get() = LocalMeskovaColors.current.Success
    val Warning: Color @Composable get() = LocalMeskovaColors.current.Warning
    val Danger: Color @Composable get() = LocalMeskovaColors.current.Danger
    val OnStatus: Color @Composable get() = LocalMeskovaColors.current.OnStatus

    val Border: Color @Composable get() = LocalMeskovaColors.current.Border
    val BorderStrong: Color @Composable get() = LocalMeskovaColors.current.BorderStrong
}
