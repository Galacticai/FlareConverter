package com.galacticai.flareconverter.ui.themes.models

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.galacticai.flareconverter.ui.themes.GalacticColorScheme

open class ColorGroup(
    val color: Color,
    val onColor: Color = color,
    val colorVariant: Color = color,
    val colorContainer: Color = color,
    val onColorContainer: Color = color,
)

open class ThemedColorGroup(
    val light: ColorGroup,
    val dark: ColorGroup,
) {
    fun of(isDark: Boolean) =
            if (isDark) dark else light

    val current: ColorGroup
        @Composable get() = of(GalacticColorScheme.isDark())
}