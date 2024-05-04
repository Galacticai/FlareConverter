package com.galacticai.flareconverter.util

import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.models.ffmpeg.media_info.codec.CodecType

object MimeTypeUtils {
    val MimeType.dashed get() = "$category — $key" //? yes I write em dash, not AI

    val regex = Regex(
        """^(?<category>\*|[a-z0-9\-+.]+)/(?<key>\*|[a-z0-9\-+.]+)$""",
        RegexOption.IGNORE_CASE
    )
    val String.isMimeType get() = regex.matches(this)

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