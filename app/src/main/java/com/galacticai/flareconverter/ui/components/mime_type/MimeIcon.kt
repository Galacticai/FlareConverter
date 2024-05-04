package com.galacticai.flareconverter.ui.components.mime_type

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material.icons.rounded.Audiotrack
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.galacticai.flareconverter.models.MimeType

@Composable
fun MimeIcon(mimeCategory: String, modifier: Modifier = Modifier) {
    val icon = when (mimeCategory) {
        MimeType.IMAGE -> Icons.Rounded.Image
        MimeType.VIDEO -> Icons.Rounded.Videocam
        MimeType.AUDIO -> Icons.Rounded.Audiotrack
        else -> Icons.Rounded.BrokenImage
    }
    Icon(
        modifier = modifier,
        imageVector = icon,
        contentDescription = "Category",
        tint = LocalContentColor.current
    )
}