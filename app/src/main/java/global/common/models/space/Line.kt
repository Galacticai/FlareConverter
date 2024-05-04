package global.common.models.space

import global.common.models.space.SpaceCommon.EPSILON

/** y=[a]x+[b] */
data class Line(val a: Double, val b: Double, val tolerance: Double = EPSILON) {
    fun isParallel(other: Line) = (a - other.a) < tolerance
    fun isPerpendicular(other: Line) = (a * other.a + 1.0) < tolerance
}
