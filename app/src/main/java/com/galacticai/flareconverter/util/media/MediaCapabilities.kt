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

        /**
         * key: (isDecoder, isEncoder)
         * - (true, false) = decoders only
         * - (false, true) = encoders only
         * - (true, true) = both
         * - (false, false) = unused sentinel
         */
        private val cache = mutableMapOf<Pair<Boolean, Boolean>, List<DeviceCodec>>()

        /** cached FFmpeg codecs indexed by isEncoder. */
        private val ffmpegCodecsCache = mutableMapOf<Boolean?, Map<String, Codec>?>()

        /** get cached FFmpeg codecs with encoder/decoder filtering */
        private fun getCachedCodecs(isEncoder: Boolean?): Map<String, Codec>? {
            return ffmpegCodecsCache.getOrPut(isEncoder) {
                FFmpeg.codecs?.let { codecs ->
                    when (isEncoder) {
                        true -> codecs.filterValues { it.canEncode }
                        false -> codecs.filterValues { it.canDecode }
                        null -> codecs
                    }
                }
            }
        }

        /** get cached device codecs filtered by [isDecoder] and [isEncoder] */
        private fun getCachedDeviceCodecs(
            isDecoder: Boolean,
            isEncoder: Boolean
        ): List<DeviceCodec> {
            return cache.getOrPut(isDecoder to isEncoder) {
                Device.codecs.filter { deviceCodec ->
                    val supportsDecoder = !deviceCodec.info.isEncoder
                    val supportsEncoder = deviceCodec.info.isEncoder
                    (!isDecoder || supportsDecoder) &&
                            (!isEncoder || supportsEncoder)
                }
            }
        }

        private fun Set<String>.fuzzyIntersects(other: Set<String>) =
                any { other.contains(it) }

        /**
         * check if a device codec matches an FFmpeg codec by tokens
         * (expands both sides with synonyms, then checks for any token overlap)
         */
        private fun DeviceCodec.matchesFuzzy(ffmpegCodec: Codec): Boolean {
            val deviceTokens = buildSet {
                addAll(mime.key?.toTokens() ?: emptySet())
                addAll(info.name.toTokens())
            }.expandSynonyms()

            val ffmpegTokens = buildSet {
                addAll(ffmpegCodec.key.toTokens())
                addAll(ffmpegCodec.description.toTokens())
            }.expandSynonyms()

            //? symmetric intersection
            return deviceTokens.fuzzyIntersects(ffmpegTokens)
        }

        /**
         * find the best matching [DeviceCodec] for an FFmpeg [Codec]
         * @return first match, or null if none found
         */
        private fun List<DeviceCodec>.findBestMatch(ffmpegCodec: Codec): DeviceCodec? {
            return firstOrNull { it.matchesFuzzy(ffmpegCodec) }
        }

        /**
         * check if the device can decode the given FFmpeg codec
         * - matches by [MimeType] or [Codec.key], then verifies decoder capability
         */
        fun canDecode(ffmpegCodec: Codec): Boolean {
            val cachedCodecs = getCachedCodecs(isEncoder = false)
                ?: return false

            if (cachedCodecs[ffmpegCodec.key] == null) return false

            val deviceDecoders = getCachedDeviceCodecs(isDecoder = true, isEncoder = false)
            return deviceDecoders.findBestMatch(ffmpegCodec) != null
        }

        /**
         * check if the device can encode the given FFmpeg codec
         * - matches by [MimeType] or [Codec.key], then verifies encoder capability
         */
        fun canEncode(ffmpegCodec: Codec): Boolean {
            val cachedCodecs = getCachedCodecs(isEncoder = true)
                ?: return false

            if (cachedCodecs[ffmpegCodec.key] == null) return false

            val deviceEncoders = getCachedDeviceCodecs(isDecoder = false, isEncoder = true)
            return deviceEncoders.findBestMatch(ffmpegCodec) != null
        }

        /** all FFmpeg codecs that the device can both encode AND decode */
        fun supportedCodecs(): Set<String> {
            val allCodecs = getCachedCodecs(null) ?: return emptySet()
            val deviceDecoders = getCachedDeviceCodecs(isDecoder = true, isEncoder = false)
            val deviceEncoders = getCachedDeviceCodecs(isDecoder = false, isEncoder = true)

            return allCodecs.keys.filter { key ->
                val codec = allCodecs[key]
                    ?: return@filter false
                val canDecode = deviceDecoders.findBestMatch(codec) != null
                val canEncode = deviceEncoders.findBestMatch(codec) != null
                canDecode && canEncode
            }.toSet()
        }

        /**
         * all FFmpeg codecs that the device can decode
         * - uses cached device decoders to avoid re-scanning
         */
        fun decoders(): Map<String, Codec>? {
            val cachedCodecs = getCachedCodecs(isEncoder = false) ?: return null
            val deviceDecoders = getCachedDeviceCodecs(isDecoder = true, isEncoder = false)
            return cachedCodecs.filterKeys {
                val codec = cachedCodecs[it] ?: return@filterKeys false
                deviceDecoders.findBestMatch(codec) != null
            }
        }

        /**
         * all FFmpeg codecs that the device can encode
         * - uses cached device decoders to avoid re-scanning
         */
        fun encoders(): Map<String, Codec>? {
            val cachedCodecs = getCachedCodecs(isEncoder = true) ?: return null
            val deviceEncoders = getCachedDeviceCodecs(isDecoder = false, isEncoder = true)
            return cachedCodecs.filterKeys {
                val codec = cachedCodecs[it] ?: return@filterKeys false
                deviceEncoders.findBestMatch(codec) != null
            }
        }
    }
}

