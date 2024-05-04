package com.galacticai.flareconverter.ui.components.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.ui.components.expressive.rememberExpressiveRadiusSize
import com.galacticai.flareconverter.ui.components.mime_type.MimeTypeView
import com.galacticai.flareconverter.ui.themes.GalacticTheme
import com.galacticai.flareconverter.util.Consistent
import com.galacticai.flareconverter.util.Consistent.Animation.rememberExpressiveButtonState
import global.common.util.TextUtil.ELLIPSES
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

object InitFileConvertDialog {
    class Params(
        val from: MimeType, val to: MimeType,
        val onReject: () -> Unit,
        val onConfirm: () -> Unit,
    )

    /** not null = show dialog */
    var infoState = mutableStateOf<Params?>(null)

    fun show(
        from: MimeType, to: MimeType,
        onReject: () -> Unit,
        onConfirm: () -> Unit,
    ) {
        infoState.value = Params(from, to, onReject, onConfirm)
    }
}


@Composable
fun InitFileConvertDialog() {
    var info by remember(InitFileConvertDialog.infoState) { InitFileConvertDialog.infoState }

    if (info == null) return

    AlertDialog(
        onDismissRequest = {
            info!!.onReject()
            info = null
        },
        icon = {
            Icon(Icons.Rounded.Category, "convert icon")
        },
        title = { Text("Preparation needed") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Consistent.Pad.regular)) {
                Text("The specified file is unsupported so we need to convert it first$ELLIPSES")
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    //verticalArrangement = Arrangement.spacedBy(Consistent.Pad.smallXX)
                ) {
                    MimeTypeView(info!!.from, label = "From")
                    Icon(
                        Icons.Rounded.KeyboardArrowDown, "",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    MimeTypeView(info!!.to, label = "To")
                }
                Text("Then we can work with the supported version")
            }
        },
        confirmButton = {
            val expressive =  rememberExpressiveButtonState()
            Button(
                onClick = {
                    info!!.onConfirm()
                    info = null
                },
                modifier = expressive.modifierAll,
                shape = expressive.shape,
            ) { Text("Prepare") }
        },
        dismissButton = {
            val expressive = rememberExpressiveButtonState()
            TextButton(
                onClick = { info = null },
                modifier = expressive.modifierAll,
                shape = expressive.shape,
                colors = ButtonDefaults.textButtonColors().copy(
                    MaterialTheme.colorScheme.errorContainer,
                    MaterialTheme.colorScheme.onErrorContainer
                ),
            ) { Text("Exit") }
        }
    )
}

@Preview(showSystemUi = true)
@Composable
private fun Preview() = GalacticTheme {
    var info by remember { InitFileConvertDialog.infoState }
    //? somewhere else...
    InitFileConvertDialog.show(
        MimeType.Heic, MimeType.Png,
        {}, {}
    )
    LaunchedEffect(info) {
        if (info != null) return@LaunchedEffect
        delay(2.seconds)

    }
    InitFileConvertDialog()
}