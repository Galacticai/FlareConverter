package global.common.ui.bounds_resolver

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity

object BoundPlacementUtil {

    @Composable
    fun PlacementState.rememberScrollConnection(
        listState: LazyListState,
    ): NestedScrollConnection {
        val density = LocalDensity.current

        fun consumeParentDelta(deltaPx: Float, distanceMaxPx: Float): Float {
            if (distanceMaxPx <= 0f) return 0f
            val oldRatio = ratio
            ratio += deltaPx / distanceMaxPx
            val consumedRatio = ratio - oldRatio
            return consumedRatio * distanceMaxPx
        }

        return object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source != NestedScrollSource.UserInput)
                    return Offset.Zero
                val dy = available.y
                if (dy < 0f && !listState.canScrollBackward) {
                    val consumed = consumeParentDelta(
                        -dy,
                        with(density) { distanceMax.toPx() })
                    return Offset(0f, -consumed)
                }
                if (dy > 0f && !listState.canScrollBackward) {
                    val consumed = consumeParentDelta(
                        -dy,
                        with(density) { distanceMax.toPx() })
                    return Offset(0f, -consumed)
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (!listState.canScrollBackward) {
                    settle(with(density) { available.y.toDp() })
                    return available
                }
                return Velocity.Zero
            }
        }
    }


    @Composable
//    fun PlacementState.rememberNestedScrollConnection(
//        listState: LazyListState,
//    ): NestedScrollConnection = rememberNestedScrollConnection(
//        canScrollBackward = { listState.canScrollBackward },
//        canScrollForward = { listState.canScrollForward }
//    )
//
//    @Composable
//    fun PlacementState.rememberNestedScrollConnection(
//        canScrollBackward: () -> Boolean = { false },
//        canScrollForward: () -> Boolean = { false }
//    ): NestedScrollConnection {
//        val density = LocalDensity.current
//        val coroutineScope = rememberCoroutineScope()
//        val currentCanScrollBackward by rememberUpdatedState(canScrollBackward)
//        val currentCanScrollForward by rememberUpdatedState(canScrollForward)
//
//        return remember(
//            orientation,
//            orientationSign,
//            dragState,
//        ) {
//            fun performScroll(availableAxisDelta: Float): Float {
//                if (availableAxisDelta == 0f || distanceMax.value == 0f) return 0f
//
//                val availableDp = with(density) { availableAxisDelta.toDp() }
//                val ratioDelta = (availableDp.value * orientationSign) / distanceMax.value
//
//                val oldRatio = ratio
//                val newRatio = (oldRatio + ratioDelta).coerceIn(0f, 1f)
//
//                if (oldRatio == newRatio) return 0f
//
//                ratio = newRatio
//                coroutineScope.launch {
//                    ratioAnimated.snapTo(newRatio)
//                }
//
//                val actualRatioDelta = newRatio - oldRatio
//                val consumedDp = (actualRatioDelta * distanceMax.value) / orientationSign
//                return with(density) { consumedDp.dp.toPx() }
//            }
//
//            fun canChildScrollInCollapsingDirection(): Boolean {
//                return if (orientationSign > 0) currentCanScrollForward()
//                else currentCanScrollBackward()
//            }
//
//            object : NestedScrollConnection {
//                override fun onPreScroll(
//                    available: Offset,
//                    source: NestedScrollSource,
//                ): Offset {
//                    val availableAxisDelta = available.axisValue(orientation)
//
//                    val isExpanding = availableAxisDelta * orientationSign > 0f
//                    val isCollapsing = availableAxisDelta * orientationSign < 0f
//
//                    if (isExpanding && ratio < 1f) {
//                        val consumedPx = performScroll(availableAxisDelta)
//                        return createOffset(orientation, consumedPx)
//                    }
//
//                    if (isCollapsing && !canChildScrollInCollapsingDirection() && ratio > 0f) {
//                        val consumedPx = performScroll(availableAxisDelta)
//                        return createOffset(orientation, consumedPx)
//                    }
//
//                    return Offset.Zero
//                }
//
//                override fun onPostScroll(
//                    consumed: Offset,
//                    available: Offset,
//                    source: NestedScrollSource,
//                ): Offset {
//                    val availableAxisDelta = available.axisValue(orientation)
//                    if (availableAxisDelta != 0f) {
//                        val consumedPx = performScroll(availableAxisDelta)
//                        return createOffset(orientation, consumedPx)
//                    }
//                    return Offset.Zero
//                }
//
//                override suspend fun onPreFling(available: Velocity): Velocity {
//                    val availableVelocity = available.axisValue(orientation)
//
//                    val isFlingExpanding = availableVelocity * orientationSign > 0f
//                    val isFlingCollapsing = availableVelocity * orientationSign < 0f
//
//                    if ((isFlingExpanding && ratio < 1f) ||
//                        (isFlingCollapsing && !canChildScrollInCollapsingDirection() && ratio > 0f)
//                    ) {
//                        val velocityDp = with(density) { availableVelocity.toDp() }
//                        settle(velocityDp)
//                        return Velocity.forAxis(orientation, availableVelocity)
//                    }
//                    return Velocity.Zero
//                }
//
//                override suspend fun onPostFling(
//                    consumed: Velocity,
//                    available: Velocity,
//                ): Velocity {
//                    val availableVelocity = available.axisValue(orientation)
//                    if (availableVelocity != 0f) {
//                        val velocityDp = with(density) { availableVelocity.toDp() }
//                        settle(velocityDp)
//                        return available
//                    }
//                    return Velocity.Zero
//                }
//            }
//        }
//    }

    private fun Offset.axisValue(orientation: Orientation) = when (orientation) {
        Orientation.Horizontal -> x
        Orientation.Vertical -> y
    }

    private fun Velocity.axisValue(orientation: Orientation) = when (orientation) {
        Orientation.Horizontal -> x
        Orientation.Vertical -> y
    }

    private fun createOffset(
        orientation: Orientation,
        value: Float
    ) = when (orientation) {
        Orientation.Horizontal -> Offset(value, 0f)
        Orientation.Vertical -> Offset(0f, value)
    }

    private fun Velocity.Companion.forAxis(
        orientation: Orientation,
        value: Float
    ) = when (orientation) {
        Orientation.Horizontal -> Velocity(value, 0f)
        Orientation.Vertical -> Velocity(0f, value)
    }
}
