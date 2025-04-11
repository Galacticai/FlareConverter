package com.galacticai.flareconverter.ui

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.StatisticsCallback
import com.galacticai.flareconverter.models.FFmpegCommand
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.models.ShareInfo
import com.galacticai.flareconverter.models.exceptions.InvalidLaunchCommand
import com.galacticai.flareconverter.util.AppDefaults.inputDir
import com.galacticai.flareconverter.util.AppDefaults.outputDir
import com.galacticai.flareconverter.util.MediaUtils
import global.common.IOUtils.child
import global.common.IOUtils.copyToFile
import global.common.models.FutureValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.time.Duration
import java.time.Instant
import java.util.Date
import java.util.UUID

class ConvertActivityVM : ViewModel() {
    /** The shared file after being copied to the app input directory ([inputDir]) */
    val inFileLive = FutureValue.live<File>(FutureValue.Pending())

    /** The converted file in the app output directory ([outputDir]) */
    val outFileLive = FutureValue.live<File>(FutureValue.Pending())

    /** The selected frame out of the input file in case it's a video or it will be a [FutureValue.Failed.Error] */
    val inFileFrameLive: MutableLiveData<FutureValue<File>> =
        FutureValue.live(FutureValue.Pending())

    private fun invalid(msg: String) =
        InvalidLaunchCommand(ConvertActivity::class.java, msg)

    private fun invalidInput(msg: String) =
        inFileLive.postValue(FutureValue.Failed.Error(invalid(msg)))

    private fun invalidOutput(msg: String) =
        outFileLive.postValue(FutureValue.Failed.Error(invalid(msg)))

    /** Prepare the files for the conversion process
     *  @return the shared file after copying it to the app input directory */
    fun initFile(
        intent: Intent?,
        inputDir: File,
        contentResolver: ContentResolver
    ): ShareInfo? {
        inFileLive.postValue(FutureValue.Running())

        val info = ShareInfo.getShareInfo(intent, contentResolver, ::invalidInput)
            ?: return null
        val uuid = UUID.randomUUID().toString()
        val inputFile = inputDir.child("$uuid.${info.extension}")

        viewModelScope.launch(Dispatchers.IO) {
            contentResolver.openInputStream(info.uri)?.copyToFile(inputFile)
                ?: return@launch invalidOutput("Failed to copy file")
            inFileLive.postValue(FutureValue.Finished(inputFile))

            val mimeCategory = info.mime.split('/')[0]
            when (mimeCategory) {
                MimeType.IMAGE -> inFileFrameLive.postValue(FutureValue.Finished(inputFile))
                MimeType.VIDEO -> MediaUtils.postVideoFrame(inputFile, live = inFileFrameLive)
                else -> inFileFrameLive.postValue(
                    FutureValue.Failed.Error(
                        IllegalStateException("Unsupported mime type: ${info.mime}")
                    )
                )
            }
        }
        return info
    }

    fun convert(
        context: Context,
        outMime: MimeType,
        onStatistics: StatisticsCallback? = null,
        onFinished: (finished: FutureValue.Finished<File>) -> Unit,
    ) {
        val inFile = inFileLive.value
            ?.finishedValue
            ?.let {
                // intentionally fall back to invalidOutput if not exists or not a file
                if (it.exists() || !it.isFile) it else null
            }
            ?: return invalidOutput("Invalid input file")

        val outFile = context.outputDir.child(
            "${inFile.nameWithoutExtension}.${outMime.extension}"
        )

        val job = Job()
        val startedAt = Date()

        outFileLive.postValue(FutureValue.Running(job, startedAt))

        viewModelScope.launch(Dispatchers.IO + job) {
            val cmd = FFmpegCommand().io(inFile.absolutePath, outFile.absolutePath)
            FFmpegKit.executeAsync(
                cmd.argsOnly,
                {
                    val runtime = Duration.between(startedAt.toInstant(), Instant.now())
                    val finished = FutureValue.Finished(outFile, startedAt, runtime)
                    viewModelScope.launch(Dispatchers.Main) {
                        // set instantly (requires main thread) rather than waiting for postValue
                        outFileLive.value = finished
                        onFinished(finished)
                    }
                },
                {

                },
                onStatistics
            )
        }
    }


    /** Key is the [MimeType.category] */
    val ffmpegOptions: Map<String, *> =
        mapOf(
            MimeType.IMAGE to listOf(
                FFmpegCommand::startTime,
                FFmpegCommand::endTime,
                FFmpegCommand::bitrateVideo,
            ),
            MimeType.VIDEO to listOf(

            ),
        )
}

