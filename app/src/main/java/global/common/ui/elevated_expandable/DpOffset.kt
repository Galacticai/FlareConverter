package global.common.ui.elevated_expandable

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import global.common.DpUtils.toDp


data class DpOffset(val x: Dp, val y: Dp) {
    constructor(x: Int, y: Int, density: Float = 1f)
            : this(x.toDp(density), y.toDp(density))

    constructor(x: Float, y: Float, density: Float = 1f)
            : this(x.toDp(density), y.toDp(density))

    constructor(offset: Offset, density: Float)
            : this(offset.x, offset.y, density)
}
