package com.galacticai.flareconverter.ui.components.thumbnail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.galacticai.flareconverter.models.MediaFileBase

class ThumbnailState(
    val files: Thumbnail.Files,
    val inColors: ProgressiveThumbnailColors,
    val outColors: ProgressiveThumbnailColors,
) {
    var inFilePreview: MediaFileBase? by mutableStateOf(null)

    companion object {
        @Composable
        fun remember(files: Thumbnail.Files): ThumbnailState {
            val inColors = ProgressiveThumbnailColors.animated(files.inFile)
            val outColors = ProgressiveThumbnailColors.animated(files.outFile)

            //? more once needed
            return remember(files) {
                ThumbnailState(files, inColors, outColors)
            }
        }
    }
}