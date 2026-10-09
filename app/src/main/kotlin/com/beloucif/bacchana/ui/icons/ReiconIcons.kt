package com.beloucif.bacchana.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * The few Reicon glyphs the app draws, as Compose vectors.
 *
 * Reicon (MIT, https://reicon.dev) ships SVG and web packages only, so the path
 * data is carried here on the same 24 px grid. Each path is filled with a
 * single colour that `Icon(tint = ...)` replaces, and the flag says whether
 * the path uses the even-odd fill rule.
 */
object ReiconIcons {
    /** Reicon `Plus`, Filled. */
    val Add: ImageVector by lazy {
        reicon(
            "Add",
            "M11 20C11 20.5523 11.4477 21 12 21C12.5523 21 13 20.5523 13 20V13H20C20.5523 13 21 12.5523 21 12C21 11.4477 20.5523 11 20 11H13V4C13 3.44771 12.5523 3 12 3C11.4477 3 11 3.44771 11 4V11H4C3.44771 11 3 11.4477 3 12C3 12.5523 3.44771 13 4 13H11V20Z" to false,
        )
    }

    /** Reicon `X`, Filled. */
    val Close: ImageVector by lazy {
        reicon(
            "Close",
            "M18.2929 19.7071C18.6834 20.0976 19.3166 20.0976 19.7071 19.7071C20.0976 19.3166 20.0976 18.6834 19.7071 18.2929L13.4142 12L19.7071 5.70711C20.0976 5.31658 20.0976 4.68342 19.7071 4.29289C19.3166 3.90237 18.6834 3.90237 18.2929 4.29289L12 10.5858L5.70711 4.29289C5.31658 3.90237 4.68342 3.90237 4.29289 4.29289C3.90237 4.68342 3.90237 5.31658 4.29289 5.70711L10.5858 12L4.29289 18.2929C3.90237 18.6834 3.90237 19.3166 4.29289 19.7071C4.68342 20.0976 5.31658 20.0976 5.70711 19.7071L12 13.4142L18.2929 19.7071Z" to false,
        )
    }

    /** Reicon `Gear`, Filled. */
    val Settings: ImageVector by lazy {
        reicon(
            "Settings",
            "M2.51888 16.0495C1.96542 15.0892 2.29369 13.861 3.25278 13.3063C4.25649 12.7257 4.25649 11.274 3.25278 10.6935C2.29369 10.1388 1.96542 8.9106 2.51888 7.95027L3.75832 5.79967C4.31192 4.8391 5.53845 4.50967 6.49777 5.06452C7.50054 5.64451 8.75537 4.92 8.75537 3.75827C8.75537 2.64955 9.65268 1.75 10.7605 1.75H13.2395C14.3474 1.75 15.2447 2.64957 15.2447 3.7583C15.2447 4.92008 16.4995 5.64461 17.5023 5.06462C18.4616 4.50976 19.6881 4.8392 20.2417 5.79978L21.4812 7.95045C22.0346 8.91074 21.7064 10.1389 20.7473 10.6936C19.7437 11.2741 19.7437 12.7257 20.7473 13.3062C21.7064 13.8609 22.0346 15.089 21.4812 16.0493L20.2417 18.2C19.6881 19.1605 18.4616 19.49 17.5023 18.9351C16.4995 18.3552 15.2447 19.0797 15.2447 20.2416C15.2447 21.3503 14.3473 22.25 13.2394 22.25H10.7606C9.65272 22.25 8.75537 21.3504 8.75537 20.2416C8.75537 19.0798 7.50053 18.3553 6.49774 18.9353C5.53841 19.4901 4.31192 19.1607 3.75832 18.2001L2.51888 16.0495ZM8.75006 12C8.75006 10.2051 10.2051 8.75 12.0001 8.75C13.795 8.75 15.2501 10.2051 15.2501 12C15.2501 13.7949 13.795 15.25 12.0001 15.25C10.2051 15.25 8.75006 13.7949 8.75006 12Z" to true,
        )
    }

    /** Reicon `Lock`, Filled. */
    val Lock: ImageVector by lazy {
        reicon(
            "Lock",
            "M5.25 10.0546V8C5.25 4.27208 8.27208 1.25 12 1.25C15.7279 1.25 18.75 4.27208 18.75 8V10.0546C19.8648 10.1379 20.5907 10.348 21.1213 10.8787C22 11.7574 22 13.1716 22 16C22 18.8284 22 20.2426 21.1213 21.1213C20.2426 22 18.8284 22 16 22H8C5.17157 22 3.75736 22 2.87868 21.1213C2 20.2426 2 18.8284 2 16C2 13.1716 2 11.7574 2.87868 10.8787C3.40931 10.348 4.13525 10.1379 5.25 10.0546ZM6.75 8C6.75 5.10051 9.10051 2.75 12 2.75C14.8995 2.75 17.25 5.10051 17.25 8V10.0036C16.867 10 16.4515 10 16 10H8C7.54849 10 7.13301 10 6.75 10.0036V8Z" to true,
        )
    }

    /** Reicon `Check`, Filled. */
    val Check: ImageVector by lazy {
        reicon(
            "Check",
            "M21.7071 5.29289C22.0976 5.68342 22.0976 6.31658 21.7071 6.70711L9.70711 18.7071C9.31658 19.0976 8.68342 19.0976 8.29289 18.7071L2.29289 12.7071C1.90237 12.3166 1.90237 11.6834 2.29289 11.2929C2.68342 10.9024 3.31658 10.9024 3.70711 11.2929L9 16.5858L20.2929 5.29289C20.6834 4.90237 21.3166 4.90237 21.7071 5.29289Z" to false,
        )
    }

    /** Reicon `ArrowLeft`, Filled. */
    val ArrowBack: ImageVector by lazy {
        reicon(
            "ArrowBack",
            "M20 11.25C20.4142 11.25 20.75 11.5858 20.75 12C20.75 12.4142 20.4142 12.75 20 12.75H10.75L10.75 18C10.75 18.3034 10.5673 18.5768 10.287 18.6929C10.0068 18.809 9.68417 18.7449 9.46967 18.5304L3.46967 12.5304C3.32902 12.3897 3.25 12.1989 3.25 12C3.25 11.8011 3.32902 11.6103 3.46967 11.4697L9.46967 5.46969C9.68417 5.25519 10.0068 5.19103 10.287 5.30711C10.5673 5.4232 10.75 5.69668 10.75 6.00002L10.75 11.25H20Z" to false,
        )
    }
}

private fun reicon(name: String, vararg paths: Pair<String, Boolean>): ImageVector =
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        for ((data, evenOdd) in paths) {
            addPath(
                pathData = PathParser().parsePathString(data).toNodes(),
                pathFillType = if (evenOdd) PathFillType.EvenOdd else PathFillType.NonZero,
                fill = SolidColor(Color.Black),
            )
        }
    }.build()

