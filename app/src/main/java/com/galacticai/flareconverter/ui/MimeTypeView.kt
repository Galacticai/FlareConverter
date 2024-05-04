package com.galacticai.flareconverter.ui

import androidx.annotation.ColorRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.flareconverter.R
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.util.Consistent


data class MimeTypeViewStyle(
    @ColorRes val containerColor: Int = R.color.surface,
    @ColorRes val textColor: Int = R.color.onSurface,
    @ColorRes val titleColor: Int = textColor,
    val fontSize: TextUnit = 14.sp,
    val iconSize: Dp = 32.dp,
    val padding: Dp = Consistent.padRegular
)

@Composable
fun MimeTypeView(
    mimeType: MimeType,
    modifier: Modifier = Modifier,
    title: String? = null,
    showAllExtensions: Boolean = false,
    style: MimeTypeViewStyle = MimeTypeViewStyle(),
) {
    val icon by remember(mimeType) {
        derivedStateOf {
            when (mimeType.category) {
                MimeType.IMAGE -> Icons.Rounded.AccountBox
                MimeType.VIDEO -> Icons.Rounded.PlayArrow
                else -> Icons.Rounded.Star
            }
        }
    }

    val text by remember(mimeType, showAllExtensions) {
        derivedStateOf { getText(mimeType, showAllExtensions) }
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors().copy(
            containerColor = colorResource(style.containerColor)
        )
    ) {
        val paddingVertical = style.padding / 2
        Row(
            modifier = Modifier.padding(
                top = paddingVertical, bottom = paddingVertical,
                start = style.padding, end = style.padding * 1.6f
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(style.padding),
        ) {
            Icon(
                icon,
                mimeType.category,
                Modifier.size(style.iconSize),
                colorResource(style.textColor)
            )

            @Composable
            fun TextView(text: String, style: MimeTypeViewStyle) {
                Text(text, fontSize = style.fontSize, color = colorResource(style.textColor))
            }
            if (title == null) TextView(text, style)
            else {
                Column(
                    verticalArrangement = Arrangement.spacedBy((-2).dp)
                ) {
                    Text(
                        title,
                        fontSize = style.fontSize / 1.6f,
                        color = colorResource(style.titleColor).copy(alpha = 0.75f)
                    )
                    TextView(text, style)
                }
            }
        }
    }
}


private fun getText(mimeType: MimeType, showAllExtensions: Boolean): String {
    val category =
        if (mimeType.category == "*") "(Anything)"
        else mimeType.category

    val extension =
        if (showAllExtensions)  mimeType.extensions.joinToString(", ")
        else mimeType.extension

    return mutableListOf (category,extension)
        .filter { it.isNotBlank() }
        .joinToString(" — ")
}

@Composable
@Preview
fun MimeTypeViewPreview() {
    Column(
        modifier = Modifier.padding(Consistent.padRegular),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Consistent.padSmallX),
    ) {
        MimeTypeView(MimeType.Anything)
        MimeTypeView(MimeType.Avi)
        MimeTypeView(MimeType.Gif)
        MimeTypeView(MimeType.Avi, title = "From")
        MimeTypeView(
            MimeType.Jpeg,
            title = "To",
            style = MimeTypeViewStyle(
                containerColor = R.color.primaryContainer,
                textColor = R.color.primary,
            )
        )
        MimeTypeView(
            MimeType.Gif,
            title = "To",
            style = MimeTypeViewStyle(
                containerColor = R.color.secondaryContainer,
                textColor = R.color.secondary,
            )
        )
    }
}