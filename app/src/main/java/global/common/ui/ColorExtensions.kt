package global.common.ui

import androidx.compose.ui.graphics.Color

fun Color.getSaturation(): Float {
    val max = maxOf(red, green, blue)
    val min = minOf(red, green, blue)
    val delta = max - min
    return if (max <= 0f) 0f  //? grayscale
    else delta / max
}
