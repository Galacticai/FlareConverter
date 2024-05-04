package com.galacticai.flareconverter.ui.components.inputs.util

import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.galacticai.flareconverter.R

@Composable
fun getWarnColors(
    isWarn: Boolean,
    /** flip container / content */
    invertPrimary: Boolean = false,
    /** flip container / content */
    invertWarn: Boolean = false
): Pair<Color, Color> {
    val colors = MaterialTheme.colorScheme
    val primaryContainer = colors.primaryContainer
    val onPrimaryContainer = colors.onPrimaryContainer
    val primary = colors.primary
    val onPrimary = colors.onPrimary

    val warningContainer = colorResource(R.color.warningContainer)
    val onWarningContainer = colorResource(R.color.onWarningContainer)
    val warning = colorResource(R.color.warning)
    val onWarning = colorResource(R.color.onWarning)

    val primaryPair =
        if (invertPrimary) primary to onPrimary
        else primaryContainer to onPrimaryContainer
    val warningPair =
        if (invertWarn) warningContainer to onWarningContainer
        else warning to onWarning

    val bg =
        if (isWarn) warningPair.first
        else primaryPair.first
    val fg =
        if (isWarn) warningPair.second
        else primaryPair.second

    val bgAnimated by animateColorAsState(
        targetValue = bg,
        label = "backgroundColor"
    )
    val fgAnimated by animateColorAsState(
        targetValue = fg,
        label = "foregroundColor"
    )
    return remember(bgAnimated, fgAnimated) {
        bgAnimated to fgAnimated
    }
}