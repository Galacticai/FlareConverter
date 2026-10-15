package com.galacticai.flareconverter.models.ffmpeg.ff_command.args

import global.common.models.command.Command
import global.common.util.TextUtil.sentenceCase

sealed class FFArg(
    val order: ArgOrder,
    /** command arg key (not an identifier) */
    argKey: String,
    val parse: FFArgParserFn,
    title: String? = null,
) : Comparable<FFArg> {
    val id = listOfNotNull(
        this::class.java.simpleName, order.ordinal, argKey, title,
    ).joinToString("-")

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

    companion object {
        inline fun <reified T : FFArg> all(): Map<String, T> =
                T::class.sealedSubclasses
                    .mapNotNull { it.objectInstance }
                    .sortedBy { it.order }
                    .associateBy { it.id }

        inline fun <reified T : FFArg> from(id: String) = all<T>()[id]

        fun <T : FFArg> Iterable<T>.sortedArgs(): List<T> =
                this.sortedWith(compareBy({ it.order }, { it.id }))
    }
}

