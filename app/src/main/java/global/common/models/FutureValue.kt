package global.common.models

import androidx.lifecycle.MutableLiveData
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
    class Pending<out V>(
        val addedAt: Date? = null
    ) : FutureValue<V>()

    /** Running in order to get the value */
    class Running<out V>(
        val job: Job? = null,
        val startedAt: Date? = null,
    ) : FutureValue<V>()

    /** Stopped intentionally
     * @param runtime the amount of time */
    class Stopped<out V>(
        val startedAt: Date? = null,
        val runtime: Duration? = null,
    ) : Failed<V>()

    /** Done, and the value is ready */
    class Finished<out V>(
        val value: V,
        val startedAt: Date? = null,
        val runtime: Duration? = null,
    ) : FutureValue<V>()

    /** Failed to get the value */
    sealed class Failed<out V> : FutureValue<V>() {
        /** Ran out of time
         * @param duration the amount of time */
        data class Timeout<out V>(
            val timeout: Duration,
            val startedAt: Date? = null,
        ) : Failed<V>()

        /** Something went wrong
         * @param error the error thrown by the runner */
        class Error<out V>(
            val error: Exception,
            val startedAt: Date? = null,
            val runtime: Duration? = null,
        ) : Failed<V>()
    }

    companion object {
        /** [MutableLiveData] with [Pending] as the initial value and [V] as the value type */
        fun <V> live(initial: FutureValue<V>) = MutableLiveData(initial)

        /** [MutableLiveData] with [Pending] as the initial value and [V] as the value type */
        fun <V> live() = MutableLiveData<FutureValue<V>>()
    }
}
