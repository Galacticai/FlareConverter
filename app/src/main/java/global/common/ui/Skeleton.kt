package global.common.ui

import androidx.compose.animation.core.DurationBasedAnimationSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.hypot

@Composable
fun Skeleton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(10.dp),
    animation: DurationBasedAnimationSpec<Float> = tween(2000),
    containerColor: Color = MaterialTheme.colorScheme.surface.copy(.5f),
    highlightColor: Color = MaterialTheme.colorScheme.surfaceVariant,
) = Box(modifier.skeleton(shape, animation, containerColor, highlightColor))

@Composable
fun Modifier.skeleton(
    shape: Shape = RoundedCornerShape(10.dp),
    animation: DurationBasedAnimationSpec<Float> = tween(2000),
    baseColor: Color = MaterialTheme.colorScheme.surface.copy(.5f),
    highlightColor: Color = MaterialTheme.colorScheme.surfaceVariant,
): Modifier {
    var size by remember { mutableStateOf(IntSize(0, 0)) }

    return key(size) {
        val t = rememberInfiniteTransition(label = "skeleton")

        val travel = hypot(
            size.width.toFloat(),
            size.height.toFloat()
        )

        val x by t.animateFloat(
            initialValue = -travel,
            targetValue = travel,
            animationSpec = infiniteRepeatable(
                animation,
                repeatMode = RepeatMode.Restart
            ),
            label = "shimmer"
        )

        this
            .clip(shape)
            .onSizeChanged { size = it }
            .background(
                brush = Brush.linearGradient(
                    listOf(baseColor, highlightColor, baseColor),
                    start = Offset(x, 0f),
                    end = Offset(
                        x + travel,
                        size.height.toFloat()
                    )
                )
            )
    }
}


@Preview(showBackground = true)
@Composable
private fun Preview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .skeleton(CircleShape)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.55f)
                        .height(18.dp)
                        .skeleton()
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(14.dp)
                        .skeleton()
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(14.dp)
                        .skeleton()
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .skeleton()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .skeleton()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(14.dp)
                    .skeleton()
            )
        }
    }
}
