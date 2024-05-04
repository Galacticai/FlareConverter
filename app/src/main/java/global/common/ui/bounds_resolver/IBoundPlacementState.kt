package global.common.ui.bounds_resolver

import androidx.annotation.FloatRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import global.common.models.space.dp_bound.DpBound
import global.common.models.space.dp_bound.DpBoundUtil.difference
import global.common.models.space.dp_bound.DpBoundUtil.getOrientation
import global.common.models.space.dp_bound.DpBoundUtil.getOrientationSign
import global.common.models.space.dp_bound.DpBoundUtil.interpolate
import global.common.models.space.dp_bound.Interpolation
import kotlinx.coroutines.launch

@LayoutScopeMarker
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class BoundPlacementScopeMarker

//! intentional: interface because it needs to be overridden by `@Composable` fields
//!              .. yes a class is possible but, the current version is much shorter

@BoundPlacementScopeMarker
@Stable
interface IBoundPlacementState {
    val source: DpBound
    val destination: DpBound
    val current: DpBound
    val ratio: Float
    val distance: Dp
    val distanceMax: Dp
    val orientation: Orientation
    val orientationSign: Int
    val dragDp: Dp
    val dragState: DraggableState
    val dragDirection get() = dragDp.value.compareTo(0f) * orientationSign

    val settleThreshold: Float
    val ratioAnimated: Animatable<Float, AnimationVector1D>

    /** drag velocity threshold ([Dp]/sec) where it's treated as flinging instead of regular dragging */
    val velocityThreshold: Dp

    fun setSource(value: DpBound)
    fun setDestination(value: DpBound)
    fun setCurrent(value: DpBound)
    fun setRatio(value: Float)
    fun animateRatio(value: Float)

    /**
     * rest after user interaction has finished (empty = stay where user released)
     * @param velocity drag velocity ([Dp]/sec)
     * @return true if settled on the other side
     */
    fun settle(velocity: Dp): Boolean

    object Defaults {
        val VELOCITY_THRESHOLD = 650.dp
        const val SETTLE_THRESHOLD = .5f
    }

    companion object {
        @Composable
        fun remember(
            @FloatRange(0.0, 1.0)
            settleThreshold: Float = Defaults.SETTLE_THRESHOLD,
            velocityThreshold: Dp = Defaults.VELOCITY_THRESHOLD,
        ): IBoundPlacementState {
            val coroutineScope = rememberCoroutineScope()
            val density = LocalDensity.current
            var source by remember { mutableStateOf(DpBound.zero) }
            var destination by remember { mutableStateOf(DpBound.zero) }
            var ratio by remember { mutableFloatStateOf(0f) }

            val distanceMax = remember(source, destination) {
                source.difference(destination)
            }

            val current by remember(source, destination, ratio) {
                derivedStateOf { source.interpolate(destination, ratio, Interpolation.Linear) }
            }
            val distance by remember(source, destination, ratio, distanceMax) {
                derivedStateOf {
                    when (ratio) {
                        0f -> 0.dp
                        1f -> distanceMax
                        else -> source.difference(current)
                    }
                }
            }

            val orientation = remember(source, destination) {
                source.getOrientation(destination)
            }
            val orientationSign = remember(source, destination, orientation) {
                source.getOrientationSign(destination, orientation)
            }

            val ratioAnimated = remember { Animatable(ratio) }

            var dragDp by remember { mutableStateOf(0.dp) }
            val dragState = rememberDraggableState { deltaPx ->
                val deltaDp = with(density) { deltaPx.toDp() }
                dragDp += deltaDp
                val ratioDelta = (deltaDp.value * orientationSign) / distanceMax.value
                ratio = (ratio + ratioDelta).coerceIn(0f, 1f)

                coroutineScope.launch {
                    ratioAnimated.snapTo(ratio)
                }
            }

            return remember(
                source, destination, distanceMax,
                ratio, ratioAnimated,
                orientation, orientationSign,
                dragState, settleThreshold, velocityThreshold,
            ) {
                object : IBoundPlacementState {
                    override val source get() = source
                    override val destination get() = destination
                    override val ratio get() = ratio
                    override val ratioAnimated get() = ratioAnimated
                    override val distance get() = distance
                    override val distanceMax get() = distanceMax
                    override val current get() = current
                    override val orientation get() = orientation
                    override val orientationSign get() = orientationSign
                    override val dragDp get() = dragDp
                    override val dragState get() = dragState
                    override val settleThreshold get() = settleThreshold
                    override val velocityThreshold get() = velocityThreshold

                    override fun setSource(value: DpBound) {
                        source = value
                    }

                    override fun setDestination(value: DpBound) {
                        destination = value
                    }

                    override fun setCurrent(value: DpBound) {
                        val distance = source.difference(value)
                        val ratio = distance / distanceMax
                        this.setRatio(ratio)
                    }

                    override fun setRatio(value: Float) {
                        ratio = value.coerceIn(0f, 1f)
                    }

                    override fun animateRatio(value: Float) {
                        coroutineScope.launch {
                            ratioAnimated.snapTo(ratio)
                            ratioAnimated.animateTo(value, tween()) {
                                setRatio(this.value)
                            }
                        }
                    }

                    override fun settle(velocity: Dp): Boolean {
                        val velocitySigned = velocity * orientationSign
                        val target = when {
                            velocitySigned > velocityThreshold -> 1f
                            velocitySigned < -velocityThreshold -> 0f
                            ratio < settleThreshold -> 0f
                            else -> 1f
                        }

                        val isOtherSide = (ratio < settleThreshold) != (target < settleThreshold)

                        animateRatio(target)

                        dragDp = 0.dp
                        return isOtherSide
                    }
                }
            }
        }
    }
}

