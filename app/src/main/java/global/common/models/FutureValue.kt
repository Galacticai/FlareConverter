package global.common.models

import androidx.lifecycle.MutableLiveData
import global.common.TimeUtils
import kotlinx.coroutines.Job
import java.time.Duration
import java.util.Date

/** Value to become available in the future
 * - Useful for task related stuff where the value is not immediately available
 * @param V Value type */
open class FutureValue<out V> {
    /** The value if this is [Finished] */
    val finishedValue: V? get() = (this as? Finished)?.value

    /** Waiting to get started later */
    data class Pending<out V>(
        val addedAt: Date? = null
    ) : FutureValue<V>()

    /** Running in order to get the value */
    data class Running<out V>(
        val job: Job? = null,
        val startedAt: Date? = null,
        val progress: Float? = null
    ) : FutureValue<V>()

    /** Stopped intentionally
     * @param runtime the amount of time */
    data class Stopped<out V>(
        val startedAt: Date? = null,
        val runtime: Duration? = null,
    ) : Failed<V>()

    /** Done, and the value is ready */
    data class Finished<out V>(
        val value: V,
        val startedAt: Date? = null,
        val runtime: Duration? = null,
    ) : FutureValue<V>()

    /** Failed to get the value */
    sealed class Failed<out V> : FutureValue<V>() {
        /** Ran out of time */
        data class Timeout<out V>(
            val timeout: Duration,
            val startedAt: Date? = null,
            val runtime: Duration? = null
        ) : Failed<V>()

        /** Something went wrong
         * @param error the error thrown by the runner */
        data class Error<out V>(
            val error: Exception,
            val startedAt: Date? = null,
            val runtime: Duration? = null,
        ) : Failed<V>()
    }

    companion object {
        /** [MutableLiveData] with [Pending] as the initial value and [V] as the value type */
        fun <V> live(initial: FutureValue<V>) = MutableLiveData(initial)

        /** [MutableLiveData] with [Pending] as the initial value and [V] as the value type */
        fun <V> live() = live(Pending<V>(Date()))

        fun <V> MutableLiveData<FutureValue<V>>.stop() {
            val current = value
            if (current !is Running) return
            current.job?.cancel()
            val stopped = Stopped<V>(
                current.startedAt,
                current.startedAt?.let { TimeUtils.getDuration(it) }
            )
            this.postValue(stopped)
        }


        /** take the first value of a pair */
        fun <V1, V2> FutureValue<Pair<V1, V2>>.pickFirst(): FutureValue<V1> =
            @Suppress("UNCHECKED_CAST")
            when (this) {
                is Finished<Pair<V1, V2>> -> Finished(value.first, startedAt, runtime)
                else -> this as FutureValue<V1>
            }

        /** take the second value of a pair */
        fun <V1, V2> FutureValue<Pair<V1, V2>>.pickSecond(): FutureValue<V2> =
            @Suppress("UNCHECKED_CAST")
            when (this) {
                is Finished<Pair<V1, V2>> -> Finished(value.second, startedAt, runtime)
                else -> this as FutureValue<V2>
            }

    }
}
