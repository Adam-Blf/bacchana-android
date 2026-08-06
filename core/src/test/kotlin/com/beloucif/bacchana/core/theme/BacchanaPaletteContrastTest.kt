package com.beloucif.bacchana.core.theme

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Mechanical WCAG 2.1 contrast guard for the Bacchana palette. Every pair below reads its colors
 * straight off [BacchanaPalette.Light] / [BacchanaPalette.Dark] - the exact same objects
 * `app/.../ui/theme/Color.kt` builds the rendered Compose colors from - so there is no
 * hand-typed hex list to drift out of sync with the real palette. Run with `./gradlew :core:test`.
 *
 * Real bug this guards against (reported twice by Adam while playing the app): [BacchanaPalette.ink]
 * inverts with the theme (dark ink in light theme, cream in dark theme) while the `pop-*`/`neon*`
 * accent fills stay light in BOTH themes. Text painted with `ink` on top of one of those fills
 * fell to ~1.2:1 in dark theme - nowhere near the 4.5:1 AA floor. Fixed on web 2026-08-04
 * (`docs/DESIGN_TOKENS.md` section 2bis, `scripts/check_contrast.mjs`), ported here 2026-08-05.
 *
 * Each pair is tagged with the screen(s) that actually render it, mirroring the web guard's
 * `PAIRS` table - kept a curated list (not a brute-force cross product of every role against
 * every role) because a handful of role combinations are legitimately never rendered together
 * (e.g. `ink` painted directly on `neon` is exactly the bug this file exists to prevent).
 */
class BacchanaPaletteContrastTest {

    private data class Pair(
        val label: String,
        val fg: (BacchanaPalette) -> PaletteColor,
        val bg: (BacchanaPalette) -> PaletteColor,
        val threshold: Double,
        val usage: String,
    )

    private val pairs = listOf(
        // --- Texte de base sur les fonds de page (baseline, regression globale) ---
        Pair("ink / bg", { it.ink }, { it.bg }, WcagContrast.AA_NORMAL_TEXT, "corps de texte, toutes les ecrans"),
        Pair("inkSecondary / bg", { it.inkSecondary }, { it.bg }, WcagContrast.AA_NORMAL_TEXT, "texte secondaire"),
        Pair("inkMuted / bg", { it.inkMuted }, { it.bg }, WcagContrast.AA_NORMAL_TEXT, "legendes/labels discrets"),
        Pair("orangeInk / bg", { it.orangeInk }, { it.bg }, WcagContrast.AA_NORMAL_TEXT, "liens/labels orange"),
        Pair("premium / bg", { it.premium }, { it.bg }, WcagContrast.AA_NORMAL_TEXT, "badges premium (texte, pas fond)"),
        Pair("success / bg", { it.success }, { it.bg }, WcagContrast.AA_NORMAL_TEXT, "texte succes"),
        Pair("warning / bg", { it.warning }, { it.bg }, WcagContrast.AA_NORMAL_TEXT, "texte warning"),
        Pair("danger / bg", { it.danger }, { it.bg }, WcagContrast.AA_NORMAL_TEXT, "texte erreur/danger"),

        // --- Hierarchie d'elevation (cartes, modales) ---
        Pair("ink / surface", { it.ink }, { it.surface }, WcagContrast.AA_NORMAL_TEXT, "HubScreen ModeTile, RecapScreen, listes de joueurs"),
        Pair("ink / surfaceElevated", { it.ink }, { it.surfaceElevated }, WcagContrast.AA_NORMAL_TEXT, "ConsentBanner"),
        Pair("inkSecondary / surfaceElevated", { it.inkSecondary }, { it.surfaceElevated }, WcagContrast.AA_NORMAL_TEXT, "corps de texte ConsentBanner"),

        // --- Cartes a jouer (objet physique, fond blanc fixe dans les 2 themes) ---
        Pair("cardInk / cardFace", { it.cardInk }, { it.cardFace }, WcagContrast.AA_NORMAL_TEXT, "PlayingCard, QuizQuestionCard, AuctionThemeCard, TribunalCharge, RankingJudging"),
        Pair("cardRed / cardFace", { it.cardRed }, { it.cardFace }, WcagContrast.AA_NORMAL_TEXT, "pips rouges des cartes, RouletteResultCard"),
        Pair("cardAccent / cardFace", { it.cardAccent }, { it.cardFace }, WcagContrast.AA_NORMAL_TEXT, "WouldYouRatherOptionCard label A/B"),

        // --- Texte pose sur un aplat pop/neon plein (le bug corrige) ---
        Pair("tileInk / popYellow", { it.tileInk }, { it.popYellow }, WcagContrast.AA_NORMAL_TEXT, "RouletteWheel segments, HubScreen theme toggle"),
        Pair("tileInk / popPink", { it.tileInk }, { it.popPink }, WcagContrast.AA_NORMAL_TEXT, "invariant du design system (docs section 2bis)"),
        Pair("tileInk / popBlue", { it.tileInk }, { it.popBlue }, WcagContrast.AA_NORMAL_TEXT, "HubScreen theme toggle"),
        Pair("tileInk / popLime", { it.tileInk }, { it.popLime }, WcagContrast.AA_NORMAL_TEXT, "invariant du design system (docs section 2bis)"),
        Pair("tileInk / neon", { it.tileInk }, { it.neon }, WcagContrast.AA_NORMAL_TEXT, "RankingReturn, boutons CTA primaires (Quiz/Ranking/Tribunal/Auction/WYR)"),
        Pair("tileInk / neonDeep", { it.tileInk }, { it.neonDeep }, WcagContrast.AA_NORMAL_TEXT, "WelcomeScreen icone ajout joueur, PromptScreen bouton suivant"),
        Pair("tileInk / neonSoft", { it.tileInk }, { it.neonSoft }, WcagContrast.AA_NORMAL_TEXT, "RankingHandoff, RankingJudging (ligne selectionnee), TribunalHandoff, QuizBadge, QuizChoiceCard"),

        // --- Ink adapte aux fonds semantiques (direction inverse de bg entre les 2 themes) ---
        Pair("onStatus / premium", { it.onStatus }, { it.premium }, WcagContrast.AA_NORMAL_TEXT, "AuctionScreen stepper mise (icone +)"),
        Pair("onStatus / success", { it.onStatus }, { it.success }, WcagContrast.AA_NORMAL_TEXT, "AuctionScreen stepper cite, AuctionResult succes, RankingGuessing reveal (bonne reponse)"),
        Pair("onStatus / warning", { it.onStatus }, { it.warning }, WcagContrast.AA_NORMAL_TEXT, "invariant du design system, aucun usage actuel"),
        Pair("onStatus / danger", { it.onStatus }, { it.danger }, WcagContrast.AA_NORMAL_TEXT, "invariant du design system, aucun usage actuel"),
        Pair("cardFace / cardRed", { it.cardFace }, { it.cardRed }, WcagContrast.AA_NORMAL_TEXT, "AuctionResult echec, RankingGuessing reveal (mauvaise reponse), WouldYouRatherOptionCard minorite"),
    )

