package com.galacticai.flareconverter.ui.components.inputs.resolution_input.components.preview_box

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.flareconverter.ui.components.inputs.resolution_input.ProperResolutionInput.formatResolutionSpacer
import com.galacticai.flareconverter.ui.components.inputs.resolution_input.ResolutionInputScope
import com.galacticai.flareconverter.ui.components.inputs.resolution_input.components.WarnBox
import com.galacticai.flareconverter.util.Consistent
import global.common.ui.animtion.AnimatedText
import kotlin.math.roundToInt

@Composable
fun ResolutionInputScope.PreviewBoxWrapper(columnScope: ColumnScope) {
    Box(contentAlignment = Alignment.Center) {
        BoxWithConstraints(
            modifier = constraintsModifier,
            contentAlignment = Alignment.Center,
        ) {
            val constrainedPreviewScope = ConstrainedPreviewScope.remember(
                this@PreviewBoxWrapper,
                this@BoxWithConstraints
            )
            with(constrainedPreviewScope) {
                PrimaryPreview()
                SecondaryPreviews(columnScope)
            }
        }
        WarnBox(columnScope, this@Box)
        ResolutionNumber()
    }
}

@Composable
fun ConstrainedPreviewScope.PrimaryPreview() {
    Card(
        Modifier.fillMaxSize(),
        shape = Consistent.Shape.Rounded.all,
        colors = CardDefaults.cardColors()
            .copy(colors.primaryContainer)
    ) {}
    Card(
        Modifier.size(widthAnimated, heightAnimated),
        border = BorderStroke(
            2.dp,
            MaterialTheme.colorScheme.primary,
        ),
        colors = CardDefaults.cardColors()
            .copy(colors.secondaryContainer),
        shape = Consistent.Shape.Rounded.all
    ) {}
}

@Composable
private fun ConstrainedPreviewScope.SecondaryPreviews(
    columnScope: ColumnScope,
) = entries.forEach {
    val size = remember(scaleAnimated) {
        DpSize(
            (it.width * scaleAnimated).roundToInt().dp,
            (it.height * scaleAnimated).roundToInt().dp,
        )
    }
    val visible = remember(size, this.tolerance) {
        size.width >= this.tolerance.minVisibleSize &&
                size.height >= this.tolerance.minVisibleSize &&
                size.width <= maxWidth + this.tolerance.exitPad
    }

    val isInput = remember(input.height) {
        it.height == input.height
    }
    val borderColor = remember(isInput) {
        if (isInput) colors.secondary else colors.secondary.copy(.1f)
    }

    val visibleLabel = remember(isInput, size, tolerance) {
        isInput &&
                size.width >= tolerance.minVisibleSize * 2 &&
                size.height >= tolerance.minVisibleSize * 2
    }

    columnScope.AnimatedVisibility(
        visible,
        enter = fadeIn(), exit = fadeOut(),
    ) {
        Box(
            Modifier
                .size(size.width, size.height)
                .border(
                    2.dp,
                    borderColor,
                    Consistent.Shape.Rounded.all
                ),
            contentAlignment = Alignment.BottomCenter
        ) {
            val labelScale = remember(size.width) {
                (size.width / 150.dp).coerceIn(0.6f, 1.2f)
            }
            columnScope.AnimatedVisibility(
                visible = visibleLabel,
                enter = fadeIn(), exit = fadeOut(),
                modifier = Modifier
                    .padding(Consistent.Pad.regular)
                    .scale(labelScale),
            ) {
                Card(
                    shape = Consistent.Shape.Rounded.pill,
                    elevation = Consistent.Elevation.elevationLow,
                    colors = CardDefaults.cardColors()
                        .copy(this@SecondaryPreviews.colors.secondaryContainer),
                ) {
                    Text(
                        "${it.width}${formatResolutionSpacer()}${it.height}",
                        style = this@SecondaryPreviews.textStyle.copy(fontSize = 10.sp),
                        maxLines = 1, softWrap = false,
                        color = this@SecondaryPreviews.colors.secondary,
                        modifier = Modifier.padding(horizontal = Consistent.Pad.smallX),
                    )
                }
            }

        }
    }
}

@Composable
fun ResolutionInputScope.ResolutionNumber() {
    Card(
        shape = Consistent.Shape.Rounded.pill,
        elevation = Consistent.Elevation.elevationMedium,
        colors = CardDefaults.cardColors().copy(colorsAnimated.first),
    ) {
        Row(
            Modifier
                .animateContentSize()
                .align(Alignment.CenterHorizontally)
                .padding(
                    horizontal = Consistent.Pad.regular,
                    vertical = Consistent.Pad.smallX
                ),
            horizontalArrangement = Arrangement.Center,
        ) {
            listOf(width, height).forEachIndexed { i, v ->
                if (i > 0) {
                    Text(
                        formatResolutionSpacer(),
                        style = textStyle,
                        color = colorsAnimated.second
                    )
                }

                AnimatedText(v.toString()) { c, modifier ->
                    Text(
                        c.toString(),
                        modifier = modifier,
                        style = textStyle,
                        color = colorsAnimated.second
                    )
                }
            }
        }
    }
}