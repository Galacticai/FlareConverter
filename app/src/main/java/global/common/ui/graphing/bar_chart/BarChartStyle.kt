package global.common.ui.graphing.bar_chart

import androidx.compose.ui.graphics.Color

data class BarChartStyle<T>(
    val bar: BarStyle<T> = BarStyle(),
    val bgColor: Color = Color.Unspecified,
    val xValue: BarValueStyle.XBarValueStyle<T>? = BarValueStyle.XBarValueStyle(),
    val yValue: BarValueStyle.YBarValueStyle? = BarValueStyle.YBarValueStyle(),
    val scrollButton: BarScrollButtonStyle? = BarScrollButtonStyle(),
    val horizontalScale: BarHScaleStyle? = BarHScaleStyle(),
)