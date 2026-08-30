package com.beloucif.bacchana.core.theme

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * WCAG 2.1 relative luminance and contrast ratio - pure math, no platform dependency, so it
 * runs identically here (JVM unit test) and would on Android/iOS if ever needed there. Formula:
 * https://www.w3.org/TR/WCAG21/#dfn-relative-luminance
 */
object WcagContrast {

    /** AA threshold for normal body text (< 18pt regular / < 14pt bold). */
    const val AA_NORMAL_TEXT = 4.5

    /** AA threshold for large text (>= 18pt regular / >= 14pt bold) and non-text UI objects. */
    const val AA_LARGE_TEXT = 3.0

    private fun channelLuminance(channel: Int): Double {
        val s = channel / 255.0
        return if (s <= 0.03928) s / 12.92 else ((s + 0.055) / 1.055).pow(2.4)
    }

    fun relativeLuminance(color: PaletteColor): Double =
        0.2126 * channelLuminance(color.red) +
            0.7152 * channelLuminance(color.green) +
            0.0722 * channelLuminance(color.blue)

    /** Contrast ratio in [1, 21] - order of [a]/[b] does not matter, the formula picks lighter/darker itself. */
    fun ratio(a: PaletteColor, b: PaletteColor): Double {
        val luminanceA = relativeLuminance(a)
        val luminanceB = relativeLuminance(b)
        val lighter = max(luminanceA, luminanceB)
        val darker = min(luminanceA, luminanceB)
        return (lighter + 0.05) / (darker + 0.05)
    }

    /**
     * Contrast of [fg] alpha-blended over [bg] (e.g. a translucent border drawn over the page
     * background), evaluated against [bg] - the WCAG 1.4.11 case for non-text UI objects
     * (borders, focus rings, progress tracks). [alpha] in `0.0..1.0`.
     */
    fun blendedRatio(fg: PaletteColor, alpha: Double, overBg: PaletteColor): Double {
        fun blendChannel(f: Int, b: Int): Int = ((f * alpha) + (b * (1 - alpha))).toInt().coerceIn(0, 255)
        val blended = PaletteColor(
            red = blendChannel(fg.red, overBg.red),
            green = blendChannel(fg.green, overBg.green),
            blue = blendChannel(fg.blue, overBg.blue),
        )
        return ratio(blended, overBg)
    }
}
