package com.galacticai.flareconverter.models.ffmpeg.ff_command.args

import global.common.models.command.Command
import global.common.util.TextUtil.sentenceCase

sealed class FFArg(
    val order: ArgOrder,
    /** command arg key (not an identifier) */
    val argKey: String,
    val parse: FFArgParserFn,
    title: String? = null,
) : Comparable<FFArg> {
    abstract val id: Int

    override fun compareTo(other: FFArg): Int = when {
        this.order != other.order -> order.compareTo(other.order)
        else -> id.compareTo(other.id)
    }

    val key =
            if (argKey.isBlank()) Command.Argument.Key.empty
            else Command.Argument.Key(argKey, Command.Argument.PREFIX)
    val name = this::class.simpleName!!.sentenceCase
    val title = title ?: name
    override fun toString() = title
}

fun <T : FFArg> Iterable<T>.sortedArgs(): List<T> =
        this.sortedWith(compareBy({ it.order }, { it.id }))
