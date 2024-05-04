package com.galacticai.flareconverter.ui.share_activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.galacticai.flareconverter.ui.share_activity.components.ShareActivityView
import com.galacticai.flareconverter.ui.themes.GalacticTheme
import com.galacticai.flareconverter.util.AppDefaults.clearInputDir
import com.galacticai.flareconverter.util.AppDefaults.clearOutputDir
import com.galacticai.flareconverter.util.AppDefaults.inputDir
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

class ShareActivity : AppCompatActivity() {
    val vm by viewModels<ShareActivityVM>()
    val helpers = ShareActivityHelpers(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { GalacticTheme { ShareActivityView() } }
        vm.async { init() }
    }

    private suspend fun CoroutineScope.init() {
        //TODO: handle with settings (delete by age)
        clearInputDir(true)
        clearOutputDir(true)

        //? cache the supported codecs and formats
//        FFUtil.Info.CODECS_AVAILABLE
//        FFUtil.Info.FORMATS_AVAILABLE

        vm.shareInfoState.value = helpers.initFile(
            coroutineContext[Job],
            intent, inputDir, contentResolver
        )
    }

    override fun finish() {
        // TODO: use vm.isRunning() to alert user before finishing if needed
        vm.stopAll()
        super.finish()
    }
}
