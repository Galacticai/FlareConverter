package com.galacticai.flareconverter.ui.components.expressive.loading

import androidx.compose.animation.core.DurationBasedAnimationSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State

/** @return [State] of [Float] between 0-1 which [repeat]s and follows the [animation] curve */
@Composable
fun rememberLooping(
    animation: DurationBasedAnimationSpec<Float>,
    repeat: RepeatMode = RepeatMode.Restart
): State<Float> {
    val transition = rememberInfiniteTransition(label = "expressive shape loader")
    return transition.animateFloat(
        0f, 1f,
        animationSpec = infiniteRepeatable(animation, repeat),
        label = "t"
    )
}