package com.galacticai.flareconverter.ui.components.thumbnail.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.galacticai.flareconverter.ui.components.thumbnail.ThumbnailScope
import com.galacticai.flareconverter.ui.components.thumbnail.components.thumbnail_renderer.FinishedThumbnailRenderer
import com.galacticai.flareconverter.ui.components.thumbnail.components.thumbnail_renderer.ThumbnailRenderer
import com.galacticai.flareconverter.util.Consistent
import global.common.models.progressive.Progressive
import java.io.File


@Composable
fun ThumbnailScope.ThumbnailColumn(targetFuture: Progressive<File>?) {
    AnimatedContent(
        targetFuture,
        transitionSpec = { fadeIn() togetherWith fadeOut() }
    ) {
        Column(
            Modifier
                .wrapContentHeight()
                .widthIn(max = 400.dp)
                .padding(
                    horizontal = Consistent.Pad.largeX,
                    vertical = Consistent.Pad.moderate
                ),
            verticalArrangement = Arrangement.spacedBy(Consistent.Pad.regular),
        ) {
            val renderer = ThumbnailRenderer.from(it)
            with(renderer) {
                if (renderer is FinishedThumbnailRenderer) {
                    Card(
                        Modifier.aspectRatio(1f),
                        shape = Consistent.Shape.Rounded.all,
                        colors = CardDefaults.cardColors().copy(
                            MaterialTheme.colorScheme.background //TODO: for testing only
                        ),
                    ) { Preview() }
                } else Preview()
                Info()
            }
        }
    }
}