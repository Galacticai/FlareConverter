package com.galacticai.flareconverter.models

import global.common.models.Command
import global.common.models.bitvalue.BitValue


class FFmpegCommand : Command {
    constructor(args: MutableList<Argument>) : super(EXECUTABLE, args)
    constructor(vararg args: Argument) : super(EXECUTABLE, *args)

    override fun arg(argument: Argument): FFmpegCommand {
        val i = super.args.indexOfFirst { it.key == argument.key }
        if (i >= 0) super.args[i] = argument
        else super.args.add(argument)
        return this
    }

    override fun arg(key: Argument.Key, vararg value: String) =
        arg(Argument(key, *value))

    private fun arg(key: String, vararg value: String) =
        arg(key.toKey(), *value)

    // ===== Arguments =====

    fun version() = apply { arg("version") }
    fun v() = version()
    fun help() = apply { arg("h") }
    fun h() = help()

    fun io(input: String, output: String? = null) = apply {
        if (output == null) arg("i", input)
        else arg("i", input, output)
    }

    fun frameCount(value: Int) =
        apply { arg("vframes", value.toString()) }

    fun frameRate(x: Int) =
        apply { arg("r", x.toString()) }

    fun sampleRate(x: Int) =
        apply { arg("ar", x.toString()) }

    fun channels(x: Int) =
        apply { arg("ac", x.toString()) }

    fun bitrateOverall(value: String) =
        apply { arg("b", value) }

    fun bitrateVideo(value: String) = bitrateType(value, "v")
    fun bitrateAudio(value: String) = bitrateType(value, "a")
    private fun bitrateType(value: String, type: String) =
        apply { arg("b:$type", value) }

    fun codecVideo(value: String) = codec(value, "v")
    fun codecAudio(value: String) = codec(value, "a")
    private fun codec(codec: String, type: String) =
        apply { arg("c:$type", codec) }

    fun resolution(width: Int, height: Int) =
        apply { arg("s", "${width}x$height") }

    fun duration(milliseconds: Long) = duration(formatDuration(milliseconds))
    fun duration(value: String) =
        apply { arg("t", value) }

    fun format(value: String) =
        apply { arg("f", value) }

    fun startTime(milliseconds: Long) = startTime(formatDuration(milliseconds))
    fun startTime(value: String) =
        apply { arg("ss", value) }

    fun endTime(milliseconds: Long) = endTime(formatDuration(milliseconds))
    fun endTime(value: String) =
        apply { arg("to", value) }

    fun filterVideo(value: String) = filter(value, "v")
    fun filterAudio(value: String) = filter(value, "a")
    private fun filter(value: String, type: String) =
        apply { arg("${type}f", value) }

    fun scale(width: Int, height: Int) =
        filterVideo("scale=$width:$height")

    fun crop(x: Int, y: Int, width: Int, height: Int) =
        filterVideo("crop=$x:$y:$width:$height")

    fun speed(value: Float) =
        filterVideo("setpts=$value*PTS")

    fun pitch(value: Float) =
        filterAudio("atempo=$value")

    fun speedVideoAudio(value: Float) =
        speed(value).pitch(1 / value)

    fun metadata(value: String) =
        apply { arg("metadata", value) }

    fun metadata(vararg pairs: Pair<String, String>) = metadata(
        pairs.joinToString(" ") {
            "${it.first}=\"${it.second}\""
        }
    )

    fun metadata(map: Map<String, String>) = metadata(*map.toList().toTypedArray())

    fun preset(value: String) =
        apply { arg("preset", value) }

    fun crf(value: Int) =
        apply { arg("crf", value.toString()) }

    // fun hideBanner() = apply { arg("hide_banner") }

    fun maxSize(value: BitValue) = maxSize(value.toString())
    fun maxSize(value: String) =
        apply { arg("fs", value) }


    companion object {
        const val EXECUTABLE = "ffmpeg"
        private fun String.toKey() = Argument.Key(this, Argument.PREFIX)

        fun formatDuration(duration: Long): String {
            val sTotal = duration / 1000
            val h = sTotal / 3600
            val m = (sTotal % 3600) / 60
            val s = sTotal % 60
            val ms = duration % 1000
            return "$h:$m:$s.$ms"
        }
    }
}
