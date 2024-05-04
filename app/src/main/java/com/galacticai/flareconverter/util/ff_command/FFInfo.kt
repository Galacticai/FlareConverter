package com.galacticai.flareconverter.util.ff_command

import android.util.Log
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.models.ffmpeg.ff_command.FFprobeCommand
import com.galacticai.flareconverter.models.ffmpeg.ff_command.LogLevel
import com.galacticai.flareconverter.models.ffmpeg.media_info.MediaInfo
import com.galacticai.flareconverter.models.ffmpeg.media_info.codec.CodecType
import com.galacticai.flareconverter.models.ffmpeg.media_info.media_stream.MScore
import com.galacticai.flareconverter.models.ffmpeg.media_info.media_stream.MScoreType
import com.galacticai.flareconverter.models.ffmpeg.media_info.media_stream.MutableParsedMediaStreamsMap
import com.galacticai.flareconverter.models.ffmpeg.media_info.media_stream.ParsedMediaStream
import com.galacticai.flareconverter.models.ffmpeg.media_info.media_stream.ParsedMediaStreamsMap
import com.galacticai.flareconverter.util.Resolution
import java.io.File

object FFInfo {

    suspend fun getMediaInfo(file: File, mime: MimeType): MediaInfo? = FFprobeCommand()
        .input(file.absolutePath)
        .logLevel(LogLevel.Error)
        .printFormat("json=compact=1")
        .showFormat()
        .showStreams()
        .apply { if (mime in MimeType.Inputs.hasStreamGroup) showStreamGroups() }
        .run { MediaInfo(it.output) }

    /** @return
     * - key = mime category (`image`|`video`|`audio`|`subtitle`)
     * - value = sorted scores list of [ParsedMediaStream] (1st = most likely the target stream) */
    fun MediaInfo.parseMediaStreams(
        /** matching this yields much higher scores */
        mimeCategory: String
    ): ParsedMediaStreamsMap? {
        //TODO: also check MediaInfo.format

        Log.d("MediaInfo parseMediaStreams", "start")
        if (streams.isNullOrEmpty()) return null
        val map: MutableParsedMediaStreamsMap = mutableMapOf()

        data class StreamContext(
            val width: Int?,
            val height: Int?,
            val isGroup: Boolean,
            val groupType: String?
        )

        val streamContexts = mutableMapOf<Int, StreamContext>()

        streamGroups?.forEach { group ->
            group.components?.forEach { component ->
                component.subcomponents?.forEach { subComp ->
                    if (subComp.streamIndex == null) return@forEach
                    streamContexts[subComp.streamIndex!!] = StreamContext(
                        component.width, component.height,
                        true, group.typeby,
                    )
                }
            }
        }
        Log.d(
            "MediaInfo parseMediaStreams",
            "streams = " + (
                    streams?.groupingBy { it.codecType }
                        ?.eachCount()?.entries
                        ?.joinToString(" | ") { "${it.key}=${it.value}" } ?: "null"
                    )
        )


        for (stream in streams!!) {
            val index = stream.index ?: continue
            val codecNameKey = stream.codecName ?: continue
            val codec = FFStatic.CODECS_AVAILABLE?.get(codecNameKey) ?: continue
            if (!codec.canDecode) continue
            val context = streamContexts[index]
            val isGroup = context?.isGroup ?: false
            val width = context?.width ?: stream.width
            val height = context?.height ?: stream.height
            val resolution =
                    if (width != null && height != null) Resolution(width, height)
                    else null

            val duration = stream.duration
            val bitrate = stream.bitRate?.value?.toLong()
            val frameRate = stream.avgFrameRate?.let {
                val parts = it.split("/")
                if (parts.size == 2) {
                    val num = parts[0].toIntOrNull() ?: 0
                    val den = parts[1].toIntOrNull() ?: 1
                    if (den > 0) num / den else null
                } else null
            }
            val frameCount = stream.nbFrames?.toLongOrNull()
            val sampleRate = stream.sampleRate

            /** ⚠️ images are also categorized as [CodecType.Video] */
            val isVideo = codecNameKey in FFStatic.getCodecsOf(CodecType.Video)!!
            val isAudio = codecNameKey in FFStatic.getCodecsOf(CodecType.Audio)!!
            val isSubtitle = codec.type == CodecType.Subtitle || stream.codecType == "subtitle"

            val codecType = when {
                isVideo -> CodecType.Video
                isAudio -> CodecType.Audio
                isSubtitle -> CodecType.Subtitle
                else -> continue
            }

            val parsed = when (codecType) {
                CodecType.Video -> {
//                        Log.d(
//                            "MediaInfo parseMediaStreams",
//                            "Video"
//                                    + "\n | codec = $codec"
//                                    + "\n | isGroup = $isGroup"
//                                    + "\n | resolution = $resolution"
//                                    + "\n | duration = $duration"
//                                    + "\n | bitrate = $bitrate"
//                                    + "\n | frameRate = $frameRate"
//                                    + "\n | frameCount = $frameCount"
//                        )
                    val maybeImage = listOf(duration, bitrate, frameRate)
                        .any { it == null }
                    if (maybeImage) {
                        ParsedMediaStream.Image(
                            codec, isGroup,
                            resolution ?: continue,
                        )
                    } else {
                        ParsedMediaStream.Video(
                            codec, isGroup,
                            resolution ?: continue,
                            duration ?: continue,
                            bitrate ?: continue,
                            frameRate ?: continue,
                            frameCount ?: continue,
                        )
                    }
                }

                CodecType.Audio -> {
//                        Log.d(
//                            "MediaInfo parseMediaStreams",
//                            "Audio"
//                                    + "\n | codec = $codec"
//                                    + "\n | isGroup = $isGroup"
//                                    + "\n | duration = $duration"
//                                    + "\n | bitrate = $bitrate"
//                                    + "\n | sampleRate = $sampleRate"
//                        )
                    ParsedMediaStream.Audio(
                        codec, isGroup,
                        duration ?: continue,
                        bitrate ?: continue,
                        sampleRate ?: continue,
                    )
                }

                CodecType.Subtitle -> {
//                        Log.d(
//                            "MediaInfo parseMediaStreams",
//                            "Subtitle"
//                                    + "\n | codec = $codec"
//                                    + "\n | isGroup = $isGroup"
//                                    + "\n | duration = $duration"
//                        )
                    ParsedMediaStream.Subtitle(
                        codec = codec,
                        isGroup = isGroup,
                        duration = duration ?: continue,
                    )
                }

                else -> continue
            }

            val score = MScore(parsed)
            if (index == 0) {
                score + MScoreType.IsFirst
            }
            if (stream.disposition?.default == 1) {
                score + MScoreType.IsDefault
            }
            if (stream.disposition?.original == 1) {
                score + MScoreType.IsOriginal
            }
            if (isGroup) {
                score + MScoreType.IsStreamGroup
            }

            val streamType = stream.codecType ?: continue
            if (
                listOf(streamType, codecType.mimeCategory).any {
                    mimeCategory.equals(it, true)
                }
            ) {
                score + MScoreType.IsSameCategory
            } else if (
                mimeCategory.equals(MimeType.IMAGE, true)
                && codecType == CodecType.Video
            ) {
                score + MScoreType.MaybeSameCategory
            }

            map.getOrPut(codecType) { mutableListOf() }.add(score)
        }
        Log.d("MediaInfo parseMediaStreams", "streamContexts ${streamContexts.size}")

        val result = map.mapValues { (_, list) ->
            list.sortedByDescending { it.score }
        }.ifEmpty { null }

        return result
    }
}