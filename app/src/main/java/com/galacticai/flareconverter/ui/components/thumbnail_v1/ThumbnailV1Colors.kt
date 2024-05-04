package com.galacticai.flareconverter.ui.components.thumbnail_v1

import androidx.annotation.ColorRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource


data class ThumbnailV1Colors(
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
        ) = ThumbnailV1Colors(
            colorResource(main),
            colorResource(secondary),
            colorResource(bg)
        )
    }
}
