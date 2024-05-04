package com.galacticai.flareconverter.ui.components.inputs.util

import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.galacticai.flareconverter.ui.themes.v1.GalacticColorFamilyV1

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

    val warning = GalacticColorFamilyV1.current.warning

    val primaryPair =
            if (invertPrimary) primary to onPrimary
            else primaryContainer to onPrimaryContainer
    val warningPair =
            if (invertWarn) warning.colorContainer to warning.onColorContainer
            else warning.color to warning.onColor

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