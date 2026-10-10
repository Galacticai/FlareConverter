package com.galacticai.flareconverter.util

import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.models.ffmpeg.media_info.codec.CodecType

object MimeTypeUtils {
    @get:Deprecated("use `MimeTypeView` instead")
    val MimeType.dashed get() = "$category — $key" //? yes I write em dash, not AI

    val String.isMimeType get() = MimeType.regex.matches(this)

    /** Get the [Settings.OutMime] for this [MimeType] */
    val MimeType.outMimeSetting get() = Settings.OutMime(this)

    val MimeType.codecType: CodecType?
        get() = when (category) {
            //! images+videos use video codecs
            MimeType.IMAGE, MimeType.VIDEO -> CodecType.Video
            MimeType.AUDIO -> CodecType.Audio
            else -> null
        }
}