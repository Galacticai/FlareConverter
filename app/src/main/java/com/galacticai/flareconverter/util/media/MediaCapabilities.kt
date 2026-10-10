package com.galacticai.flareconverter.util.media

import android.os.Build
import androidx.annotation.RequiresApi
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.models.ffmpeg.ff_command.FFmpegCommand
import com.galacticai.flareconverter.models.ffmpeg.media_info.DeviceCodec
import com.galacticai.flareconverter.models.ffmpeg.media_info.codec.Codec
import com.galacticai.flareconverter.models.ffmpeg.media_info.codec.CodecType
import com.galacticai.flareconverter.models.ffmpeg.media_info.encoder.Encoder
import com.galacticai.flareconverter.models.ffmpeg.media_info.format.Format
import kotlinx.coroutines.runBlocking

/** things that never change (get once) across the runtime (like supported formats ...etc...) */
object MediaCapabilities {
    object FFmpeg {
        /** key = [Format.key] */
        val FORMATS by lazy {
            FFmpegCommand().hideBanner().formats()
                .getSupportedBy(
                    "---" //? this is how ffmpeg outputs it...
                ) { Format.from(it) }
                ?.associateBy { it.key }
        }

        /** key = [Codec.key] */
        val CODECS: Map<String, Codec>? by lazy {
            FFmpegCommand().hideBanner().codecs()
                .getSupportedBy(
                    "------" //? this is how ffmpeg outputs it...
                ) { Codec.from(it) }
                ?.associateBy { it.key }
        }

        /** key = [Encoder.key] */
        val ENCODERS: Map<String, Encoder>? by lazy {
            FFmpegCommand().hideBanner().encoders()
                .getSupportedBy(
                    "-------" //? this is how ffmpeg outputs it...
                ) { Encoder.from(it) }
                ?.associateBy { it.key }
        }

        /** ⚠️ images are also categorized as [com.galacticai.flareconverter.models.ffmpeg.media_info.codec.CodecType.Video] */
        fun getCodecsOf(type: CodecType) = CODECS?.filter { it.value.type == type }

        /** ⚠️ blocks main thread - will only be used for tiny operations */
        private fun <T> FFmpegCommand.getSupportedBy(
            split: String, map: (line: String) -> T,
        ) = runBlocking {
            //! intentional: runBlocking - very lightweight so it's ok to run on main thread
            this@getSupportedBy.run { it.output }
        }?.split(split)
            ?.getOrNull(1) // second part = actual values
            ?.split('\n')
            ?.filter { it.isNotEmpty() }
            ?.mapNotNull { map(it) }
    }

    object Device {
        val CODECS: List<DeviceCodec> by lazy { DeviceCodec.getAll() }
        val CODECS_ENCODERS get() = CODECS.filter { it.info.isEncoder }
        val CODECS_DECODERS get() = CODECS.filterNot { it.info.isEncoder }
        val CODECS_HW_ACCELERATED
            @RequiresApi(Build.VERSION_CODES.Q)
            get() = CODECS.filter { it.info.isHardwareAccelerated }
        val MIMES: List<MimeType> by lazy {
            CODECS.map { it.mime }.distinctBy { it.mime }
        }
    }
}