    @Test
    fun `every ink-on-fill pair used by the app clears WCAG AA in light theme`() = assertAllPass(BacchanaPalette.Light, "clair")

    @Test
    fun `every ink-on-fill pair used by the app clears WCAG AA in dark theme`() = assertAllPass(BacchanaPalette.Dark, "sombre")

    private fun assertAllPass(palette: BacchanaPalette, themeName: String) {
        val failures = pairs.mapNotNull { pair ->
            val ratio = WcagContrast.ratio(pair.fg(palette), pair.bg(palette))
            if (ratio < pair.threshold) {
                "  $themeName / ${pair.label} (${pair.usage}): ${"%.2f".format(ratio)}:1 < ${pair.threshold}:1"
            } else {
                null
            }
        }
        assertTrue(
            "Contraste WCAG insuffisant detecte:\n${failures.joinToString("\n")}",
            failures.isEmpty(),
        )
    }

    /**
     * Locks the original bug in place as a documented, permanently-red combination: `ink`
     * painted directly on `popYellow` in dark theme must never pass AA. If this assertion ever
     * starts failing, it means `ink`'s dark value stopped inverting - which is exactly the
     * property the rest of this file relies on to justify [BacchanaPalette.tileInk]/[BacchanaPalette.onStatus]
     * existing as separate, non-thematic roles. Matches the ~1.2:1 measured on web before the fix.
     */
    @Test
    fun `regression fixture - ink on popYellow in dark theme stays below AA`() {
        val ratio = WcagContrast.ratio(BacchanaPalette.Dark.ink, BacchanaPalette.Dark.popYellow)
        assertTrue(
            "ink on popYellow in dark theme should stay under the AA floor (got $ratio:1) - " +
                "this pairing must never be used in the UI, see tileInk",
            ratio < WcagContrast.AA_NORMAL_TEXT,
        )
    }

    /**
     * Dark-theme border alpha check (WCAG 1.4.11, non-text UI objects, 3:1 floor): the fine
     * divider/border blended over [BacchanaPalette.bg] at [BacchanaPalette.borderAlpha]. Docs
     * section 3.4: this is the actual value that changed as part of this refonte, from 0.20
     * (1.76:1, below floor) to 0.38 (>= 3:1) - light theme's thin border (0.15 alpha, unchanged,
     * pre-existing) is intentionally left out here: [BacchanaPalette.borderStrong] (opaque,
     * >= 16:1 in both themes) is the documented primary elevation cue in both themes, the thin
     * border is a secondary reinforcement, not something this task's bug touches.
     */
    @Test
    fun `dark theme border alpha clears the 3-to-1 non-text UI floor`() {
        val ratio = WcagContrast.blendedRatio(BacchanaPalette.Dark.border, BacchanaPalette.Dark.borderAlpha, BacchanaPalette.Dark.bg)
        assertTrue(
            "dark border blended over bg should clear ${WcagContrast.AA_LARGE_TEXT}:1 (got $ratio:1)",
            ratio >= WcagContrast.AA_LARGE_TEXT,
        )
    }
}
