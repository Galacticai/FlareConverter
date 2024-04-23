package global.common.ui.graphing.bar_chart

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class BarHScaleStyle(
    val count: Int = 10,
    val thickness: Dp = 1.dp,
    val color: Color = Color.Gray.copy(alpha = .25f),
) {
    init {
        if (count < 3) throw IllegalArgumentException("count must be greater than 3")
    }
}