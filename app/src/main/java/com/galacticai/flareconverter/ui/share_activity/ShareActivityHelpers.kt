package com.galacticai.flareconverter.ui.share_activity

import android.content.ContentResolver
import android.content.Intent
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.galacticai.flareconverter.models.ConvertStage
import com.galacticai.flareconverter.models.MediaFile
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.models.ShareInfo
import com.galacticai.flareconverter.models.exceptions.FFmpegFailed
import com.galacticai.flareconverter.models.exceptions.InvalidLaunchCommand
import com.galacticai.flareconverter.models.ffmpeg.ff_command.FFmpegCommand.Companion.toCommand
import com.galacticai.flareconverter.models.ffmpeg.ff_command.args.FFmpegArg
import com.galacticai.flareconverter.ui.share_activity.components.LocalShareActivityStates.LocalConfigListState
import com.galacticai.flareconverter.util.MimeTypeUtils.outMimeSetting
import com.galacticai.flareconverter.util.media.FFInfo
import global.common.models.progressive.Progressive
import global.common.ui.bounds_resolver.LocalPlacementState
import global.common.util.IOUtil.child
import global.common.util.IOUtil.copyToFile
import global.common.util.IOUtil.toExtension
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.update
import java.io.File
import java.io.IOException
import java.util.Date
import java.util.UUID

class ShareActivityHelpers(private val activity: ShareActivity) {
    private val vm get() = activity.vm

    @Composable
    fun rememberConvertStage(): ConvertStage {
        val inFile by vm.inFileFlow.collectAsState()
        val outFile by vm.outFileFlow.collectAsState()

        return remember(inFile, outFile) {
            when (inFile) {
                is Progressive.Pending,
                is Progressive.Running -> ConvertStage.Init

                is Progressive.Failed -> ConvertStage.InitFail

                is Progressive.Done -> when (outFile) {
                    is Progressive.Pending -> ConvertStage.Config
                    is Progressive.Running -> ConvertStage.Converting
                    is Progressive.Failed -> ConvertStage.ConvertFail
                    is Progressive.Done -> ConvertStage.Sharing
                    else -> null
                }

                else -> null
            } ?: throw NotImplementedError(
                "Unknown state: `Progressive` class is open and may have extra unimplemented types"
            )
        }
    }

    /** Prepare the files for the conversion process
     *  @return the shared file after copying it to the app input directory */
    suspend fun initFile(
        job: Job?,
        intent: Intent?,
        inputDir: File,
        contentResolver: ContentResolver,
    ): ShareInfo? {
        val startedAt = Date()

        fun fail(cause: Exception) = vm.inFileFlow.update {
            Log.e("initFile - fail", cause.toString())
            Progressive.Failed(
                Progressive.Failed.Type.Error,
                cause,
                startedAt,
                Date()
            )
        }

        fun progress(
            progressNew: Float? = null,
            typeNew: Progressive.Running.Type? = null
        ) = vm.inFileFlow.update {
            val running = it as? Progressive.Running
            Progressive.Running(
                typeNew ?: running?.type ?: Progressive.Running.Type.Pre,
                progressNew,
                job,
                startedAt,
            )
        }
        progress()

        val info = ShareInfo.from(intent, contentResolver) {
            fail(InvalidLaunchCommand(it))
        } ?: return null

        progress(.4f, Progressive.Running.Type.Main)

        val uuid = UUID.randomUUID().toString()
        val inFile = inputDir.child("$uuid.${info.extension}")

        info.uri.copyToFile(contentResolver, inFile)
            ?: return run {
                fail(IOException("Failed to copy file"))
                null
            }

        progress(.6f)

        val mediaInfo = FFInfo.getMediaInfo(inFile, info.inMime)
            ?: return run {
                fail(FFmpegFailed("Failed to get media information"))
                null
            }

        val informedFile = MediaFile(inFile, info.inMime, mediaInfo)

        progress(.7f)

        //! intentional: will also trigger the lazy loaders in `InformedFile`
        if (informedFile.mainStreamMime == null) return run {
            fail(FFmpegFailed("Failed to parse media streams"))
            null
        }

        progress(1f)
        vm.inFileFlow.update {
            Progressive.Done(informedFile, startedAt, Date())
        }

        return info
    }

    /** ⚠️ call only in the right conditions. no failsafes */
    suspend fun convert(
        job: Job?,
        outMime: MimeType,
        args: Map<FFmpegArg, String>
    ): MediaFile? {
        val media = vm.inFileFlow.value.done!!
        val outFile = media.fileInfo.file.toExtension(outMime.extension)

        return args.toCommand().runFlow(
            vm.outFileFlow,
            getDuration = media.mainStreamMime?.duration?.let { { it } },
        ) {
            val mediaInfo = FFInfo.getMediaInfo(outFile, outMime)
                ?: return@runFlow null
            MediaFile(outFile, outMime, mediaInfo)
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
                        FFmpegArg.Version,
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
                        FFmpegArg.Version,
                    ),
                    MimeType.AUDIO to listOf(
                        FFmpegArg.Input,
                        FFmpegArg.Speed,
                        FFmpegArg.Pitch,
                        FFmpegArg.BitrateAudio,
                        // FFmpegArg.Crf, //TODO: bitrate | crf . or remove crf
                        FFmpegArg.StartTimeByMs,
                        FFmpegArg.EndTimeByMs,
                        FFmpegArg.Version,
                    )
                )

        /**
         * sync:
         * - [ShareActivityVM.configShownFlow]
         * - [ShareActivityVM.configExpandFlow] entries
         *
         * based on [ShareActivityVM.shareInfoState]
         */
        @Composable
        fun SyncArgs() {
            val activity = LocalActivity.current as ShareActivity
            val placementState = LocalPlacementState.current
            val configListState = LocalConfigListState.current
            val shareInfo by activity.vm.shareInfoState
            if (shareInfo == null) return
            val configExpandFlow = activity.vm.configExpandFlow
            val configShownFlow = activity.vm.configShownFlow

            val outMime by shareInfo!!.inMime.outMimeSetting
                .rememberObject(false, shareInfo)

            suspend fun resetArgsExpand() {
                val args = ffmpegAllowedArgs[outMime.category] ?: emptyList()

                configShownFlow.update { args }

                configExpandFlow.update {
                    args.map { it.name }.associateWith { false }
                }
                configListState.animateScrollToItem(0)
            }

            LaunchedEffect(outMime.category) {
                resetArgsExpand()
            }
            LaunchedEffect(placementState.ratio) {
                if (placementState.ratio == 0f) resetArgsExpand()
            }

            LaunchedEffect(configExpandFlow) {
                var previous = emptyMap<String, Boolean>()

                configExpandFlow.collect {
                    val isExpandedAny = it.entries.firstOrNull { (key, isExpanded) ->
                        key != FFmpegArg.Input.name //! intentional: skip FFmpegArg.Input
                                && isExpanded
                                && previous[key] != true
                    }

                    //? expanded child => expand parent
                    if (isExpandedAny != null) {
                        placementState.animateRatio(1f)
                    }

                    previous = it
                }
            }
        }
    }
}