package com.beloucif.bacchana.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.beloucif.bacchana.core.theme.BacchanaPalette
import com.beloucif.bacchana.core.theme.PaletteColor

/**
 * Tavern neobrutalist palette, mirrors src/styles/tokens.css on the web (source of truth) -
 * do not hand-tune a value here without updating tokens.css first. Light is the default
 * theme: cream paper background, ink text, orange accent, hard black shadows (no glow).
 * Dark is the "pop" candlelit variant: neutral ink background (never brown/wood), the same
 * festive pops and orange accent lightened to clear WCAG on a dark surface.
 *
 * Every field is built from [com.beloucif.bacchana.core.theme.BacchanaPalette] (`:core`, pure
 * JVM) via [Color], never re-typed as a hex literal here - the exact same numbers that render
 * the UI are the ones `BacchanaPaletteContrastTest` (`:core:test`) verifies, so the two can
 * never drift out of sync.
 */
data class BacchanaColorScheme(
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
     * `core`'s `BacchanaPaletteContrastTest`.
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

private fun BacchanaPalette.toColorScheme(borderAlphaByte: Int): BacchanaColorScheme = BacchanaColorScheme(
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
// BacchanaPalette.borderAlpha exactly (docs/DESIGN_TOKENS.md section 3.4).
val LightBacchanaColors: BacchanaColorScheme = BacchanaPalette.Light.toColorScheme(borderAlphaByte = 0x26)
val DarkBacchanaColors: BacchanaColorScheme = BacchanaPalette.Dark.toColorScheme(borderAlphaByte = 0x61)

val LocalBacchanaColors = staticCompositionLocalOf { LightBacchanaColors }

/**
 * Theme-aware accessor kept as an object so every existing `BacchanaColors.Bg`-style call
 * site (300+ across the screens) keeps compiling unchanged: each property is a `@Composable`
 * getter reading [LocalBacchanaColors], exactly like `MaterialTheme.colorScheme` does.
 */
object BacchanaColors {
    val Bg: Color @Composable get() = LocalBacchanaColors.current.Bg
    val BgRaised: Color @Composable get() = LocalBacchanaColors.current.BgRaised
    val Surface: Color @Composable get() = LocalBacchanaColors.current.Surface
    val SurfaceElevated: Color @Composable get() = LocalBacchanaColors.current.SurfaceElevated

    val Ink: Color @Composable get() = LocalBacchanaColors.current.Ink
    val InkSecondary: Color @Composable get() = LocalBacchanaColors.current.InkSecondary
    val InkMuted: Color @Composable get() = LocalBacchanaColors.current.InkMuted

    val Neon: Color @Composable get() = LocalBacchanaColors.current.Neon
    val NeonDeep: Color @Composable get() = LocalBacchanaColors.current.NeonDeep
    val NeonSoft: Color @Composable get() = LocalBacchanaColors.current.NeonSoft
    val OrangeInk: Color @Composable get() = LocalBacchanaColors.current.OrangeInk

    val PopYellow: Color @Composable get() = LocalBacchanaColors.current.PopYellow
    val PopPink: Color @Composable get() = LocalBacchanaColors.current.PopPink
    val PopBlue: Color @Composable get() = LocalBacchanaColors.current.PopBlue
    val PopLime: Color @Composable get() = LocalBacchanaColors.current.PopLime

    val CardFace: Color @Composable get() = LocalBacchanaColors.current.CardFace
    val CardInk: Color @Composable get() = LocalBacchanaColors.current.CardInk
    val CardRed: Color @Composable get() = LocalBacchanaColors.current.CardRed

    val TileInk: Color @Composable get() = LocalBacchanaColors.current.TileInk
    val CardAccent: Color @Composable get() = LocalBacchanaColors.current.CardAccent

    val Premium: Color @Composable get() = LocalBacchanaColors.current.Premium
    val Success: Color @Composable get() = LocalBacchanaColors.current.Success
    val Warning: Color @Composable get() = LocalBacchanaColors.current.Warning
    val Danger: Color @Composable get() = LocalBacchanaColors.current.Danger
    val OnStatus: Color @Composable get() = LocalBacchanaColors.current.OnStatus

    val Border: Color @Composable get() = LocalBacchanaColors.current.Border
    val BorderStrong: Color @Composable get() = LocalBacchanaColors.current.BorderStrong
}
