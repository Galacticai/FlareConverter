package com.galacticai.flareconverter.ui.activities.convert_activity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import global.common.models.FutureValue
import java.io.File

class ConvertActivityVM : ViewModel() {
    /** The shared file after being copied to the app input directory ([inputDir]) */
    val inFileLive = FutureValue.live<File>(FutureValue.Pending())

    /** The converted file in the app output directory ([outputDir]) */
    val outFileLive = FutureValue.live<File>(FutureValue.Pending())

    /** The selected frame out of the input file in case it's a video or it will be a [FutureValue.Failed.Error] */
    val inFileFrameLive: MutableLiveData<FutureValue<File>> =
        FutureValue.live(FutureValue.Pending())
}