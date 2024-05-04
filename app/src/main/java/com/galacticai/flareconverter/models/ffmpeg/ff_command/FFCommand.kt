package com.galacticai.flareconverter.models.ffmpeg.ff_command

import com.galacticai.flareconverter.models.ffmpeg.ff_command.args.FFArg
import global.common.models.command.Command
import java.io.File

sealed class FFCommand<TArg : FFArg> : Command {
    constructor(executable: String, vararg args: Argument) : super(executable, *args)
    constructor(executable: File, vararg args: Argument) : this(executable.absolutePath, *args)

    abstract fun arg(arg: TArg, vararg value: Any): FFCommand<TArg>
    abstract fun findArg(key: String): TArg?
    abstract fun sortArgs(arg: TArg): Int

    override val args: MutableList<Argument>
        get() = super.args.apply {
            sortBy { argument ->
                findArg(argument.key.name)?.let { sortArgs(it) } ?: Int.MAX_VALUE
            }
        }

    override fun arg(argument: Argument): FFCommand<TArg> {
        val i = super.args.indexOfFirst { it.key == argument.key }
        if (i >= 0) super.args[i] = argument
        else super.args.add(argument)
        return this
    }

    override fun arg(key: Argument.Key, vararg value: String): FFCommand<TArg> =
            arg(Argument(key, *value))

    companion object {
        private fun Long.pad(length: Int = 2) =
                this.toString().padStart(length, '0')

        fun formatDuration(duration: Long): String {
            val sTotal = duration / 1000
            val h = sTotal / 3600
            val m = (sTotal / 60) % 60
            val s = sTotal % 60
            val ms = duration % 1000

            return "${h.pad()}:${m.pad()}:${s.pad()}.${ms.pad(3)}"
        }
    }
}
