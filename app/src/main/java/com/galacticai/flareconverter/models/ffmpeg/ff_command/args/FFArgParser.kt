package com.galacticai.flareconverter.models.ffmpeg.ff_command.args

import com.galacticai.flareconverter.models.ffmpeg.ff_command.FFCommand


typealias FFArgParserFn = (input: Array<out Any>) -> String

object FFArgParser {
    val none: FFArgParserFn = { "" }
    val single: FFArgParserFn = { it.firstOrNull()?.toString() ?: "" }

    fun join(separator: String, count: Int = Int.MAX_VALUE): FFArgParserFn = { params ->
        params.take(count).joinToString(separator) { it.toString() }
    }

    val affix: (
        affix: Pair<String?, String?>,
        valueParser: FFArgParserFn
    ) -> FFArgParserFn = { affix, valueParser ->
        { params ->
            listOf(affix.first, valueParser(params), affix.second)
                .joinToString("") { it ?: "" }
        }
    }

    fun filter(name: String, sep: String, count: Int = Int.MAX_VALUE): FFArgParserFn =
            affix("$name=" to "", join(sep, count))

    val timestamp: FFArgParserFn = { params ->
        val ms = when (val first = params.firstOrNull()) {
            is Number -> first.toLong()
            is String -> first.toLongOrNull() ?: 0L
            else -> 0L
        }
        FFCommand.formatDuration(ms)
    }
}