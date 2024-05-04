package com.galacticai.flareconverter.ui.components.thumbnail_v1.components.thumbnail_renderer

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.galacticai.flareconverter.ui.components.thumbnail_v1.Thumbnail
import com.galacticai.flareconverter.ui.components.thumbnail_v1.ThumbnailV1Scope
import com.galacticai.flareconverter.ui.components.thumbnail_v1.components.InfoSkeleton
import com.galacticai.flareconverter.util.Consistent
import global.common.models.progressive.Progressive
import java.io.File

class PendingThumbnailRenderer(
    private val targetFuture: Progressive.Pending<File>
) : ThumbnailRenderer {
    @Composable
    override fun ThumbnailV1Scope.Preview() = Thumbnail.CenterBox {
        CircularProgressIndicator(
            Modifier.size(Consistent.Pad.large),
            color = colors.main,
            trackColor = colors.secondary
        )
    }

    @Composable
    override fun ThumbnailV1Scope.Info() {
        InfoSkeleton()
    }
}
