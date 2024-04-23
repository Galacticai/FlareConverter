package global.common.models.patient_task_queue

import java.time.Duration
import java.util.Date

sealed interface PatientTaskEvent {
    data class TaskAdd<K>(val key: K) : PatientTaskEvent
    data class TaskStart<K>(val key: K, val startedAt: Date) : PatientTaskEvent
    data class TaskStop<K>(
        val key: K, val startedAt: Date, val runtime: Duration
    ) : PatientTaskEvent

    data class TaskDone<K, V>(
        val key: K, val value: V?, val startedAt: Date, val runtime: Duration
    ) : PatientTaskEvent

    data class TaskTimeout<K>(val key: K, val timeout: Duration, val startedAt: Date) :
        PatientTaskEvent

    data class TaskError<K>(
        val key: K, val error: Exception, val startedAt: Date, val runtime: Duration
    ) : PatientTaskEvent

    data class TaskFinally<K, V>(val key: K, val info: TaskInfo<V>) : PatientTaskEvent
}