package com.galacticai.flareconverter.models.ffmpeg.ff_command.args

import global.common.util.TextUtil.sentenceCase

sealed class FFprobeArg(
    id: Int,
    order: ArgOrder,
    key: String,
    parseCmd: FFArgParserFn = FFArgParser.none,
    title: String = javaClass.simpleName.sentenceCase,
) : FFArg(id, order, key, parseCmd, title) {

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

    object Version : FFprobeArg(0, ArgOrder.First, "version")
    object Help : FFprobeArg(1, ArgOrder.First, "h")
    object Input : FFprobeArg(2, ArgOrder.Input, "i", FFArgParser.single)
    object LogLevel : FFprobeArg(3, ArgOrder.First, "v", FFArgParser.single)
    object HideBanner : FFprobeArg(4, ArgOrder.First, "hide_banner")
    object PrintFormat : FFprobeArg(5, ArgOrder.First, "print_format", FFArgParser.single)
    object OutputFormat : FFprobeArg(6, ArgOrder.First, "of", FFArgParser.single)
    object SelectStreams : FFprobeArg(7, ArgOrder.First, "select_streams", FFArgParser.single)
    object ShowStreams : FFprobeArg(8, ArgOrder.First, "show_streams")
    object ShowStreamGroups : FFprobeArg(9, ArgOrder.First, "show_stream_groups")
    object ShowFormat : FFprobeArg(10, ArgOrder.First, "show_format")
    object ShowPrograms : FFprobeArg(11, ArgOrder.First, "show_programs")
    object ShowChapters : FFprobeArg(12, ArgOrder.First, "show_chapters")
    object ShowPackets : FFprobeArg(13, ArgOrder.First, "show_packets")
    object ShowFrames : FFprobeArg(14, ArgOrder.First, "show_frames")
    object ShowData : FFprobeArg(15, ArgOrder.First, "show_data")
    object ShowError : FFprobeArg(16, ArgOrder.First, "show_error")
    object ShowEntries : FFprobeArg(17, ArgOrder.First, "show_entries", FFArgParser.single)
    object CountFrames : FFprobeArg(18, ArgOrder.First, "count_frames")
    object CountPackets : FFprobeArg(19, ArgOrder.First, "count_packets")
    object ReadIntervals : FFprobeArg(20, ArgOrder.First, "read_intervals", FFArgParser.single)
    object ShowPixelFormats : FFprobeArg(21, ArgOrder.First, "show_pixel_formats")
    object ShowCodecs : FFprobeArg(22, ArgOrder.First, "codecs")
    object ShowFormats : FFprobeArg(23, ArgOrder.First, "formats")
    object ShowProtocols : FFprobeArg(24, ArgOrder.First, "protocols")
}
