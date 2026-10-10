package com.galacticai.flareconverter.models.ffmpeg.ff_command.args

sealed class FFmpegArg(
    order: ArgOrder,
    argKey: String,
    parseCmd: FFArgParserFn = FFArgParser.join(" "),
    title: String? = null,
) : FFArg(order, argKey, parseCmd, title) {

    override val id = entries.indexOf(this)

    companion object {
        fun from(id: Int) = entries.find { id == it.id }

        val entries: List<FFmpegArg> by lazy {
            listOf(
                Version, Help, HideBanner, Codecs, Encoders, Formats, Progress, Input, FrameCount,
                FrameRate, SampleRate, Channels, Bitrate, BitrateVideo, BitrateAudio, CodecVideo,
                CodecAudio, Resolution, DurationByString, DurationByMs, Format, StartTimeByMs,
                StartTimeByString, EndTimeByMs, EndTimeByString, Scale, Crop, Speed, Pitch,
                MetadataByString, MetadataByPairs, MetadataByMap, Preset, Crf, MaxSizeByAmount,
                MaxSizeByString, Output,
            )
        }
    }

    object Version : FFmpegArg(ArgOrder.First, "version", FFArgParser.none)
    object Help : FFmpegArg(ArgOrder.First, "h", FFArgParser.none)
    object HideBanner : FFmpegArg(ArgOrder.First, "hide_banner", FFArgParser.none)
    object Stats : FFmpegArg(ArgOrder.First, "stats", FFArgParser.none)
    object Codecs : FFmpegArg(ArgOrder.First, "codecs", FFArgParser.none)
    object Encoders : FFmpegArg(ArgOrder.First, "encoders", FFArgParser.none)
    object Formats : FFmpegArg(ArgOrder.First, "formats", FFArgParser.none)
    object Progress : FFmpegArg(ArgOrder.First, "progress", FFArgParser.none)

    /** expects [log level] */
    object LogLevel : FFmpegArg(ArgOrder.First, "v", FFArgParser.single)

    /** expects: `[input]` */
    object Input : FFmpegArg(ArgOrder.Input, "i", FFArgParser.single, title = "Convert")

    /** expects: `[count]` */
    object FrameCount : FFmpegArg(ArgOrder.AfterInput, "vframes", FFArgParser.single)

    /** expects: `[fps]` */
    object FrameRate : FFmpegArg(ArgOrder.AfterInput, "r", FFArgParser.single)

    /** expects: `[rate]` */
    object SampleRate : FFmpegArg(ArgOrder.AfterInput, "ar", FFArgParser.single)

    /** expects: `[count]` */
    object Channels : FFmpegArg(ArgOrder.AfterInput, "ac", FFArgParser.single)

    /** expects: `[bitrate]` */
    object Bitrate : FFmpegArg(ArgOrder.AfterInput, "b", FFArgParser.single)

    /** expects: `[bitrate]` */
    object BitrateVideo : FFmpegArg(ArgOrder.AfterInput, "b:v", FFArgParser.single)

    /** expects: `[bitrate]` */
    object BitrateAudio : FFmpegArg(ArgOrder.AfterInput, "b:a", FFArgParser.single)

    /** expects: `[codec]` */
    object CodecVideo : FFmpegArg(ArgOrder.AfterInput, "c:v", FFArgParser.single)
    object CodecAudio : FFmpegArg(ArgOrder.AfterInput, "c:a", FFArgParser.single)

    /** expects: `[width, height]` */
    object Resolution : FFmpegArg(ArgOrder.AfterInput, "s", FFArgParser.join("x", 2))

    /** expects: `[duration: String]` */
    object DurationByString : FFmpegArg(ArgOrder.AfterInput, "t", FFArgParser.single)

    /** expects: `[milliseconds: Long]` */
    object DurationByMs : FFmpegArg(ArgOrder.AfterInput, "t", FFArgParser.timestamp)

    /** expects: `[format]` */
    object Format : FFmpegArg(ArgOrder.AfterInput, "f", FFArgParser.single)

    /** expects: `[milliseconds: Long]` */
    object StartTimeByMs : FFmpegArg(ArgOrder.AfterInput, "ss", FFArgParser.timestamp)

    /** expects: `[time: String]` */
    object StartTimeByString : FFmpegArg(ArgOrder.AfterInput, "ss", FFArgParser.single)

    /** expects: `[milliseconds: Long]` */
    object EndTimeByMs : FFmpegArg(ArgOrder.AfterInput, "to", FFArgParser.timestamp)

    /** expects: `[time: String]` */
    object EndTimeByString : FFmpegArg(ArgOrder.AfterInput, "to", FFArgParser.single)

    /** expects: `[width, height]` */
    object Scale : FFmpegArg(ArgOrder.AfterInput, "vf", FFArgParser.filter("scale", ":", 2))

    /** expects: `[width, height, x, y]` */
    object Crop : FFmpegArg(ArgOrder.AfterInput, "vf", FFArgParser.filter("crop", ":", 4))

    /** expects: `[factor: Float]` */
    object Speed : FFmpegArg(
        ArgOrder.AfterInput,
        "vf",
        FFArgParser.affix("setpts=" to "*PTS", FFArgParser.single)
    )

    /** expects: `[factor: Float]` */
    object Pitch : FFmpegArg(
        ArgOrder.AfterInput,
        "af",
        FFArgParser.affix(
            "asetrate=44100*pow(2," to "/12),aresample=44100", FFArgParser.single
        ),
    )

    /** expects: `[key=value]` */
    object MetadataByString : FFmpegArg(ArgOrder.AfterInput, "metadata", FFArgParser.join("="))

    /** expects: `[key1, value1, key2, value2, ...]` */
    object MetadataByPairs : FFmpegArg(ArgOrder.AfterInput, "metadata", { params ->
        val n = params.size
        (0 until (n - 1) step 2).joinToString(",") { i ->
            "${params[i]}=${params[i + 1]}"
        }
    })

    /** expects: `[map: Map<String, String>]` */
    object MetadataByMap : FFmpegArg(ArgOrder.AfterInput, "metadata", { params ->
        val m = params.firstOrNull() as? Map<*, *>
        m?.entries?.joinToString(",") { e ->
            "${e.key}=${e.value}"
        } ?: ""
    })

    /** expects: `[preset]` */
    object Preset : FFmpegArg(ArgOrder.AfterInput, "preset", FFArgParser.single)

    /** expects: `[crf]` */
    object Crf : FFmpegArg(ArgOrder.AfterInput, "crf", FFArgParser.single)

    /** expects: `[Amount]` */
    object MaxSizeByAmount : FFmpegArg(ArgOrder.AfterInput, "fs", FFArgParser.single)

    /** expects: `[size]` */
    object MaxSizeByString : FFmpegArg(ArgOrder.AfterInput, "fs", FFArgParser.single)

    /** expects: `[output]` */
    object Output : FFmpegArg(ArgOrder.Output, "", FFArgParser.single)
}
