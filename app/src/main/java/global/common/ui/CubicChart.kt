package global.common.ui


import android.graphics.Paint.Align
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

sealed class CubicChartScaleStyle(
    val lineWidth: Float = 2f,
    val lineColor: Color = Color.Black,
    val textColor: Color = Color.Black,
    val textSize: Float = 12f,
    val unit: String? = null,
) {
    class CubicChartXScaleStyle(
        lineWidth: Float = 2f,
        lineColor: Color = Color.Black,
        textColor: Color = Color.Black,
        textSize: Float = 12f,
        unit: String? = null,
    ) : CubicChartScaleStyle(lineWidth, lineColor, textColor, textSize, unit)

    class CubicChartYScaleStyle(
        val lineCount: Int = 5,
        lineWidth: Float = 2f,
        lineColor: Color = Color.Black,
        textColor: Color = Color.Black,
        textSize: Float = 12f,
        unit: String? = null,
    ) : CubicChartScaleStyle(lineWidth, lineColor, textColor, textSize, unit)
}

data class CubicChartStyle(
    val bgColor: Color = Color.White,
    val graphColor: Color = Color.Blue,
    val pointColor: Color = Color.Black,
    val pointSize: Dp = 3.dp,
    val lineWidth: Dp = 3.dp,
    val yScaleStyle: CubicChartScaleStyle.CubicChartYScaleStyle = CubicChartScaleStyle.CubicChartYScaleStyle(),
    val xScaleStyle: CubicChartScaleStyle.CubicChartXScaleStyle = CubicChartScaleStyle.CubicChartXScaleStyle(),
)

data class CubicChartData(val items: List<CubicChartItem>) {
    val labels = items.map { it.label }
    val values = items.map { it.value }

    companion object {
        fun fromItems(vararg item: CubicChartItem) = CubicChartData(item.toList())
    }
}

data class CubicChartItem(val label: String, val value: Float)

@Composable
fun CubicChart(
    modifier: Modifier = Modifier,
    data: CubicChartData,
    height: Dp = 200.dp,
    style: CubicChartStyle = CubicChartStyle(),
) {

    Surface(
        color = style.bgColor,
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .then(modifier)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .padding(4.dp)
        ) {

            val maxY = data.values.maxOrNull() ?: 1f

            val spacePerHour = (size.width) / (data.values.size - 1)
            val scaleY = size.height / maxY

            val normX = mutableListOf<Float>()
            val normY = mutableListOf<Float>()

            val strokePath = Path().apply {
                for (i in data.values.indices) {
                    val currentX = i * spacePerHour

                    val currentY = size.height - data.values[i] * scaleY // Scale y-coordinate

                    if (i == 0) {
                        moveTo(currentX, currentY)
                    } else {
                        val previousX = (i - 1) * spacePerHour

                        val conX1 = (previousX + currentX) / 2f
                        val conX2 = (previousX + currentX) / 2f

                        val conY1 = size.height - data.values[i - 1] * scaleY // Scale y-coordinate
                        val conY2 = currentY

                        cubicTo(
                            x1 = conX1,
                            y1 = conY1,
                            x2 = conX2,
                            y2 = conY2,
                            x3 = currentX,
                            y3 = currentY
                        )
                    }
                    // Circle dot points
                    normX.add(currentX)
                    normY.add(currentY)
                }
            }

            val bgPaint = android.graphics.Paint().apply {
                color = style.bgColor.copy(alpha = .75f).toArgb()
                this.style = android.graphics.Paint.Style.FILL
            }
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawRect(
                    0f,
                    0f,
                    maxY,
                    maxY,
                    bgPaint
                )
            }
            drawPath(
                path = strokePath,
                color = style.graphColor,
                style = Stroke(
                    width = style.lineWidth.toPx(),
                    cap = StrokeCap.Round
                )
            )

            (normX.indices).forEach {
                drawCircle(
                    style.pointColor,
                    radius = style.pointSize.toPx(),
                    center = Offset(normX[it], normY[it])
                )
            }

            // y axis
            drawIntoCanvas { canvas ->
                val textPaint = android.graphics.Paint().apply {
                    color = style.yScaleStyle.textColor.toArgb()
                    textSize = style.yScaleStyle.textSize
                    textAlign = Align.LEFT
                }

                for (i in 0 until style.yScaleStyle.lineCount) {
                    val sectionHeight = (size.height / (style.yScaleStyle.lineCount - 1))
                    var yPosition =
                        size.height - i * sectionHeight + (style.yScaleStyle.textSize / 2)
                    drawLine(
                        color = style.yScaleStyle.lineColor,
                        start = Offset(0f, yPosition),
                        end = Offset(size.width, yPosition),
                        strokeWidth = style.yScaleStyle.lineWidth
                    )

                    val text: String
                    if (i == 0) {
                        text = ""
                        yPosition -= style.yScaleStyle.textSize
                    } else text =
                        "${((maxY / (style.yScaleStyle.lineCount - 1)) * i).toInt()}" +
                                (style.yScaleStyle.unit ?: "")

                    val yPositionText = yPosition + (style.yScaleStyle.textSize / 2)

                    // Draw translucent rectangle behind text
                    val rectLeft = 0f
                    val rectTop = yPositionText - style.yScaleStyle.textSize
                    val rectRight =
                        rectLeft + (style.yScaleStyle.textSize * (text.trim().length * .6f))
                    val rectBottom = yPositionText + style.yScaleStyle.textSize / 2
                    canvas.nativeCanvas.drawRect(
                        rectLeft,
                        rectTop,
                        rectRight,
                        rectBottom,
                        bgPaint
                    )

                    // Draw text on top of the rectangle
                    canvas.nativeCanvas.drawText(text, 0f, yPositionText, textPaint)
                }
            }

            // x axis
            drawIntoCanvas { canvas ->
                val textPaint = android.graphics.Paint().apply {
                    color = style.xScaleStyle.textColor.toArgb()
                    textSize = style.xScaleStyle.textSize
                    textAlign = Align.CENTER
                }

                for (i in data.labels.indices) {
                    val xPosition = i * (size.width / (data.labels.size - 1))
                    drawLine(
                        color = style.xScaleStyle.lineColor,
                        start = Offset(xPosition, 0f),
                        end = Offset(xPosition, size.height),
                        strokeWidth = style.xScaleStyle.lineWidth
                    )

                    val text = data.labels[i]

                    // Draw translucent rectangle behind text
                    val rectLeft = xPosition - style.xScaleStyle.textSize * text.length / 4
                    val rectTop = size.height - style.xScaleStyle.textSize
                    val rectRight = rectLeft + (style.xScaleStyle.textSize * text.length * 0.6f)
                    val rectBottom = size.height
                    canvas.nativeCanvas.drawRect(
                        rectLeft,
                        rectTop,
                        rectRight,
                        rectBottom,
                        bgPaint
                    )

                    // Draw text on top of the rectangle
                    canvas.nativeCanvas.drawText(
                        text,
                        xPosition,
                        size.height - style.xScaleStyle.textSize / 2,
                        textPaint
                    )
                }
            }
        }
    }
}
