package global.common.ui.elevated_expandable

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import global.common.DpUtils.toPx
import kotlinx.coroutines.launch

typealias BoundsFromTo = Pair<DpBounds, DpBounds>
typealias ElevatedExpandableContent = @Composable (
    modifier: Modifier,
    progress: Float,
    currentBounds: DpBounds
) -> Unit

@Composable
fun ElevatedExpandable(
    modifier: Modifier = Modifier,
    bounds: BoundsFromTo,
    scrollLock: Boolean = false,
    tensionFactor: Float = 0.5f,
    tensionLength: Dp = 200.dp,
    vibrateOnThreshold: Boolean = true,
    content: ElevatedExpandableContent,
) {
    val progress = remember { Animatable(0f) }
    var dragAccumulator by remember { mutableFloatStateOf(0f) }

    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    val haptic = LocalHapticFeedback.current
    var lastHapticsBucket by remember { mutableStateOf<Boolean?>(null) }

    val distance by remember {
        derivedStateOf {
            bounds.second.position.y - bounds.first.position.y
        }
    }
    val scrollScalePx by remember {
        derivedStateOf {
            val d = distance.toPx(density.density)
            if (d == 0f) 1f else d
        }
    }
    val isMoving by remember {
        derivedStateOf {
            scrollLock || progress.value < 1f
        }
    }

    fun vibrate(currentProgress: Float) {
        if (!vibrateOnThreshold) return
        val bucket = currentProgress > tensionFactor
        if (bucket == lastHapticsBucket) return
        lastHapticsBucket = bucket
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    fun lerp(from: Dp, to: Dp, t: Float): Dp {
        val delta = to - from
        val offset = delta * t
        return from + offset
    }

    val boundsCurrent by remember(
        progress, bounds
    ) {
        derivedStateOf {
            when (val t = progress.value.coerceIn(0f, 1f)) {
                0f -> bounds.first
                1f -> bounds.second
                else -> DpBounds(
                    x = lerp(bounds.first.position.x, bounds.second.position.x, t),
                    y = lerp(bounds.first.position.y, bounds.second.position.y, t),
                    w = lerp(bounds.first.size.x, bounds.second.size.x, t),
                    h = lerp(bounds.first.size.y, bounds.second.size.y, t),
                )
            }
        }
    }

    /** rest on nearest edge */
    suspend fun rest() {
        progress.animateTo(
            if (progress.value > tensionFactor)
                1f else 0f
        )
    }

    val nestedScrollConnection = remember(scrollScalePx, scrollLock) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (source != NestedScrollSource.UserInput) return Offset.Zero

                val delta = available.y
                val amount = delta / scrollScalePx

                if (scrollLock) {
                    val current = progress.value
                    val next = (current + amount).coerceIn(0f, 1f)
                    if (next != current) {
                        coroutineScope.launch {
                            progress.snapTo(next)
                            vibrate(next)
                        }
                    }
                    return Offset(0f, available.y)
                }

                if (delta > 0 && progress.value < 1f) {
                    val current = progress.value
                    val next = (current + amount).coerceIn(0f, 1f)
                    val consumed = next - current
                    if (consumed != 0f) {
                        coroutineScope.launch {
                            progress.snapTo(next)
                            vibrate(next)
                        }
                        return Offset(0f, consumed * scrollScalePx)
                    }
                }

                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (scrollLock || source != NestedScrollSource.UserInput) return Offset.Zero

                val delta = available.y
                val amount = delta / scrollScalePx

                if (delta < 0 && progress.value > 0f) {
                    val current = progress.value
                    val next = (current + amount).coerceIn(0f, 1f)
                    val consumedProgress = next - current
                    if (consumedProgress != 0f) {
                        coroutineScope.launch {
                            progress.snapTo(next)
                            vibrate(next)
                        }
                        return Offset(0f, consumedProgress * scrollScalePx)
                    }
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (scrollLock || (progress.value > 0f && progress.value < 1f)) {
                    rest()
                    return available
                }
                return Velocity.Zero
            }
        }
    }

    val gesture = Modifier
        .nestedScroll(nestedScrollConnection) then run {
        if (!isMoving) return@run Modifier
        Modifier.pointerInput(scrollScalePx) {
            detectVerticalDragGestures(
                onDragStart = {
                    dragAccumulator = progress.value
                },
                onDragEnd = {
                    coroutineScope.launch { rest() }
                },
                onVerticalDrag = { change, amount ->
                    val current = progress.value
                    val next = (current + amount / scrollScalePx).coerceIn(0f, 1f)
                    val consumed = next - current

                    if (consumed != 0f || scrollLock) {
                        change.consume()
                        coroutineScope.launch {
                            progress.snapTo(next)
                            vibrate(next)
                        }
                    }
                }
            )
        }
    }



    content(
        modifier then gesture,
        progress.value,
        boundsCurrent
    )
}
