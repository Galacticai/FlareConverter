package com.galacticai.flareconverter.ui.components.thumbnail_v1.components.thumbnail_renderer

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.galacticai.flareconverter.ui.components.LabeledIcon
import com.galacticai.flareconverter.ui.components.thumbnail_v1.Thumbnail
import com.galacticai.flareconverter.ui.components.thumbnail_v1.ThumbnailV1Scope
import com.galacticai.flareconverter.ui.components.thumbnail_v1.components.InfoSkeleton
import global.common.models.progressive.Progressive
import java.io.File

class FailedThumbnailRenderer(
    private val targetFuture: Progressive.Failed<File>
) : ThumbnailRenderer {
    @Composable
    override fun ThumbnailV1Scope.Preview() {
        Thumbnail.CenterBox {
            LabeledIcon(label = targetFuture.type.name, color = colors.main)
        }
    }

    @Composable
    override fun ThumbnailV1Scope.Info() {

        Text(buildString {
            append(targetFuture.type.name)
            targetFuture.cause?.message?.let { append("\n$it") }
        })
        InfoSkeleton()//TODO
    }

}
