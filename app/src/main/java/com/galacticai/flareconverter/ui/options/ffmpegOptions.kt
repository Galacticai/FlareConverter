package com.galacticai.flareconverter.ui.options

import com.galacticai.flareconverter.models.MimeType
import global.common.ui.ExpandableGroupItemFactory


private val common: List<ExpandableGroupItemFactory>
    get() = listOf(
        {
//            val resolution by Settings.FFmpeg.;
//            QuickInput(
//                InputData.IntInputData(
//                    0,
//                    "Resolution: Width"
//                )
//            )
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
