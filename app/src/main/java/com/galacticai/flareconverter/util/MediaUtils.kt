package com.galacticai.flareconverter.util

import androidx.lifecycle.MutableLiveData
import com.arthenica.ffmpegkit.FFprobeKit
import com.arthenica.ffmpegkit.MediaInformation
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.models.ffmpeg.FFmpegCommand
import com.galacticai.flareconverter.util.ffmpeg.FFmpegUtils.ffmpegRunLive
import global.common.IOUtils.child
import global.common.IOUtils.mime
import global.common.TimeUtils
import global.common.models.FutureValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date

object MediaUtils {

    val MediaInformation.mainStream
        get() = streams?.let { allStreams ->
            if (allStreams.isEmpty()) return@let null
            val visualStreams = allStreams.filter { it.width != null && it.height != null }
            if (visualStreams.isNotEmpty()) {
                visualStreams.maxByOrNull { (it.width ?: 0L) * (it.height ?: 0L) }
            } else {
                allStreams.firstOrNull { it.type == "audio" } ?: allStreams.firstOrNull()
            }
        }

    /** Get a frame from the given [file] at the given "[from]" ratio and post the results to [live] as [FutureValue]s
     * @param from ratio from 0 to 1 (0 = start, 1 = end) */
    private fun postFrame(
        file: File,
        from: Float = .5f,
        live: MutableLiveData<FutureValue<File>>,
        mimeCategory: String
    ) {
        live.postValue(FutureValue.Pending())

        val startedAt = Date()
        CoroutineScope(Dispatchers.IO).launch {
            if (file.mime?.startsWith(mimeCategory) != true)
                return@launch live.postValue(
                    FutureValue.Failed.Error(
                        IllegalStateException("Not a $mimeCategory: $file"),
                        startedAt,
                        TimeUtils.getDuration(startedAt)
                    )
                )

            val imageName = "${file.nameWithoutExtension}.frame.${MimeType.Jpeg.extension}"
            val image = file.parentFile?.child(imageName)
                ?: return@launch live.postValue(
                    FutureValue.Failed.Error(
                        IllegalStateException("Failed to reference image: $imageName"),
                        startedAt,
                        TimeUtils.getDuration(startedAt)
                    )
                )


            val info = FFprobeKit.getMediaInformation(file.absolutePath)
            val ms = ((info?.duration ?: 0L) * from).toLong()
            val cmd = FFmpegCommand()
                .io(file.absolutePath, image.absolutePath)
                .frameCount(1)
                .startTime(ms)
                .build()

            ffmpegRunLive(cmd, live, info.mediaInformation) {
                image
            }

        }
    }

    fun postVideoFrame(
        video: File,
        from: Float = .5f,
        live: MutableLiveData<FutureValue<File>>,
    ) = postFrame(video, from, live, MimeType.VIDEO)

    fun postAudioFrame(
        audio: File,
        live: MutableLiveData<FutureValue<File>>,
    ) = postFrame(audio, 0f, live, MimeType.AUDIO)
}