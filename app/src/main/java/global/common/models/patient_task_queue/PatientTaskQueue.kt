package global.common.models.patient_task_queue

import android.util.Log
import global.common.models.progressive.Progressive
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.time.Duration
import java.util.Date
import java.util.SortedMap
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.milliseconds

/** Queue that runs tasks when calling [run] but only triggers the event of a task when it is the first one in the queue */
class PatientTaskQueue<K : Comparable<K>, V> {
    abstract class PatientTaskEvents<K : Comparable<K>, V> {
        abstract fun onAllListener(ev: PatientTaskEvent<K, V>)
        abstract fun onAddListener(ev: PatientTaskEvent<K, V>)
        abstract fun onStartListener(ev: PatientTaskEvent<K, V>)
        abstract fun onStopListener(ev: PatientTaskEvent<K, V>)
        abstract fun onDoneListener(ev: PatientTaskEvent<K, V>)
        abstract fun onTimeoutListener(ev: PatientTaskEvent<K, V>)
        abstract fun onErrorListener(ev: PatientTaskEvent<K, V>)
        abstract fun onFinallyListener(ev: PatientTaskEvent<K, V>)
    }

    constructor()
    constructor(
        onAllListener: PatientTaskHandler<K, V>? = null,
        onAddListener: PatientTaskHandler<K, V>? = null,
        onStartListener: PatientTaskHandler<K, V>? = null,
        onStopListener: PatientTaskHandler<K, V>? = null,
        onDoneListener: PatientTaskHandler<K, V>? = null,
        onTimeoutListener: PatientTaskHandler<K, V>? = null,
        onErrorListener: PatientTaskHandler<K, V>? = null,
        onFinallyListener: PatientTaskHandler<K, V>? = null,
    ) : this() {
        this.onAllListener = onAllListener
        this.onAddListener = onAddListener
        this.onStartListener = onStartListener
        this.onTimeoutListener = onTimeoutListener
        this.onStopListener = onStopListener
        this.onErrorListener = onErrorListener
        this.onDoneListener = onDoneListener
        this.onFinallyListener = onFinallyListener
    }

    constructor(events: PatientTaskEvents<K, V>) : this(
        events::onAllListener,
        events::onAddListener,
        events::onStartListener,
        events::onStopListener,
        events::onDoneListener,
        events::onTimeoutListener,
        events::onErrorListener,
        events::onFinallyListener,
    )

    private val tasks: SortedMap<K, TaskInfo<V>> = sortedMapOf()


    /** Get a [TaskInfo] of the provided [key] or throws [NoSuchElementException] if not found
     * @return [TaskInfo] corresponding to [key]
     * @exception NoSuchElementException if the task was not found */
    private fun getOrThrow(key: K): TaskInfo<V> =
        tasks[key] ?: throw NoSuchElementException("Task not found: $key")


    /** Add a task
     * @return true if added
     * @exception IllegalArgumentException if timeout is zero or negative */
    fun add(key: K, timeout: Duration, task: PatientTask<V>): Boolean {
        if (timeout.isZero || timeout.isNegative)
            throw IllegalArgumentException("Timeout duration must be positive and non zero")

        if (tasks.containsKey(key)) return false

        val info = TaskInfo(task, timeout, Progressive.Pending())
        tasks[key] = info
        val ev = PatientTaskEvent(key, info)
        onAddListener?.invoke(ev)
        onAllListener?.invoke(ev)
        return true
    }

    /** Check whether a task of the provided [key] is currently pending
     * @return true if pending */
    fun isPending(key: K): Boolean = tasks[key]?.progressive is Progressive.Pending

    /** Check whether a task of the provided [key] is currently running
     * @return true if running */
    fun isRunning(key: K): Boolean = tasks[key]?.progressive is Progressive.Running

