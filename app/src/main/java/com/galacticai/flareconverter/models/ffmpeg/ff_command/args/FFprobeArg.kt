package com.galacticai.flareconverter.models.ffmpeg.ff_command.args

sealed class FFprobeArg(
    order: ArgOrder,
    argKey: String,
    parseCmd: FFArgParserFn = FFArgParser.none,
    title: String? = null,
) : FFArg(order, argKey, parseCmd, title) {

    override val id = entries.indexOf(this)

    companion object {
        fun from(id: Int) = entries.find { id == it.id }

        val entries: List<FFprobeArg> by lazy {
            listOf(
                Version, Help, Input, LogLevel, HideBanner, PrintFormat, OutputFormat,
                SelectStreams, ShowStreams, ShowStreamGroups, ShowFormat, ShowPrograms,
                ShowChapters, ShowPackets, ShowFrames, ShowData, ShowError, ShowEntries,
                CountFrames, CountPackets, ReadIntervals, ShowPixelFormats, ShowCodecs,
                ShowFormats, ShowProtocols,
            )
        }
    }

    object Version : FFprobeArg(ArgOrder.First, "version")
    object Help : FFprobeArg(ArgOrder.First, "h")
    object Input : FFprobeArg(ArgOrder.Input, "i", FFArgParser.single)
    object LogLevel : FFprobeArg(ArgOrder.First, "v", FFArgParser.single)
    object HideBanner : FFprobeArg(ArgOrder.First, "hide_banner")
    object PrintFormat : FFprobeArg(ArgOrder.First, "print_format", FFArgParser.single)
    object OutputFormat : FFprobeArg(ArgOrder.First, "of", FFArgParser.single)
    object SelectStreams : FFprobeArg(ArgOrder.First, "select_streams", FFArgParser.single)
    object ShowStreams : FFprobeArg(ArgOrder.First, "show_streams")
    object ShowStreamGroups : FFprobeArg(ArgOrder.First, "show_stream_groups")
    object ShowFormat : FFprobeArg(ArgOrder.First, "show_format")
    object ShowPrograms : FFprobeArg(ArgOrder.First, "show_programs")
    object ShowChapters : FFprobeArg(ArgOrder.First, "show_chapters")
    object ShowPackets : FFprobeArg(ArgOrder.First, "show_packets")
    object ShowFrames : FFprobeArg(ArgOrder.First, "show_frames")
    object ShowData : FFprobeArg(ArgOrder.First, "show_data")
    object ShowError : FFprobeArg(ArgOrder.First, "show_error")
    object ShowEntries : FFprobeArg(ArgOrder.First, "show_entries", FFArgParser.single)
    object CountFrames : FFprobeArg(ArgOrder.First, "count_frames")
    object CountPackets : FFprobeArg(ArgOrder.First, "count_packets")
    object ReadIntervals : FFprobeArg(ArgOrder.First, "read_intervals", FFArgParser.single)
    object ShowPixelFormats : FFprobeArg(ArgOrder.First, "show_pixel_formats")
    object ShowCodecs : FFprobeArg(ArgOrder.First, "codecs")
    object ShowFormats : FFprobeArg(ArgOrder.First, "formats")
    object ShowProtocols : FFprobeArg(ArgOrder.First, "protocols")
}
