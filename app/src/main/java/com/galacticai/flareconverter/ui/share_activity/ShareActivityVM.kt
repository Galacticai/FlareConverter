package com.galacticai.flareconverter.ui.share_activity

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.galacticai.flareconverter.models.MediaFile
import com.galacticai.flareconverter.models.ShareInfo
import com.galacticai.flareconverter.models.ffmpeg.ff_command.args.FFmpegArg
import global.common.models.progressive.Progressive
import global.common.models.progressive.Progressive.Companion.stop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ShareActivityVM : ViewModel() {
    val shareInfoState = mutableStateOf<ShareInfo?>(null)

//    /** read by FFmpeg (based on file) */
//    val mediaInfoState = mutableStateOf<MediaInformation?>(null)

    /** The shared file after being copied to the app input directory ([inputDir]) */
    val inFileFlow = Progressive.flow<MediaFile>()

    /** The converted file in the app output directory ([outputDir]) */
    val outFileFlow = Progressive.flow<MediaFile>()

    /** [FFmpegArg]s allowed within the current context */
    val configShownFlow = MutableStateFlow(listOf<FFmpegArg>())

    /** key = arg  |  value = parsed arg string */
    val configFlow = MutableStateFlow(mapOf<FFmpegArg, String>())

    /** key = arg key  |  value = ui is open (expanded) */
    val configExpandFlow = MutableStateFlow(mapOf<String, Boolean>())

    val flowsAll get() = listOf(inFileFlow, outFileFlow)
    val flowsRunning get() = flowsAll.filter { it.value is Progressive.Running }
    fun stopAll() = flowsRunning.forEach { it.stop() }

    fun async(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(Dispatchers.IO, block = block)
    }
}

