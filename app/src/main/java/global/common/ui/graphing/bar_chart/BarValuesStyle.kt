package global.common.ui.graphing.bar_chart

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

sealed class BarValueStyle(
    val fontSize: TextUnit,
    val fontWeight: FontWeight,
    val padding: PaddingValues,
    val margin: PaddingValues,
    val bgRadius: Dp,
) {
    class XBarValueStyle<T>(
        fontSize: TextUnit = 10.sp,
        fontWeight: FontWeight = FontWeight.Light,
        padding: PaddingValues = PaddingValues(horizontal = 5.dp),
        margin: PaddingValues = PaddingValues(horizontal = 5.dp, vertical = 2.dp),
        bgRadius: Dp = 5.dp,
        val color: (data: T, parsed: BarData, range: ClosedRange<Float>) -> Color = { _, _, _ -> Color.White },
        val bgColor: (data: T, parsed: BarData, range: ClosedRange<Float>) -> Color = { _, _, _ ->
            Color.DarkGray.copy(alpha = .5f)
        },
        val format: (data: T, parsed: BarData, range: ClosedRange<Float>) -> String = { _, d, _ -> d.value.toString() },
    ) : BarValueStyle(
        fontSize,
        fontWeight,
        padding,
        margin,
        bgRadius,
    )

    class YBarValueStyle(
        fontSize: TextUnit = 10.sp,
        fontWeight: FontWeight = FontWeight.Bold,
        padding: PaddingValues = PaddingValues(horizontal = 5.dp),
        margin: PaddingValues = PaddingValues(horizontal = 5.dp, vertical = 2.dp),
        bgRadius: Dp = 5.dp,
        val color: (value: Float, range: ClosedRange<Float>, index: Int) -> Color = { _, _, _ -> Color.Black },
        val bgColor: (value: Float, range: ClosedRange<Float>, index: Int) -> Color = { _, _, _ ->
            Color.White.copy(alpha = .5f)
        },
        val format: (value: Float, range: ClosedRange<Float>, index: Int) -> String = { v, _, _ -> v.toString() },
    ) : BarValueStyle(
        fontSize,
        fontWeight,
        padding,
        margin,
        bgRadius,
    )
}