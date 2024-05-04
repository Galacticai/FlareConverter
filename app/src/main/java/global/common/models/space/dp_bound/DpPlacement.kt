package global.common.models.space.dp_bound

import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

data class DpPlacement(
    val position: DpOffset,
    val size: DpOffset,
) {
    constructor(x: Dp, y: Dp, w: Dp, h: Dp) :
            this(DpOffset(x, y), DpOffset(w, h))

    constructor(x: Int, y: Int, w: Int, h: Int) :
            this(x.dp, y.dp, w.dp, h.dp)

    override fun toString() =
        "x=${position.x},y=${position.y} | w=${size.x},h=${size.y}"


    companion object {
        val zero: DpPlacement get() = DpPlacement(0, 0, 0, 0)

        fun Modifier.bounds(
            //! intentional: function to avoid recomposition
            getBound: () -> DpPlacement
        ) = this
            .offset {
                val bound = getBound()
                IntOffset(
                    bound.position.x.roundToPx(),
                    bound.position.y.roundToPx()
                )
            }
            .layout { measurable, _ ->
                val bound = getBound()
                val w = bound.size.x.roundToPx()
                val h = bound.size.y.roundToPx()
                val placeable = measurable.measure(Constraints.fixed(w, h))
                layout(w, h) {
                    placeable.place(0, 0)
                }
            }
    }
}

