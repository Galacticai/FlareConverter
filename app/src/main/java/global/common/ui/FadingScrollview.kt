package global.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize

@Composable
fun FadingScrollView(
    orientation: Orientation,
    length: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var scrollPosition by remember { mutableIntStateOf(0) }
    val scrollState = rememberScrollState(scrollPosition)
    var contentSize by remember { mutableStateOf(Size.Zero) }

    Box(
        modifier = Modifier
            .scrollable(scrollState, orientation)
            .onGloballyPositioned { contentSize = it.size.toSize() }
            .background(
                brush = when (orientation) {
                    Orientation.Horizontal -> {
                        val gradientWidth = contentSize.width
                        val gradientLength =
                            gradientLength(scrollPosition, gradientWidth, length)
                        Brush.horizontalGradient(
                            0f to Color.Transparent,
                            16f to Color.Transparent,
                            gradientWidth - gradientLength to Color.Transparent,
                            gradientWidth to Color.Black
                        )
                    }

                    Orientation.Vertical -> {
                        val gradientHeight = contentSize.height
                        val gradientLength =
                            gradientLength(scrollPosition, gradientHeight, length)
                        Brush.verticalGradient(
                            0f to Color.Transparent,
                            16f to Color.Transparent,
                            gradientHeight - gradientLength to Color.Transparent,
                            gradientHeight to Color.Black
                        )
                    }
                }
            )
            .then(modifier),
    ) {
        // Observe scroll position
        LaunchedEffect(key1 = scrollPosition) {
            snapshotFlow { scrollPosition }.collect { scrollPosition = it }
        }
        content()
    }
}

private fun gradientLength(
    scrollPosition: Int,
    containerSize: Float,
    length: Dp
): Float {
    val percentage = (length - scrollPosition.dp) / length
    return containerSize * percentage
}
