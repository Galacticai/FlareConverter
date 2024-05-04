package com.galacticai.flareconverter.ui.share_activity

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthenica.ffmpegkit.MediaInformation
import com.galacticai.flareconverter.models.ShareInfo
import com.galacticai.flareconverter.util.AppDefaults.inputDir
import com.galacticai.flareconverter.util.AppDefaults.outputDir
import global.common.models.progressive.Progressive
import global.common.models.progressive.Progressive.Companion.stop
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

data class InformedFile(
    val file: File,
    val info: MediaInformation,
)

class ShareActivityVM : ViewModel() {
    val shareInfoState = mutableStateOf<ShareInfo?>(null)

//    /** read by FFmpeg (based on file) */
//    val mediaInfoState = mutableStateOf<MediaInformation?>(null)

    /** The shared file after being copied to the app input directory ([inputDir]) */
    val inFileLive = Progressive.live<InformedFile>()

    /** The converted file in the app output directory ([outputDir]) */
    val outFileLive = Progressive.live<InformedFile>()

    val configExpandStates = mutableMapOf<String, MutableState<Boolean>>()

    val liveAll get() = listOf(inFileLive, outFileLive)
    val liveRunning get() = liveAll.filter { it.value is Progressive.Running }
    fun stopAll() {
        liveRunning.forEach { it.stop() }
    }

    fun async(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(Dispatchers.IO, block = block)
    }
}

