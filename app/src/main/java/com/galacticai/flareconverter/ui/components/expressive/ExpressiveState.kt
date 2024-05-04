package com.galacticai.flareconverter.ui.components.expressive

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import com.galacticai.flareconverter.util.Consistent
import com.galacticai.flareconverter.util.Modifiers.expandIf
import com.galacticai.flareconverter.util.Modifiers.onPress

open class ExpressiveState(
    val isPressed: Boolean,
    val shape: RoundedCornerShape,
    val modifierPress: Modifier,
)

class ExpressiveStateSize(
    isPressed: Boolean, shape: RoundedCornerShape, modifierPress: Modifier,
    val modifierSize: Modifier = Modifier,
) : ExpressiveState(isPressed, shape, modifierPress) {
    /** [modifierPress] + [modifierSize] */
    val modifierAll get() = modifierPress then modifierSize
}

@Composable
fun rememberExpressiveRadius(
    radius: ExpressiveRadius<*> = Consistent.Shape.Radius.expressiveDp,
    animationSpec: AnimationSpec<*> = Consistent.Animation.getSpring<Dp>(),
    onChange: ((Any) -> Unit)? = null,
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
    onChange: ((Any) -> Unit)? = null,
): ExpressiveStateSize {
    var isPressed by remember { mutableStateOf(false) }
    val shape = ExpressiveRadius.rememberShape(
        isPressed, radius, animationSpec, onChange
    )
    val modifierPress = Modifier.onPress(Unit) {
        isPressed = it
    }
    val modifierSize = Modifier.expandIf(isPressed, expansion, animationSpec)
    return ExpressiveStateSize(isPressed, shape, modifierPress, modifierSize)
}