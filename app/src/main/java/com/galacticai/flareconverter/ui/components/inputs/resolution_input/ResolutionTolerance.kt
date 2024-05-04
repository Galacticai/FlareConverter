package com.galacticai.flareconverter.ui.components.inputs.resolution_input

import androidx.compose.ui.unit.Dp
import com.galacticai.flareconverter.util.Consistent
import com.galacticai.flareconverter.util.Pixels

/** delta tolerance */
data class ResolutionTolerance(
    /** warn before reaching the [boundary] */
    val warn: ClosedFloatingPointRange<Float> =
        .33f..1.25f,

    /** allowed delta range */
    val boundary: ClosedFloatingPointRange<Float> =
        .1f..2f,

    /** same as [boundary] but in pixels (both applied together) */
    val boundaryPx: IntRange =
        Pixels.P144.p..Pixels.P7680.p,

    /** keep input size visible if the current index delta is not far from the input index */
    val stickySteps: Int = 4,

    /** minimum size of a preview box before it disappears */
    val minVisibleSize: Dp = Consistent.Pad.largeX,

    /** extra padding to allow the preview box to exit smoothly instead of disappearing on edge  */
    val exitPad: Dp = Consistent.Pad.regular,
)