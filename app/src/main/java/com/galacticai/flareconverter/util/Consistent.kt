package com.galacticai.flareconverter.util

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Common UI runtime values for consistency such as padding, corner radius, and more... */
object Consistent {
    /** Corner radius */
    val radius get() = padMedium

    val padTiny = 1.dp
    val padSmallXX = 2.dp
    val padSmallX = 5.dp
    val padRegular = 10.dp
    val padMedium = 20.dp
    val padBig = 30.dp
    val padLarge = 40.dp

    /** Rounded corners (using [Consistent.radius] as value) */
    val shape = RoundedCornerShape(radius)

    /** Rounded start corners (using [Consistent.radius] as value) */
    val shapeStart = RoundedCornerShape(topStart = radius, bottomStart = radius)

    /** Rounded end corners (using [Consistent.radius] as value) */
    val shapeEnd = RoundedCornerShape(topEnd = radius, bottomEnd = radius)

    /** Rounded top corners (using [Consistent.radius] as value) */
    val shapeTop = RoundedCornerShape(topStart = radius, topEnd = radius)

    /** Rounded bottom corners (using [Consistent.radius] as value) */
    val shapeBottom = RoundedCornerShape(bottomStart = radius, bottomEnd = radius)

    /** Screen horizontal padding */
    val screenHorizontalPadding = 10.dp

    /** Screen horizontal padding [Modifier] (using [Consistent.screenHorizontalPadding] as value) */
    fun Modifier.screenHPadding() = this.padding(horizontal = screenHorizontalPadding)
}