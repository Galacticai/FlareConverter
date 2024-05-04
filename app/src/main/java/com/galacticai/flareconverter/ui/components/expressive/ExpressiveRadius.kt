package com.galacticai.flareconverter.ui.components.expressive

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.galacticai.flareconverter.util.Consistent

sealed class ExpressiveRadius<T> {
    abstract val neutral: T
    abstract val clicked: T

    data class Absolute(
        override val neutral: Dp,
        override val clicked: Dp = neutral / 2,
    ) : ExpressiveRadius<Dp>()

    data class Percent(
        override val neutral: Float,
        override val clicked: Float = neutral / 2,
    ) : ExpressiveRadius<Float>()

    companion object {
        operator fun invoke(neutral: Dp, clicked: Dp) = Absolute(neutral, clicked)
        operator fun invoke(neutral: Float, clicked: Float) = Percent(neutral, clicked)

        private fun getProgress(
            radiusCurrent: Float,
            neutral: Float, clicked: Float
        ): Float {
            val delta = clicked - neutral
            if (delta == 0f) return 0f
            return ((radiusCurrent - neutral) / delta).coerceIn(0f, 1f)
        }

        @Suppress("UNCHECKED_CAST") //? type is ensured by sealed `ExpressiveRadius` already
        @Composable
        fun rememberShape(
            isPressed: Boolean,
            radius: ExpressiveRadius<*>,
            animationSpec: AnimationSpec<*>? = null,
            /** @param first current radius ([Dp] absolute OR [Float] percent) */
            onChange: ((Any) -> Unit)? = null,
        ): RoundedCornerShape {
            val targetValue = if (isPressed) radius.clicked else radius.neutral

            val shape = when (radius) {
                is Absolute -> {
                    val spec = (animationSpec as? AnimationSpec<Dp>)
                        ?: Consistent.Animation.getSpring(true)
                    val cardRadius by animateDpAsState(
                        targetValue as Dp,
                        animationSpec = spec,
                        label = "expressive card radius dp"
                    )
                    if (onChange != null) {
                        LaunchedEffect(cardRadius) {
                            onChange(cardRadius)
                        }
                    }

                    RoundedCornerShape(cardRadius.coerceAtLeast(0.dp))
                }

                is Percent -> {
                    val spec = (animationSpec as? AnimationSpec<Float>)
                        ?: Consistent.Animation.getSpring()
                    val cardRadius by animateFloatAsState(
                        targetValue as Float,
                        animationSpec = spec,
                        label = "expressive card radius percent"
                    )
                    if (onChange != null) {
                        LaunchedEffect(cardRadius) {
                            onChange(cardRadius)
                        }
                    }
                    RoundedCornerShape(cardRadius.coerceIn(0f..100f))
                }
            }

            return remember(
                isPressed, radius,
                animationSpec, shape,
            ) { shape }
        }
    }
}