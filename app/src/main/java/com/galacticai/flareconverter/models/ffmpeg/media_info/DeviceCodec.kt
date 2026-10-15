package com.galacticai.flareconverter.models.ffmpeg.media_info

import android.media.MediaCodecInfo
import android.media.MediaCodecList
import com.galacticai.flareconverter.models.mimes.MimeType

data class DeviceCodec(
    val mime: MimeType,
    val info: MediaCodecInfo
) {
    companion object {
        fun getAll(): List<DeviceCodec> {
            val codecs = MediaCodecList(
                MediaCodecList.REGULAR_CODECS
            ).codecInfos

            return codecs.flatMap { codec ->
                codec.supportedTypes.mapNotNull { mimeString ->
                    val mime = MimeType.from(mimeString)
                        ?: return@mapNotNull null
                    DeviceCodec(mime, codec)
                }
            }
        }
    }
}