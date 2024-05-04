package com.galacticai.flareconverter.ui.components.thumbnail_v1.components.thumbnail_renderer

import android.system.Os.stat
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.ui.components.LabeledIcon
import com.galacticai.flareconverter.ui.components.thumbnail_v1.Thumbnail
import com.galacticai.flareconverter.ui.components.thumbnail_v1.Thumbnail.Audio
import com.galacticai.flareconverter.ui.components.thumbnail_v1.Thumbnail.Image
import com.galacticai.flareconverter.ui.components.thumbnail_v1.Thumbnail.Video
import com.galacticai.flareconverter.ui.components.thumbnail_v1.ThumbnailV1Scope
import global.common.models.amount.Amount
import global.common.models.amount.units.BaseUnit
import global.common.models.amount.units.BinarySystem
import global.common.models.progressive.Progressive
import java.io.File

class FinishedThumbnailRenderer(
    private val targetFuture: Progressive.Done<File>
) : ThumbnailRenderer {
    @Composable
    override fun ThumbnailV1Scope.Preview() {
        val isPreview = LocalInspectionMode.current
        val file = targetFuture.value
        val mimeCategoryParsed = remember(mimeCategory) {
            mimeCategory
                ?: MimeType.from(
                    file,
                    acceptAnything = true,
                    allowUnsupported = true
                )?.category
        }
        if (isPreview) Thumbnail.CenterBox {
            Text(mimeCategoryParsed ?: "Unsupported")
        } else {
            when (mimeCategoryParsed) {
                MimeType.IMAGE -> Image(file = file)
                MimeType.VIDEO -> Video(file = file)
                MimeType.AUDIO -> Audio(file = file)
                else -> Thumbnail.CenterBox {
                    LabeledIcon(label = "Unsupported", color = colors.main)
                }
            }
        }
    }

    @Composable
    override fun ThumbnailV1Scope.Info() {
        val isPreview = LocalInspectionMode.current
        val value = targetFuture.value
        Thumbnail.InfoRow { Text("${value.name}") }
        Thumbnail.InfoRow {
            if (isPreview) {
                Text("12 MiB")
                Text("4000x3000")
                return@InfoRow
            }

            val path = value.absolutePath
            val amount = remember(path) {
                val stats = stat(path)
                val b = BaseUnit.byte()
                val mb = BinarySystem.mebi() and b
                Amount(
                    stats.st_size.toDouble(),
                    b
                ).toUnit(mb)
            }
            Text("$amount")
        }
    }

}
