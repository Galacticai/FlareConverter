package global.common.models

import java.io.Serializable

/** Building blocks for a command
 * - Example: "target --arg1 value1 -a2 value2.1 value2.2"
 * @param target The target executable
 * @param args The arguments for the executable */
open class Command(
    val target: String,
    val args: MutableList<Argument> = mutableListOf()
) : Serializable {
    constructor(target: String, vararg args: Argument) : this(target, args.toMutableList())

    val argsOnly get() = args.joinToString(" ") { it.toString() }

    override fun toString() = buildString {
        append(target)
        val a = argsOnly
        if (a.isNotEmpty()) append(" $a")
    }

    protected open fun arg(argument: Argument) = apply {
        args.removeIf { it.key == argument.key }
        args.add(argument)
    }

    protected open fun arg(key: Argument.Key, vararg value: String) = arg(Argument(key, *value))


    class Argument(
        val key: Key,
        val values: MutableList<String>
    ) : Serializable {

        constructor(key: Key, vararg values: String) :
                this(key, values.toMutableList())

        override fun toString() = buildString {
            append(key.toString())
            for (value in values) {
                append(' ')
                append(value.trim())
            }
        }

        companion object {
            const val PREFIX = "-"
            const val PREFIX_DOUBLE = "--"
        }

        data class Key(
            val name: String,
            val prefix: String = PREFIX_DOUBLE
        ) : Serializable {
            constructor(name: Char, prefix: String = PREFIX_DOUBLE) :
                    this(name.toString(), prefix)

            override fun toString() = "${prefix.trim()}${name.trim()}"

            companion object {
                fun String.toKey(prefix: String = PREFIX_DOUBLE) =
                    Key(this, prefix)
            }
        }
    }
}