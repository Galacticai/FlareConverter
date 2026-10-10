package com.galacticai.flareconverter.models.ffmpeg.media_info.encoder

/**
 *  -  V..... = Video
 *  -  A..... = Audio
 *  -  S..... = Subtitle
 */
enum class EncoderType(val key: Char) {
    /** ⚠️ images are also categorized as [EncoderType.Video] */
    Video('V'),
    Audio('A'),
    Subtitle('S'),
    Data('D'),
    Attachment('T');

    companion object {
        fun from(key: Char) = entries.find { it.key.equals(key, true) }
        fun from(key: String) = from(key.first())
    }
}