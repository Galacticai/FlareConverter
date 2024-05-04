package com.galacticai.flareconverter.ui.options

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.util.Resolution
import com.galacticai.flareconverter.util.Settings
import global.common.ui.ExpandableGroupItemFactory
import global.common.ui.dialogs.quick_input.InputData
import global.common.ui.dialogs.quick_input.QuickInput


@OptIn(ExperimentalMaterial3Api::class)
private val common: List<ExpandableGroupItemFactory>
    get() = listOf(
        {
            var resolution by Settings.FFmpeg.Resolution.rememberObject()
            ModalBottomSheet(onDismissRequest = {}) {
                Row {
                    QuickInput(
                        InputData.IntInputData(
                            0, "Width", {}, {},
                            { resolution = Resolution.fromWidth(it, resolution.aspectRatio) }
                        )
                    )
                    QuickInput(
                        InputData.IntInputData(
                            0,
                            "Height",
                            {},
                            {},
                            { resolution = Resolution.fromHeight(it, resolution.aspectRatio) }
                        )
                    )
                }
            }
        },
        {

        }
    )

private val video: List<ExpandableGroupItemFactory>
    get() = listOf(
        {

        },
        {

        },
    )
private val image: List<ExpandableGroupItemFactory>
    get() = listOf(
        {

        },
        {

        },
    )
val MimeType.ffmpegOptions: List<ExpandableGroupItemFactory>
    get() = common + when (category) {
        MimeType.IMAGE -> image
        MimeType.VIDEO -> video
        else -> emptyList()
    }
