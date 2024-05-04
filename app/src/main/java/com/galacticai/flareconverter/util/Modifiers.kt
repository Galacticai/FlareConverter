package com.galacticai.flareconverter.util

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import global.common.ui.DimenUtils.toDp

object Modifiers {
    fun Modifier.onPress(
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

    @Composable
    fun Modifier.expandIf(
        isExpanded: Boolean,
        delta: DpSize,
        spec: AnimationSpec<Dp> = Consistent.Animation.getSpring(),
    ): Modifier {
        val density = LocalDensity.current
        var size by remember { mutableStateOf<DpSize?>(null) }

        val deltaWidthAnimated by animateDpAsState(
            targetValue = if (isExpanded) delta.width else 0.dp,
            animationSpec = spec,
            label = "expandWidth",
        )

        val deltaHeightAnimated by animateDpAsState(
            targetValue = if (isExpanded) delta.height else 0.dp,
            animationSpec = spec,
            label = "expandHeight",
        )

        val sizeModifier = remember(isExpanded, size, deltaWidthAnimated, deltaHeightAnimated) {
            if (deltaWidthAnimated > 0.dp || deltaHeightAnimated > 0.dp) {
                size?.let {
                    Modifier.requiredSize(
                        it.width + deltaWidthAnimated,
                        it.height + deltaHeightAnimated,
                    )
                } ?: Modifier
            } else Modifier
        }

        return this.onSizeChanged {
            if (deltaWidthAnimated > 0.dp || deltaHeightAnimated > 0.dp)
                return@onSizeChanged
            size = it.toDp(density)
        } then sizeModifier
    }

    fun Modifier.expandOnPress(
        expansion: DpSize,
        expressiveSpring: AnimationSpec<Dp>,
        onPressed: ((Boolean) -> Unit)? = null,
    ): Modifier = composed {
        var isPressed by remember { mutableStateOf(false) }
        this
            .onPress(Unit) {
                isPressed = it
                onPressed?.invoke(it)
            }
            .expandIf(
                isPressed,
                expansion, expressiveSpring
            )
    }
}