package com.galacticai.flareconverter.ui.components.inputs.resolution_input

import android.util.Size
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.flareconverter.ui.components.inputs.resolution_input.components.ControlRow
import com.galacticai.flareconverter.ui.components.inputs.resolution_input.components.preview_box.PreviewBoxWrapper
import com.galacticai.flareconverter.ui.themes.GalacticTheme
import com.galacticai.flareconverter.util.Consistent
import com.galacticai.flareconverter.util.Pixels
import global.common.TextUtil.NON_BREAKING_SPACE

object ProperResolutionInput {
    @Composable
    fun textStyle(fontSize: TextUnit = 24.sp) = LocalTextStyle.current.copy(
        fontSize = fontSize,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )

    fun formatResolutionSpacer(spacer: Char? = NON_BREAKING_SPACE) =
        if (spacer == null) "×" else "$spacer×$spacer"
}


/** proportional resolution input (1 slider) */
@Composable
fun ResolutionInput(
    modifier: Modifier = Modifier,
    input: Size,
    tolerance: ResolutionTolerance = ResolutionTolerance(),
    maxBoxSize: Dp? = null,
    textStyle: TextStyle = ProperResolutionInput.textStyle(),
    previewScale: Float = .8f,
    onSelect: (Size) -> Unit,
) {
    val scope = ResolutionInputScope.remember(
        input,
        tolerance,
        maxBoxSize,
        textStyle,
        previewScale,
        onSelect
    )

    with(scope) {
        Column(
            Modifier.animateContentSize() then modifier,
            verticalArrangement = Arrangement.spacedBy(Consistent.Pad.regular),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PreviewBoxWrapper(this@Column)
            ControlRow()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SliderPreview1() {
    GalacticTheme {
        ResolutionInput(
            Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            Size(Pixels.P1080.p, Pixels.P1920.p),
            maxBoxSize = 400.dp
        ) {}
    }
}

@Preview(showBackground = true)
@Composable
private fun SliderPreview2() {
    GalacticTheme {
        ResolutionInput(
            Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            Size(Pixels.P1920.p, Pixels.P1920.p),
            maxBoxSize = 400.dp
        ) {}
    }
}

@Preview(showBackground = true)
@Composable
private fun SliderPreview3() {
    GalacticTheme {
        ResolutionInput(
            Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            Size(Pixels.P1920.p, Pixels.P1080.p),
            maxBoxSize = 400.dp
        ) {}
    }
}

