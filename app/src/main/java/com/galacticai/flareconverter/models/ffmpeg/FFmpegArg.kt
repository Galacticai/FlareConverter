package com.galacticai.flareconverter.models.ffmpeg

import com.galacticai.flareconverter.util.ffmpeg.FFmpegArgParser
import com.galacticai.flareconverter.util.ffmpeg.FFmpegArgParserFn
import global.common.models.amount.Amount
import global.common.util.TextUtil.sentenceCase

//? enum required for deterministic selection of args -
enum class FFmpegArg(
    val key: String,
    val parseCmd: FFmpegArgParserFn = FFmpegArgParser.join(" "),
    title: String? = null,
) {
    Version("version", FFmpegArgParser.none),
    Help("h", FFmpegArgParser.none),

    /** expects: `[input, output]` */
    Input("i", title = "Convert"),

    /** expects: `[count]` */
    FrameCount("vframes", FFmpegArgParser.single),

    /** expects: `[fps]` */
    FrameRate("r", FFmpegArgParser.single),

    /** expects: `[rate]` */
    SampleRate("ar", FFmpegArgParser.single),

    /** expects: `[count]` */
    Channels("ac", FFmpegArgParser.single),

    /** expects: `[bitrate]` */
    Bitrate("b", FFmpegArgParser.single),

    /** expects: `[bitrate]` */
    BitrateVideo("b:v", FFmpegArgParser.single),

    /** expects: `[bitrate]` */
    BitrateAudio("b:a", FFmpegArgParser.single),

    /** expects: `[codec]` */
    CodecVideo("c:v", FFmpegArgParser.single),
    CodecAudio("c:a", FFmpegArgParser.single),

    /** expects: `[width, height]` */
    Resolution("s", FFmpegArgParser.join("x", 2)),

    /** expects: `[duration: String]` */
    DurationByString("t", FFmpegArgParser.single),

    /** expects: `[milliseconds: Long]` */
    DurationByMs("t", FFmpegArgParser.timestamp),

    /** expects: `[format]` */
    Format("f", FFmpegArgParser.single),

    /** expects: `[milliseconds: Long]` */
    StartTimeByMs("ss", FFmpegArgParser.timestamp),

    /** expects: `[time: String]` */
    StartTimeByString("ss", FFmpegArgParser.single),

    /** expects: `[milliseconds: Long]` */
    EndTimeByMs("to", FFmpegArgParser.timestamp),

    /** expects: `[time: String]` */
    EndTimeByString("to", FFmpegArgParser.single),

    /** expects: `[width, height]` */
    Scale("vf", FFmpegArgParser.filter("scale", ":", 2)),

    /** expects: `[width, height, x, y]` */
    Crop("vf", FFmpegArgParser.filter("crop", ":", 4)),

    /** expects: `[factor: Float]` */
    Speed(
        "vf",
        FFmpegArgParser.affix("setpts=" to "*PTS", FFmpegArgParser.single)
    ),

    /** expects: `[factor: Float]` */
    Pitch(
        "af",
        FFmpegArgParser.affix(
            "asetrate=44100*pow(2," to "/12),aresample=44100", FFmpegArgParser.single
        ),
    ),

    /** expects: `[key=value]` */
    MetadataByString("metadata", FFmpegArgParser.join("=")),

    /** expects: `[key1, value1, key2, value2, ...]` */
    MetadataByPairs("metadata", { params ->
        val n = params.size
        (0 until (n - 1) step 2).joinToString(",") { i ->
            "${params[i]}=${params[i + 1]}"
        }
    }),

    /** expects: `[map: Map<String, String>]` */
    MetadataByMap("metadata", { params ->
        val m = params.firstOrNull() as? Map<*, *>
        m?.entries?.joinToString(",") { e ->
            "${e.key}=${e.value}"
        } ?: ""
    }),

    /** expects: `[preset]` */
    Preset("preset", FFmpegArgParser.single),

    /** expects: `[crf]` */
    Crf("crf", FFmpegArgParser.single),

    /** expects: `[Amount]` */
    MaxSizeByAmount("fs", FFmpegArgParser.single),

    /** expects: `[size]` */
    MaxSizeByString("fs", FFmpegArgParser.single);

    val title = title ?: name.sentenceCase
    override fun toString() = title
}

