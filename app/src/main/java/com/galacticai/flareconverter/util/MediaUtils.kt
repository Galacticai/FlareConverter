package com.galacticai.flareconverter.util

import androidx.lifecycle.MutableLiveData
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFprobeKit
import com.galacticai.flareconverter.models.FFmpegCommand
import com.galacticai.flareconverter.models.MimeType
import global.common.IOUtils.child
import global.common.IOUtils.mime
import global.common.models.FutureValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.floor

object MediaUtils {
    /** Get a frame from the given [video] at the given "[from]" ratio and post the results to [live] as [FutureValue]s
     * @param from ratio from 0 to 1 (0 = start, 1 = end) */
    fun postVideoFrame(
        video: File,
        from: Float = .5f,
        live: MutableLiveData<FutureValue<File>>,
    ) {
        live.postValue(FutureValue.Pending())

        CoroutineScope(Dispatchers.IO).launch {
            live.postValue(FutureValue.Running())

            if (video.mime?.startsWith(MimeType.VIDEO) != true)
                return@launch live.postValue(
                    FutureValue.Failed.Error(
                        IllegalStateException("Not a video: $video")
                    )
                )

            val imageName = "${video.nameWithoutExtension}.${MimeType.Jpeg.extension}"
            val image = video.parentFile?.child(imageName)
                ?: return@launch live.postValue(
                    FutureValue.Failed.Error(
                        IllegalStateException("Failed to reference image: $imageName")
                    )
                )

            val cmdProbe = FFmpegCommand().io(video.absolutePath)
            FFprobeKit.executeAsync(cmdProbe.argsOnly) {
                val fromRatio = from.coerceAtLeast(0f).coerceAtMost(1f)
                val frameTime = floor(it.duration * fromRatio).toLong()
                val cmdGetFrame = FFmpegCommand()
                    .io(video.absolutePath, image.absolutePath)
                    .frameCount(1)
                    .startTime(frameTime)

                FFmpegKit.executeAsync(cmdGetFrame.argsOnly) {
                    live.postValue(FutureValue.Finished(image))
                }
            }
        }
    }
}