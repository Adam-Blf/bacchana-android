package com.beloucif.bacchana.core.theme

/**
 * Single source of truth for the Bacchana color tokens, pure Kotlin (no Android dependency) so
 * it is usable both by `app/.../ui/theme/Color.kt` (which builds the rendered
 * `androidx.compose.ui.graphics.Color` values straight from these fields, never re-typing a
 * hex) and by this module's WCAG contrast guard. Mirrors `docs/DESIGN_TOKENS.md` and
 * `src/styles/tokens.css` on the web - same role names, same hex - do not hand-tune a value on
 * one platform without updating the others.
 *
 * Three roles exist alongside the themed tokens because they do NOT follow the theme, each for
 * a distinct reason documented at its declaration: [tileInk], [cardAccent], [onStatus].
 *
 * ALIGNEE SUR LE WEB LE 2026-09-14, et la derive qui a rendu ca necessaire vaut d'etre notee :
 * le web etait passe au pourpre (direction « Tirage de nuit », reportee depuis Figma le
 * 2026-08-30), ce fichier etait reste a l'orange. Accent #FA5600 contre #5B2C87, encre #111111
 * contre #2A1140 : les trois applications de la meme marque ne se ressemblaient plus. La
 * consigne ci-dessus - « same hex, do not hand-tune one platform » - etait juste ; c'est
 * qu'elle ne soit QU'UNE CONSIGNE qui a laisse passer. Tant que ces valeurs sont recopiees a
 * la main, elles rederiveront : les generer depuis `tokens.css` est la seule correction
 * durable, et elle reste a faire.
 *
 * L'alignement n'a PAS ete un echange de valeurs, et [onAccent] en est la trace : l'accent
 * etait un orange, donc un aplat clair dans les deux themes, et l'encre posee dessus pouvait
 * etre la meme que sur les ambres ([tileInk]). Il est desormais pourpre sur fond clair et
 * jaune sur fond pourpre - il change de clarte avec le theme - donc l'encre qui va dessus doit
 * en faire autant. Sans ce role, `tileInk` sur `neon` tombait a 1,72:1.
 */
