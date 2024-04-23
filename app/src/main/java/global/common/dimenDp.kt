package global.common

import android.content.Context
import androidx.annotation.DimenRes
import androidx.compose.ui.unit.Dp

fun dimenDp(context: Context, @DimenRes id: Int): Dp {
    return Dp(
        context.resources.getDimension(id)
                / context.resources.displayMetrics.density
    )
}