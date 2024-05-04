package com.galacticai.flareconverter.models.ffmpeg

import global.common.models.Command
import global.common.models.amount.Amount


//@Deprecated("")
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

    fun arg(arg: FFmpegArg, vararg value: Any) =
        arg(arg.toKey(), arg.parse(value))

    // ===== Arguments =====

    fun version() = apply { arg(FFmpegArg.Version) }
    fun v() = version()
    fun help() = apply { arg(FFmpegArg.Help) }
    fun h() = help()

    fun io(input: String, output: String? = null) = apply {
        if (output == null) arg(FFmpegArg.Input, input)
        else arg(FFmpegArg.Input, input, output)
    }

    fun frameCount(value: Int) =
        apply { arg(FFmpegArg.FrameCount, value) }

    fun frameRate(x: Int) =
        apply { arg(FFmpegArg.FrameRate, x) }

    fun sampleRate(x: Int) =
        apply { arg(FFmpegArg.SampleRate, x) }

    fun channels(x: Int) =
        apply { arg(FFmpegArg.Channels, x) }

    fun bitrateOverall(value: String) =
        apply { arg(FFmpegArg.Bitrate, value) }

    fun bitrateVideo(value: String) =
        apply { arg(FFmpegArg.BitrateVideo, value) }

    fun bitrateAudio(value: String) =
        apply { arg(FFmpegArg.BitrateAudio, value) }

    fun codecVideo(value: String) =
        apply { arg(FFmpegArg.CodecVideo, value) }

    fun codecAudio(value: String) =
        apply { arg(FFmpegArg.CodecAudio, value) }

    fun resolution(width: Int, height: Int) =
        apply { arg(FFmpegArg.Resolution, width, height) }

    fun duration(value: String) =
        apply { arg(FFmpegArg.DurationByString, value) }

    fun duration(milliseconds: Long) =
        apply { arg(FFmpegArg.DurationByMs, milliseconds) }

    fun format(value: String) =
        apply { arg(FFmpegArg.Format, value) }

    fun startTime(milliseconds: Long) =
        apply { arg(FFmpegArg.StartTimeByMs, milliseconds) }

    fun startTime(value: String) =
        apply { arg(FFmpegArg.StartTimeByString, value) }

    fun endTime(milliseconds: Long) =
        apply { arg(FFmpegArg.EndTimeByMs, milliseconds) }

    fun endTime(value: String) =
        apply { arg(FFmpegArg.EndTimeByString, value) }

//    fun filterVideo(value: String) =
//        apply { arg(FFmpegArg.FilterVideo, value) }
//
//    fun filterAudio(value: String) =
//        apply { arg(FFmpegArg.FilterAudio, value) }

    fun scale(width: Int, height: Int) =
        apply { arg(FFmpegArg.Scale, width, height) }

    fun crop(x: Int, y: Int, width: Int, height: Int) =
        apply { arg(FFmpegArg.Crop, x, y, width, height) }

    fun speed(value: Float) =
        apply { arg(FFmpegArg.Speed, value) }

    fun pitch(value: Float) =
        apply { arg(FFmpegArg.Pitch, value) }

    fun speedPitch(value: Float) =
        speed(value).pitch(1 / value)

    fun metadata(value: String) =
        apply { arg(FFmpegArg.MetadataByString, value) }

    fun metadata(vararg pairs: Pair<String, String>) =
        apply { arg(FFmpegArg.MetadataByPairs, pairs) }

    fun metadata(map: Map<String, String>) =
        apply { arg(FFmpegArg.MetadataByMap, map) }

    fun preset(value: String) =
        apply { arg(FFmpegArg.Preset, value) }

    fun crf(value: Int) =
        apply { arg(FFmpegArg.Crf, value) }

    // fun hideBanner() = apply { arg("hide_banner") }

    fun maxSize(value: Amount) =
        apply { arg(FFmpegArg.MaxSizeByAmount, value) }

    fun maxSize(value: String) =
        apply { arg(FFmpegArg.MaxSizeByString, value) }


    companion object {
        const val EXECUTABLE = "ffmpeg"
        private fun FFmpegArg.toKey() = Argument.Key(this.key, Argument.PREFIX)

        fun formatDuration(duration: Long): String {
            val sTotal = duration / 1000
            val h = sTotal / 3600
            val m = (sTotal / 60) % 60
            val s = sTotal % 60
            val ms = duration % 1000

            val hStr = h.toString().padStart(2, '0')
            val mStr = m.toString().padStart(2, '0')
            val sStr = s.toString().padStart(2, '0')
            val msStr = ms.toString().padStart(3, '0')

            return "$hStr:$mStr:$sStr.$msStr"
        }
    }
}
