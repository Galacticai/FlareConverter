package com.galacticai.flareconverter.ui.components.thumbnail.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.sp
import com.galacticai.flareconverter.ui.components.thumbnail.Thumbnail


@Composable
fun InfoSkeleton() {
    val title = 20.sp
    val label = 16.sp
    val align = PlaceholderVerticalAlign.Center

    Thumbnail.InfoRow {
        // name
        Placeholder(50.sp, title, align)
    }
    Thumbnail.InfoRow {
        // size
        Placeholder(20.sp, label, align)
        // resolution
        Placeholder(30.sp, label, align)
    }
    Thumbnail.InfoRow {
        // from-mime text
        Placeholder(20.sp, label, align)
        // to-mime selector
        Placeholder(35.sp, title, align)
    }
}