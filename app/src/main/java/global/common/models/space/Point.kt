package global.common.models.space

import global.common.models.space.SpaceCommon.EPSILON
import kotlin.math.pow
import kotlin.math.sqrt

data class Point(val x: Double, val y: Double, val tolerance: Double = EPSILON) {
    override fun toString() = "($x, $y)"

    val distanceToOrigin get() = distance(origin)

    fun distance(other: Point) = sqrt(
        (x - other.x).pow(2) +
                (y - other.y).pow(2)
    )

    fun isOn(other: Point) =
        (x - other.x) < tolerance
                && (y - other.y) < tolerance

    companion object {
        val origin get() = Point(0.0, 0.0)
    }
}