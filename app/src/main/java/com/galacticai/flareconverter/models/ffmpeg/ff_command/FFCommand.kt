package com.galacticai.flareconverter.models.ffmpeg.ff_command

import com.galacticai.flareconverter.models.ffmpeg.ff_command.args.FFArg
import global.common.models.command.Command
import java.io.File

sealed class FFCommand<TArg : FFArg> : Command {
    constructor(executable: String, vararg args: Argument) : super(executable, *args)
    constructor(executable: File, vararg args: Argument) : this(executable.absolutePath, *args)

    protected val argsSortedMap = sortedMapOf<TArg, Argument>()
    override val args: MutableList<Argument>
        get() = argsSortedMap.values.toMutableList()

    //! intentional: not allowed: bypasses sort
    override fun arg(arg: Argument) = throw UnsupportedOperationException()

    fun arg(arg: TArg, vararg value: Any): FFCommand<TArg> = apply {
        argsSortedMap[arg] = Argument(
            arg.key,
            arg.parse(value)
        )
    }

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
