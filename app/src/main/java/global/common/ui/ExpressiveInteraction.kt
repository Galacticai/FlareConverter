package global.common.ui

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import global.common.ui.ExpressiveInteraction.expressiveInteraction

object ExpressiveInteraction {
    fun Modifier.isInteracting(
        key: Any? = null,
        onChange: (Boolean) -> Unit
    ) = this.pointerInput(key ?: Unit) {
        awaitEachGesture {
            awaitFirstDown(
                requireUnconsumed = false,
                pass = PointerEventPass.Initial
            )
            onChange(true)
            try {
                do {
                    val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                } while (event.changes.any { it.pressed })
            } finally {
                onChange(false)
            }
        }
    }

    fun Modifier.expressiveInteraction(
        expansion: DpSize,
        expressiveSpring: AnimationSpec<Dp>,
        onChanged: ((Boolean) -> Unit)? = null,
    ): Modifier = composed {
        val density = LocalDensity.current

        var pressed by remember { mutableStateOf(false) }
        var initialSize by remember { mutableStateOf<IntSize?>(null) }

        val width by animateDpAsState(
            targetValue = if (pressed) expansion.width else 0.dp,
            animationSpec = expressiveSpring,
            label = "expressiveWidth",
        )

        val height by animateDpAsState(
            targetValue = if (pressed) expansion.height else 0.dp,
            animationSpec = expressiveSpring,
            label = "expressiveHeight",
        )

        val sizeModifier = initialSize?.let { initial ->
            Modifier.expandSize(
                width = expansion.width.takeIf { it != 0.dp }?.let {
                    with(density) { initial.width.toDp() } + width
                },
                height = expansion.height.takeIf { it != 0.dp }?.let {
                    with(density) { initial.height.toDp() } + height
                },
            )
        } ?: Modifier.Companion

        this
            .onSizeChanged { initialSize = initialSize ?: it }
            .isInteracting(Unit) {
                pressed = it
                onChanged?.invoke(it)
            } then sizeModifier
    }

    private fun Modifier.expandSize(
        width: Dp?,
        height: Dp?,
    ): Modifier = when {
        width != null && height != null -> requiredSize(width, height)
        width != null -> requiredWidth(width)
        height != null -> requiredHeight(height)
        else -> this
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpressiveInteractionPreview() {
    var isInteracting by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(32.dp)
            .size(100.dp)
            .expressiveInteraction(
                DpSize(24.dp, 16.dp),
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium,
                )
            ) { isInteracting = it }
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Press",
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(
                horizontal = 24.dp,
                vertical = 16.dp,
            ),
        )
    }
}
