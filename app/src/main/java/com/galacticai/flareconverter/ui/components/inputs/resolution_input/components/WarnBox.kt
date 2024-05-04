package com.galacticai.flareconverter.ui.components.inputs.resolution_input.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import com.galacticai.flareconverter.ui.components.InfoBox
import com.galacticai.flareconverter.ui.components.inputs.resolution_input.ResolutionInputScope
import com.galacticai.flareconverter.util.Consistent
import global.common.util.TextUtil.ELLIPSES

@Composable
fun ResolutionInputScope.WarnBox(columnScope: ColumnScope, boxScope: BoxScope) {
    val isBig = remember(height, input.height) {
        height > input.height
    }
    columnScope.AnimatedVisibility(
        deltaIsBig,
        modifier = Modifier
            .padding(Consistent.Pad.regular) then
                with(boxScope) { Modifier.align(Alignment.TopCenter) },
        enter = fadeIn() + scaleIn(initialScale = .85f),
        exit = scaleOut(targetScale = .85f) + fadeOut(),
    ) {
        InfoBox(
            Modifier.scale(.75f),
            elevation = Consistent.Elevation.elevationLow,
            config = InfoBox.Config.warnOutline
        ) {
            Text(
                "Too ${if (isBig) "big" else "small"}$ELLIPSES",
                maxLines = 1, softWrap = false
            )
        }
    }
}