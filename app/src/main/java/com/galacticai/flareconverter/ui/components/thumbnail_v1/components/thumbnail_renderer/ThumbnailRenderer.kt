package com.galacticai.flareconverter.ui.components.thumbnail_v1.components.thumbnail_renderer

import androidx.compose.runtime.Composable
import com.galacticai.flareconverter.ui.components.thumbnail_v1.ThumbnailV1Scope
import global.common.models.progressive.Progressive
import java.io.File

interface ThumbnailRenderer {
    /** preview card content (like image thumbnail + click to open...etc...) */
    @Composable
    fun ThumbnailV1Scope.Preview()

    /** info column below the preview */
    @Composable
    fun ThumbnailV1Scope.Info()


    companion object {
        /** auto select the [ThumbnailRenderer] according to [targetFuture] */
        fun from(
            targetFuture: Progressive<File>?
        ): ThumbnailRenderer = when (targetFuture) {
            is Progressive.Pending -> PendingThumbnailRenderer(targetFuture)
            is Progressive.Running -> RunningThumbnailRenderer(targetFuture)
            is Progressive.Done -> FinishedThumbnailRenderer(targetFuture)
            is Progressive.Failed -> FailedThumbnailRenderer(targetFuture)
            else -> FallbackThumbnailRenderer()
        }
    }
}

