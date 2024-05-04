package com.galacticai.flareconverter.ui.components.thumbnail

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.material.icons.rounded.HourglassBottom
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.galacticai.flareconverter.models.ConvertStage
import com.galacticai.flareconverter.models.MediaFile
import com.galacticai.flareconverter.ui.components.Chip
import com.galacticai.flareconverter.ui.components.expressive.loading.ExpressiveLoadingIndicator
import com.galacticai.flareconverter.ui.components.mime_type.MimeTypeView
import com.galacticai.flareconverter.ui.components.thumbnail.LocalThumbnailStates.LocalThumbnailState
import com.galacticai.flareconverter.ui.share_activity.components.LocalShareActivityStates.LocalConvertStage
import com.galacticai.flareconverter.ui.themes.GalacticTheme
import com.galacticai.flareconverter.util.App
import com.galacticai.flareconverter.util.Consistent
import global.common.models.progressive.Progressive
import global.common.ui.Skeleton
import global.common.util.AndroidUtil.Intents.openURL
import global.common.util.TextUtil.ELLIPSES
import global.common.util.TextUtil.sentenceCase
import java.io.InvalidClassException
import java.util.Date


@Composable
fun Thumbnail(
    state: ThumbnailState,
    modifier: Modifier = Modifier,
    coverModifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
) = Box(modifier) {
    val convertStage = LocalConvertStage.current
    LocalThumbnailStates.Provider(state) {
        ThumbnailCover(coverModifier, state.files.inFile)

        Column(
            contentModifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Consistent.Pad.medium),
        ) {
            ImagePreviewCard()

            AnimatedContent(
                targetState = convertStage,
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) { stage ->
                val stageFiles = remember(stage) { state.files }
                when (stage) {
                    ConvertStage.Converting -> {
                        val outRunning = stageFiles.outFile as Progressive.Running
                        outRunning.progress.let { progress ->
                            if (progress == null) LinearWavyProgressIndicator()
                            else LinearWavyProgressIndicator({ progress })
                        }
                    }

                    ConvertStage.Init -> {}
                    ConvertStage.InitFail -> ErrorInfoView(FileSelection.Input)
                    ConvertStage.Config -> FileInfoView(stageFiles.inFile as Progressive.Done)
                    ConvertStage.ConvertFail -> ErrorInfoView(FileSelection.Output)
                    ConvertStage.Sharing -> {} //TODO
                }
            }
        }
    }
}

object Thumbnail {
    val previewSize = 120.dp
    val previewLoaderSize = 80.dp

    data class Files(
        val inFile: Progressive<MediaFile>,
        val outFile: Progressive<MediaFile>,
    )
}

enum class FileSelection { Input, Output }

@Composable
private fun ImagePreviewCard() {
    val state = LocalThumbnailState.current
    val convertStage = LocalConvertStage.current
    Card(
        Modifier.size(Thumbnail.previewSize),
        shape = Consistent.Shape.Rounded.all,
        colors = CardDefaults.cardColors().copy(state.inColors.bg),
        border = BorderStroke(
            .5.dp,
            state.inColors.fg.copy(
                if (state.files.outFile is Progressive.Running) 0f else 1f
            )
        ),
    ) {
        AnimatedContent(
            targetState = convertStage,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            contentAlignment = Alignment.Center,
        ) {
            when (it) {
                ConvertStage.Init -> {
                    Skeleton(Modifier.fillMaxSize())
                }

                ConvertStage.InitFail -> FailIcon(FileSelection.Input)
                ConvertStage.ConvertFail -> FailIcon(FileSelection.Output)

                ConvertStage.Config -> {
                    AsyncImage(
                        model = state.inFilePreview?.fileInfo?.file,
                        contentDescription = "Image thumbnail",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }

                ConvertStage.Converting -> {
                    AsyncImage(
                        model = state.inFilePreview?.fileInfo?.file,
                        contentDescription = "Image thumbnail",
                        modifier = Modifier.fillMaxSize().alpha(.5f),
                        contentScale = ContentScale.Crop,
                    )
                    ExpressiveLoadingIndicator(
                        Modifier.fillMaxSize(),
                        size = Thumbnail.previewLoaderSize,
                        containerColor = state.outColors.bgSurface,
                        indicatorColor = state.outColors.hero,
                        pulseColor = state.outColors.bgSurfaceSecondary,
                    )
                }

                ConvertStage.Sharing -> {} //TODO
            }
        }
    }
}

@Composable
private fun ErrorInfoView(
    selected: FileSelection,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val state = LocalThumbnailState.current

    val (file, colors) = remember(selected) {
        when (selected) {
            FileSelection.Input -> state.files.inFile to state.inColors
            FileSelection.Output -> state.files.outFile to state.outColors
        }
    }

    if (file !is Progressive.Failed) return
    val errorName = file.cause?.javaClass?.simpleName
        ?: return

    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Consistent.Pad.small)
    ) {
        Card(
            colors = CardDefaults.cardColors().copy(
                colors.bgSurface, colors.fg
            )
        ) {
            Text(
                errorName,
                fontSize = 12.sp,
                modifier = Modifier.padding(
                    horizontal = Consistent.Pad.small,
                    vertical = Consistent.Pad.tiny
                ),
            )
        }
        Text(
            file.cause.message ?: "Something went wrong$ELLIPSES",
            color = colors.fg,
        )
        OutlinedButton({ context.openURL(App.GITHUB_ISSUES) }) {
            Text("Report issue")
        }
    }
}

@Composable
private fun FileInfoView(info: Progressive.Done<MediaFile>) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Consistent.Pad.small)
    ) {
        MimeTypeView(
            info.value.mime,
            label = info.value.fileInfo.file.nameWithoutExtension
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Consistent.Pad.small),
        ) {
            val colors = MaterialTheme.colorScheme
            Chip(
                "${info.value.fileInfo.size}",
                colors = CardDefaults.cardColors().copy(
                    colors.secondaryContainer, colors.onSecondaryContainer,
                ),
            )
//            Chip(
//                info.value.fileInfo.createdAt.format(),
//                colors = CardDefaults.cardColors().copy(
//                    colors.tertiaryContainer, colors.onTertiaryContainer,
//                ),
//            )
        }
    }
}

@Composable
private fun FailIcon(selected: FileSelection) {
    val state = LocalThumbnailState.current

    val (failed, colors) = remember(selected) {
        when (selected) {
            FileSelection.Input -> state.files.inFile as Progressive.Failed to state.inColors
            FileSelection.Output -> state.files.outFile as Progressive.Failed to state.outColors
        }
    }

    Icon(
        when (failed.type) {
            Progressive.Failed.Type.Timeout -> Icons.Rounded.HourglassBottom
            Progressive.Failed.Type.Stopped,
            Progressive.Failed.Type.Error -> Icons.Rounded.BrokenImage
        },
        failed.type.name.sentenceCase,
        Modifier.fillMaxSize(),
        tint = colors.hero
    )
}

@Preview(showSystemUi = true)
@Composable
private fun Preview() = GalacticTheme {
    val state = ThumbnailState.remember(
        Thumbnail.Files(
            Progressive.Failed(
                Progressive.Failed.Type.Error,
                InvalidClassException("Failed class bruh"),
                Date(), Date()
            ),
            Progressive.Running(
                Progressive.Running.Type.Main,
                null, null, Date()
            )
        )
    )
    Thumbnail(
        state,
        Modifier
            .fillMaxWidth()
            .height(300.dp),
        Modifier.padding(10.dp),
        contentModifier = Modifier.padding(20.dp)
    )
}