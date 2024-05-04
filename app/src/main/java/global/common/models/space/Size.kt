package global.common.models.space

import global.common.models.space.SpaceCommon.EPSILON
import java.io.Serializable

data class Size(val w: Int, val h: Int, val tolerance: Double = EPSILON) : Serializable {
    override fun toString() = "($w, $h)"
    fun isSquare() = w == h

    fun isOn(other: Size) =
        (w - other.w) < tolerance
                && (h - other.h) < tolerance
}