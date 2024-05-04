package global.common.util

import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.Color
import global.common.models.ColorHSL.Companion.toHSL

object ColorUtil {
    fun colorInBetween(
        @FloatRange(0.0, 1.0) x: Float,
        @FloatRange(0.0, 1.0) xMin: Float,
        @FloatRange(0.0, 1.0) xMax: Float,
        colorMin: Color,
        colorMax: Color,
    ): Color {
        if (x < xMin) return colorMin
        if (x > xMax) return colorMax

        val fraction = (x - xMin) / (xMax - xMin)
        return Color(
            red = colorMax.red + (colorMin.red - colorMax.red) * fraction,
            green = colorMax.green + (colorMin.green - colorMax.green) * fraction,
            blue = colorMax.blue + (colorMin.blue - colorMax.blue) * fraction,
            alpha = colorMax.alpha + (colorMin.alpha - colorMax.alpha) * fraction,
        )
    }

    fun Color.getSaturation(): Float {
        val max = maxOf(red, green, blue)
        val min = minOf(red, green, blue)
        val delta = max - min
        return if (max <= 0f) 0f  //? grayscale
        else delta / max
    }

    /** modify [hue]/[saturation]/[lightness] of this [Color] */
    fun Color.hsl(
        @FloatRange(0.0, 1.0) hue: Float? = null,
        @FloatRange(0.0, 1.0) saturation: Float? = null,
        @FloatRange(0.0, 1.0) lightness: Float? = null,
        @FloatRange(0.0, 1.0) alpha: Float = this.alpha,
    ) = toHSL().let {
        it.copy(
            hue = hue ?: it.hue,
            saturation = saturation ?: it.saturation,
            lightness = lightness ?: it.lightness,
        )
    }.toColor().let {
        this.copy(alpha, it.red, it.green, it.blue)
    }
}