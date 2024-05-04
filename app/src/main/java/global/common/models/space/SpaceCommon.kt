package global.common.models.space

import kotlin.math.abs

object SpaceCommon {
    const val EPSILON = 1e-10

    fun Double.vsEpsilon() = abs(this) < EPSILON
}