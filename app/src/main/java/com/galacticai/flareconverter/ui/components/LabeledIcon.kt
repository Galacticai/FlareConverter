package com.galacticai.flareconverter.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp


@Composable
fun LabeledIcon(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Rounded.BrokenImage,
    iconSize: Dp = 48.dp,
    label: String? = null,
    fontSize: TextUnit = TextUnit.Unspecified,
    color: Color? = null,
    contentDescription: String? = null,
) {
    val tint = color ?: LocalContentColor.current
 
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            modifier = Modifier.size(iconSize),
            imageVector = icon,
            contentDescription = contentDescription ?: label ?: "LabeledIcon",
            tint = tint
        )
        if (label != null) {
            Text(
                label,
                textAlign = TextAlign.Center,
                fontSize = fontSize,
                color = tint
            )
        }
    }
}