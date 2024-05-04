package com.galacticai.flareconverter.ui.components.thumbnail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf

object LocalThumbnailStates {
    /** null = loading */
    val LocalThumbnailState = compositionLocalOf<ThumbnailState> {
        error("Preview file not provided")
    }

    @Composable
    fun Provider(
        thumbnailState: ThumbnailState,
        content: @Composable () -> Unit,
    ) {
        CompositionLocalProvider(
            LocalThumbnailState provides thumbnailState
        ) {
            content()
        }
    }
}