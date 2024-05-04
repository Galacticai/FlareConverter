package global.common.models.progressive

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.Date

/** value in progress */
open class Progressive<out V> {
    /** @return [Done.value] or null if not [Done]  */
    val done get() = (this as? Done<V>)?.value

    class Pending<out V> : Progressive<V>()

    class Running<out V>(
        var type: Type = Type.Main,
        /** null: continuous progress / unspecified percentage */
        var progress: Float? = null,
        val job: Job?,
        override val start: Date,
    ) : Progressive<V>(), TimedStart {
        enum class Type {
            /** before [Main] */
            Pre,
            Main,

            /** after [Main] */
            Post
        }

        fun stop(cause: CancellationException? = null) =
            job?.cancel(cause)
    }

    class Done<out V>(
        val value: V,
        override val start: Date,
        override val end: Date,
    ) : Progressive<V>(), Timed {
        companion object {
            fun <V> from(running: Running<V>, value: V) =
                Done(value, running.start, Date())
        }
    }


    open class Failed<out V>(
        val type: Type = Type.Error,
        val cause: Exception? = null,
        override val start: Date,
        override val end: Date,
    ) : Progressive<V>(), Timed {
        enum class Type {
            /** stopped intentionally */
            Stopped,
            Timeout,
            Error
        }

        companion object {
            fun <V> from(
                running: Running<V>,
                type: Type = Type.Error,
                cause: Exception? = null
            ) = Failed<V>(
                type, cause,
                running.start, Date()
            )
        }
    }

    /**
     * convert [Progressive] value ([V]) to something else ([VNew])
     * @param converter called on [Done] to convert [V] to [VNew]
     */
    @Suppress("UNCHECKED_CAST") //? type is ensured by `when`+`converter`
    fun <VNew> convert(
        converter: (value: V) -> VNew
    ): Progressive<VNew> {
        return when (val current = this) {
            is Done -> {
                val value = converter(current.value)
                Done(value, current.start, current.end)
            }
            //? others don't have a value : can simply cast
            else -> this as Progressive<VNew>
        }
    }

    companion object {
        /** [MutableStateFlow] with [Pending] as the initial value and [V] as the value type */
        fun <V> flow(initial: Progressive<V>) = MutableStateFlow(initial)

        /** [MutableStateFlow] with [Pending] as the initial value and [V] as the value type */
        fun <V> flow() = flow<V>(Pending())

        fun <V> MutableStateFlow<Progressive<V>>.stop(
            cause: CancellationException? = null
        ) = update {
            val current = value
            if (current !is Running) return@update value
            current.job?.cancel(cause)
            val stopped = Failed<V>(
                Failed.Type.Stopped, cause,
                current.start, Date()
            )
            stopped
        }
    }
}
