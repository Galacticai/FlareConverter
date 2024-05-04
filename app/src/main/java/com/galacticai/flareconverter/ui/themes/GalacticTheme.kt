package com.galacticai.flareconverter.ui.themes

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.galacticai.flareconverter.util.Consistent

@Composable
fun GalacticTheme(content: @Composable () -> Unit) {
    val colorScheme = GalacticColorScheme.current()

    val shapes = MaterialTheme.shapes.copy(
        extraSmall = MaterialTheme.shapes.extraSmall.copy(CornerSize(Consistent.Pad.smallX)),
        small = MaterialTheme.shapes.small.copy(CornerSize(Consistent.Pad.regular)),
        medium = MaterialTheme.shapes.medium.copy(CornerSize(Consistent.Pad.medium)),
        large = MaterialTheme.shapes.large.copy(CornerSize(Consistent.Pad.big)),
        extraLarge = MaterialTheme.shapes.extraLarge.copy(CornerSize(Consistent.Pad.large)),
    )

    MaterialTheme(colorScheme, shapes) {
        content()
    }
}