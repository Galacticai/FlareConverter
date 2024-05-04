package com.galacticai.flareconverter.ui.components.expressive

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.galacticai.flareconverter.R
import com.galacticai.flareconverter.ui.themes.GalacticTheme
import com.galacticai.flareconverter.util.Consistent
import global.common.ui.DiscreteSlider
import global.common.ui.DiscreteSliderOnChange
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private typealias Comp<T> = @Composable () -> T

object ActionDiscreteSlider {
    data class Colors(
        val sliderColors: Comp<SliderColors> = { SliderDefaults.colors() },
        val sliderContainer: Comp<Color> = { MaterialTheme.colorScheme.primaryContainer },
        val buttonColors: Comp<ButtonColors> = { ButtonDefaults.buttonColors() },
    )
}

@Composable
fun <T> ActionDiscreteSlider(
    modifier: Modifier = Modifier,
    values: List<T>,
    iSelectedCustom: Int? = null,
    iInitial: Int = 0,
    /** center the slider around this index */
    iCenter: Int = 0,
    /** null = hide */
    renderValue: (Comp<Unit>)? = null,
    colors: ActionDiscreteSlider.Colors = ActionDiscreteSlider.Colors(),
    onChange: DiscreteSliderOnChange<T>
) {
    var iSelected by remember(iInitial) { mutableIntStateOf(iInitial) }
    LaunchedEffect(iSelectedCustom) {
        if (iSelectedCustom != null) iSelected = iSelectedCustom
    }

    ExpressiveActionRow(
        modifier = modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(Consistent.Pad.small),
        verticalAlignment = Alignment.CenterVertically,
        action = { buttonModifier ->
            PercentButton(
                modifier = Modifier.fillMaxHeight() then buttonModifier,
                renderValue = renderValue,
                colors = colors.buttonColors(),
                onClick = {
                    val accepted = iSelected != iInitial
                    if (accepted) {
                        iSelected = iInitial
                        onChange(values[iInitial], iInitial)
                    }
                    accepted
                }
            )
        }
    ) { cardModifier ->
        val expressive = rememberExpressiveRadius (
            Consistent.Shape.Radius.expressive,
        )
        Card(
            modifier = Modifier.fillMaxHeight() then cardModifier then expressive.modifierAll,
            colors = CardDefaults.cardColors()
                .copy(colors.sliderContainer()),
            shape = expressive.shape,
        ) {
            Box(Modifier.fillMaxHeight(), contentAlignment = Alignment.Center) {
                DiscreteSlider(
                    modifier = Modifier.padding(
                        horizontal = Consistent.Pad.moderate, vertical = Consistent.Pad.smallXX
                    ),
                    values = values,
                    iSelectedCustom = iSelected,
                    iCenter = iCenter,
                    iInitial = iInitial,
                    colors = colors.sliderColors(),
                    onChange = { value, i ->
                        iSelected = i
                        onChange(value, i)
                    },
                )
            }
        }
    }
}

@Composable
private fun PercentButton(
    modifier: Modifier = Modifier,
    /** null = hide */
    renderValue: (Comp<Unit>)? = null,
    colors: ButtonColors,
    /** @returns true if click was accepted */
    onClick: () -> Boolean,
) {
    val coroutineScope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }

    Button(
        modifier = modifier,
        shapes = Consistent.Shape.Rounded.button,
        colors = colors,
        contentPadding =
            if (renderValue == null)
                ButtonDefaults.contentPaddingFor(ButtonDefaults.MinHeight)
            else PaddingValues(
                start = Consistent.Pad.regular,
                end = Consistent.Pad.small,
                top = Consistent.Pad.small,
                bottom = Consistent.Pad.small
            ),
        onClick = {
            val accepted = onClick()
            if (!accepted) return@Button
            coroutineScope.launch {
                rotation.animateTo(
                    rotation.value + 360f,
                    tween(500)
                )
                rotation.snapTo(0f)
            }
        },
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Consistent.Pad.smallX),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Rounded.Refresh, "Reset",
                Modifier.rotate(rotation.value)
            )
            renderValue?.invoke()
        }
    }
}


@Preview
@Composable
private fun ManagedDiscreteSliderPreview() {
    val values = listOf(144, 240, 360, 480, 720, 1080, 1440, 2161)
    val iInitial = 3
    val valueInitial = values[iInitial]
    val iSelectedState = remember { mutableIntStateOf(5) }
    var iSelected by iSelectedState
    val selected = values[iSelected]
    val isWarn = selected == values[0] || selected == values[values.lastIndex]

    @Composable
    fun renderValue(isWarn: Boolean) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                //? preserve space
                text = "XXX%", color = Color.Transparent,
                maxLines = 1, softWrap = false,
            )
            val value = (selected.toFloat() / valueInitial * 100).roundToInt()
            Text(
                "$value%",
                maxLines = 1, softWrap = false,
                fontWeight = FontWeight.Bold,
                color = if (isWarn) colorResource(R.color.warning) else MaterialTheme.colorScheme.primary
            )
        }
    }
    GalacticTheme {
        ActionDiscreteSlider(
            Modifier.padding(15.dp),
            iSelectedCustom = iSelected,
            values = values,
            iCenter = iInitial,
            iInitial = iInitial,
            colors = ActionDiscreteSlider.Colors().copy(
                buttonColors = {
                    ButtonDefaults.buttonColors().copy(
                        containerColor =
                            if (isWarn) colorResource(R.color.warningContainer)
                            else MaterialTheme.colorScheme.primaryContainer,
                        contentColor =
                            if (isWarn) colorResource(R.color.warning)
                            else MaterialTheme.colorScheme.primary,
                    )
                }
            ),
            renderValue = { renderValue(isWarn) },
        ) { _, i -> iSelected = i }
    }
}
