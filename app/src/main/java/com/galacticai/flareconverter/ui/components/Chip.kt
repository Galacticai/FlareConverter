package com.galacticai.flareconverter.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.galacticai.flareconverter.util.Consistent

@Composable
fun Chip(
    label: String,
    modifier: Modifier = Modifier,
    shape: Shape = Consistent.Shape.Rounded.pill,
    colors: CardColors = CardDefaults.cardColors().copy(
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.onPrimaryContainer,
    ),
    border: BorderStroke = BorderStroke(
        .5.dp, colors.contentColor.copy(.5f),
    ),
) {
    Card(
        modifier = modifier,
        shape = shape,
        colors = colors,
        border = border,
    ) {
        Text(
            label,
            Modifier.padding(horizontal = Consistent.Pad.regular, vertical = Consistent.Pad.tiny),
        )
    }
}