    /** Check whether a task of the provided [key] has either finished, stopped, or failed
     * - [Progressive.Done] : Success
     * - [Progressive.Failed] : Error or Timeout
     * @return true if one of the above types
     * @exception NoSuchElementException if the task was not found */
    private fun isEnded(key: K): Boolean = when (val value = getOrThrow(key).progressive) {
        is Progressive.Done -> true
        is Progressive.Failed -> when (value.type) {
            Progressive.Failed.Type.Error,
            Progressive.Failed.Type.Stopped -> true

            else -> false
        }

        else -> false
    }

    /** Run a task
     * @return Task value or null failed
     * @exception IllegalStateException if the task is already running
     * @exception NoSuchElementException if the task was not found */
    fun run(key: K, coroutineContext: CoroutineContext = Dispatchers.IO): V? {
        val info = getOrThrow(key)

        if (!isPending(key))
            throw IllegalStateException("Task is not pending: $key")

        val startedAt = Date()
        val job = Job()
        info.progressive = Progressive.Running(job = job, start = startedAt)
        val ev = PatientTaskEvent(key, info)
        var value: V? = null
        onStartListener?.invoke(ev)
        onAllListener?.invoke(ev)
        CoroutineScope(job + coroutineContext).launch {
            try {
                withTimeout(info.timeout.toMillis().milliseconds) {
                    Log.d("PatientTaskQueue", "run $key . withTimeout")
                    try {
                        value = info.task()
                        info.progressive = Progressive.Done(value, startedAt, Date())
                        Log.d("PatientTaskQueue", "run $key . Finished")
                    } catch (error: Exception) {
                        info.progressive = Progressive.Failed(
                            Progressive.Failed.Type.Error,
                            error,
                            startedAt, Date()
                        )
                        Log.d("PatientTaskQueue", "run $key . Failed.Error")
                    } finally {
                        onFinallyListener?.invoke(ev)
                        onAllListener?.invoke(ev)
                    }
                }
            } catch (error: CancellationException) {
                if (error is TimeoutCancellationException) {
                    info.progressive = Progressive.Failed(
                        Progressive.Failed.Type.Error,
                        error,
                        startedAt,
                        Date()
                    )
                    Log.d("PatientTaskQueue", "run $key . Timeout")
                } else {
                    info.progressive = Progressive.Failed(
                        Progressive.Failed.Type.Stopped,
                        error,
                        startedAt, Date()
                    )
                    Log.d("PatientTaskQueue", "run $key . Stopped")
                }
            } finally {
                onFinallyListener?.invoke(ev)
                onAllListener?.invoke(ev)
            }
        }
        return value
    }

    fun addRunRoll(key: K, timeout: Duration, task: PatientTask<V>) {
        if (!add(key, timeout, task)) return
        run(key)
        rollForward()
    }

    fun runAndRoll() {
        if (tasks.isEmpty()) return
        run(tasks.keys.first())
        rollForward()
    }

    /** Trigger, in order, the events of tasks that have ended ([Progressive.Done], [Progressive.Failed])...
     *
     * And stop if a task has not ended yet ([Progressive.Pending], [Progressive.Running]) */
    fun rollForward() {
        val keys = tasks.keys.toList()
        for (key in keys) {
            val ended = triggerTaskEvent(key, tasks[key]!!)
            if (!ended) break
            tasks.remove(key)
        }
    }

    /** Trigger the event of a task
     * @return true if the task has ended */
    private fun triggerTaskEvent(key: K, taskInfo: TaskInfo<V>): Boolean {
        val ev = PatientTaskEvent(key, taskInfo)
        when (val taskValue = taskInfo.progressive) {
            is Progressive.Done -> onDoneListener?.invoke(ev)
            is Progressive.Failed -> when (taskValue.type) {
                Progressive.Failed.Type.Stopped -> onStopListener?.invoke(ev)
                Progressive.Failed.Type.Timeout -> onTimeoutListener?.invoke(ev)
                Progressive.Failed.Type.Error -> onErrorListener?.invoke(ev)
            }

            else -> {} //? not ended, do nothing
        }
        onAllListener?.invoke(ev)
        return isEnded(key)
    }

