package com.galacticai.flareconverter.models.ffmpeg.media_info.codec

/**
 *  - ..V... = Video codec
 *  - ..A... = Audio codec
 *  - ..S... = Subtitle codec
 *  - ..D... = Data codec
 *  - ..T... = Attachment codec
 */
enum class CodecType(val key: Char, val mimeCategory: String? = null) {
    /** ⚠️ images are also categorized as [CodecType.Video] */
    Video('V', "video"),
    Audio('A', "audio"),
    Subtitle('S'),
    Data('D'),
    Attachment('T');

    companion object {
        fun from(key: Char) = entries.find { it.key.equals(key, true) }
        fun from(key: String) = from(key.first())
    }
}