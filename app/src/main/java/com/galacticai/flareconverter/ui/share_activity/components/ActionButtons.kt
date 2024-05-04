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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import com.galacticai.flareconverter.ui.share_activity.ShareActivity
import com.galacticai.flareconverter.util.Consistent


@Composable
fun ActionButtons(modifier: Modifier = Modifier) {
    val isPreview = LocalInspectionMode.current
    val activity = LocalActivity.current as? ShareActivity

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
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(
                onClick = {
                    if (!isPreview) activity!!.finish()
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) { Text("Close") }
        }
    }
}