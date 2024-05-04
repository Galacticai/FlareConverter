package com.galacticai.flareconverter.ui.share_activity.components

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.galacticai.flareconverter.models.ConvertStage
import com.galacticai.flareconverter.ui.share_activity.ShareActivity
import com.galacticai.flareconverter.ui.share_activity.components.LocalShareActivityStates.LocalConvertStage
import com.galacticai.flareconverter.util.Consistent
import com.galacticai.flareconverter.util.Consistent.Animation.rememberExpressiveButtonState
import com.galacticai.flareconverter.util.MimeTypeUtils.outMimeSetting
import kotlinx.coroutines.Job

val ACTION_HEIGHT = 48.dp

@Composable
fun ActionButtons(modifier: Modifier = Modifier) {
    Box(
        modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .fillMaxWidth()
            .height(Consistent.Size.actions + (Consistent.Pad.small * 2))
            .padding(
                horizontal = Consistent.Pad.regular,
                vertical = Consistent.Pad.small
            )
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CloseButton()
            ConvertButton()
        }
    }
}

@Composable
private fun CloseButton() {
    val activity = LocalActivity.current as ShareActivity
    TextButton(
        modifier = Modifier.height(ACTION_HEIGHT),
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        ),
        onClick = { activity.finish() },
    ) { Text("Close") }
}

@Composable
private fun ConvertButton() {
    val activity = LocalActivity.current as ShareActivity
    val convertStage = LocalConvertStage.current
    val config by activity.vm.configFlow.collectAsState()
    val inFile by activity.vm.inFileFlow.collectAsState()
    val inFileDone = inFile.done
    var running by remember { mutableStateOf(false) }
    val enabled = remember(inFile, running, convertStage) {
        inFileDone != null && !running
                && convertStage < ConvertStage.Converting
    }

    val outMimeState = inFileDone?.mime?.outMimeSetting
        ?.rememberObject(false, inFileDone)

    val expressive = rememberExpressiveButtonState()
    Button(
        enabled = enabled,
        modifier = Modifier.height(ACTION_HEIGHT).let {
            if (enabled) it then expressive.modifierAll
            else it
        },
        shape = expressive.shape,
        onClick = {
            if (!enabled) return@Button //failsafe
            activity.vm.async {
                running = true
                activity.helpers.convert(coroutineContext[Job], outMimeState!!.value, config)
                running = false
            }
        },
    ) { Text("Convert") }
}