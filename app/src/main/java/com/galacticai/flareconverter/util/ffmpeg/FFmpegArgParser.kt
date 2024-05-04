package com.galacticai.flareconverter.util.ffmpeg

import com.galacticai.flareconverter.models.ffmpeg.FFmpegCommand.Companion.formatDuration


typealias FFmpegArgParserFn = (input: Array<out Any>) -> String

object FFmpegArgParser {
    val none: FFmpegArgParserFn = { "" }
    val single: FFmpegArgParserFn = { it.firstOrNull()?.toString() ?: "" }

    fun join(separator: String, count: Int = Int.MAX_VALUE): FFmpegArgParserFn = { params ->
        params.take(count).joinToString(separator) { it.toString() }
    }

    val affix: (
        affix: Pair<String?, String?>,
        valueParser: FFmpegArgParserFn
    ) -> FFmpegArgParserFn = { affix, valueParser ->
        { params ->
            listOf(affix.first, valueParser(params), affix.second)
                .joinToString("") { it ?: "" }
        }
    }

    fun filter(name: String, sep: String, count: Int = Int.MAX_VALUE): FFmpegArgParserFn =
        affix("$name=" to "", join(sep, count))

    val timestamp: FFmpegArgParserFn = { params ->
        val ms = when (val first = params.firstOrNull()) {
            is Number -> first.toLong()
            is String -> first.toLongOrNull() ?: 0L
            else -> 0L
        }
        formatDuration(ms)
    }
}