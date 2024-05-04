package global.common.ui.elevated_expandable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class DpBounds(
    val position: DpOffset,
    val size: DpOffset,
) {
    constructor(x: Dp, y: Dp, w: Dp, h: Dp) : this(
        DpOffset(x, y),
        DpOffset(w, h)
    )

    constructor(x: Int, y: Int, w: Int, h: Int) : this(
        x.dp, y.dp, w.dp, h.dp
    )


    companion object {

        val zero: DpBounds get() = DpBounds(0, 0, 0, 0)

        fun Modifier.bounds(value: DpBounds) = this
            .offset(value.position.x, value.position.y)
            .size(value.size.x, value.size.y)

        /** resolve [DpBounds] dynamically by letting android decide using the given element */
        @Composable
        fun Resolve(
            modifier: Modifier = Modifier,
            customResolver: ((LayoutCoordinates) -> DpBounds)? = null,
            onResult: (DpBounds) -> Unit,
            /** optional content like loading or whatever */
            content: @Composable BoxScope.() -> Unit = { }
        ) {
            val density = LocalDensity.current
            var bounds by remember { mutableStateOf<DpBounds?>(null) }

            Box(
                modifier.onGloballyPositioned {
                    if (customResolver != null) {
                        val boundsCustom = customResolver(it)
                        bounds = boundsCustom
                        return@onGloballyPositioned
                    }
                    val position = DpOffset(it.positionInParent(), density.density)
                    val size = DpOffset(it.size.width, it.size.height, density.density)
                    bounds = DpBounds(position, size)
                }
            ) {
                val boundsCaptured = bounds ?: return@Box
                onResult(boundsCaptured)
                content()
            }
        }
    }
}
