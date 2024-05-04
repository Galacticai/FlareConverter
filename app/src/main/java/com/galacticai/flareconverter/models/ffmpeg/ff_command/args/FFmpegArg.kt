package com.galacticai.flareconverter.models.ffmpeg.ff_command.args

import global.common.util.TextUtil.sentenceCase

sealed class FFmpegArg(
    id: Int,
    order: ArgOrder,
    key: String,
    parseCmd: FFArgParserFn = FFArgParser.join(" "),
    title: String = javaClass.simpleName.sentenceCase,
) : FFArg(id, order, key, parseCmd, title) {

    companion object {
        fun from(id: Int) = entries.find { id == it.id }

        val entries: List<FFmpegArg> by lazy {
            listOf(
                Version, Help, HideBanner, Codecs, Formats, Progress, Input, FrameCount, FrameRate,
                SampleRate, Channels, Bitrate, BitrateVideo, BitrateAudio, CodecVideo, CodecAudio,
                Resolution, DurationByString, DurationByMs, Format, StartTimeByMs,
                StartTimeByString, EndTimeByMs, EndTimeByString, Scale, Crop, Speed, Pitch,
                MetadataByString, MetadataByPairs, MetadataByMap, Preset, Crf, MaxSizeByAmount,
                MaxSizeByString, Output,
            )
        }
    }

    object Version : FFmpegArg(0, ArgOrder.First, "version", FFArgParser.none)
    object Help : FFmpegArg(1, ArgOrder.First, "h", FFArgParser.none)
    object HideBanner : FFmpegArg(2, ArgOrder.First, "hide_banner", FFArgParser.none)
    object Stats : FFmpegArg(3, ArgOrder.First, "stats", FFArgParser.none)
    object Codecs : FFmpegArg(4, ArgOrder.First, "codecs", FFArgParser.none)
    object Formats : FFmpegArg(5, ArgOrder.First, "formats", FFArgParser.none)
    object Progress : FFmpegArg(6, ArgOrder.First, "progress", FFArgParser.none)

    /** expects [log level] */
    object LogLevel : FFmpegArg(7, ArgOrder.First, "v", FFArgParser.single)

    /** expects: `[input]` */
    object Input : FFmpegArg(8, ArgOrder.Input, "i", FFArgParser.single, title = "Convert")

    /** expects: `[count]` */
    object FrameCount : FFmpegArg(9, ArgOrder.AfterInput, "vframes", FFArgParser.single)

    /** expects: `[fps]` */
    object FrameRate : FFmpegArg(10, ArgOrder.AfterInput, "r", FFArgParser.single)

    /** expects: `[rate]` */
    object SampleRate : FFmpegArg(11, ArgOrder.AfterInput, "ar", FFArgParser.single)

    /** expects: `[count]` */
    object Channels : FFmpegArg(12, ArgOrder.AfterInput, "ac", FFArgParser.single)

    /** expects: `[bitrate]` */
    object Bitrate : FFmpegArg(13, ArgOrder.AfterInput, "b", FFArgParser.single)

    /** expects: `[bitrate]` */
    object BitrateVideo : FFmpegArg(14, ArgOrder.AfterInput, "b:v", FFArgParser.single)

    /** expects: `[bitrate]` */
    object BitrateAudio : FFmpegArg(15, ArgOrder.AfterInput, "b:a", FFArgParser.single)

    /** expects: `[codec]` */
    object CodecVideo : FFmpegArg(16, ArgOrder.AfterInput, "c:v", FFArgParser.single)
    object CodecAudio : FFmpegArg(17, ArgOrder.AfterInput, "c:a", FFArgParser.single)

    /** expects: `[width, height]` */
    object Resolution : FFmpegArg(18, ArgOrder.AfterInput, "s", FFArgParser.join("x", 2))

    /** expects: `[duration: String]` */
    object DurationByString : FFmpegArg(19, ArgOrder.AfterInput, "t", FFArgParser.single)

    /** expects: `[milliseconds: Long]` */
    object DurationByMs : FFmpegArg(20, ArgOrder.AfterInput, "t", FFArgParser.timestamp)

    /** expects: `[format]` */
    object Format : FFmpegArg(21, ArgOrder.AfterInput, "f", FFArgParser.single)

    /** expects: `[milliseconds: Long]` */
    object StartTimeByMs : FFmpegArg(22, ArgOrder.AfterInput, "ss", FFArgParser.timestamp)

    /** expects: `[time: String]` */
    object StartTimeByString : FFmpegArg(23, ArgOrder.AfterInput, "ss", FFArgParser.single)

    /** expects: `[milliseconds: Long]` */
    object EndTimeByMs : FFmpegArg(24, ArgOrder.AfterInput, "to", FFArgParser.timestamp)

    /** expects: `[time: String]` */
    object EndTimeByString : FFmpegArg(25, ArgOrder.AfterInput, "to", FFArgParser.single)

    /** expects: `[width, height]` */
    object Scale : FFmpegArg(26, ArgOrder.AfterInput, "vf", FFArgParser.filter("scale", ":", 2))

    /** expects: `[width, height, x, y]` */
    object Crop : FFmpegArg(27, ArgOrder.AfterInput, "vf", FFArgParser.filter("crop", ":", 4))

    /** expects: `[factor: Float]` */
    object Speed : FFmpegArg(
        26,
        ArgOrder.AfterInput,
        "vf",
        FFArgParser.affix("setpts=" to "*PTS", FFArgParser.single)
    )

    /** expects: `[factor: Float]` */
    object Pitch : FFmpegArg(
        27,
        ArgOrder.AfterInput,
        "af",
        FFArgParser.affix(
            "asetrate=44100*pow(2," to "/12),aresample=44100", FFArgParser.single
        ),
    )

    /** expects: `[key=value]` */
    object MetadataByString : FFmpegArg(28, ArgOrder.AfterInput, "metadata", FFArgParser.join("="))

    /** expects: `[key1, value1, key2, value2, ...]` */
    object MetadataByPairs : FFmpegArg(29, ArgOrder.AfterInput, "metadata", { params ->
        val n = params.size
        (0 until (n - 1) step 2).joinToString(",") { i ->
            "${params[i]}=${params[i + 1]}"
        }
    })

    /** expects: `[map: Map<String, String>]` */
    object MetadataByMap : FFmpegArg(30, ArgOrder.AfterInput, "metadata", { params ->
        val m = params.firstOrNull() as? Map<*, *>
        m?.entries?.joinToString(",") { e ->
            "${e.key}=${e.value}"
        } ?: ""
    })

    /** expects: `[preset]` */
    object Preset : FFmpegArg(31, ArgOrder.AfterInput, "preset", FFArgParser.single)

    /** expects: `[crf]` */
    object Crf : FFmpegArg(32, ArgOrder.AfterInput, "crf", FFArgParser.single)

    /** expects: `[Amount]` */
    object MaxSizeByAmount : FFmpegArg(33, ArgOrder.AfterInput, "fs", FFArgParser.single)

    /** expects: `[size]` */
    object MaxSizeByString : FFmpegArg(34, ArgOrder.AfterInput, "fs", FFArgParser.single)

    /** expects: `[output]` */
    object Output : FFmpegArg(35, ArgOrder.Output, "", FFArgParser.single)
}

