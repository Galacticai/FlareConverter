package global.common.ui.colors

import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class ColorHSL(
    val hue: Float,
    val saturation: Float,
    val lightness: Float,
    val alpha: Float
) {
    fun toColor(): Color {
        val h = ((hue % 360f) + 360f) % 360f //? [0,360)
        val s = saturation.coerceIn(0f, 1f)
        val l = lightness.coerceIn(0f, 1f)

        val c = (1f - abs(2f * l - 1f)) * s
        val x = c * (1f - abs((h / 60f) % 2f - 1f))
        val m = l - c / 2f

        val (r1, g1, b1) = when {
            h < 60f -> Triple(c, x, 0f)
            h < 120f -> Triple(x, c, 0f)
            h < 180f -> Triple(0f, c, x)
            h < 240f -> Triple(0f, x, c)
            h < 300f -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }

        val r = (r1 + m).coerceIn(0f, 1f)
        val g = (g1 + m).coerceIn(0f, 1f)
        val b = (b1 + m).coerceIn(0f, 1f)

        return Color(r, g, b, alpha)
    }

    companion object {
        fun Color.toHSL() = from(this)
        fun from(color: Color): ColorHSL {
            val r = color.red
            val g = color.green
            val b = color.blue
            val a = color.alpha

            val maxVal = max(r, max(g, b))
            val minVal = min(r, min(g, b))
            val delta = maxVal - minVal

            val l = (maxVal + minVal) / 2f

            val s = if (delta == 0f) {
                0f
            } else {
                delta / (1f - abs(2f * l - 1f))
            }

            val h = if (delta == 0f) {
                0f
            } else {
                val huePrime = when (maxVal) {
                    r -> ((g - b) / delta) % 6f
                    g -> ((b - r) / delta) + 2f
                    else -> ((r - g) / delta) + 4f
                }
                (huePrime * 60f + 360f) % 360f
            }

            return ColorHSL(h, s, l, a)
        }
    }
}