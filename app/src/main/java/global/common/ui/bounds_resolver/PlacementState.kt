package global.common.ui.bounds_resolver

import androidx.annotation.FloatRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import global.common.models.space.dp_bound.DpPlacement
import global.common.models.space.dp_bound.DpPlacementUtil.difference
import global.common.models.space.dp_bound.DpPlacementUtil.getOrientation
import global.common.models.space.dp_bound.DpPlacementUtil.getOrientationSign
import global.common.models.space.dp_bound.DpPlacementUtil.interpolate
import global.common.models.space.dp_bound.Interpolation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@LayoutScopeMarker
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class BoundPlacementScopeMarker

//! intentional: interface because it needs to be overridden by `@Composable` fields
//!              .. yes a class is possible but, the current version is much shorter

@BoundPlacementScopeMarker
@Stable
class PlacementState(
    private val coroutineScope: CoroutineScope,
    private val density: Density,
    val settleThreshold: Float,
    val velocityThreshold: Dp,
    val animationSpec: AnimationSpec<Float>,
) {
    var source: DpPlacement by mutableStateOf(DpPlacement.zero)
    var destination: DpPlacement by mutableStateOf(DpPlacement.zero)

    private val _ratio: MutableFloatState = mutableFloatStateOf(0f)
    var ratio: Float
        get() = _ratio.floatValue
        set(value) {
            _ratio.floatValue = value.coerceIn(0f, 1f)
        }

    val distanceMax: Dp by derivedStateOf {
        source.difference(destination)
    }

    val current: DpPlacement by derivedStateOf {
        source.interpolate(destination, ratio, Interpolation.Linear)
    }

    val distance: Dp by derivedStateOf {
        when (ratio) {
            0f -> 0.dp
            1f -> distanceMax
            else -> source.difference(current)
        }
    }

    val orientation: Orientation by derivedStateOf {
        source.getOrientation(destination)
    }

    val orientationSign: Int by derivedStateOf {
        source.getOrientationSign(destination, orientation)
    }

    val ratioAnimated: Animatable<Float, AnimationVector1D> =
        Animatable(ratio)

    /** [ratio]/sec */
    val velocity: Float
        get() = ratioAnimated.velocity

    /** [Dp]/sec */
    val velocityDp: Dp
        get() = distanceMax * velocity


    var dragDp: Dp by mutableStateOf(0.dp)

    val dragDirection: Int by derivedStateOf {
        dragDp.value.compareTo(0f) * orientationSign
    }

    val dragState: DraggableState = DraggableState { deltaPx ->
        val deltaDp = with(density) { deltaPx.toDp() }
        dragDp += deltaDp
        val ratioDelta = (deltaDp.value * orientationSign) / distanceMax.value
        ratio += ratioDelta

        coroutineScope.launch {
            ratioAnimated.snapTo(ratio)
        }
    }

    fun animateRatio(
        value: Float, initialVelocity: Float = velocity
    ): Job = coroutineScope.launch {
        ratioAnimated.snapTo(ratio)
        ratioAnimated.animateTo(
            value, animationSpec, initialVelocity
        ) {
            ratio = this.value
        }
    }

    fun settle(velocity: Dp): Boolean {
        val velocitySigned = velocity * orientationSign
        val target = when {
            velocitySigned > velocityThreshold -> 1f
            velocitySigned < -velocityThreshold -> 0f
            ratio < settleThreshold -> 0f
            else -> 1f
        }

        val isOtherSide = (ratio < settleThreshold) != (target < settleThreshold)

        val ratioVelocity =
            if (distanceMax.value == 0f) 0f
            else velocity.value * orientationSign / distanceMax.value

        animateRatio(target, ratioVelocity)

        dragDp = 0.dp
        return isOtherSide
    }

    object Defaults {
        val VELOCITY_THRESHOLD = 650.dp
        const val SETTLE_THRESHOLD = .33f
        val ANIMATION_SPEC = spring<Float>(
            Spring.DampingRatioNoBouncy,
            Spring.StiffnessLow,
        )

    }

    companion object {
        @Composable
        fun remember(
            @FloatRange(0.0, 1.0)
            settleThreshold: Float = Defaults.SETTLE_THRESHOLD,
            velocityThreshold: Dp = Defaults.VELOCITY_THRESHOLD,
            animationSpec: AnimationSpec<Float> = Defaults.ANIMATION_SPEC,
        ): PlacementState {
            val coroutineScope = rememberCoroutineScope()
            val density = LocalDensity.current

            return remember(settleThreshold, velocityThreshold, density, coroutineScope) {
                PlacementState(
                    coroutineScope,
                    density,
                    settleThreshold, velocityThreshold,
                    animationSpec,
                )
            }
        }
    }
}