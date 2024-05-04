package com.galacticai.flareconverter.ui.components.inputs.resolution_input.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.galacticai.flareconverter.ui.components.expressive.ActionDiscreteSlider
import com.galacticai.flareconverter.ui.components.inputs.resolution_input.ResolutionInputScope
import com.galacticai.flareconverter.ui.components.inputs.util.getWarnColors
import kotlin.math.roundToInt

@Composable
fun ResolutionInputScope.ControlRow() {
    val buttonColors = getWarnColors(
        isWarn = deltaIsBig,
        invertPrimary = false, invertWarn = true
    )
    val actionSliderColors = ActionDiscreteSlider.Colors().copy(
        buttonColors = {
            ButtonDefaults.buttonColors().copy(
                buttonColors.first, buttonColors.second,
            )
        }
    )
    ActionDiscreteSlider(
        values = entries,
        iCenter = iInitial,
        iInitial = iInitial,
        colors = actionSliderColors,
        renderValue = {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    //? preserve space
                    text = "XXX%", color = Color.Transparent,
                    maxLines = 1, softWrap = false,
                )
                val value = (delta.first * 100).roundToInt()
                Text(
                    "$value%",
                    maxLines = 1, softWrap = false,
                    fontWeight = FontWeight.Bold,
                    color = LocalTextStyle.current.color
                )
            }
        },
    ) { entry, _ -> setHeight(entry.height) }
}