package com.galacticai.flareconverter.ui.components.thumbnail_v1.components.thumbnail_renderer

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.galacticai.flareconverter.ui.components.LabeledIcon
import com.galacticai.flareconverter.ui.components.thumbnail_v1.Thumbnail
import com.galacticai.flareconverter.ui.components.thumbnail_v1.ThumbnailV1Scope
import global.common.models.progressive.Progressive
import global.common.util.TextUtil

/** unrecognized [Progressive] renderer */
class FallbackThumbnailRenderer : ThumbnailRenderer {
    @Composable
    override fun ThumbnailV1Scope.Preview() {
        Thumbnail.CenterBox { LabeledIcon(color = colors.main) }
    }

    @Composable
    override fun ThumbnailV1Scope.Info() {
        Thumbnail.InfoRow {
            Text("Unknown file state${TextUtil.ELLIPSES}")
        }
    }
}