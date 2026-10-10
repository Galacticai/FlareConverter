package com.galacticai.flareconverter.models.ffmpeg.media_info

import android.media.MediaCodecInfo
import android.media.MediaCodecList
import com.galacticai.flareconverter.models.MimeType

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
                    val mime = MimeType.from(
                        mimeString,
                        allowWild = true, allowUnsupported = true
                    ) ?: return@mapNotNull null

                    DeviceCodec(mime, codec)
                }
            }
        }
    }
}