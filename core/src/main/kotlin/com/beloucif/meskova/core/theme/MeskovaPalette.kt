package com.beloucif.meskova.core.theme

/**
 * Single source of truth for the Meskova color tokens, pure Kotlin (no Android dependency) so
 * it is usable both by `app/.../ui/theme/Color.kt` (which builds the rendered
 * `androidx.compose.ui.graphics.Color` values straight from these fields, never re-typing a
 * hex) and by this module's WCAG contrast guard. Mirrors `docs/DESIGN_TOKENS.md` and
 * `src/styles/tokens.css` on the web - same role names, same hex - do not hand-tune a value on
 * one platform without updating the others.
 *
 * Two roles exist alongside the themed tokens because they do NOT follow the theme, each for a
 * distinct reason documented at its declaration: [tileInk], [cardAccent], [onStatus].
 */
data class MeskovaPalette(
    val bg: PaletteColor,
    val bgRaised: PaletteColor,
    val surface: PaletteColor,
    val surfaceElevated: PaletteColor,

    val ink: PaletteColor,
    val inkSecondary: PaletteColor,
    val inkMuted: PaletteColor,

    val neon: PaletteColor,
    val neonDeep: PaletteColor,
    val neonSoft: PaletteColor,
    val orangeInk: PaletteColor,

    val popYellow: PaletteColor,
    val popPink: PaletteColor,
    val popBlue: PaletteColor,
    val popLime: PaletteColor,

    val cardFace: PaletteColor,
    val cardInk: PaletteColor,
    val cardRed: PaletteColor,

    /**
     * Fixed ink for text/icons/borders drawn on top of a `pop-*` or `neon*` fill: those fills
     * stay light in BOTH themes, so the ink on top of them must never follow [ink] (which
     * inverts with the theme). Real bug, reported twice by Adam while playing the app ("du
     * blanc sur du jaune c'est illisible"), fixed on web 2026-08-04, ported here 2026-08-05.
     * See `docs/DESIGN_TOKENS.md` section 2bis. Same literal value as [cardInk] for the same
     * underlying reason (a visual object that does not change with the room's lighting), kept
     * as a separate named role to mirror the web's `tile-ink` token 1:1.
     */
    val tileInk: PaletteColor,

    /**
     * Fixed orange for text drawn on [cardFace] (always white, a physical card face never
     * follows the theme): unlike [orangeInk], which is recalculated per theme against the
     * themed [bg], this value must stay correct against a surface that never changes - so it
     * never changes either.
     */
    val cardAccent: PaletteColor,

    val premium: PaletteColor,
    val success: PaletteColor,
    val warning: PaletteColor,
    val danger: PaletteColor,

    /**
     * Ink for text/icons drawn on a solid [premium]/[success]/[warning]/[danger] fill. Unlike
     * the pop/neon family (always light in both themes), these semantic colors get DARKER in
     * light theme and LIGHTER in dark theme - the opposite direction from [bg] - so the correct
     * on-fill ink flips too: light ink in light theme, dark ink in dark theme. Computed once
     * per theme instance below (not derived ad hoc at the call site) so a caller can never pick
     * the wrong direction by accident.
     */
    val onStatus: PaletteColor,

    val border: PaletteColor,
    val borderAlpha: Double,
    val borderStrong: PaletteColor,
) {
    companion object {
        private val TILE_INK = paletteColor(0x111111)
        private val CARD_ACCENT = paletteColor(0xC74300)

        val Light = MeskovaPalette(
            bg = paletteColor(0xFFF9F0),
            bgRaised = paletteColor(0xFFF3E0),
            surface = paletteColor(0xFFFFFF),
            surfaceElevated = paletteColor(0xFFEFD6),

            ink = paletteColor(0x111111),
            inkSecondary = paletteColor(0x44444A),
            inkMuted = paletteColor(0x6B6B70),

            neon = paletteColor(0xFA5600),
            neonDeep = paletteColor(0xE24E00),
            neonSoft = paletteColor(0xFF8A3D),
            orangeInk = paletteColor(0xC74300),

            popYellow = paletteColor(0xFFD029),
            popPink = paletteColor(0xFF6FB2),
            popBlue = paletteColor(0x6E9BFF),
            popLime = paletteColor(0x9BE94C),

            cardFace = paletteColor(0xFFFFFF),
            cardInk = paletteColor(0x111111),
            cardRed = paletteColor(0xC71F2D),

            tileInk = TILE_INK,
            cardAccent = CARD_ACCENT,

            premium = paletteColor(0x855C12),
            success = paletteColor(0x177C50),
            warning = paletteColor(0xB45309),
            danger = paletteColor(0xC71F2D),
            // Light-theme premium/success/warning/danger are dark accents -> light ink on top.
            onStatus = paletteColor(0xFFFFFF),

            border = paletteColor(0x111111),
            borderAlpha = 0.15,
            borderStrong = paletteColor(0x111111),
        )

        val Dark = MeskovaPalette(
            bg = paletteColor(0x141216),
            bgRaised = paletteColor(0x221E28),
            surface = paletteColor(0x2E2836),
            surfaceElevated = paletteColor(0x3C3446),

            ink = paletteColor(0xF4EFE6),
            inkSecondary = paletteColor(0xA39DB0),
            inkMuted = paletteColor(0x958FA3),

            neon = paletteColor(0xFF7A2E),
            neonDeep = paletteColor(0xE86014),
            neonSoft = paletteColor(0xFF9E5C),
            orangeInk = paletteColor(0xFF7A2E),

            popYellow = paletteColor(0xFFD84D),
            popPink = paletteColor(0xFF7FBE),
            popBlue = paletteColor(0x7FB0FF),
            popLime = paletteColor(0xA6F05A),

            cardFace = paletteColor(0xFFFFFF),
            cardInk = paletteColor(0x111111),
            cardRed = paletteColor(0xC71F2D),

            tileInk = TILE_INK,
            cardAccent = CARD_ACCENT,

            premium = paletteColor(0xD9A441),
            success = paletteColor(0x3EA876),
            warning = paletteColor(0xD67428),
            danger = paletteColor(0xFF7878),
            // Dark-theme premium/success/warning/danger are light accents -> dark ink on top.
            onStatus = TILE_INK,

            border = paletteColor(0xF4EFE6),
            borderAlpha = 0.38,
            borderStrong = paletteColor(0xF4EFE6),
        )
    }
}
