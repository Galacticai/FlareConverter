package com.galacticai.flareconverter.models.ffmpeg.ff_command

import com.galacticai.flareconverter.models.ffmpeg.ff_command.args.FFprobeArg
import com.galacticai.flareconverter.models.ffmpeg.ff_command.args.sortedArgs
import com.galacticai.flareconverter.util.App


class FFprobeCommand : FFCommand<FFprobeArg> {
    constructor(vararg args: Argument) : super(App.ffprobe, *args)

    fun version() = apply { arg(FFprobeArg.Version) }
    fun help() = apply { arg(FFprobeArg.Help) }
    fun h() = help()
    fun input(value: String) = apply { arg(FFprobeArg.Input, value) }
    fun logLevel(value: LogLevel) = apply { arg(FFprobeArg.LogLevel, value.key) }
    fun hideBanner() = apply { arg(FFprobeArg.HideBanner) }
    fun printFormat(value: String) = apply { arg(FFprobeArg.PrintFormat, value) }
    fun outputFormat(value: String) = apply { arg(FFprobeArg.OutputFormat, value) }
    fun json() = printFormat("json")
    fun xml() = printFormat("xml")
    fun csv() = printFormat("csv")
    fun compact() = printFormat("compact")
    fun defaultFormat() = printFormat("default")

    /** @param value examples: `v:0` `a:0` `v` */
    fun selectStreams(value: String) =
            apply { arg(FFprobeArg.SelectStreams, value) }

    fun videoStream(index: Int = 0) = selectStreams("v:$index")
    fun audioStream(index: Int = 0) = selectStreams("a:$index")

    fun showStreams() =
            apply { arg(FFprobeArg.ShowStreams) }

    fun showStreamGroups() =
            apply { arg(FFprobeArg.ShowStreamGroups) }

    fun showFormat() =
            apply { arg(FFprobeArg.ShowFormat) }

    fun showPrograms() =
            apply { arg(FFprobeArg.ShowPrograms) }

    fun showChapters() =
            apply { arg(FFprobeArg.ShowChapters) }

    fun showPackets() =
            apply { arg(FFprobeArg.ShowPackets) }

    fun showFrames() =
            apply { arg(FFprobeArg.ShowFrames) }

    fun showData() =
            apply { arg(FFprobeArg.ShowData) }

    fun showError() =
            apply { arg(FFprobeArg.ShowError) }

    /** @param value example: `stream=codec_name,width,height`*/
    fun showEntries(value: String) =
            apply { arg(FFprobeArg.ShowEntries, value) }

    /** @param sections example: `"stream" to listOf("codec_name", "width", "height")` */
    fun showEntries(
        vararg sections: Pair<String, Iterable<String>>
    ) = showEntries(
        sections.joinToString(":") { (section, entries) ->
            "$section=${entries.joinToString(",")}"
        }
    )

    fun countFrames() =
            apply { arg(FFprobeArg.CountFrames) }

    fun countPackets() =
            apply { arg(FFprobeArg.CountPackets) }

    /** @param value example: `"10%+5"` or `"01:00%+10"` */
    fun readIntervals(value: String) =
            apply { arg(FFprobeArg.ReadIntervals, value) }

    fun showPixelFormats() =
            apply { arg(FFprobeArg.ShowPixelFormats) }

    fun showCodecs() =
            apply { arg(FFprobeArg.ShowCodecs) }

    fun showFormats() =
            apply { arg(FFprobeArg.ShowFormats) }

    fun showProtocols() =
            apply { arg(FFprobeArg.ShowProtocols) }

    companion object {
        fun Map<FFprobeArg, String>.toCommand(
            hideBanner: Boolean = true
        ): FFprobeCommand {
            val cmd = FFprobeCommand()
            if (FFprobeArg.HideBanner !in this && hideBanner) cmd.hideBanner()
            return cmd.apply {
                val keysSorted = this@toCommand.keys.sortedArgs()
                for (key in keysSorted) arg(key, this@toCommand[key]!!)
            }
        }
    }
}
