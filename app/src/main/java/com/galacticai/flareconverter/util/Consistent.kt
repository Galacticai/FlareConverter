package com.galacticai.flareconverter.util

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.flareconverter.ui.components.expressive.ExpressiveRadius
import com.galacticai.flareconverter.ui.components.expressive.rememberExpressiveRadiusSize
import com.galacticai.flareconverter.util.Consistent.Pad.screenHorizontal

/** Common UI runtime values for consistency such as padding, corner radius, and more... */
object Consistent {

    object Size {
        val header = 52.dp
        val actions = 40.dp
    }

    object Text {
        val header = 16.sp
    }

    object Pad {
        val tiny = 1.dp
        val smallXX = 2.dp
        val smallX = 5.dp
        val small = 8.dp
        val regular = 10.dp
        val moderate = regular * 1.5f
        val medium = regular * 2
        val big = regular * 3
        val large = regular * 4
        val largeX = regular * 5

        /** Screen horizontal padding */
        val screenHorizontal = regular

        /** Screen horizontal padding [Modifier] (using [screenHorizontal] as value) */
        fun Modifier.screenHPadding() = this.padding(horizontal = screenHorizontal)
    }

    object Elevation {
        @get:Composable
        val elevationLow get() = CardDefaults.cardElevation(Pad.smallX)

        @get:Composable
        val elevationMedium get() = CardDefaults.cardElevation(Pad.small)

        @get:Composable
        val elevationHigh get() = CardDefaults.cardElevation(Pad.regular)
    }

    object Shape {
        object Radius {
            infix fun Dp.paddedBy(pad: Dp) = this - pad

            val medium = Pad.medium
            val expressive = ExpressiveRadius(100f, 35f)
            val expressiveDp = ExpressiveRadius(medium, medium / 2)
        }

        object Rounded {
            val all get() = RoundedCornerShape(Radius.medium)
            val start
                get() = RoundedCornerShape(topStart = Radius.medium, bottomStart = Radius.medium)
            val end get() = RoundedCornerShape(topEnd = Radius.medium, bottomEnd = Radius.medium)
            val top get() = RoundedCornerShape(topStart = Radius.medium, topEnd = Radius.medium)
            val bottom
                get() = RoundedCornerShape(bottomStart = Radius.medium, bottomEnd = Radius.medium)

            val pill get() = RoundedCornerShape(Radius.expressive.neutral)
            val pillClick get() = RoundedCornerShape(Radius.expressive.clicked)

            @get:Composable
            val button get() = ButtonDefaults.shapes().copy(shape = pill, pressedShape = pillClick)
        }
    }

    object Animation {
        fun <T> getSpring(bounce: Boolean = false) = spring<T>(
            dampingRatio = if (bounce) Spring.DampingRatioMediumBouncy else Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium,
        )

        val inUp get() = fadeIn() + slideInVertically { it }
        val outDown get() = slideOutVertically { -it } + fadeOut()

        @Composable
        fun rememberExpressiveButtonState() = rememberExpressiveRadiusSize(
            DpSize(Pad.regular, 0.dp),
            Shape.Radius.expressive,
            getSpring()
        )
    }
}