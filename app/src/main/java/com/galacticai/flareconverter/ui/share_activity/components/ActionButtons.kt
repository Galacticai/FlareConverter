package com.galacticai.flareconverter.ui.share_activity.components

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.galacticai.flareconverter.ui.share_activity.ShareActivity
import com.galacticai.flareconverter.util.Consistent
import com.galacticai.flareconverter.util.Consistent.Animation.rememberExpressiveButtonState
import com.galacticai.flareconverter.util.MimeTypeUtils.outMimeSetting


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
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        ),
        onClick = { activity.finish() },
    ) { Text("Close") }
}

@Composable
private fun ConvertButton() {
    val activity = LocalActivity.current as ShareActivity
    val shareInfo by activity.vm.shareInfoState

    AnimatedVisibility(
        shareInfo != null,
        enter = Consistent.Animation.inUp,
        exit = Consistent.Animation.outDown,
    ) {
        if (shareInfo == null) return@AnimatedVisibility

        val config by activity.vm.configFlow.collectAsState()
        val outMime by shareInfo!!.inMime.outMimeSetting
            .rememberObject(false, shareInfo)

        val expressive = rememberExpressiveButtonState()
        Button(
            modifier = expressive.modifierAll,
            shape = expressive.shape,
            onClick = {
                activity.helpers.convert(outMime, config)
            },
        ) { Text("Convert") }
    }
}