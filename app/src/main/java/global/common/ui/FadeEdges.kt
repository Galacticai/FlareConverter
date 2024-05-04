package global.common.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.galacticai.flareconverter.ui.components.FFmpegArgView
import com.galacticai.flareconverter.util.Consistent
import java.io.File
import kotlin.math.pow

enum class FadeDirection { Vertical, Horizontal }

@Composable
fun FadeEdges(
    size: Dp,
    direction: FadeDirection,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.background,
    edgeAlpha: Float = 1f,
    beginAlpha: Float = 1f,
    endAlpha: Float = 1f,
//    animationMs: Int = 2000
) {
    val beginAnim by animateFloatAsState(
        targetValue = beginAlpha,
        animationSpec = tween(600),
        label = "fadeBeginAlpha",
    )
    val endAnim by animateFloatAsState(
        targetValue = endAlpha,
        animationSpec = tween(600),
        label = "fadeEndAlpha"
    )

    val stops = 16 // Sufficient for smooth quadratic fade

    val beginBrush = remember(color, edgeAlpha, direction) {
        when (direction) {
            FadeDirection.Vertical -> Brush.verticalGradient(
                List(stops) { i ->
                    val t = i.toFloat() / (stops - 1)
                    color.copy(alpha = edgeAlpha * (1f - t).pow(2))
                }
            )

            FadeDirection.Horizontal -> Brush.horizontalGradient(
                List(stops) { i ->
                    val t = i.toFloat() / (stops - 1)
                    color.copy(alpha = edgeAlpha * (1f - t).pow(2))
                }
            )
        }
    }

    val endBrush = remember(color, edgeAlpha, direction) {
        when (direction) {
            FadeDirection.Vertical -> Brush.verticalGradient(
                List(stops) { i ->
                    val t = i.toFloat() / (stops - 1)
                    color.copy(alpha = edgeAlpha * t.pow(2))
                }
            )

            FadeDirection.Horizontal -> Brush.horizontalGradient(
                List(stops) { i ->
                    val t = i.toFloat() / (stops - 1)
                    color.copy(alpha = edgeAlpha * t.pow(2))
                }
            )
        }
    }

    Box(modifier.fillMaxSize()) {
        //? begin
        Box(
            modifier = Modifier
                .align(
                    if (direction == FadeDirection.Vertical)
                        Alignment.TopCenter
                    else Alignment.CenterStart
                )
                .then(
                    if (direction == FadeDirection.Vertical)
                        Modifier
                            .fillMaxWidth()
                            .height(size)
                    else Modifier
                        .fillMaxHeight()
                        .width(size)
                )
                .graphicsLayer { alpha = beginAnim }
                .background(beginBrush)
        )

        //? end
        Box(
            modifier = Modifier
                .align(
                    if (direction == FadeDirection.Vertical)
                        Alignment.BottomCenter
                    else Alignment.CenterEnd
                )
                .then(
                    if (direction == FadeDirection.Vertical)
                        Modifier
                            .fillMaxWidth()
                            .height(size)
                    else Modifier
                        .fillMaxHeight()
                        .width(size)
                )
                .graphicsLayer { alpha = endAnim }
                .background(endBrush)
        )
    }
}


@Composable
fun FadeEdgesContainer(
    size: Dp,
    direction: FadeDirection,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.background,
    edgeAlpha: Float = 1f,
    beginAlpha: Float = 1f,
    endAlpha: Float = 1f,
    content: @Composable BoxScope.() -> Unit
) {
    Box {
        content()
        FadeEdges(
            size,
            direction,
            modifier,
            color,
            edgeAlpha,
            beginAlpha,
            endAlpha,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FadeEdgesPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .size(800.dp, 600.dp) // caller sets bounds
        ) {
            LazyColumn(Modifier.matchParentSize()) {
                items(30) { index ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .padding(vertical = 1.dp)
                            .background(
                                MaterialTheme.colorScheme.surface,
                                shape = MaterialTheme.shapes.medium
                            )
                    )
                }
            }

            FadeEdges(
                size = 400.dp,
                direction = FadeDirection.Vertical,
                modifier = Modifier.matchParentSize(),
                color = MaterialTheme.colorScheme.primary,
                beginAlpha = 0f
            )
            FadeEdges(
                size = 40.dp,
                direction = FadeDirection.Horizontal,
                modifier = Modifier.matchParentSize(),
                color = MaterialTheme.colorScheme.error,
                beginAlpha = 0f
            )
        }
    }
}
