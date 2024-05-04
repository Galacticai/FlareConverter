package global.common

import android.content.Context
import androidx.annotation.DimenRes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object DpUtils {
    fun Int.toDp(density: Float) = (this / density).dp
    fun Float.toDp(density: Float) = (this / density).dp

    fun Dp.toPx(density: Float) = this.value * density

    fun dimenDp(context: Context, @DimenRes id: Int): Dp {
        return Dp(
            context.resources.getDimension(id)
                    / context.resources.displayMetrics.density
        )
    }
}