package com.galacticai.flareconverter.models

import com.galacticai.flareconverter.models.mimes.MimeType

open class ConversionInfo(
    val from: MimeType,
    val to: MimeType,
) {
    class ToVideo(from: MimeType, to: MimeType) : ConversionInfo(from, to) {
        init {
            assert(from.category == "video")
        }
    }
}