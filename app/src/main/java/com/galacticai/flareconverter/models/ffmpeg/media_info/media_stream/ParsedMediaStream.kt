package com.galacticai.flareconverter.models.ffmpeg.media_info.media_stream

import com.galacticai.flareconverter.models.ffmpeg.media_info.codec.Codec
import com.galacticai.flareconverter.models.ffmpeg.media_info.codec.CodecType
import com.galacticai.flareconverter.util.Resolution
import global.common.models.amount.Amount
import global.common.models.amount.units.BaseUnit
import kotlin.time.Duration

/** [ParsedMediaStream] with their scores ([MScore]) separated by type ([CodecType]) */
typealias ParsedMediaStreamsMap = Map<CodecType, List<MScore<ParsedMediaStream>>>
typealias MutableParsedMediaStreamsMap = MutableMap<CodecType, MutableList<MScore<ParsedMediaStream>>>
/** [ParsedMediaStreamsMap] - but only top scores */
typealias ParsedMediaStreamsMainMap = Map<CodecType, ParsedMediaStream>

sealed class ParsedMediaStream(
    val codec: Codec,
    val isGroup: Boolean,
) {
    open val resolution: Resolution? get() = null
    open val duration: Duration? get() = null
    open val bitrate: Long? get() = null
    open val frameRate: Int? get() = null
    open val sampleRate: Long? get() = null
    open val frameCount: Long? get() = null

    open val bitrateAmount: Amount?
        get() = bitrate?.let {
            Amount(it.toDouble(), BaseUnit.bitPerSecond())
        }

    /** single image */
    class Image(
        codec: Codec, isGroup: Boolean,
        override val resolution: Resolution,
    ) : ParsedMediaStream(codec, isGroup)

    /** video or animated */
    class Video(
        codec: Codec, isGroup: Boolean,
        override val resolution: Resolution,
        override val duration: Duration,
        override val bitrate: Long,
        override val frameRate: Int,
        override val frameCount: Long,
    ) : ParsedMediaStream(codec, isGroup) {
        override val bitrateAmount get() = super.bitrateAmount!!
    }

    /** audio */
    class Audio(
        codec: Codec, isGroup: Boolean,
        override val duration: Duration,
        override val bitrate: Long,
        override val sampleRate: Long,
    ) : ParsedMediaStream(codec, isGroup)

    /** subtitle */
    class Subtitle(
        codec: Codec, isGroup: Boolean,
        override val duration: Duration,
    ) : ParsedMediaStream(codec, isGroup)

    companion object {
        val ParsedMediaStreamsMap.main: ParsedMediaStreamsMainMap
            get() = this.mapValues { it.value.first().value }
    }
}
