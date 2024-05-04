package com.galacticai.flareconverter.ui.share_activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.viewModelScope
import com.galacticai.flareconverter.ui.share_activity.components.ShareActivityView
import com.galacticai.flareconverter.ui.themes.GalacticTheme
import com.galacticai.flareconverter.util.AppDefaults.clearInputDir
import com.galacticai.flareconverter.util.AppDefaults.clearOutputDir
import com.galacticai.flareconverter.util.AppDefaults.inputDir

class ShareActivity : AppCompatActivity() {
    val vm by viewModels<ShareActivityVM>()
    val helpers = ShareActivityHelpers(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        vm.async { initPre() }
        setContent { GalacticTheme { ShareActivityView() } }
        initPost()
    }

    private fun initPre() {
        //TODO: handle with settings (delete by age)
        clearInputDir(true)
        clearOutputDir(true)

        vm.shareInfoState.value = helpers.initFile(
            intent,
            inputDir,
            contentResolver,
            vm.viewModelScope
        )
    }


    private fun initPost() {
    }

    override fun finish() {
        // TODO: use vm.isRunning() to alert user before finishing if needed
        vm.stopAll()
        super.finish()
    }
}
