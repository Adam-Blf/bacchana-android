package com.beloucif.bacchus.core.theme

/**
 * Plain RGB triple, zero Android/Compose dependency - the pure-JVM mirror of a design token.
 * This is the single source of truth consumed by both `app/.../ui/theme/Color.kt` (which
 * builds the rendered `androidx.compose.ui.graphics.Color` values from these exact numbers)
 * and this module's WCAG contrast guard (`BacchusPaletteContrastTest`). Changing a hex value
 * here changes both the rendered UI and the test's expectations from the same number - there
 * is no hand-copied hex on either side that can quietly drift out of sync.
 */
data class PaletteColor(val red: Int, val green: Int, val blue: Int) {
    init {
        require(red in 0..255) { "red channel out of range: $red" }
        require(green in 0..255) { "green channel out of range: $green" }
        require(blue in 0..255) { "blue channel out of range: $blue" }
    }

    /** Packed 0xRRGGBB, ready for `Color(0xFF000000.toInt() or rgbInt)` on the Android side. */
    val rgbInt: Int get() = (red shl 16) or (green shl 8) or blue

    val hex: String get() = "#%02X%02X%02X".format(red, green, blue)
}

/** Builds a [PaletteColor] from a 0xRRGGBB literal, e.g. `paletteColor(0xFF7A2E)`. */
fun paletteColor(rgbHex: Int): PaletteColor = PaletteColor(
    red = (rgbHex shr 16) and 0xFF,
    green = (rgbHex shr 8) and 0xFF,
    blue = rgbHex and 0xFF,
)