    /** Add and run a task
     * @exception IllegalStateException if the task is already running
     * @exception NoSuchElementException if the task was not found
     * @return Task value or null if not added or failed */
    fun addRun(key: K, timeout: Duration, task: PatientTask<V>): V? {
        Log.d("PatientTaskQueue", "addRun $key")
        val added = add(key, timeout, task)
        if (!added) return null
        return run(key)
    }

    /** Check if a task exists in the queue
     * @return true if found */
    fun contains(key: K): Boolean = tasks.containsKey(key)

    /** Stop a task
     * @return true if found + was running + stopped
     * @exception IllegalStateException if the task is already running */
    fun stop(key: K, cause: CancellationException? = null): Boolean {
        if (!contains(key)) return false

        if (!isRunning(key))
            throw IllegalStateException("Task is not running")
        val info = tasks[key]!!
        val running = info.progressive as Progressive.Running

        running.stop(cause)

        info.progressive =
            Progressive.Failed(
                Progressive.Failed.Type.Stopped,
                cause,
                running.start,
                Date()
            )
        return true
    }

    /** Remove a task
     * @return true if found + not running + removed */
    fun remove(key: K, stopRunning: Boolean = false): Boolean {
        if (!contains(key)) return true

        if (isRunning(key)) {
            if (stopRunning) {
                val stopped = stop(key)
                if (!stopped) return false
            } else return false
        }
        return tasks.remove(key) != null
    }


    /** Called whenever any event occurs */
    private var onAllListener: PatientTaskHandler<K, V>? = null

    /** Called when a task is added */
    private var onAddListener: PatientTaskHandler<K, V>? = null

    /** Called when a task is started */
    private var onStartListener: PatientTaskHandler<K, V>? = null

    /** Called when a task gets canceled */
    private var onStopListener: PatientTaskHandler<K, V>? = null

    /** Called when a task is done */
    private var onDoneListener: PatientTaskHandler<K, V>? = null

    /** Called when a task times out */
    private var onTimeoutListener: PatientTaskHandler<K, V>? = null

    /** Called when a task throws an error */
    private var onErrorListener: PatientTaskHandler<K, V>? = null

    /** Called after the task had ended (even if failed)... (Useful for cleaning up...) */
    private var onFinallyListener: PatientTaskHandler<K, V>? = null

    /** Called whenever any event occurs */
    fun setOnAnyListener(listener: PatientTaskHandler<K, V>?) {
        onAllListener = listener
    }

    /** Called when a task is added */
    fun setOnAddListener(listener: PatientTaskHandler<K, V>?) {
        onAddListener = listener
    }

    /** Called when a task is started */
    fun setOnStartListener(listener: PatientTaskHandler<K, V>?) {
        onStartListener = listener
    }

    /** Called when a task times out */
    fun setOnTimeoutListener(listener: PatientTaskHandler<K, V>?) {
        onTimeoutListener = listener
    }

    /** Called when a task gets canceled */
    fun setOnStopListener(listener: PatientTaskHandler<K, V>?) {
        onStopListener = listener
    }

    /** Called when a task throws an error */
    fun setOnErrorListener(listener: PatientTaskHandler<K, V>?) {
        onErrorListener = listener
    }

    /** Called when a task is done */
    fun setOnDoneListener(listener: PatientTaskHandler<K, V>?) {
        onDoneListener = listener
    }

    /** Called after the task had ended (even if failed)... (Useful for cleaning up...) */
    fun setOnFinallyListener(listener: PatientTaskHandler<K, V>?) {
        onFinallyListener = listener
    }


}

data class TaskInfo<V>(
    val task: PatientTask<V>,
    val timeout: Duration,
    var progressive: Progressive<V?>
)

data class PatientTaskEvent<K : Comparable<K>, V>(
    val key: K,
    val info: TaskInfo<V>
)

typealias PatientTask<V> = () -> V
typealias PatientTaskHandler<K, V> = (ev: PatientTaskEvent<K, V>) -> Unit
