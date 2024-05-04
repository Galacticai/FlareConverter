package com.galacticai.flareconverter.ui.components.expressive.expressive_card

import androidx.compose.ui.unit.Dp

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
    }
}