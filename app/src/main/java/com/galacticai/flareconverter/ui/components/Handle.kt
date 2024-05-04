package com.galacticai.flareconverter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import com.galacticai.flareconverter.util.Consistent

/** just a simple handle rail */
@Composable
fun Handle(
    modifier: Modifier = Modifier,
    width: Dp = Consistent.Pad.regular * 10, height: Dp = Consistent.Pad.smallX,
    color: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = Consistent.Shape.Rounded.pill
) {
    Box(
        modifier then Modifier
            .background(color, shape)
            .size(width, height)
    )
}