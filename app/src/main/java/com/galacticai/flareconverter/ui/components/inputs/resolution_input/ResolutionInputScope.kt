package com.galacticai.flareconverter.ui.components.inputs.resolution_input

import android.util.Size
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import com.galacticai.flareconverter.ui.components.inputs.util.getWarnColors
import com.galacticai.flareconverter.util.Pixels
import kotlin.math.roundToInt

/** [ResolutionInputScope] marker to prevent leaking scope members to nested layouts */
@LayoutScopeMarker
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class ResolutionInputScopeMarker

/** [ResolutionInput] scope */
@ResolutionInputScopeMarker
@Stable
interface ResolutionInputScope {
    // Parameters
    val input: Size
    val tolerance: ResolutionTolerance
    val maxBoxSize: Dp?
    val textStyle: TextStyle
    val previewScale: Float

    // State & Derived Fields
    val colors: ColorScheme
    val ratio: Float
    val width: Int
    val height: Int
    val entries: List<Size>
    val iInitial: Int
    val iSelected: Int
    val delta: Pair<Float, Float>
    val deltaIsBig: Boolean
    val colorsAnimated: Pair<Color, Color>
    val constraintsModifier: Modifier

    fun setHeight(value: Int)


    companion object {
        fun pickEntries(ratio: Float, input: Size, tolerance: ResolutionTolerance): List<Size> {
            val base = Pixels.entries.map { it.p } + input.width + input.height

            val sizes = base
                .distinct()
                .sorted()
                .map { Size((it * ratio).roundToInt(), it) }

            return sizes.filter {
                val wDelta = it.width.toFloat() / input.width
                val hDelta = it.height.toFloat() / input.height

                val isValidRatio = wDelta in tolerance.boundary && hDelta in tolerance.boundary
                val isValidPx =
                    it.width in tolerance.boundaryPx && it.height in tolerance.boundaryPx

                isValidRatio && isValidPx
            }
        }

        @Composable
        fun remember(
            input: Size,
            tolerance: ResolutionTolerance,
            maxBoxSize: Dp?,
            textStyle: TextStyle,
            previewScale: Float,
            onSelect: (Size) -> Unit
        ): ResolutionInputScope {
            val colors = MaterialTheme.colorScheme
            val ratio = androidx.compose.runtime.remember(input) {
                input.width.toFloat() / input.height
            }

            var height by rememberSaveable(input.height) { mutableIntStateOf(input.height) }
            val width = remember(height, ratio) { (height * ratio).roundToInt() }

            val entries = remember(ratio, input, tolerance) {
                pickEntries(ratio, input, tolerance)
            }
            val initialIndex = remember(input.height) {
                entries.indexOfFirst { it.height == input.height }
            }
            val selectedIndex = remember(height) {
                entries.indexOfFirst { it.height == height }
            }

            val delta = remember(input, width, height) {
                val w = width.toFloat() / input.width
                val h = height.toFloat() / input.height
                w to h
            }
            val deltaIsBig = remember(delta, tolerance) {
                val wWarn = delta.first !in tolerance.warn
                val hWarn = delta.second !in tolerance.warn
                wWarn || hWarn
            }
            val colorsAnimated = getWarnColors(
                deltaIsBig,
                invertPrimary = true, invertWarn = true
            )

            val constraintsModifier = remember(ratio, maxBoxSize) {
                val base =
                    if (maxBoxSize == null) Modifier.fillMaxWidth()
                    else Modifier.sizeIn(maxWidth = maxBoxSize, maxHeight = maxBoxSize)
                base.aspectRatio(ratio)
            }

            LaunchedEffect(width, height) {
                onSelect(Size(width, height))
            }

            return remember(
                input, tolerance, maxBoxSize, textStyle, previewScale,
                colors, ratio, width, height, entries, initialIndex, selectedIndex,
                delta, deltaIsBig, colorsAnimated, constraintsModifier
            ) {
                object : ResolutionInputScope {
                    override val input = input
                    override val tolerance = tolerance
                    override val maxBoxSize = maxBoxSize
                    override val textStyle = textStyle
                    override val previewScale = previewScale
                    override val colors = colors
                    override val ratio = ratio
                    override val width = width
                    override val height = height
                    override val entries = entries
                    override val iInitial = initialIndex
                    override val iSelected = selectedIndex
                    override val delta = delta
                    override val deltaIsBig = deltaIsBig
                    override val colorsAnimated = colorsAnimated
                    override val constraintsModifier = constraintsModifier
                    override fun setHeight(value: Int) {
                        height = value
                    }
                }
            }
        }
    }
}