data class BacchanaPalette(
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

    /**
     * Les quatre aplats de tuile. Une ROTATION, pas quatre roles : ils se distribuent par index
     * sur les modes de jeu, aucun ne porte de sens propre.
     *
     * Ils s'appelaient popYellow, popPink, popBlue et popLime jusqu'au 2026-09-14. Trois de ces
     * noms mentaient sur la teinte des le passage au pourpre - les quatre valeurs sont
     * desormais des ambres - et un nom qui ment survit plus longtemps qu'une couleur qui
     * change. Memes noms que `--color-aplat-1` a `-4` cote web.
     *
     * FIXES dans les deux themes, comme [tileInk] : l'encre posee dessus ne suit pas le theme,
     * donc le fond ne le peut pas non plus.
     */
    val aplat1: PaletteColor,
    val aplat2: PaletteColor,
    val aplat3: PaletteColor,
    val aplat4: PaletteColor,

    val cardFace: PaletteColor,
    val cardInk: PaletteColor,
    val cardRed: PaletteColor,

    /**
     * Fixed ink for text/icons/borders drawn on top of one of the four AMBRES ([aplat1] to
     * [aplat4]): those fills stay light in BOTH themes, so the ink on top of them must never
     * follow [ink] (which inverts with the theme). Anything drawn on an ACCENT fill uses
     * [onAccent] instead - the accent no longer stays light in both themes.
     *
     * Real bug, reported twice by Adam while playing the app ("du blanc sur du jaune c'est
     * illisible"), fixed on web 2026-08-04, ported here 2026-08-05.
     * See `docs/DESIGN_TOKENS.md` section 2bis. Same literal value as [cardInk] for the same
     * underlying reason (a visual object that does not change with the room's lighting), kept
     * as a separate named role to mirror the web's `tile-ink` token 1:1.
     */
    val tileInk: PaletteColor,

    /**
     * Encre posee sur un aplat d'ACCENT ([neon], [neonDeep], [neonSoft], et les fonds Material
     * primary qui en derivent). Le `--color-sur-surimpression` du web.
     *
     * A ne pas confondre avec [tileInk], qui va sur les quatre AMBRES ([aplat1] a [aplat4]) :
     * les ambres restent clairs dans les deux themes, l'accent non. Creme sur l'accent pourpre
     * du theme clair (9,31:1), pourpre sur l'accent jaune du theme sombre (11,42:1) - les deux
     * mesures sont dans `BacchanaPaletteContrastTest`.
     */
    val onAccent: PaletteColor,

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
     * the ambres (always light in both themes), these semantic colors get DARKER in
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
        // Les roles FIXES dans les deux themes. Un objet physique - une carte a jouer, un
        // aplat de tuile - ne change pas de couleur quand la piece s'assombrit.
        private val TILE_INK = paletteColor(0x2A1140)
        private val CARD_ACCENT = paletteColor(0x5B2C87)
        private val APLAT_1 = paletteColor(0xFFD029)
        private val APLAT_2 = paletteColor(0xFFB020)
        private val APLAT_3 = paletteColor(0xFFE07A)
        private val APLAT_4 = paletteColor(0xE8B81C)
        private val CARD_FACE = paletteColor(0xFFF9F0)
        private val CARD_INK = paletteColor(0x2A1140)
        private val CARD_RED = paletteColor(0x5B2C87)

        val Light = BacchanaPalette(
            bg = paletteColor(0xFFF9F0),
            bgRaised = paletteColor(0xF3E9DC),
            surface = paletteColor(0xFFFDF8),
            surfaceElevated = paletteColor(0xF3E9DC),

            ink = paletteColor(0x2A1140),
            inkSecondary = paletteColor(0x4A2470),
            inkMuted = paletteColor(0x6B4A8C),

            neon = paletteColor(0x5B2C87),
            neonDeep = paletteColor(0x4C2371),
            neonSoft = paletteColor(0x7E49AE),
            orangeInk = paletteColor(0x5B2C87),

            aplat1 = APLAT_1,
            aplat2 = APLAT_2,
            aplat3 = APLAT_3,
            aplat4 = APLAT_4,

            cardFace = CARD_FACE,
            cardInk = CARD_INK,
            cardRed = CARD_RED,

            tileInk = TILE_INK,
            // L'accent vaut pourpre sur fond clair : l'encre posee dessus est le creme du fond.
            onAccent = paletteColor(0xFFF9F0),
            cardAccent = CARD_ACCENT,

            premium = paletteColor(0x5B2C87),
            success = paletteColor(0x1B6B45),
            warning = paletteColor(0x7A5200),
            danger = paletteColor(0x8E2A14),
            // Light-theme premium/success/warning/danger are dark accents -> light ink on top.
            onStatus = paletteColor(0xFFF9F0),

            border = paletteColor(0x2A1140),
            borderAlpha = 0.48,
            borderStrong = paletteColor(0x2A1140),
        )

        val Dark = BacchanaPalette(
            // Le theme sombre du web n'assombrit pas le creme : il POSE L'APLAT POURPRE. Le
            // fond, le releve et la surface valent donc la meme teinte, et l'elevation se lit
            // au filet - le systeme n'a plus d'ombre portee.
            bg = paletteColor(0x5B2C87),
            bgRaised = paletteColor(0x5B2C87),
            surface = paletteColor(0x5B2C87),
            surfaceElevated = paletteColor(0x4C2371),

            ink = paletteColor(0xFFF9F0),
            inkSecondary = paletteColor(0xDCCFEA),
            inkMuted = paletteColor(0xC0AAD6),

            neon = paletteColor(0xFFD029),
            neonDeep = paletteColor(0xE8B81C),
            neonSoft = paletteColor(0xFFE07A),
            orangeInk = paletteColor(0xFFD029),

            aplat1 = APLAT_1,
            aplat2 = APLAT_2,
            aplat3 = APLAT_3,
            aplat4 = APLAT_4,

            cardFace = CARD_FACE,
            cardInk = CARD_INK,
            cardRed = CARD_RED,

            tileInk = TILE_INK,
            // L'accent vaut jaune sur fond pourpre : l'encre posee dessus repasse au pourpre.
            onAccent = paletteColor(0x2A1140),
            cardAccent = CARD_ACCENT,

            premium = paletteColor(0xFFD029),
            success = paletteColor(0x86DCAC),
            warning = paletteColor(0xFFB020),
            danger = paletteColor(0xFF9C84),
            // Dark-theme premium/success/warning/danger are light accents -> dark ink on top.
            onStatus = paletteColor(0x2A1140),

            border = paletteColor(0xFFF9F0),
            borderAlpha = 0.48,
            borderStrong = paletteColor(0xFFF9F0),
        )
    }
}
