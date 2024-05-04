package global.common.util

import androidx.compose.ui.graphics.Color

object ColorUtil {
    fun colorInBetween(
        x: Float,
        xMin: Float,
        xMax: Float,
        colorMin: Color,
        colorMax: Color
    ): Color {
        if (x < xMin) return colorMin
        if (x > xMax) return colorMax

        val fraction = (x - xMin) / (xMax - xMin)
        return Color(
            red = (colorMin.red - colorMax.red) * fraction + colorMax.red,
            green = (colorMin.green - colorMax.green) * fraction + colorMax.green,
            blue = (colorMin.blue - colorMax.blue) * fraction + colorMax.blue
        )
    }

    fun Color.getSaturation(): Float {
        val max = maxOf(red, green, blue)
        val min = minOf(red, green, blue)
        val delta = max - min
        return if (max <= 0f) 0f  //? grayscale
        else delta / max
    }

}