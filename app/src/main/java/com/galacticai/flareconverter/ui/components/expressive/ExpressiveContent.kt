package com.galacticai.flareconverter.ui.components.expressive

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import com.galacticai.flareconverter.util.Consistent
import com.galacticai.flareconverter.util.Modifiers.expandOnPress

@Deprecated("use `ExpressiveState.rememberState` instead")
@Composable
fun ExpressiveContent(
    expansion: DpSize,
    radius: ExpressiveRadius<*> = Consistent.Shape.Radius.expressiveDp,
    animationSpec: AnimationSpec<Dp> = Consistent.Animation.getSpring(),
    content: @Composable (expansionMod: Modifier, shape: Shape) -> Unit
) {
    var isInteracting by remember { mutableStateOf(false) }

    val shape = ExpressiveRadius.rememberShape(isInteracting, radius, animationSpec)
    val expansionMod = Modifier.expandOnPress(
        expansion,
        animationSpec,
    ) { isInteracting = it }

    content(expansionMod, shape)
}

@Preview
@Composable
private fun Preview() {

}