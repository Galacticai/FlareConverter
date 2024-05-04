package com.galacticai.flareconverter.ui.components.thumbnail

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.galacticai.flareconverter.ui.themes.v1.GalacticColorFamilyV1
import global.common.models.progressive.Progressive

data class ProgressiveThumbnailColors(
    val bg: Color,
    val bgSurface: Color,
    val bgSurfaceSecondary: Color = bgSurface,
    val fg: Color,
    val hero: Color,
) {
    companion object {
        private val colors @Composable get() = MaterialTheme.colorScheme

        val neutral
            @Composable get() = ProgressiveThumbnailColors(
                bg = colors.background,
                bgSurface = colors.primaryContainer,
                bgSurfaceSecondary = colors.secondaryContainer,
                fg = colors.onPrimaryContainer,
                hero = colors.primary,
            )
        val warn
            @Composable get() = ProgressiveThumbnailColors(
                bg = colors.background,
                bgSurface = GalacticColorFamilyV1.current.warning.colorContainer,
                fg = GalacticColorFamilyV1.current.warning.onColorContainer,
                hero = GalacticColorFamilyV1.current.warning.color,
            )
        val error
            @Composable get() = ProgressiveThumbnailColors(
                bg = colors.background,
                bgSurface = colors.errorContainer,
                fg = colors.onErrorContainer,
                hero = colors.error,
            )


        @Composable
        fun from(progressive: Progressive<*>) = when (progressive) {
            is Progressive.Pending,
            is Progressive.Running,
            is Progressive.Done -> neutral

            is Progressive.Failed -> when (progressive.type) {
                Progressive.Failed.Type.Timeout,
                Progressive.Failed.Type.Error -> error

                Progressive.Failed.Type.Stopped -> warn
            }

            else -> { //? failsafe: `Progressive` class is open
                Log.d("ProgressiveThumbnailColors.from", "Unexpected type: $progressive")
                neutral
            }
        }

        @Composable
        fun animated(progressive: Progressive<*>) = with(from(progressive)) {
            val bg by animateColorAsState(bg)
            val bgSurface by animateColorAsState(bgSurface)
            val bgSurfaceSecondary by animateColorAsState(bgSurfaceSecondary)
            val fg by animateColorAsState(fg)
            val hero by animateColorAsState(hero)
            ProgressiveThumbnailColors(
                bg, bgSurface, bgSurfaceSecondary,
                fg, hero,
            )
        }
    }
}
