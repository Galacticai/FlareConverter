package com.galacticai.flareconverter.ui.themes.v1

import androidx.compose.ui.graphics.Color
import com.galacticai.flareconverter.ui.themes.models.ColorFamily
import com.galacticai.flareconverter.ui.themes.models.ColorFamilyBase
import com.galacticai.flareconverter.ui.themes.models.ColorGroup
import com.galacticai.flareconverter.ui.themes.models.ThemedColorGroup
import com.galacticai.flareconverter.ui.themes.v1.GalacticColorsV1.Blue
import com.galacticai.flareconverter.ui.themes.v1.GalacticColorsV1.Yellow

object GalacticColorFamilyV1 : ColorFamily(
    primary = ThemedColorGroup(Light.primary, Dark.primary),
    secondary = ThemedColorGroup(Light.secondary, Dark.secondary),
    background = ThemedColorGroup(Light.background, Dark.background),
    surface = ThemedColorGroup(Light.surface, Dark.surface),
    success = ThemedColorGroup(Light.success, Dark.success),
    warning = ThemedColorGroup(Light.warning, Dark.warning),
    error = ThemedColorGroup(Light.error, Dark.error),
) {
    object Light : ColorFamilyBase<ColorGroup>(
        primary = ColorGroup(
            color = Blue.Dark.blue600,
            onColor = Blue.Dark.blue50,
            colorContainer = Blue.Light.blue100,
            onColorContainer = Blue.Dark.blue900,
        ),
        secondary = ColorGroup(
            color = Yellow.Dark.yellow500,
            onColor = Yellow.Light.yellow100,
            colorContainer = Yellow.Light.yellow100,
            onColorContainer = Yellow.Dark.yellow900,
        ),
        background = ColorGroup(
            color = Blue.Dark.blue50,
            onColor = Blue.Dark.blueDarker,
        ),
        surface = ColorGroup(
            color = Blue.Dark.blue100,
            onColor = Blue.Dark.blueDarker,
        ),
        success = ColorGroup(
            color = Color(0xff1aba1a),
            onColor = Color.White,
            colorContainer = Color(0xffd6ffda),
            onColorContainer = Color(0xff024100),
        ),
        warning = ColorGroup(
            color = Color(0xffba911a),
            onColor = Color.Black,
            colorContainer = Color(0xfffff9d6),
            onColorContainer = Color(0xff412e00),
        ),
        error = ColorGroup(
            color = Color(0xffba1a1a),
            onColor = Color.White,
            colorContainer = Color(0xffffdad6),
            onColorContainer = Color(0xff410002),
        ),
    )

    object Dark : ColorFamilyBase<ColorGroup>(
        primary = ColorGroup(
            color = Blue.Light.blue300,
            onColor = Blue.Dark.blue900,
            colorContainer = Blue.Dark.blue900,
            onColorContainer = Blue.Light.blue100,
        ),
        secondary = ColorGroup(
            color = Yellow.Light.yellow200,
            onColor = Yellow.Dark.yellow900,
            colorContainer = Yellow.Dark.yellow800,
            onColorContainer = Yellow.Light.yellow50,
        ),
        background = ColorGroup(
            color = Blue.Dark.blueDarker,
            onColor = Blue.Dark.blue50,
        ),
        surface = ColorGroup(
            color = Blue.Dark.blueDark,
            onColor = Blue.Dark.blue50,
        ),
        success = ColorGroup(
            color = Color(0xff1aba1a),
            onColor = Color.White,
            colorContainer = Color(0xff024100),
            onColorContainer = Color(0xffd6ffda),
        ),
        warning = ColorGroup(
            color = Color(0xffba911a),
            onColor = Color.Black,
            colorContainer = Color(0xff412e00),
            onColorContainer = Color(0xfffff9d6),
        ),
        error = ColorGroup(
            color = Color(0xffba1a1a),
            onColor = Color.White,
            colorContainer = Color(0xff410002),
            onColorContainer = Color(0xffffdad6),
        ),
    )
}
