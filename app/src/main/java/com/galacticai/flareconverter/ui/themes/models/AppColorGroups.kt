package com.galacticai.flareconverter.ui.themes.models

import androidx.compose.runtime.Composable
import com.galacticai.flareconverter.ui.themes.GalacticColorScheme

open class ColorFamilyBase<T>(
    val primary: T,
    val secondary: T,
    // tertiary: T, //TODO
    val background: T,
    val surface: T,
    val success: T,
    val warning: T,
    val error: T,
)

open class ColorFamily(
    primary: ThemedColorGroup,
    secondary: ThemedColorGroup,
    // tertiary: ThemedColorGroup,
    background: ThemedColorGroup,
    surface: ThemedColorGroup,
    success: ThemedColorGroup,
    warning: ThemedColorGroup,
    error: ThemedColorGroup,
) : ColorFamilyBase<ThemedColorGroup>(
    primary, secondary, //tertiary
    background, surface,
    success, warning, error,
) {
    val current: ColorFamilyBase<ColorGroup>
        @Composable get() {

            val isDark = GalacticColorScheme.isDark() //? performance: call once
            return ColorFamilyBase(
                primary.of(isDark),
                secondary.of(isDark),
                //tertiary.of(isDark),
                background.of(isDark),
                surface.of(isDark),
                success.of(isDark),
                warning.of(isDark),
                error.of(isDark),
            )
        }
}
