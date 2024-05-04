package global.common.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberRangeSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

typealias DiscreteSliderOnChange<T> = (value: T, i: Int) -> Unit

@Composable
fun <T> DiscreteSlider(
    values: List<T>,
    modifier: Modifier = Modifier,
    /** if defined then the current selected index will be controlled by this value */
    iSelectedCustom: Int? = null,
    /** initial index */
    iInitial: Int? = null,
    /** center the slider on this index */
    iCenter: Int? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: SliderColors = SliderDefaults.colors(),
    enabled: Boolean = true,
    vibrate: Boolean = true,
    onChange: DiscreteSliderOnChange<T>,
) {
    assert(values.size > 1)
    val haptic = LocalHapticFeedback.current

    fun Int.clamp() = this.coerceIn(values.indices)

    val iInitialComputed = remember(values, iSelectedCustom, iInitial, iCenter) {
        (iSelectedCustom ?: iInitial ?: iCenter)?.clamp() ?: 0
    }
    var iCurrent by rememberSaveable(values, iInitialComputed) {
        mutableIntStateOf(iInitialComputed)
    }
    LaunchedEffect(iSelectedCustom) {
        if (iSelectedCustom == null || iCurrent == iSelectedCustom)
            return@LaunchedEffect
        iCurrent = iSelectedCustom.clamp()
    }

    val iLast = values.lastIndex
    val valueRange = 0f..iLast.toFloat()
    val steps = values.size - 2

    fun onValueChange(value: Float) {
        val i = value.roundToInt().clamp()
        if (i == iCurrent) return
        iCurrent = i
        onChange(values[i], i)
        if (vibrate) {
            haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
        }
    }

    if (iCenter == null) {
        return Slider(
            modifier = modifier,
            enabled = enabled,
            value = iCurrent.toFloat(),
            onValueChange = ::onValueChange,
            valueRange = valueRange,
            steps = steps,
            colors = colors,
            interactionSource = interactionSource,
        )
    }

    val trackRange = remember(iCenter, iCurrent) {
        object {
            val activeRangeStart = min(iCenter, iCurrent).toFloat()
            val activeRangeEnd = max(iCenter, iCurrent).toFloat()
        }
    }
    val trackState = rememberRangeSliderState(
        activeRangeStart = trackRange.activeRangeStart,
        activeRangeEnd = trackRange.activeRangeEnd,
        steps = steps,
        valueRange = valueRange
    ).apply {
        activeRangeStart = trackRange.activeRangeStart
        activeRangeEnd = trackRange.activeRangeEnd
    }

    Slider(
        modifier = modifier,
        enabled = enabled,
        value = iCurrent.toFloat(),
        onValueChange = ::onValueChange,
        valueRange = valueRange,
        steps = steps,
        colors = colors,
        interactionSource = interactionSource,
        track = {
            SliderDefaults.Track(
                rangeSliderState = trackState,
                colors = colors,
                enabled = enabled
            )
        }
    )
}


@Preview(showBackground = true)
@Composable
private fun DiscreteSliderPreview() {
    val values = listOf(0, 20, 43, 60, 81, 100, -90)
    val i = 4
    var n by remember { mutableIntStateOf(values[i]) }

    Row(
        Modifier.padding(50.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DiscreteSlider(
            modifier = Modifier.weight(.9f),
            values = values,
            iInitial = i,
            onChange = { v, _ -> n = v }
        )

        Text(n.toString(), modifier = Modifier.weight(.1f))
    }
}

@Preview(showBackground = true)
@Composable
private fun DiscreteSliderPreviewCentered() {
    val values = listOf(-90, -50, 0, 20, 43, 60, 81, 100)
    val i = 5
    var n by remember { mutableIntStateOf(values[i]) }

    Row(
        Modifier.padding(50.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DiscreteSlider(
            modifier = Modifier.weight(.9f),
            values = values,
            iCenter = 2,
            iInitial = i,
            onChange = { v, _ -> n = v }
        )

        Text(n.toString(), modifier = Modifier.weight(.1f))
    }
}
