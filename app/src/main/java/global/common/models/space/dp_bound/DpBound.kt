package global.common.models.space.dp_bound

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import global.common.ui.elevated_expandable.DpOffset


data class DpBound(
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
        val zero: DpBound get() = DpBound(0, 0, 0, 0)

        fun Modifier.bounds(value: DpBound) = this
            .offset(value.position.x, value.position.y)
            .size(value.size.x, value.size.y)
    }
}

