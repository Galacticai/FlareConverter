package global.common.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ln

enum class FadeDirection { Vertical, Horizontal }

fun Modifier.fadeEdges(
    size: Dp,
    direction: FadeDirection,
    edgeAlpha: Float = 1f,
    beginAlpha: Float = 1f,
    endAlpha: Float = 1f,
): Modifier = composed {
    val density = LocalDensity.current
    val fadeSizePx = with(density) { size.toPx() }

    val beginAlphaAnimated by animateFloatAsState(
        targetValue = beginAlpha.coerceIn(0f, 1f),
        animationSpec = tween(600),
        label = "fadeBeginAlpha",
    )

    val endAlphaAnimated by animateFloatAsState(
        targetValue = endAlpha.coerceIn(0f, 1f),
        animationSpec = tween(600),
        label = "fadeEndAlpha",
    )

    graphicsLayer {
        compositingStrategy = CompositingStrategy.Offscreen
    }.drawWithCache {
        val max = when (direction) {
            FadeDirection.Vertical -> this.size.height
            FadeDirection.Horizontal -> this.size.width
        }
        val fadeFraction = if (max > 0f) fadeSizePx / max else 0f

        val colorStops = curvedFadeStops(
            fadeFraction = fadeFraction,
            edgeAlpha = edgeAlpha,
            beginAlpha = beginAlphaAnimated,
            endAlpha = endAlphaAnimated,
        )

        val brush = when (direction) {
            FadeDirection.Vertical -> Brush.verticalGradient(*colorStops)
            FadeDirection.Horizontal -> Brush.horizontalGradient(*colorStops)
        }

        onDrawWithContent {
            drawContent()
            drawRect(brush, blendMode = BlendMode.DstIn)
        }
    }
}

private fun fastLog(t: Float, strength: Float = 6f): Float {
    val x = t.coerceIn(0f, 1f)
    return (ln(1f + strength * x) / ln(1f + strength)).coerceIn(0f, 1f)
}

private fun curvedFadeStops(
    fadeFraction: Float,
    edgeAlpha: Float,
    beginAlpha: Float,
    endAlpha: Float,
    samples: Int = 32,
): Array<Pair<Float, Color>> {
    val fade = fadeFraction.coerceIn(0f, 0.5f)
    val edge = edgeAlpha.coerceIn(0f, 1f)

    val stops = buildList {
        for (i in 0..samples) {
            val position = i / samples.toFloat()
            val offset = position * fade
            val x = fastLog(position)
            val alpha = 1f - edge * beginAlpha * (1f - x)
            add(offset to Color.White.copy(alpha))
        }

        add((1f - fade) to Color.White)

        for (i in 0..samples) {
            val position = i / samples.toFloat()
            val offset = (1f - fade) + position * fade
            val x = fastLog(1f - position)
            val alpha = 1f - edge * endAlpha * (1f - x)
            add(offset to Color.White.copy(alpha))
        }
    }

    return stops
        .distinctBy { it.first }
        .toTypedArray()
}

@Preview
@Composable
private fun Preview() {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.error)
            .padding(16.dp)
    ) {
        val state = rememberLazyListState()
        LazyColumn(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .fadeEdges(
                    size = 30.dp,
                    direction = FadeDirection.Vertical,
                    beginAlpha = if (!state.canScrollBackward) 0f else 1f,
                    endAlpha = if (state.canScrollForward) 1f else 0f,
                ),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            items(20) {
                Card(Modifier.fillMaxWidth()) {
                    Text(
                        text = "Item $it",
                        Modifier
                            .fillMaxSize()
                            .padding(5.dp, 10.dp),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
