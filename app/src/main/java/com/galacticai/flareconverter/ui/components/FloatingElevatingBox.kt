package com.galacticai.flareconverter.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import global.common.ui.elevated_expandable.DpBounds
import kotlinx.coroutines.launch
import kotlin.math.pow


@Composable
fun FloatingElevatingBox(
    bounds: DpBounds,
    expandedHeight: Dp,
    modifier: Modifier = Modifier,
    tensionThreshold: Dp = 200.dp,
    minTension: Float = 0.5f,
    canScrollBackward: () -> Boolean = { false },
    content: @Composable BoxScope.(progress: Float) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current
    val initialHeightPx by remember(bounds) { derivedStateOf { with(density) { bounds.size.y.toPx() } } }
    val expandedHeightPx by remember(expandedHeight) { derivedStateOf { with(density) { expandedHeight.toPx() } } }
    val thresholdPx by remember(tensionThreshold) { derivedStateOf { with(density) { tensionThreshold.toPx() } } }

    val coroutineScope = rememberCoroutineScope()
    val animatedHeight = remember { Animatable(initialHeightPx) }
    var rawTargetHeightPx by remember(bounds) { mutableFloatStateOf(initialHeightPx) }
    var hasBrokenTension by remember { mutableStateOf(false) }

    suspend fun settle(velocity: Float) {
        val target = when {
            velocity < -500f -> expandedHeightPx
            velocity > 500f -> initialHeightPx
            hasBrokenTension || animatedHeight.value > (initialHeightPx + expandedHeightPx) * 0.5f -> expandedHeightPx
            else -> initialHeightPx
        }
        rawTargetHeightPx = target
        hasBrokenTension = target == expandedHeightPx
        animatedHeight.animateTo(
            target,
            spring(stiffness = Spring.StiffnessLow)
        )
    }

    fun performScroll(delta: Float): Float {
        if (delta == 0f) return 0f

        val scrollUp = delta < 0

        val canConsume = if (scrollUp) {
            rawTargetHeightPx < expandedHeightPx
        } else {
            rawTargetHeightPx > initialHeightPx
        }

        if (!canConsume) return 0f

        val oldRaw = rawTargetHeightPx
        val newRaw = (oldRaw - delta).coerceIn(initialHeightPx, expandedHeightPx)
        val consumedRaw = oldRaw - newRaw
        rawTargetHeightPx = newRaw

        //? tension
        val tensionProgress = ((rawTargetHeightPx - initialHeightPx) / thresholdPx).coerceIn(0f, 1f)
        if (tensionProgress >= 1f) {
            if (!hasBrokenTension) {
                haptic.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
            }
            hasBrokenTension = true
        } else if (rawTargetHeightPx <= initialHeightPx + 1f) {
            hasBrokenTension = false
        }

        val desiredHeightPx = if (hasBrokenTension) {
            rawTargetHeightPx
        } else {
            //? rubber band effect
            val curve = tensionProgress.toDouble().pow(1.5).toFloat()
            initialHeightPx + (rawTargetHeightPx - initialHeightPx) *
                    (minTension + (1f - minTension) * curve * 0.1f)
        }

        coroutineScope.launch {
            animatedHeight.animateTo(
                desiredHeightPx,
                spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness =
                        if (hasBrokenTension) Spring.StiffnessMediumLow
                        else Spring.StiffnessLow
                )
            )
        }

        return consumedRaw
    }

    val nestedScrollConnection = remember(initialHeightPx, expandedHeightPx) {
        val consume: (Float) -> Offset = { delta ->
            val consumed = performScroll(delta)
            if (consumed != 0f) Offset(0f, consumed) else Offset.Zero
        }

        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y

                // 1. Swiping up (delta < 0): Expand container first.
                // We consume as long as the container isn't fully expanded.
                if (delta < 0 && rawTargetHeightPx < expandedHeightPx) {
                    return consume(delta)
                }

                // 2. Swiping down (delta > 0): Collapse container ONLY if child is at the top.
                if (delta > 0 && !canScrollBackward() && rawTargetHeightPx > initialHeightPx) {
                    return consume(delta)
                }

                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                // If there's leftover scroll (e.g., child hit the top while swiping down),
                // the container should consume the remainder.
                return if (available.y != 0f) consume(available.y)
                else Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                val velocity = available.y

                // Container takes over the fling if:
                // - Flinging up and not expanded
                // - Flinging down and child is at the top
                if ((velocity < 0 && rawTargetHeightPx < expandedHeightPx) ||
                    (velocity > 0 && !canScrollBackward() && rawTargetHeightPx > initialHeightPx)
                ) {
                    settle(velocity)
                    return available
                }

                return Velocity.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                // If the child finished its fling and there is leftover velocity, settle the container.
                if (available.y != 0f) {
                    settle(available.y)
                }
                return available
            }
        }
    }

    val progress by remember {
        derivedStateOf {
            if (expandedHeightPx <= initialHeightPx) 0f
            else {
                val v = (animatedHeight.value - initialHeightPx) /
                        (expandedHeightPx - initialHeightPx)
                v.coerceIn(0f, 1f)
            }
        }
    }

    val currentHeightDp by remember { derivedStateOf { with(density) { animatedHeight.value.toDp() } } }

    LaunchedEffect(bounds) {
        if (progress <= 0.01f) {
            animatedHeight.snapTo(initialHeightPx)
            rawTargetHeightPx = initialHeightPx
        }
    }

    Box(
        modifier = modifier
            .offset(
                x = bounds.position.x,
                y = bounds.position.y - (currentHeightDp - bounds.size.y)
            )
            .size(bounds.size.x, currentHeightDp)
            .zIndex(if (progress > 0f) 100f else 0f)
            .nestedScroll(nestedScrollConnection)
            .draggable(
                state = rememberDraggableState { delta -> performScroll(delta) },
                orientation = Orientation.Vertical,
                onDragStopped = { velocity -> coroutineScope.launch { settle(velocity) } }
            )
            .graphicsLayer { clip = false }
    ) {
        content(progress)
    }
}
