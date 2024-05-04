package com.galacticai.flareconverter.ui.components.mime_type

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.ui.themes.GalacticTheme
import com.galacticai.flareconverter.util.Consistent
import global.common.util.TextUtil.sentenceCase
import global.common.util.TextUtil.upper

object MimeTypeView {
    data class Colors(
        val category: Pair<Color, Color>,
        val name: Pair<Color, Color>,
    ) {
        companion object {
            @get:Composable
            val default: Colors
                get() {
                    val colors = MaterialTheme.colorScheme
                    return Colors(
                        category = colors.primaryContainer to colors.onPrimaryContainer,
                        name = colors.secondaryContainer to colors.onSecondaryContainer,
                    )
                }
        }
    }
}

@Composable
fun MimeTypeView(
    mime: MimeType,
    modifier: Modifier = Modifier,
    /**
     * - null = show [MimeType.category]
     * - empty = skip label
     */
    label: String? = null,
    colors: MimeTypeView.Colors = MimeTypeView.Colors.default,
    textString: TextStyle = ButtonDefaults.textStyleFor(40.dp)
) {
    val shape = Consistent.Shape.Rounded.pill
    Card(
        modifier,
        shape = shape,
        colors = CardDefaults.cardColors().copy(
            colors.category.first,
            colors.category.second,
        ),
    ) {
        Row(
            Modifier.height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Consistent.Pad.smallX)
        ) {
            MimeIcon(
                mime.category,
                Modifier.padding(
                    top = Consistent.Pad.small,
                    bottom = Consistent.Pad.small,
                    start = Consistent.Pad.regular,
                )
            )
            when (label) {
                "" -> {}
                null -> Text(
                    mime.category.sentenceCase,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    style = textString, fontWeight = FontWeight.Bold
                )

                else -> Text(
                    label,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    style = textString, fontWeight = FontWeight.Bold
                )
            }
            Card(
                shape = shape,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(IntrinsicSize.Min)
                    .padding(
                        top = Consistent.Pad.smallX,
                        bottom = Consistent.Pad.smallX,
                        end = Consistent.Pad.smallX,
                    ),
                colors = CardDefaults.cardColors().copy(
                    colors.name.first,
                    colors.name.second,
                ),
                border = CardDefaults.outlinedCardBorder().copy(
                    1.dp, SolidColor(colors.name.second.copy(.25f))
                ),
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .widthIn(min = Consistent.Pad.big * 2)
                        .padding(
                            horizontal = Consistent.Pad.regular,
                            vertical = Consistent.Pad.smallXX
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        (mime.key ?: mime.extension).upper,
                        style = textString,
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center, fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun MimeTypePreview() = GalacticTheme {
    Column(
        Modifier.padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        MimeTypeView(MimeType.Png)
        MimeTypeView(MimeType.Quicktime)
        MimeTypeView(MimeType.Mp3)
        MimeTypeView(MimeType.Quicktime, label = "Custom label")
        MimeTypeView(MimeType.Png, label = "Label")
        MimeTypeView(MimeType.Mp3, label = "File name")
    }
}