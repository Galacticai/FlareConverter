package com.galacticai.flareconverter.util

import com.galacticai.flareconverter.models.ffmpeg.media_info.codec.CodecType
import com.galacticai.flareconverter.models.mimes.MimeType

object MimeTypeUtils {
    val String.isMimeType get() = MimeType.REGEX.matches(this)

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