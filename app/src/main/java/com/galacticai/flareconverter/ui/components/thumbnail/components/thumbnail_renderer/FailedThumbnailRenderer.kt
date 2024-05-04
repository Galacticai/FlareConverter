package com.galacticai.flareconverter.ui.components.thumbnail.components.thumbnail_renderer

import androidx.compose.runtime.Composable
import com.galacticai.flareconverter.ui.components.LabeledIcon
import com.galacticai.flareconverter.ui.components.thumbnail.Thumbnail
import com.galacticai.flareconverter.ui.components.thumbnail.ThumbnailScope
import com.galacticai.flareconverter.ui.components.thumbnail.components.InfoSkeleton
import global.common.models.progressive.Progressive
import java.io.File

class FailedThumbnailRenderer(
    private val targetFuture: Progressive.Failed<File>
) : ThumbnailRenderer {
    @Composable
    override fun ThumbnailScope.Preview() {
        Thumbnail.CenterBox {
            LabeledIcon(label = targetFuture.type.name, color = colors.main)
        }
    }

    @Composable
    override fun ThumbnailScope.Info() {
        InfoSkeleton()//TODO
    }

}
