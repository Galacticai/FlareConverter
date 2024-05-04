package global.common.models.space.dp_bound

import androidx.compose.animation.core.Easing

enum class Interpolation(val getFraction: (Float) -> Float) {
    /** match input amount */
    Linear({ it }),

    /** smoothly treat input amount */
    Smooth({ it * it * (3f - 2f * it) }),

//    /** smooth + little overshoot on the ends */
//    Expressive({
//        val smooth = Smooth.getFraction(it)
//        smooth - 0.1f * sin(2f * Math.PI.toFloat() * it)
//    }),

    /** jump between ends */
    Snap({ if (it < 1f) 0f else 1f });

    val easing get() = Easing { getFraction(it) }
}