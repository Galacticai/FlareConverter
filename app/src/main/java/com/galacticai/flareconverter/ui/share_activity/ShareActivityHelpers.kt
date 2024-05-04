package com.galacticai.flareconverter.ui.share_activity

import android.content.ContentResolver
import android.content.Intent
import com.arthenica.ffmpegkit.FFprobeKit
import com.arthenica.ffmpegkit.MediaInformation
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.models.ShareInfo
import com.galacticai.flareconverter.models.exceptions.FFmpegFailed
import com.galacticai.flareconverter.models.exceptions.InvalidLaunchCommand
import com.galacticai.flareconverter.models.ffmpeg.FFmpegArg
import com.galacticai.flareconverter.models.ffmpeg.FFmpegCommand
import com.galacticai.flareconverter.util.AppDefaults.outputDir
import com.galacticai.flareconverter.util.ffmpeg.FFmpegUtils.ffmpegRunLive
import global.common.IOUtils.child
import global.common.IOUtils.copyToFile
import global.common.TimeUtils
import global.common.models.FutureValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import java.io.File
import java.io.IOException
import java.time.Duration
import java.util.Date
import java.util.UUID

class ShareActivityHelpers(private val activity: ShareActivity) {
    private val vm get() = activity.vm

    @Deprecated("")
    private fun invalid(msg: String) =
        InvalidLaunchCommand(ShareActivity::class.java, msg)

    @Deprecated("")
    private fun invalidInput(
        msg: String,
        startedAt: Date? = null,
        runtime: Duration? = null
    ) = vm.inFileLive.postValue(
        FutureValue.Failed.Error(invalid(msg), startedAt, runtime)
    )

    @Deprecated("")
    private fun invalidOutput(
        msg: String,
        startedAt: Date? = null,
        runtime: Duration? = null
    ) = vm.outFileLive.postValue(
        FutureValue.Failed.Error(invalid(msg), startedAt, runtime)
    )

    /** Prepare the files for the conversion process
     *  @return the shared file after copying it to the app input directory */
    fun initFile(
        intent: Intent?,
        inputDir: File,
        contentResolver: ContentResolver,
        scope: CoroutineScope,
    ): ShareInfo? {
        val startedAt = Date()

        fun fail(ex: Exception) {
            vm.inFileLive.postValue(
                FutureValue.Failed.Error(
                    ex,
                    startedAt,
                    TimeUtils.getDuration(startedAt)
                )
            )
        }

        val info = ShareInfo.from(intent, contentResolver) {
            fail(InvalidLaunchCommand(it))
        } ?: return null

        vm.inFileLive.postValue(FutureValue.Running(scope.coroutineContext[Job], startedAt, null))

        val uuid = UUID.randomUUID().toString()
        val inFile = inputDir.child("$uuid.${info.extension}")

        contentResolver.openInputStream(info.uri)
            ?.copyToFile(inFile)
            ?: return run {
                fail(IOException("Failed to copy file"))
                null
            }


        val mediaInfoSession = FFprobeKit.getMediaInformation(inFile.absolutePath)
        val mediaInfo = mediaInfoSession?.mediaInformation
            ?: return run {
                fail(FFmpegFailed("Failed to get media information"))
                null
            }

        if (info.inMime.category in listOf(MimeType.IMAGE, MimeType.VIDEO)) {
            val stream = mediaInfo.mainStream
            if (stream == null) return run {
                fail(InvalidLaunchCommand("Invalid file"))
                null
            }
        }
        vm.inFileLive.postValue(
            FutureValue.Finished(inFile to mediaInfo)
        )

        return info
    }

    /** ⚠️ this will be called only in the right conditions, so the failsafes are for unexpected calls, and shouldn't happen normally */
    fun convert(
        /** must be one of [ShareInfo.outMimes] */
        outMime: MimeType,
        args: Map<FFmpegArg, Array<Any>> = mapOf()
    ) {

        val info = vm.shareInfoState.value
            ?: return invalidInput("FATAL: unexpected missing shareInfoState") //? failsafe: will never happen - [convert] is called only when the right conditions are met
        val (inFile, mediaInfo) = vm.inFileLive.value
            ?.finishedValue
            ?.let {
                val (file, _) = it
                // intentionally fall back to invalidInput if not exists or not a file
                if (file.exists() || !file.isFile) it else null
            }
            ?: return invalidInput("FATAL: unexpected missing input file") //? failsafe

        val argsAllowed = ffmpegAllowedArgs[info.inMime.category]
            ?: return invalidInput("FATAL: unexpected input mime category")
        val argsAllowedKeySet = argsAllowed.map { it.key }.toSet()

        if (!info.outMimes.contains(outMime)) {
            return invalidOutput("FATAL: unexpected output mime selected") //? failsafe
        }

        val outFile = activity.outputDir.child(
            "${inFile.nameWithoutExtension}.flare.${outMime.extension}"
        )


        val builder = FFmpegCommand()
            .io(inFile.absolutePath, outFile.absolutePath)

        for ((a, values) in args) {
            if (a.key !in argsAllowedKeySet) {
                //? failsafe
                return invalidOutput("FATAL: invalid arguments selected for the given category: ${info.inMime.category}")
            }
            builder.arg(a, *values)
        }
        val cmd = builder.build()

        activity.vm.async {
            ffmpegRunLive(cmd, vm.outFileLive, mediaInfo) {
                outFile to mediaInfo
            }
        }
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