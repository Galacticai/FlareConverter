package com.galacticai.flareconverter.ui.components.thumbnail.components.thumbnail_renderer

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.galacticai.flareconverter.ui.components.LabeledIcon
import com.galacticai.flareconverter.ui.components.thumbnail.Thumbnail
import com.galacticai.flareconverter.ui.components.thumbnail.ThumbnailScope
import global.common.models.progressive.Progressive
import global.common.util.TextUtil

/** unrecognized [Progressive] renderer */
class FallbackThumbnailRenderer : ThumbnailRenderer {
    @Composable
    override fun ThumbnailScope.Preview() {
        Thumbnail.CenterBox { LabeledIcon(color = colors.main) }
    }

    @Composable
    override fun ThumbnailScope.Info() {
        Thumbnail.InfoRow {
            Text("Unknown file state${TextUtil.ELLIPSES}")
        }
    }
}