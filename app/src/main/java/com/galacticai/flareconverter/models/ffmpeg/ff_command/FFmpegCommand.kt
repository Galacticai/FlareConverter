package com.galacticai.flareconverter.models.ffmpeg.ff_command

import com.galacticai.flareconverter.models.ffmpeg.ff_command.args.FFmpegArg
import com.galacticai.flareconverter.models.ffmpeg.ff_command.args.sortedArgs
import com.galacticai.flareconverter.models.ffmpeg.ff_command.stats.FFmpegStats
import com.galacticai.flareconverter.models.ffmpeg.ff_command.stats.FFmpegStatsBuilder
import com.galacticai.flareconverter.util.App
import global.common.models.amount.Amount
import global.common.models.command.CommandResult
import global.common.models.command.RunConfig
import global.common.models.progressive.Progressive
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.Date
import kotlin.time.Duration


class FFmpegCommand : FFCommand<FFmpegArg> {
    constructor(vararg args: Argument) : super(App.ffmpeg, *args)

    override fun arg(arg: FFmpegArg, vararg value: Any) =
            apply { arg(arg.toKey(), arg.parse(value)) }

    override fun findArg(key: String): FFmpegArg? =
            FFmpegArg.entries.find { it.key == key }

    override fun sortArgs(arg: FFmpegArg): Int =
            arg.order.ordinal * 1000 + arg.id

    // ===== Arguments =====

    fun version() = apply { arg(FFmpegArg.Version) }
    fun v() = version()
    fun help() = apply { arg(FFmpegArg.Help) }
    fun h() = help()
    fun stats() = apply { arg(FFmpegArg.Stats) }
    fun logLevel(value: LogLevel) = apply { arg(FFmpegArg.LogLevel, value.key) }
    fun hideBanner() = apply { arg(FFmpegArg.HideBanner) }
    fun codecs() = apply { arg(FFmpegArg.Codecs) }
    fun formats() = apply { arg(FFmpegArg.Formats) }

    fun input(input: String) =
            apply { arg(FFmpegArg.Input, input) }

    fun output(output: String) =
            apply { arg(FFmpegArg.Output, output) }

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

    suspend fun <T> runStreamStats(
        config: RunConfig? = null,
        onFail: (suspend (Throwable) -> Unit)? = null,
        onProgressStats: (suspend (FFmpegStats) -> Unit)? = null,
        onSuccess: (suspend (CommandResult) -> T?)? = null,
    ): T? {
        val statsBuilder = FFmpegStatsBuilder {
            onProgressStats?.invoke(it)
        }
        return super.runStream(
            config, onFail,
            onProgress = { statsBuilder.append(it) },
            onSuccess,
        )
    }

    suspend fun <T> runFlow(
        flow: MutableStateFlow<Progressive<T>>,
        job: Job? = null,
        config: RunConfig? = null,
        onFail: (suspend (Throwable) -> Unit)? = null,
        /** required to calculate [Progressive.Running.progress] */
        getDuration: (() -> Duration)? = null,
        onProgressStats: (suspend (FFmpegStats) -> Unit)? = null,
        onSuccess: (suspend (CommandResult) -> T?)? = null,
    ): T? {
        val startedAt = Date()
        flow.update {
            Progressive.Running(
                Progressive.Running.Type.Main,
                progress = null, job, startedAt,
            )
        }
        return runStreamStats(
            config,
            onFail = { ex ->
                flow.update {
                    Progressive.Failed(
                        Progressive.Failed.Type.Error,
                        ex, startedAt, Date()
                    )
                }
                onFail?.invoke(ex)
            },
            onProgressStats = { stats ->
                flow.update {
                    val running = it as Progressive.Running
                    running.progress =
                            if (getDuration == null) null
                            else stats.getProgress(getDuration())
                    running
                }
                onProgressStats?.invoke(stats)
            }
        ) {
            val result = onSuccess?.invoke(it)
                ?: return@runStreamStats null
            flow.update {
                Progressive.Done(result, startedAt, Date())
            }
            result
        }
    }

    companion object {
        private fun FFmpegArg.toKey() = Argument.Key(
            this.key,
            if (this.key.isBlank()) "" else Argument.PREFIX
        )

        fun Map<FFmpegArg, String>.toCommand(
            minimal: Boolean = true,
            progress: Boolean = true,
        ): FFmpegCommand {
            val cmd = FFmpegCommand()
            if (minimal) {
                if (FFmpegArg.HideBanner !in this) cmd.hideBanner()
                if (FFmpegArg.LogLevel !in this) cmd.logLevel(LogLevel.Error)
            }
            if (progress) {
                if (FFmpegArg.Stats !in this) cmd.stats()
            }
            return cmd.apply {
                val keysSorted = keys.sortedArgs()
                for (key in keysSorted) arg(key, this@toCommand[key]!!)
            }
        }
    }
}

