package global.common.ui.graphing.bar_chart

import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class BarStyle<T>(
    val heightMax: Dp = 200.dp,
    val width: Dp = 20.dp,
    val radius: Dp = 5.dp,
    val spacing: Dp = 2.dp,
    val color: (data: T, range: ClosedRange<Float>) -> Color = { _, _ -> Color.Blue.copy(alpha = .5f) },
    val border: BorderStroke? = null,
)