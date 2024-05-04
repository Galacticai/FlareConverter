package com.galacticai.flareconverter.ui.share_activity

import android.content.ContentResolver
import android.content.Intent
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFprobeKit
import com.arthenica.ffmpegkit.ReturnCode
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.models.ShareInfo
import com.galacticai.flareconverter.models.exceptions.FFmpegFailed
import com.galacticai.flareconverter.models.exceptions.InvalidLaunchCommand
import com.galacticai.flareconverter.models.ffmpeg.FFmpegArg
import com.galacticai.flareconverter.models.ffmpeg.FFmpegCommand
import com.galacticai.flareconverter.ui.components.dialog.InitFileConvertDialog
import com.galacticai.flareconverter.util.MediaUtils.mainStream
import com.galacticai.flareconverter.util.ffmpeg.FFmpegUtils.ffmpegRunLive
import global.common.models.progressive.Progressive
import global.common.util.IOUtil.child
import global.common.util.IOUtil.copyToFile
import global.common.util.IOUtil.toExtension
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import java.io.File
import java.io.IOException
import java.util.Date
import java.util.UUID

class ShareActivityHelpers(private val activity: ShareActivity) {
    private val vm get() = activity.vm

    /** Prepare the files for the conversion process
     *  @return the shared file after copying it to the app input directory */
    fun CoroutineScope.initFile(
        intent: Intent?,
        inputDir: File,
        contentResolver: ContentResolver,
    ): ShareInfo? {
        val startedAt = Date()

        fun fail(cause: Exception) = vm.inFileLive.update {
            Progressive.Failed(
                Progressive.Failed.Type.Error,
                cause,
                startedAt,
                Date()
            )
        }

        fun progress(progressNew: Float? = null, typeNew: Progressive.Running.Type? = null) {
            vm.inFileLive.update {
                Progressive.Running(
                    typeNew ?: Progressive.Running.Type.Pre,
                    progressNew,
                    coroutineContext[Job],
                    startedAt,
                )
            }
        }

        progress()

        var info = ShareInfo.from(intent, contentResolver) {
            fail(InvalidLaunchCommand(it))
        } ?: return null

        progress(.4f, Progressive.Running.Type.Main)

        val uuid = UUID.randomUUID().toString()
        var inFile = inputDir.child("$uuid.${info.extension}")

        info.uri.copyToFile(contentResolver, inFile)
            ?: return run {
                fail(IOException("Failed to copy file"))
                null
            }

        progress(.6f)

        val mediaInfoSession = FFprobeKit.getMediaInformation(inFile.absolutePath)
        val mediaInfo = mediaInfoSession?.mediaInformation
            ?: return run {
                fail(FFmpegFailed("Failed to get media information"))
                null
            }

        val mainStream = mediaInfo.mainStream
            ?: return run {
                fail(InvalidLaunchCommand("Invalid file"))
                null
            }

        progress(.7f)

        if (info.inMime is MimeType.Heic) {
            //? heic unsupported - convert to png first and work with png
            val png = MimeType.Png

            val inFilePng = inFile.parentFile!!.child(
                "${inFile.nameWithoutExtension}.${png.extension}"
            )

            InitFileConvertDialog.show(
                info.inMime, png,
                onReject = { activity.finish() }
            ) {
                progress(.9f, Progressive.Running.Type.Post)

                vm.async {
                    val cmd = FFmpegCommand()
                        .io(inFile.absolutePath, inFilePng.absolutePath)
                        .build()
                    val session = FFmpegKit.execute(cmd)

                    val mediaInfoNew = run {
                        val isSuccess = ReturnCode.isSuccess(session.returnCode)
                        if (!isSuccess) return@run null
                        val info = FFprobeKit.getMediaInformation(inFilePng.absolutePath)
                            ?.mediaInformation
                        info
                    }

                    if (mediaInfoNew == null) {
                        vm.inFileLive.update {
                            Progressive.Failed(
                                Progressive.Failed.Type.Error,
                                FFmpegFailed("Failed to parse converted PNG info"),
                                startedAt, Date()
                            )
                        }
                        return@async
                    }

                    val infoNew = info.toMime(png)
                    info = infoNew!!
                    inFile = inFilePng
                    activity.runOnUiThread {
                        vm.shareInfoState.value = infoNew
                    }

                    progress(1f)

                    vm.inFileLive.update {
                        Progressive.Done(
                            InformedFile(inFilePng, mediaInfoNew),
                            startedAt, Date()
                        )
                    }
                }

            }
        }

        vm.inFileLive.update {
            Progressive.Done(
                InformedFile(inFile, mediaInfo),
                startedAt, Date()
            )
        }


        return info
    }

    /** ⚠️ call only in the right conditions. no failsafes */
    fun convert(
        outMime: MimeType,
        args: Map<FFmpegArg, String>
    ) {
        val informedFile = vm.inFileLive.value.done!!
        val outFile = informedFile.file.toExtension(outMime.extension)

        val cmd = FFmpegCommand()
//            .io(
//                informedFile.file.absolutePath,
//                outFile.absolutePath
//            )
            .apply {
                for ((k, v) in args) arg(k, v)
            }.build()

        activity.vm.async {
            ffmpegRunLive(cmd, vm.outFileLive, informedFile.info) { session, current ->
                val mediaInfoSession = FFprobeKit.getMediaInformation(outFile.absolutePath)
                InformedFile(outFile, mediaInfoSession.mediaInformation!!)
            }
        }
    }

    companion object {
        /** Key = [MimeType.category] | value = allowed [FFmpegArg]s */
        val ffmpegAllowedArgs: Map<String, List<FFmpegArg>> =
            mapOf(
                MimeType.IMAGE to listOf(
                    FFmpegArg.Input,
                    FFmpegArg.Resolution,
                    // FFmpegArg.Bitrate,
                    // FFmpegArg.Crf, //TODO: bitrate | crf . or remove crf
                    FFmpegArg.Preset,
//                    FFmpegArg.Version,
                ),
                MimeType.VIDEO to listOf(
                    FFmpegArg.Input,
                    FFmpegArg.Resolution,
                    FFmpegArg.FrameRate,
                    FFmpegArg.BitrateVideo,
                    FFmpegArg.BitrateAudio,
                    FFmpegArg.CodecVideo,
                    FFmpegArg.CodecAudio,
                    // FFmpegArg.Crf, //TODO: bitrate | crf . or remove crf
                    FFmpegArg.Preset,
                    FFmpegArg.StartTimeByMs,
                    FFmpegArg.EndTimeByMs,
                    FFmpegArg.Scale,
                    FFmpegArg.Crop,
                    FFmpegArg.Speed,
                    FFmpegArg.Pitch,
                    FFmpegArg.MetadataByMap,
//                    FFmpegArg.Version,
                ),
                MimeType.AUDIO to listOf(
                    FFmpegArg.Input,
                    FFmpegArg.Speed,
                    FFmpegArg.Pitch,
                    FFmpegArg.BitrateAudio,
                    // FFmpegArg.Crf, //TODO: bitrate | crf . or remove crf
                    FFmpegArg.StartTimeByMs,
                    FFmpegArg.EndTimeByMs,
//                    FFmpegArg.Version,
                )
            )
    }
}