package com.galacticai.flareconverter.util.ffmpeg

import androidx.lifecycle.MutableLiveData
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.FFmpegSession
import com.arthenica.ffmpegkit.MediaInformation
import com.arthenica.ffmpegkit.ReturnCode
import com.arthenica.ffmpegkit.SessionState
import com.arthenica.ffmpegkit.Statistics
import com.galacticai.flareconverter.models.exceptions.FFmpegFailed
import com.galacticai.flareconverter.util.ffmpeg.FFmpegUtils.ffmpegRun
import global.common.models.amount.Amount
import global.common.models.amount.units.BaseUnit
import global.common.models.amount.units.MetricSystem
import global.common.models.progressive.Progressive
import global.common.models.progressive.Progressive.Companion.postValueBy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.util.Date

object FFmpegUtils {

    /** get [MediaInformation.bitrate] as [Amount] */
    val MediaInformation.bitrateAmount: Amount?
        get() {
            /** kb/s */
            val value = this.bitrate.toDoubleOrNull()
                ?: return null
            val unit = MetricSystem.kilo() and BaseUnit.bit() per BaseUnit.second()
            return Amount(value, unit)
        }
    val MediaInformation.durationMs: Long
        get() = ((duration?.toDoubleOrNull() ?: 0.0) * 1000).toLong()

    fun Statistics.getProgress(totalDurationMs: Long): Float {
        if (totalDurationMs <= 0) return 0f
        return (time.toFloat() / totalDurationMs.toFloat()).coerceIn(0f, 1f)
    }

    fun Statistics.getProgress(mediaInfo: MediaInformation?): Float {
        val totalMs = mediaInfo?.durationMs ?: return 0f
        return getProgress(totalMs)
    }


    fun <T> ffmpegRun(
        cmd: String,
        onStart: () -> Unit,
        onProgress: (Statistics) -> Unit,
        onFail: (Exception) -> Unit,
        onSuccess: (FFmpegSession) -> T
    ) {
        onStart()
        val session = FFmpegSession.create(
            FFmpegKitConfig.parseArguments(cmd),
            null, null,
        ) { onProgress(it) }

        try {
            FFmpegKitConfig.ffmpegExecute(session)

            if (!ReturnCode.isSuccess(session.returnCode)) {
                throw FFmpegFailed(session.returnCode.toString())
            }

            onSuccess(session)

        } catch (ex: Exception) {
            onFail(ex)

        } finally {
            if (session.state == SessionState.RUNNING) {
                FFmpegKit.cancel(session.sessionId)
            }
        }
    }

    /** @see ffmpegRun - but with [live] */
    fun <T> CoroutineScope.ffmpegRunLive(
        cmd: String,
        live: MutableLiveData<Progressive<T>>,
        mediaInfo: MediaInformation,
        onSuccess: (FFmpegSession) -> T
    ) {
        val startedAt = Date()
        val job = coroutineContext[Job]

        return ffmpegRun(
            cmd,
            onStart = {
                live.postValue(
                    Progressive.Running(
                        Progressive.Running.Type.Main,
                        0f, job, startedAt,
                    )
                )
            },
            onProgress = {
                val progress = it.getProgress(mediaInfo)
                live.postValueBy { current ->
                    val running = current as Progressive.Running<T>
                    running.progress = progress
                    running
                }
            },
            onFail = {
                live.postValue(
                    Progressive.Failed(
                        Progressive.Failed.Type.Error,
                        it, startedAt, Date()
                    )
                )
            }
        ) {
            live.postValue(
                Progressive.Done(onSuccess(it), startedAt, Date())
            )
        }
    }
}