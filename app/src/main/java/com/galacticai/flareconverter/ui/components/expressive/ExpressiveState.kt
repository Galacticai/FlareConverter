package com.galacticai.flareconverter.ui.components.expressive

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import com.galacticai.flareconverter.util.Consistent
import com.galacticai.flareconverter.util.Modifiers.expandIf
import com.galacticai.flareconverter.util.Modifiers.onPress

data class ExpressiveState(
    val isPressed: Boolean,
    val shape: Shape,
    val modifierPress: Modifier,
    val modifierSize: Modifier = Modifier,
) {
    /** [modifierPress] + [modifierSize] */
    val modifierAll get() = modifierPress then modifierSize
}

@Composable
fun rememberExpressiveRadius(
    radius: ExpressiveRadius<*> = Consistent.Shape.Radius.expressiveDp,
    animationSpec: AnimationSpec<*> = Consistent.Animation.getSpring<Dp>(),
    onChange: ((Float) -> Unit)? = null,
): ExpressiveState {
    var isPressed by remember { mutableStateOf(false) }
    val shape = ExpressiveRadius.rememberShape(
        isPressed, radius, animationSpec, onChange
    )
    val modifierPress = Modifier.onPress(Unit) {
        isPressed = it
    }
    return ExpressiveState(isPressed, shape, modifierPress)
}

/** size + radius + pressed callback */
@Composable
fun rememberExpressiveRadiusSize(
    expansion: DpSize,
    radius: ExpressiveRadius<*> = Consistent.Shape.Radius.expressiveDp,
    animationSpec: AnimationSpec<Dp> = Consistent.Animation.getSpring(),
    onChange: ((Float) -> Unit)? = null,
): ExpressiveState {
    var isPressed by remember { mutableStateOf(false) }
    val shape = ExpressiveRadius.rememberShape(
        isPressed, radius, animationSpec, onChange
    )
    val modifierPress = Modifier.onPress(Unit) {
        isPressed = it
    }
    val modifierSize = Modifier.expandIf(isPressed, expansion, animationSpec)
    return ExpressiveState(isPressed, shape, modifierPress, modifierSize)
}