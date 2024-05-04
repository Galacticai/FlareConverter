package com.galacticai.flareconverter.ui.components.thumbnail

import androidx.annotation.ColorRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource


data class ThumbnailColors(
    val main: Color,
    val secondary: Color,
    val bg: Color,
) {
    companion object {
        @Composable
        fun from(
            @ColorRes main: Int,
            @ColorRes secondary: Int,
            @ColorRes bg: Int,
        ): ThumbnailColors {
            return ThumbnailColors(
                colorResource(main),
                colorResource(secondary),
                colorResource(bg)
            )
        }
    }
}
