package global.common.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.DimenRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import global.common.ui.DimenUtils.toDp

object DpUtil {
    fun Int.toDp(density: Float) = (this / density).dp
    fun Float.toDp(density: Float) = (this / density).dp

    fun Dp.toPx(density: Float) = this.value * density

    fun dimenDp(context: Context, @DimenRes id: Int): Dp {
        return Dp(
            context.resources.getDimension(id)
                    / context.resources.displayMetrics.density
        )
    }


    @SuppressLint("UnnecessaryComposedModifier")
    fun Modifier.onSizeChangedDp(onChange: (DpSize) -> Unit) = composed {
        val density = LocalDensity.current
        onSizeChanged { onChange(it.toDp(density)) }
    }

    fun PaddingValues.replace(
        direction: LayoutDirection,
        top: Dp? = null,
        bottom: Dp? = null,
        start: Dp? = null,
        end: Dp? = null,
    ) = if (listOf(top, bottom, start, end).all { it == null })
        this
    else PaddingValues(
        top = top ?: this.calculateTopPadding(),
        bottom = bottom ?: this.calculateBottomPadding(),
        start = start ?: this.calculateStartPadding(direction),
        end = end ?: this.calculateEndPadding(direction),
    )

}