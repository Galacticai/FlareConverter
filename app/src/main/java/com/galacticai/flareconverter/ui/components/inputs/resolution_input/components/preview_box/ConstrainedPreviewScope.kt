package com.galacticai.flareconverter.ui.components.inputs.resolution_input.components.preview_box

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.galacticai.flareconverter.ui.components.inputs.resolution_input.ResolutionInputScope
import com.galacticai.flareconverter.ui.components.inputs.resolution_input.ResolutionInputScopeMarker
import kotlin.math.abs
import kotlin.math.roundToInt

/** [PreviewBoxWrapper] scope, combining resolution state with layout constraint */
@ResolutionInputScopeMarker
@Stable
interface ConstrainedPreviewScope : ResolutionInputScope, BoxWithConstraintsScope {
    val scaleTarget: Float
    val scaleAnimated: Float
    val widthAnimated: Dp
    val heightAnimated: Dp

    companion object {
        /** remember [ConstrainedPreviewScope] within [BoxWithConstraintsScope] */
        @Composable
        fun remember(
            resolutionInputScope: ResolutionInputScope,
            boxWithConstraintsScope: BoxWithConstraintsScope
        ): ConstrainedPreviewScope = with(resolutionInputScope) {
            val targetScale = remember(
                width,
                input.width,
                boxWithConstraintsScope.maxWidth,
                previewScale,
                iSelected,
                iInitial
            ) {
                val ideal = (boxWithConstraintsScope.maxWidth.value * previewScale) / width
                val maxScale = boxWithConstraintsScope.maxWidth.value / input.width
                val minScale = tolerance.minVisibleSize.value / input.width

                val clampWeight = if (iSelected == -1 || iInitial == -1) {
                    0f
                } else {
                    val dist = abs(iSelected - iInitial)
                    if (dist <= tolerance.stickySteps) 1f else 0f
                }

                val clamped = ideal.coerceIn(minScale, maxScale)
                ideal + (clamped - ideal) * clampWeight
            }

            val scaleAnimated by animateFloatAsState(
                targetValue = targetScale,
                animationSpec = tween(),
                label = "scale"
            )

            val widthAnimated by animateDpAsState(
                targetValue = min(
                    (width * scaleAnimated).roundToInt().dp,
                    boxWithConstraintsScope.maxWidth
                ),
                animationSpec = tween(),
                label = "card width"
            )

            val heightAnimated by animateDpAsState(
                targetValue = min(
                    ((height * scaleAnimated).roundToInt()).dp,
                    boxWithConstraintsScope.maxHeight
                ),
                animationSpec = tween(),
                label = "card height"
            )

            return remember(
                this,
                boxWithConstraintsScope,
                targetScale,
                scaleAnimated,
                widthAnimated,
                heightAnimated,
            ) {
                object : ConstrainedPreviewScope,
                    ResolutionInputScope by this,
                    BoxWithConstraintsScope by boxWithConstraintsScope {
                    override val scaleTarget = targetScale
                    override val scaleAnimated = scaleAnimated
                    override val widthAnimated = widthAnimated
                    override val heightAnimated = heightAnimated
                }
            }
        }
    }
}