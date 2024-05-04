package global.common.models.command

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.Serializable

/** Building blocks for a command
 * - Example: "target --arg1 value1 -a2 value2.1 value2.2"
 * @param executable The target executable
 * @param args The arguments for the executable */
open class Command(
    val executable: String,
    open val args: MutableList<Argument> = mutableListOf()
) : Serializable {
    constructor(target: String, vararg args: Argument)
            : this(target, args.toMutableList())

    constructor(target: File, vararg args: Argument)
            : this(target.absolutePath, args.toMutableList())

    val tokens: List<String>
        get() = buildList {
            add(executable)
            addAll(
                args.flatMap { it.tokens }
                    .filterNot { it.isBlank() } //failsafe
            )
        }.filterNot { it.isBlank() }

    override fun toString() = tokens.joinToString(" ")

    fun build() = toString()

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

        val tokens: List<String>
            get() = buildList {
                add(key.toString())
                addAll(values)
            }.filterNot { it.isBlank() }

        override fun toString() = buildString {
            append(key.toString())
            for (value in values) {
                if (value.isBlank()) continue
                if (isNotEmpty()) append(' ')
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

            override fun toString() = if (name.isBlank()) "" else "${prefix.trim()}${name.trim()}"

            companion object {
                fun String.toKey(prefix: String = PREFIX_DOUBLE) =
                        Key(this, prefix)
            }
        }
    }

    private fun createProcess(
        config: RunConfig? = null
    ): Process {
        val tokens = this.tokens.filter { it.isNotBlank() }
        Log.d("Command tokens", tokens.toString())
        return ProcessBuilder(tokens).apply {
            config?.workingDir?.let { directory(it) }
            if (config?.environment != null)
                environment().putAll(config.environment)
        }.start()
    }

    open suspend fun run(
        config: RunConfig? = null,
    ): CommandResult = withContext(Dispatchers.IO) {
        var process: Process? = null
        try {
            process = createProcess(config)
            Log.d("Command createProcess", "$process")

            val outDeferred = async {
                process.inputStream.bufferedReader().use { it.readText() }
            }
            val errDeferred = async {
                process.errorStream.bufferedReader().use { it.readText() }
            }

            val result = CommandResult(
                process.waitFor(),
                outDeferred.await(),
                errDeferred.await(),
            )

            Log.d("Command exitCode", result.exitCode.toString())
            Log.d("Command output", result.output.take(100))
            Log.d("Command error", result.error.take(100))

            result
        } catch (e: Throwable) {
            Log.e("Command error", e.toString())
            CommandResult(1, "", e.toString())
        } finally {
            if (process?.isAlive == true) process.destroyForcibly()
        }
    }

    suspend fun <T> run(
        config: RunConfig? = null,
        onFail: ((Throwable) -> Unit)? = null,
        onSuccess: ((CommandResult) -> T)? = null,
    ): T? = try {
        val result = run(config)
        if (result.exitCode != 0) {
            throw CommandFailedException(toString(), result.exitCode, result.error)
        }
        return onSuccess?.invoke(result)
    } catch (ex: Throwable) {
        Log.e("Command error", ex.toString())
        onFail?.invoke(ex)
        null
    }


    open fun runStream(
        config: RunConfig? = null
    ): Flow<ProcessEvent> = channelFlow {
        val process = createProcess(config)
        try {
            coroutineScope {
                val outJob = launch(Dispatchers.IO) {
                    process.inputStream.bufferedReader().useLines { lines ->
                        for (line in lines) {
                            if (!currentCoroutineContext().isActive) return@useLines
                            send(ProcessEvent.OutputLine(line))
                        }
                    }
                }

                val errJob = launch(Dispatchers.IO) {
                    process.errorStream.bufferedReader().useLines { lines ->
                        for (line in lines) {
                            if (!currentCoroutineContext().isActive) return@useLines
                            send(ProcessEvent.ErrorLine(line))
                        }
                    }
                }

                outJob.join()
                errJob.join()

                val exitCode = process.waitFor()
                send(ProcessEvent.Completed(exitCode))
            }
        } catch (ex: Throwable) {
            Log.e("Command error", ex.toString())
        } finally {
            if (process.isAlive) process.destroyForcibly()
        }
    }

    suspend fun <T> runStream(
        config: RunConfig? = null,
        onFail: (suspend (Throwable) -> Unit)? = null,
        onProgress: (suspend (line: String) -> Unit)? = null,
        onSuccess: (suspend (CommandResult) -> T)? = null,
    ): T? = try {
        val output = StringBuilder()
        val error = StringBuilder()
        var exitCode = 0

        runStream(config).collect {
            when (it) {
                is ProcessEvent.OutputLine -> {
                    output.appendLine(it.line)
                    onProgress?.invoke(it.line)
                }

                is ProcessEvent.ErrorLine -> {
                    error.appendLine(it.line)
                    onProgress?.invoke(it.line)
                }

                is ProcessEvent.Completed -> {
                    exitCode = it.exitCode
                }
            }
        }

        val result = CommandResult(exitCode, output.toString(), error.toString())

        if (result.exitCode != 0) {
            throw CommandFailedException(
                this.toString(),
                result.exitCode, result.error
            )
        }

        onSuccess?.invoke(result)
    } catch (ex: Throwable) {
        Log.e("Command error", ex.toString())
        onFail?.invoke(ex)
        null
    }
}
