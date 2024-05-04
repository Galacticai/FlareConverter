package global.common.ui.bounds_resolver

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import global.common.models.space.dp_bound.DpBound
import global.common.models.space.dp_bound.DpBound.Companion.bounds
import global.common.ui.elevated_expandable.DpOffset

/** resolve [DpBound] dynamically by letting android decide using the given element */
@Composable
fun BoundResolve(
    modifier: Modifier = Modifier,
    customResolver: ((LayoutCoordinates) -> DpBound)? = null,
    onChanged: (current: DpBound) -> Unit,
) {
    val density = LocalDensity.current
    var bounds by remember { mutableStateOf(DpBound.zero) }

    LaunchedEffect(bounds, onChanged) {
        onChanged(bounds)
    }

    Box(
        modifier.onGloballyPositioned {
            if (customResolver != null) {
                bounds = customResolver(it)
                return@onGloballyPositioned
            }
            val position = DpOffset(it.positionInParent(), density.density)
            val size = DpOffset(it.size.width, it.size.height, density.density)
            bounds = DpBound(position, size)
        }
    )
}

@Preview(showBackground = true)
@Composable
fun InterpolatePreview() {
    var progress by remember { mutableFloatStateOf(0f) }

    val scope = IBoundPlacementState.remember().apply {
        setSource(DpBound(24.dp, 24.dp, 100.dp, 20.dp))
        setDestination(DpBound(240.dp, 150.dp, 60.dp, 80.dp))
    }
    with(scope) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text("Progress: ${(progress * 100).toInt()}%")

            Slider(
                value = progress,
                onValueChange = {
                    progress = it
                    setRatio(it)
                },
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(20.dp)
                    )
            ) {
                Box(
                    Modifier
                        .bounds(source)
                        .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                )
                Box(
                    Modifier
                        .bounds(destination)
                        .border(2.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f))
                )
                Box(
                    modifier = Modifier
                        .bounds(current)
                        .background(MaterialTheme.colorScheme.primary),
                )
            }
            Text(
                current.toString()
                    .replace(Regex("""( \| |,)"""), "\n")
                    .replace(Regex("""\.\d+\.dp"""), "")
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun BoundPlacementPreview() {
    val pad = 10.dp
    val shape = RoundedCornerShape(20.dp)

    val scope = IBoundPlacementState.remember()
    with(scope) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(pad / 2)
                .clip(shape)
                .background(MaterialTheme.colorScheme.surface),
        ) {
            BoundResolve(Modifier.fillMaxSize()) { setSource(it) }
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(pad)
            ) {
                Box(
                    Modifier
                        .clip(shape)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer, shape)
                        .border(1.dp, MaterialTheme.colorScheme.primary, shape)
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Distance:\n$distance\nof $distanceMax",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                }
                BoundResolve(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = pad * 2)
                        .background(MaterialTheme.colorScheme.secondaryContainer, shape)
                        .border(1.dp, MaterialTheme.colorScheme.secondary, shape)
                ) { setDestination(it) }
                Text(
                    "$ratio\n\n$current", Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center
                )
            }

            Box(
                Modifier
                    .bounds(current)
                    .clip(shape)
                    .border(5.dp, MaterialTheme.colorScheme.secondary, shape)
                    .draggable(
                        orientation = orientation,
                        state = dragState
                    )
            )
        }
    }
}