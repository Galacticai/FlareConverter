package com.galacticai.flareconverter.ui.components.expressive

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import com.galacticai.flareconverter.ui.components.Handle
import com.galacticai.flareconverter.util.Consistent

@Composable
fun ExpressiveHandle(
    color: Color, onClickLabel: String? = null,
    onClick: () -> Unit
) {
    val expressive = rememberExpressiveRadiusSize(
        DpSize(Consistent.Pad.regular, Consistent.Pad.tiny),
        Consistent.Shape.Radius.expressive,
        Consistent.Animation.getSpring(true)
    )
    Box(
        Modifier
            .fillMaxWidth()
            .height(Consistent.Pad.large)
            .clip(Consistent.Shape.Rounded.pill)
            .clickable(
                onClickLabel = onClickLabel,
                onClick = onClick
            ) then expressive.modifierPress,
        Alignment.Center
    ) {
        Handle(
            color = color,
            modifier = expressive.modifierSize,
        )
    }
}