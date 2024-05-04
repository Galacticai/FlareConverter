package com.galacticai.flareconverter.ui.activities.convert_activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.mutableStateOf
import com.galacticai.flareconverter.models.ShareInfo
import com.galacticai.flareconverter.util.AppDefaults.clearInputDir
import com.galacticai.flareconverter.util.AppDefaults.clearOutputDir
import com.galacticai.flareconverter.util.AppDefaults.inputDir

class ConvertActivity : ComponentActivity() {
    val vm by viewModels<ConvertActivityVM>()
    val helpers = ConvertActivityHelpers(this)

    val shareInfoState = mutableStateOf<ShareInfo?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { View() }

        //TODO: handle with settings (delete by age)
        clearInputDir(true)
        clearOutputDir(true)
        shareInfoState.value = helpers.initFile(intent, inputDir, contentResolver)
    }
}