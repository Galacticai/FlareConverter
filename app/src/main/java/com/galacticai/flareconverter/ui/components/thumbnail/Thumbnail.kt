package com.galacticai.flareconverter.ui.components.thumbnail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.ui.components.thumbnail.Thumbnail.CenterBox
import com.galacticai.flareconverter.ui.components.thumbnail.Thumbnail.pad
import com.galacticai.flareconverter.ui.components.thumbnail.components.ThumbnailColumn
import com.galacticai.flareconverter.ui.themes.GalacticTheme
import com.galacticai.flareconverter.util.Consistent
import global.common.models.progressive.Progressive
import java.io.File
import java.util.Date

@Composable
fun Thumbnail(
    modifier: Modifier = Modifier,
    inFile: Progressive<File>?,
    showPercent: Boolean = true,
    /** performance: specify to skip parsing */
    mimeCategory: String? = null,
    borderWidth: Dp = .5.dp
) {
    //TODO: future resolve for source then again for thumbnail

    val scope = ThumbnailScope.remember(inFile, showPercent, mimeCategory)
    with(scope) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(containerColor = colors.bg),
            border = BorderStroke(borderWidth, colors.main),
            elevation = CardDefaults.cardElevation(5.dp)
        ) {
            CenterBox {
                ThumbnailColumn(inFile)
//                AnimatedContent(
//                    targetState = inFile,
//                    transitionSpec = { fadeIn() togetherWith fadeOut() },
//                    label = "ThumbnailTransition"
//                ) {
//                    ThumbnailColumn(it)
//                }
            }
        }
    }
}


object Thumbnail {
    @Composable
    fun Modifier.pad() = this.padding(Consistent.Pad.regular)

    @Composable
    fun CenterBox(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
        Box(
            Modifier
                .pad()
                .fillMaxSize() then modifier,
            contentAlignment = Alignment.Center,
            content = content
        )
    }


    @Composable
    fun InfoRow(content: @Composable RowScope.() -> Unit) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = Consistent.Shape.Radius.medium / 2),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) { content() }
    }

    @Composable
    fun ThumbnailScope.Image(file: File) {
        AsyncImage(
            model = file,
            contentDescription = "Image thumbnail",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }

    @Composable
    fun ThumbnailScope.Video(file: File) {
        CenterBox {
            Text("Video")
        }
    }

    @Composable
    fun ThumbnailScope.Audio(
        file: File,
    ) {
        CenterBox {
            Text("Audio")
        }
    }
}


// --------------------------

@Preview(showBackground = true, name = "Progress")
@Composable
private fun PreviewProgress() = GalacticTheme {
    val mod = Modifier
        .size(450.dp)
        .fillMaxWidth()
        .aspectRatio(1f)
    Column(
        Modifier.pad(),
        verticalArrangement = Arrangement.spacedBy(Consistent.Pad.regular)
    ) {
        Thumbnail(
            modifier = mod,
            inFile = Progressive.Pending(),
        )
        Thumbnail(
            modifier = mod,
            inFile = Progressive.Running(
                Progressive.Running.Type.Main,
                progress = (Math.random().toFloat() * .25f) + .5f,
                null,
                start = Date(),
            ),
        )
    }
}

@Preview(showBackground = true, name = "Fail")
@Composable
private fun PreviewFail() = GalacticTheme {
    val mod = Modifier
        .size(450.dp)
        .fillMaxWidth()
        .aspectRatio(1f)
    Column(
        Modifier.pad(),
        verticalArrangement = Arrangement.spacedBy(Consistent.Pad.regular)
    ) {
        Thumbnail(
            modifier = mod,
            inFile = Progressive.Failed(
                Progressive.Failed.Type.Error,
                Exception("stuff"),
                Date(), Date()
            ),
        )
        Thumbnail(
            modifier = mod,
            inFile = Progressive.Failed(
                Progressive.Failed.Type.Timeout,
                Exception("timeout"),
                Date(), Date()
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewImage() = GalacticTheme {
    val mod = Modifier
        .size(450.dp)
        .fillMaxWidth()
        .aspectRatio(1f)
    Thumbnail(
        modifier = mod,
        inFile = Progressive.Done(
            File("file.png"),
            Date(), Date()
        ),
        mimeCategory = MimeType.IMAGE
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewVideo() = GalacticTheme {
    val mod = Modifier
        .size(450.dp)
        .fillMaxWidth()
        .aspectRatio(1f)
    Thumbnail(
        modifier = mod,
        inFile = Progressive.Done(
            File("file.mp4"),
            Date(), Date()
        ),
        mimeCategory = MimeType.VIDEO
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewAudio() = GalacticTheme {
    val mod = Modifier
        .size(450.dp)
        .fillMaxWidth()
        .aspectRatio(1f)
    Thumbnail(
        modifier = mod,
        inFile = Progressive.Done(
            File("file.mp3"),
            Date(), Date()
        ),
        mimeCategory = MimeType.AUDIO
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewCrap() = GalacticTheme {
    val mod = Modifier
        .size(450.dp)
        .fillMaxWidth()
        .aspectRatio(1f)
    Thumbnail(
        modifier = mod,
        inFile = Progressive.Done(
            File("file.crap"),
            Date(), Date()
        ),
        mimeCategory = "unknown"
    )
}
