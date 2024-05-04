package com.galacticai.flareconverter.util.ff_command

object FFRun {
    /** @see run with [flow] */
//    suspend fun <T> FFprobeCommand.runFlow(
//        scope: CoroutineScope,
//        flow: MutableStateFlow<Progressive<T>>,
//        onSuccess: (result: CommandResult, current: Progressive<T>) -> T
//    ) {
//        val startedAt = Date()
//        val job = scope.coroutineContext[Job.Key]
//
//        flow.update {
//            Progressive.Running(
//                Progressive.Running.Type.Main,
//                0f, job, startedAt,
//            )
//        }
//        this.run(
//            onFail = { ex ->
//                flow.update {
//                    Progressive.Failed(
//                        Progressive.Failed.Type.Error,
//                        ex, startedAt, Date()
//                    )
//                }
//            },
//            onSuccess = { res ->
//                flow.update {
//                    val updated = onSuccess(res, it)
//                    Progressive.Done(updated, startedAt, Date())
//                }
//            }
//        )
//    }
//
//    /** @see run with [flow] */
//    suspend fun <T> FFmpegCommand.runFlow(
//        scope: CoroutineScope,
//        flow: MutableStateFlow<Progressive<T>>,
//        /** null = no progress report */
//        duration: Duration? = null,
//        /** @return null if something went wrong */
//        onSuccess: suspend (result: CommandResult, beforeSuccess: Progressive<T>) -> T?
//    ) {
//        val startedAt = Date()
//        val job = scope.coroutineContext[Job.Key]
//
//        flow.update {
//            Progressive.Running(
//                Progressive.Running.Type.Main,
//                0f, job, startedAt,
//            )
//        }
//        this.runStreamStats(
//            onProgressStats = { stats ->
//                if (duration == null) return@runStreamStats
//                //val progress = stats.getProgress(duration)
//                flow.update {
//                    val running = it as Progressive.Running<T>
//                    running.progress = stats.progressFlag
//                    running
//                }
//            },
//            onFail = { ex ->
//                flow.update {
//                    Progressive.Failed(
//                        Progressive.Failed.Type.Error,
//                        ex, startedAt, Date()
//                    )
//                }
//            },
//        ) { result ->
//            flow.update {
//                val updated = onSuccess(result, it)
//                if (updated == null)
//                    Progressive.Failed(
//                        Progressive.Failed.Type.Error,
//                        FFmpegFailed("Failed to read the output file"),
//                        startedAt, Date()
//                    )
//                else Progressive.Done(updated, startedAt, Date())
//            }
//        }
//    }
}