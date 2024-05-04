package com.galacticai.flareconverter.util

//import com.arthenica.ffmpegkit.FFprobeKit
//import com.galacticai.flareconverter.models.MimeType
//import com.galacticai.flareconverter.models.ffmpeg.ff_command.FFmpegCommand
//import com.galacticai.flareconverter.util.FFUtil.Run.executeFlow
//import com.galacticai.flareconverter.util.FFUtil.executeFlow
//import global.common.models.progressive.Progressive
//import global.common.util.IOUtil.child
//import global.common.util.IOUtil.mime
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//import java.io.File
//import java.util.Date
//
@Deprecated("not even used bruh")
object MediaUtil {
//    /** Get a frame from the given [file] at the given "[from]" ratio and post the results to [flow] as [Progressive]s
//     * @param from ratio from 0 to 1 (0 = start, 1 = end) */
//    private fun postFrame(
//        file: File,
//        from: Float = .5f,
//        flow: MutableStateFlow<Progressive<File>>,
//        mimeCategory: String
//    ) {
//        flow.update { Progressive.Pending() }
//
//        val startedAt = Date()
//        CoroutineScope(Dispatchers.IO).launch {
//            if (file.mime?.startsWith(mimeCategory) != true)
//                return@launch flow.update {
//                    Progressive.Failed(
//                        Progressive.Failed.Type.Error,
//                        IllegalStateException("Not a $mimeCategory: $file"),
//                        startedAt, Date()
//                    )
//                }
//
//            val imageName = "${file.nameWithoutExtension}.frame.${MimeType.Jpeg.extension}"
//            val image = file.parentFile?.child(imageName)
//                ?: return@launch flow.update {
//                    Progressive.Failed(
//                        Progressive.Failed.Type.Error,
//                        IllegalStateException("Failed to reference image: $imageName"),
//                        startedAt, Date()
//                    )
//                }
//
//
//            val info = FFprobeKit.getMediaInformation(file.absolutePath)
//            val ms = ((info?.duration ?: 0L) * from).toLong()
//            FFmpegCommand()
//                .io(file.absolutePath, image.absolutePath)
//                .frameCount(1)
//                .startTime(ms)
//                .executeFlow(
//                    this, flow,
//                    info.mediaInformation
//                ) { _, _ -> image }
//
//        }
//    }
//
//    fun postVideoFrame(
//        video: File,
//        from: Float = .5f,
//        flow: MutableStateFlow<Progressive<File>>,
//    ) = postFrame(video, from, flow, MimeType.VIDEO)
//
//    fun postAudioFrame(
//        audio: File,
//        flow: MutableStateFlow<Progressive<File>>,
//    ) = postFrame(audio, 0f, flow, MimeType.AUDIO)
}