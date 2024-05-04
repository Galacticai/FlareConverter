package com.galacticai.flareconverter.ui.components.thumbnail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.flareconverter.R
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.ui.components.LabeledIcon
import com.galacticai.flareconverter.ui.share_activity.InformedFile
import com.galacticai.flareconverter.util.Consistent
import global.common.models.FutureValue
import java.io.File
import java.time.Duration

@Composable
fun Thumbnail(
    modifier: Modifier = Modifier,
    inFile: FutureValue<File>?,
    showPercent: Boolean = true,
    /** performance: specify to skip parsing */
    mimeCategory: String? = null,
    borderWidth: Dp = .5.dp
) {
    //TODO: future resolve for source then again for thumbnail

    val color = when (inFile) {
        is FutureValue.Failed.Error -> ThumbnailColors.from(
            R.color.error,
            R.color.error,
            R.color.errorContainer
        )

        is FutureValue.Failed.Timeout -> ThumbnailColors.from(
            R.color.warning,
            R.color.warning,
            R.color.warningContainer
        )

        null -> ThumbnailColors.from(
            R.color.surface,
            R.color.background,
            R.color.background
        )

        else -> ThumbnailColors.from(
            R.color.primary,
            R.color.primaryContainer,
            R.color.surface
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.bg),
        border = BorderStroke(borderWidth, color.main),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        AnimatedContent(
            targetState = inFile,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "ThumbnailTransition"
        ) { targetFuture ->
            when (targetFuture) {
                is FutureValue.Pending -> CenterBox {
                    CircularProgressIndicator(
                        color = color.main,
                        trackColor = color.secondary
                    )
                }

                is FutureValue.Running -> {
                    CenterBox {
                        val progress = targetFuture.progress
                            ?: return@CenterBox CircularProgressIndicator(
                                color = color.main,
                                trackColor = color.secondary
                            )

                        CircularWavyProgressIndicator(
                            color = color.main,
                            trackColor = color.secondary,
                            progress = { progress },
                        )
                        if (showPercent) {
                            val percent = (progress * 100).toInt()
                            if (percent < 100) {
                                Text(
                                    "${percent}%",
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                is FutureValue.Finished -> {
                    val file = targetFuture.value
                    val mimeCategoryParsed = mimeCategory
                        ?: MimeType.from(
                            file,
                            acceptAnything = true,
                            allowUnsupported = true
                        )?.category
                    when (mimeCategoryParsed) {
                        MimeType.IMAGE -> ImageThumbnail(file = file)
                        MimeType.VIDEO -> VideoThumbnail(file = file)
                        MimeType.AUDIO -> AudioThumbnail(file = file)
                        else -> CenterBox {
                            LabeledIcon(label = "Unsupported", color = color.main)
                        }
                    }
                }


                is FutureValue.Stopped -> CenterBox {
                    LabeledIcon(
                        label = "Cancelled",
                        color = color.main
                    )
                }

                is FutureValue.Failed.Error -> CenterBox {
                    LabeledIcon(
                        label = "Error: ${targetFuture.error.message}",
                        contentDescription = "Error",
                        color = color.main
                    )
                }

                is FutureValue.Failed.Timeout -> CenterBox {
                    LabeledIcon(label = "Timeout", color = color.main)
                }

                null -> CenterBox {
                    LabeledIcon(color = color.main)
                }
            }
        }
    }
}

@Composable
fun ImageThumbnail(
    modifier: Modifier = Modifier,
    file: File,
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Image")
    }
}

@Composable
fun VideoThumbnail(
    modifier: Modifier = Modifier,
    file: File,
) {
    Box(
        modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Video")
    }
}

@Composable
fun AudioThumbnail(
    modifier: Modifier = Modifier,
    file: File,
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Audio")
    }
}


// --------------------------

@Composable
private fun Modifier.pad() = this.padding(Consistent.Pad.regular)

@Composable
fun CenterBox(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier
            .pad()
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}


// --------------------------

@Preview(showBackground = true, name = "Progress")
@Composable
private fun PreviewProgress() {
    Column(
        Modifier.pad(),
        verticalArrangement = Arrangement.spacedBy(Consistent.Pad.regular)
    ) {
        Thumbnail(
            modifier = Modifier.size(200.dp),
            inFile = FutureValue.Pending(),
        )
        Thumbnail(
            modifier = Modifier.size(200.dp),
            inFile = FutureValue.Running(
                progress = (Math.random().toFloat() * .25f) + .5f
            ),
        )
    }
}

@Preview(showBackground = true, name = "Fail")
@Composable
private fun PreviewFail() {
    Column(
        Modifier.pad(),
        verticalArrangement = Arrangement.spacedBy(Consistent.Pad.regular)
    ) {
        Thumbnail(
            modifier = Modifier.size(200.dp),
            inFile = FutureValue.Failed.Error(Exception("stuff")),
        )
        Thumbnail(
            modifier = Modifier.size(200.dp),
            inFile = FutureValue.Failed.Timeout(Duration.ZERO),
        )
    }
}

@OptIn(ExperimentalGridApi::class)
@Preview(showBackground = true, name = "Finished")
@Composable
private fun PreviewFinished() {

    val elementsGrouped = listOf<@Composable () -> Unit>(
        {
            Thumbnail(
                modifier = Modifier.size(200.dp),
                inFile=FutureValue.Finished(File("file.png")),
                mimeCategory = MimeType.IMAGE
            )
        },
        {
            Thumbnail(
                modifier = Modifier.size(200.dp),
                inFile=FutureValue.Finished(File("file.mp4")),
                mimeCategory = MimeType.VIDEO
            )
        },
        {
            Thumbnail(
                modifier = Modifier.size(200.dp),
                inFile=FutureValue.Finished(File("file.mp3")),
                mimeCategory = MimeType.AUDIO
            )
        },
        {
            Thumbnail(
                modifier = Modifier.size(200.dp),
                inFile=FutureValue.Finished(File("file.crap")),
                mimeCategory = "unknown"
            )
        }
    ).chunked(2)


    val arrangement = Arrangement.spacedBy(Consistent.Pad.regular)
    LazyVerticalGrid(
        modifier = Modifier.pad(),
        columns = GridCells.Fixed(2),
        horizontalArrangement = arrangement,
        verticalArrangement = arrangement,
    ) {
        for (elements in elementsGrouped) {
            items(elements.size) {
                elements[it]()
            }
        }
    }
}