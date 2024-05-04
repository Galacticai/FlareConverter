package com.galacticai.flareconverter.ui.components.expressive.loading

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.galacticai.flareconverter.ui.themes.GalacticTheme
import kotlin.math.PI
import kotlin.math.sin

object SquareToCircle {
    fun keyframes() = keyframes {
        durationMillis = 2000
        0f at 0
        .6f at 800 using EaseInOut
        1f at 2000
    }

    fun draw(
        scope: DrawScope,
        t: Float,
        color: Color,
        size: Dp = 200.dp,
        offset: DpOffset = DpOffset.Zero
    ) {
        with(scope) {
            val sizePx = size.toPx()
            val offsetPx = Offset(offset.x.toPx(), offset.y.toPx())

            val center = center + offsetPx
            val shapeSize = sizePx / 1.5f

            val phase =
                ((sin(t * 2f * PI - PI / 2f) + 1f) / 2f).toFloat()

            val cornerRadius = lerp(
                shapeSize * 0.12f,
                shapeSize / 2f,
                phase
            )

            val rotation = 180f + 360f * t

            rotate(rotation, pivot = center) {
                drawRoundRect(
                    color = color,
                    topLeft = center - Offset(shapeSize / 2f, shapeSize / 2f),
                    size = Size(shapeSize, shapeSize),
                    cornerRadius = CornerRadius(cornerRadius)
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() = GalacticTheme {
    val colors = MaterialTheme.colorScheme
    val t by rememberLooping(SquareToCircle.keyframes())

    Canvas(Modifier.size(400.dp)) {
        SquareToCircle.draw(
            this,
            t,
            colors.secondaryContainer,
            size = 200.dp,
            offset = DpOffset((-60).dp, (-20).dp)
        )
    }
}