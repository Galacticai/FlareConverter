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
import global.common.models.progressive.Progressive
import global.common.util.IOUtil.child
import global.common.util.IOUtil.copyToFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
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

        fun fail(cause: Exception) = vm.inFileLive.postValue(
            Progressive.Failed(
                Progressive.Failed.Type.Error,
                cause,
                startedAt,
                Date()
            )
        )

        fun progress(progressNew: Float? = null, typeNew: Progressive.Running.Type? = null) {
            vm.inFileLive.postValue(
                Progressive.Running(
                    typeNew ?: Progressive.Running.Type.Pre,
                    progressNew,
                    coroutineContext[Job],
                    startedAt,
                )
            )
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
                        vm.inFileLive.postValue(
                            Progressive.Failed(
                                Progressive.Failed.Type.Error,
                                FFmpegFailed("Failed to parse converted PNG info"),
                                startedAt, Date()
                            )
                        )
                        return@async
                    }

                    val infoNew = info.toMime(png)
                    activity.runOnUiThread {
                        vm.shareInfoState.value = infoNew
                    }

                    progress(1f)

                    vm.inFileLive.postValue(
                        Progressive.Done(
                            InformedFile(inFilePng, mediaInfoNew),
                            startedAt, Date()
                        )
                    )
                }

            }
        }

        vm.inFileLive.postValue(
            Progressive.Done(
                InformedFile(inFile, mediaInfo),
                startedAt, Date()
            )
        )


        return info
    }

    /** ⚠️ this will be called only in the right conditions, so the failsafes are for unexpected calls, and shouldn't happen normally */
    fun convert(
        /** must be one of [ShareInfo.outMimes] */
        outMime: MimeType,
        args: Map<FFmpegArg, Array<Any>> = mapOf()
    ) {

//        val info = vm.shareInfoState.value
//            ?: return invalidInput("FATAL: unexpected missing shareInfoState") //? failsafe: will never happen - [convert] is called only when the right conditions are met
//        val (inFile, mediaInfo) = vm.inFileLive.value
//            ?.finishedValue
//            ?.let {
//                val (file, _) = it
//                // intentionally fall back to invalidInput if not exists or not a file
//                if (file.exists() || !file.isFile) it else null
//            }
//            ?: return invalidInput("FATAL: unexpected missing input file") //? failsafe
//
//        val argsAllowed = ffmpegAllowedArgs[info.inMime.category]
//            ?: return invalidInput("FATAL: unexpected input mime category")
//        val argsAllowedKeySet = argsAllowed.map { it.key }.toSet()
//
//        if (!info.outMimes.contains(outMime)) {
//            return invalidOutput("FATAL: unexpected output mime selected") //? failsafe
//        }
//
//        val outFile = activity.outputDir.child(
//            "${inFile.nameWithoutExtension}.flare.${outMime.extension}"
//        )
//
//
//        val builder = FFmpegCommand()
//            .io(inFile.absolutePath, outFile.absolutePath)
//
//        for ((a, values) in args) {
//            if (a.key !in argsAllowedKeySet) {
//                //? failsafe
//                return invalidOutput("FATAL: invalid arguments selected for the given category: ${info.inMime.category}")
//            }
//            builder.arg(a, *values)
//        }
//        val cmd = builder.build()
//
//        activity.vm.async {
//            ffmpegRunLive(cmd, vm.outFileLive, mediaInfo) {
//                outFile to mediaInfo
//            }
//        }
    }

    companion object {
        /** Key = [MimeType.category] | value = allowed [FFmpegArg]s */
        val ffmpegAllowedArgs: Map<String, List<FFmpegArg>> =
            mapOf(
                MimeType.IMAGE to listOf(
                    FFmpegArg.Resolution,
                    // FFmpegArg.Bitrate,
                    // FFmpegArg.Crf, //TODO: bitrate | crf . or remove crf
                    FFmpegArg.Preset,
//                    FFmpegArg.Version,
                ),
                MimeType.VIDEO to listOf(
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