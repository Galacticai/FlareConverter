package com.galacticai.flareconverter.util

import com.arthenica.ffmpegkit.FFprobeKit
import com.arthenica.ffmpegkit.MediaInformation
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.models.ffmpeg.FFmpegCommand
import com.galacticai.flareconverter.util.ffmpeg.FFmpegUtils.ffmpegRunLive
import global.common.models.progressive.Progressive
import global.common.util.IOUtil.child
import global.common.util.IOUtil.mime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date

object MediaUtils {

    val MediaInformation.mainStream
        get() = streams?.let { all ->
            if (all.isEmpty()) return@let null
            val visualStreams = all.filter { it.width != null && it.height != null }
            if (visualStreams.isNotEmpty()) {
                visualStreams.maxByOrNull { (it.width ?: 0L) * (it.height ?: 0L) }
            } else {
                all.firstOrNull { it.type == "audio" } ?: all.firstOrNull()
            }
        }

    /** Get a frame from the given [file] at the given "[from]" ratio and post the results to [flow] as [Progressive]s
     * @param from ratio from 0 to 1 (0 = start, 1 = end) */
    private fun postFrame(
        file: File,
        from: Float = .5f,
        flow: MutableStateFlow<Progressive<File>>,
        mimeCategory: String
    ) {
        flow.update { Progressive.Pending() }

        val startedAt = Date()
        CoroutineScope(Dispatchers.IO).launch {
            if (file.mime?.startsWith(mimeCategory) != true)
                return@launch flow.update {
                    Progressive.Failed(
                        Progressive.Failed.Type.Error,
                        IllegalStateException("Not a $mimeCategory: $file"),
                        startedAt, Date()
                    )
                }

            val imageName = "${file.nameWithoutExtension}.frame.${MimeType.Jpeg.extension}"
            val image = file.parentFile?.child(imageName)
                ?: return@launch flow.update {
                    Progressive.Failed(
                        Progressive.Failed.Type.Error,
                        IllegalStateException("Failed to reference image: $imageName"),
                        startedAt, Date()
                    )
                }


            val info = FFprobeKit.getMediaInformation(file.absolutePath)
            val ms = ((info?.duration ?: 0L) * from).toLong()
            val cmd = FFmpegCommand()
                .io(file.absolutePath, image.absolutePath)
                .frameCount(1)
                .startTime(ms)
                .build()

            ffmpegRunLive(cmd, flow, info.mediaInformation) { _, _ ->
                image
            }

        }
    }

    fun postVideoFrame(
        video: File,
        from: Float = .5f,
        flow: MutableStateFlow<Progressive<File>>,
    ) = postFrame(video, from, flow, MimeType.VIDEO)

    fun postAudioFrame(
        audio: File,
        flow: MutableStateFlow<Progressive<File>>,
    ) = postFrame(audio, 0f, flow, MimeType.AUDIO)
}