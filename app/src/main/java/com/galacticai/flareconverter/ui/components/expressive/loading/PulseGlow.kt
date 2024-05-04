package com.galacticai.flareconverter.ui.components.expressive.loading

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.galacticai.flareconverter.ui.themes.GalacticTheme
import kotlin.math.PI
import kotlin.math.sin

object PulseGlow {
    fun keyframes () = tween<Float>(2000)

    fun draw(
        scope: DrawScope,
        t: Float,
        color: Color,
        radius: Dp = 100.dp,
        offset: DpOffset = DpOffset.Zero,
        alpha: Float = .1f
    ) = with(scope) {
        val center = center + Offset(offset.x.toPx(), offset.y.toPx())

        val pulse = ((sin(t * 2f * PI) + 1f) / 2f).toFloat()
        val radiusPx = radius.toPx() * (0.85f + pulse * 0.15f)

        drawCircle(
            color = color.copy(alpha + pulse * alpha),
            radius = radiusPx,
            center = center
        )
    }
}


@Preview
@Composable
private fun ProgressLinePreview() = GalacticTheme {
    val colors = MaterialTheme.colorScheme
    val t by rememberLooping(PulseGlow.keyframes())
    val tCircle by rememberLooping(SquareToCircle.keyframes())

    Canvas(Modifier.size(200.dp)) {
        PulseGlow.draw(
            scope = this,
            t = t,
            color = colors.secondaryContainer,
            radius = 90.dp,
        )

        SquareToCircle.draw(
            this,
            tCircle,
            color = colors.primaryContainer
        )
    }
}
