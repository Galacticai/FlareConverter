package com.galacticai.flareconverter.ui.components.expressive

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.galacticai.flareconverter.util.Consistent
import global.common.ui.ExpressiveInteraction.expressiveInteraction

private typealias Renderer = @Composable RowScope.(Modifier) -> Unit

@Composable
fun ExpressiveActionRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    expand: Dp = Consistent.Pad.regular,
    action: Renderer,
    content: Renderer,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
    ) {
        action(
            Modifier.expressiveInteraction(
                DpSize(expand, 0.dp),
                Consistent.Animation.getExpressiveSpring(true)
            )
        )
        content(Modifier.weight(1f))
    }
}