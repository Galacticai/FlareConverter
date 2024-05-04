package global.common.models.space.dp_bound

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.max

object DpBoundUtil {
    fun DpBound.difference(to: DpBound): Dp {
        val xyDelta = hypot(
            (to.position.x - position.x).value,
            (to.position.y - position.y).value,
        )
        val whDelta = hypot(
            (to.size.x - size.x).value,
            (to.size.y - size.y).value,
        )
        return max(xyDelta, whDelta).dp
    }

    fun DpBound.getOrientation(to: DpBound): Orientation {
        val xDelta = max(
            abs((to.position.x - position.x).value),
            abs((to.size.x - size.x).value)
        )
        val yDelta = max(
            abs((to.position.y - position.y).value),
            abs((to.size.y - size.y).value)
        )
        return if (xDelta > yDelta) Orientation.Horizontal else Orientation.Vertical
    }

    fun DpBound.getOrientationSign(to: DpBound, orientation: Orientation) = when (orientation) {
        Orientation.Horizontal -> if (to.position.x > position.x) 1 else -1
        Orientation.Vertical -> if (to.position.y > position.y) 1 else -1
    }

    fun DpBound.interpolate(
        to: DpBound,
        ratio: Float,
        interpolation: Interpolation = Interpolation.Smooth,
    ): DpBound {
        if (ratio == 0f) return this@interpolate
        if (ratio == 1f) return to

        val fraction = interpolation.getFraction(ratio)
        fun lerp(start: Dp, stop: Dp) = start + (stop - start) * fraction

        return DpBound(
            x = lerp(position.x, to.position.x),
            y = lerp(position.y, to.position.y),
            w = lerp(size.x, to.size.x),
            h = lerp(size.y, to.size.y)
        )
    }

    fun DpBound.interpolate(
        to: DpBound,
        distance: Dp,
        interpolation: Interpolation = Interpolation.Smooth,
        delta: Dp? = null,
    ): DpBound {
        val delta = delta ?: difference(to)

        val ratio =
            if (delta == 0.dp) 1f
            else (distance / delta).coerceIn(0f, 1f)
        if (ratio == 1f) return to

        return interpolate(to, ratio, interpolation)
    }
}

