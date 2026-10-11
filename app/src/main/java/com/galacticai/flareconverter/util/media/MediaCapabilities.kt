package com.galacticai.flareconverter.util.media

import android.os.Build
import androidx.annotation.RequiresApi
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.models.SynonyMap
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
        val formats: Map<String, Format>? by lazy {
            FFmpegCommand().hideBanner().formats()
                .getSupportedBy(
                    "---" //? this is how ffmpeg outputs it...
                ) { Format.from(it) }
                ?.associateBy { it.key }
        }

        /** key = [Codec.key] */
        val codecs: Map<String, Codec>? by lazy {
            FFmpegCommand().hideBanner().codecs()
                .getSupportedBy(
                    "------" //? this is how ffmpeg outputs it...
                ) { Codec.from(it) }
                ?.associateBy { it.key }
        }

        /** key = [Encoder.key] */
        val encoders: Map<String, Encoder>? by lazy {
            FFmpegCommand().hideBanner().encoders()
                .getSupportedBy(
                    "-------" //? this is how ffmpeg outputs it...
                ) { Encoder.from(it) }
                ?.associateBy { it.key }
        }

        /** ⚠️ images are also categorized as [com.galacticai.flareconverter.models.ffmpeg.media_info.codec.CodecType.Video] */
        fun getCodecsOf(type: CodecType) = codecs?.filter { it.value.type == type }

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
        val codecs: List<DeviceCodec> by lazy { DeviceCodec.getAll() }
        val codecsEncoders get() = codecs.filter { it.info.isEncoder }
        val codecsDecoders get() = codecs.filterNot { it.info.isEncoder }
        val codersHWAccelerated
            @RequiresApi(Build.VERSION_CODES.Q)
            get() = codecs.filter { it.info.isHardwareAccelerated }
        val mimes: List<MimeType> by lazy {
            codecs.map { it.mime }.distinctBy { it.mime }
        }
    }

    object Intersect {
        private fun String.toTokens() = this
            .lowercase()
            .split(Regex("[^a-z0-9]+"))
            .asSequence()
            .filter { it.length > 1 } //? skip noise
            .toSet()

        private val synonyms = SynonyMap(
            setOf("3gpp", "amr", "amrnb", "amr_nb", "h263", "amrwb", "amr_wb"),
            setOf("g711", "mlaw", "alaw", "pcm", "mulaw", "pcm_alaw", "pcm_mulaw"),
            setOf("ima", "adpcm"),
            setOf("heic", "hevc", "heif"),
            setOf("raw", "pcm", "rawvideo"),
        )

        private fun Set<String>.expandSynonyms() = buildSet {
            addAll(this@expandSynonyms)
            for (token in this@expandSynonyms) {
                addAll(synonyms[token])
            }
        }

        val cache = mutableMapOf<Boolean?, Map<String, Codec>?>()

        /** intersects [FFmpeg.codecs] with [Device.codecs] dynamically */
        fun codecs(
            /**
             * - `null` = all
             * - `true` = [Device.codecsEncoders]
             * - `false` = [Device.codecsDecoders]
             */
            isEncoder: Boolean? = null
        ): Map<String, Codec>? {
            if (cache.contains(isEncoder)) return cache[isEncoder]

            val ffmpegCodecs = FFmpeg.codecs ?: return null
            val deviceCodecs = when (isEncoder) {
                true -> Device.codecsEncoders
                false -> Device.codecsDecoders
                null -> Device.codecs
            }

            val deviceTokens = deviceCodecs.flatMap {
                val mimeTokens = it.mime.key?.toTokens() ?: emptySet()
                val nameTokens = it.info.name.toTokens()
                mimeTokens + nameTokens
            }.toSet().expandSynonyms()

            val result = ffmpegCodecs.filter { (key, codec) ->
                val keyTokens = key.toTokens()
                val descTokens = codec.description.toTokens()
                val codecTokens = (keyTokens + descTokens).expandSynonyms()

                val matches = (deviceTokens.intersect(codecTokens)).isNotEmpty()

                if (!matches) return@filter false

                when (isEncoder) {
                    true -> codec.canEncode
                    false -> codec.canDecode
                    null -> codec.canEncode || codec.canDecode
                }
            }

            cache[isEncoder] = result
            return result
        }
    }

    fun canInput(codec: Codec) =
            Intersect.codecs(false)?.contains(codec.key)

    fun canOutput(codec: Codec) =
            Intersect.codecs(true)?.contains(codec.key)
}

