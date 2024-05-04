package com.galacticai.flareconverter.models.ffmpeg.ff_command.args

sealed class FFArg(
    val id: Int,
    val order: ArgOrder,
    /** command arg key (not an identifier) */
    val key: String,
    val parse: FFArgParserFn,
    val title: String,
) {
    val name: String = javaClass.simpleName

    override fun toString() = title
}

fun <T : FFArg> Iterable<T>.sortedArgs(): List<T> =
        this.sortedWith(compareBy({ it.order }, { it.id }))
