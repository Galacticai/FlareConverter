package global.common.ui


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection


object DimenUtils {

    fun IntSize.toDp(density: Density) = with(density) {
        DpSize(width.toDp(), height.toDp())
    }
    
    /** add padding to size */
    fun Modifier.paddedSize(
        width: Dp? = null, height: Dp? = null,
        pad: PaddingValues,
    ): Modifier {
        if (width == null && height == null) {
            throw IllegalArgumentException("must specify at least one of width or height")
        }

        var mod = this
        if (width != null) {
            //! intentional: direction doesn't matter because it's summing both anyway
            val padHorizontal = pad.calculateStartPadding(LayoutDirection.Ltr) +
                    pad.calculateEndPadding(LayoutDirection.Ltr)
            mod = mod.width(width + padHorizontal)
        }

        if (height != null) {
            val padVertical = pad.calculateTopPadding() + pad.calculateBottomPadding()
            mod = mod.height(height + padVertical)
        }

        return mod.padding(pad)
    }

    fun Modifier.paddedSize(size: DpSize, pad: PaddingValues) =
        this.paddedSize(size.width, size.height, pad)


    fun Modifier.vertical() = this then object : LayoutModifier {
        override fun MeasureScope.measure(
            measurable: Measurable,
            constraints: Constraints
        ): MeasureResult {
            val placeable = measurable.measure(
                constraints.copy(
                    minWidth = constraints.minHeight,
                    maxWidth = constraints.maxHeight,
                    minHeight = constraints.minWidth,
                    maxHeight = constraints.maxWidth
                )
            )
            return layout(placeable.height, placeable.width) {
                placeable.placeWithLayer(
                    x = -(placeable.width - placeable.height) / 2,
                    y = (placeable.width - placeable.height) / 2
                ) {
                    rotationZ = -90f
                    transformOrigin = TransformOrigin.Center
                }
            }
        }

        override fun IntrinsicMeasureScope.minIntrinsicWidth(
            measurable: IntrinsicMeasurable,
            height: Int
        ): Int =
            measurable.minIntrinsicHeight(height)

        override fun IntrinsicMeasureScope.maxIntrinsicWidth(
            measurable: IntrinsicMeasurable,
            height: Int
        ): Int =
            measurable.maxIntrinsicHeight(height)

        override fun IntrinsicMeasureScope.minIntrinsicHeight(
            measurable: IntrinsicMeasurable,
            width: Int
        ): Int =
            measurable.minIntrinsicWidth(width)

        override fun IntrinsicMeasureScope.maxIntrinsicHeight(
            measurable: IntrinsicMeasurable,
            width: Int
        ): Int =
            measurable.maxIntrinsicWidth(width)
    }
}