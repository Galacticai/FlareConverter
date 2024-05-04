package global.common.models.progressive

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import java.util.Date

/** value in progress */
open class Progressive<out V> {
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
        /** [MutableLiveData] with [Pending] as the initial value and [V] as the value type */
        fun <V> live(initial: Progressive<V>) = MutableLiveData(initial)

        /** [MutableLiveData] with [Pending] as the initial value and [V] as the value type */
        fun <V> live() = live<V>(Pending())

        fun <T> MutableLiveData<T>.postValueBy(
            converter: (current: T?) -> T
        ) = postValue(
            converter(value)
        )

        fun <V> MutableLiveData<Progressive<V>>.stop(cause: CancellationException? = null) {
            val current = value
            if (current !is Running) return
            current.job?.cancel(cause)
            val stopped = Failed<V>(
                Failed.Type.Stopped, cause,
                current.start, Date()
            )
            this.postValue(stopped)
        }
    }
}
