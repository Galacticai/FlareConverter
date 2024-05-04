package com.galacticai.flareconverter.ui.share_activity.components

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.arthenica.ffmpegkit.MediaInformation
import com.galacticai.flareconverter.models.ffmpeg.FFmpegArg
import com.galacticai.flareconverter.ui.components.FFmpegArgView
import com.galacticai.flareconverter.ui.components.inputs.SelectMimeTypeByCategory
import com.galacticai.flareconverter.ui.share_activity.ShareActivity
import com.galacticai.flareconverter.ui.share_activity.ShareActivityHelpers
import com.galacticai.flareconverter.util.Consistent
import com.galacticai.flareconverter.util.Consistent.Animation.slideUpDown
import com.galacticai.flareconverter.util.Consistent.Shape.Radius.paddedBy
import com.galacticai.flareconverter.util.Settings
import global.common.TextUtil.ELLIPSES
import global.common.TextUtil.sentenceCase
import global.common.models.FutureValue.Companion.pickFirst
import global.common.models.FutureValue.Companion.pickSecond
import global.common.ui.ExpandableGroup
import global.common.ui.FadeDirection
import global.common.ui.FadeEdgesContainer
import java.io.File

@Composable
fun ConfigView(
    modifier: Modifier = Modifier,
    colors: BgVariants,
    lazyColumnState: LazyListState = rememberLazyListState(),
) {
    val activity = LocalActivity.current as ShareActivity
    val shareInfo by activity.vm.shareInfoState
    if (shareInfo == null) return
    val inFilePair by activity.vm.inFileLive.observeAsState()
    val mediaInfo by remember(inFilePair) {
        derivedStateOf {
            inFilePair?.pickSecond()?.finishedValue
        }
    }

    val ready by remember(mediaInfo, shareInfo) {
        derivedStateOf { mediaInfo != null && shareInfo != null }
    }

    AnimatedContent(
        ready,
        transitionSpec = { slideUpDown },
        contentAlignment = Alignment.Center,
    ) { ready ->
        if (!ready) return@AnimatedContent
        FadeEdgesContainer(
            modifier = modifier,
            size = Consistent.Pad.largeX,
            direction = FadeDirection.Vertical,
            beginAlpha = if (lazyColumnState.canScrollBackward) 1f else 0f,
            endAlpha = if (lazyColumnState.canScrollForward) 1f else 0f,
            color = colors.first
        ) {
            var outMime by Settings.OutMime(shareInfo!!.inMime)
                .rememberObject(true, shareInfo)

            val allowedArgs = ShareActivityHelpers.ffmpegAllowedArgs[
                outMime.category
            ]!!

            LazyColumn(
                state = lazyColumnState,
                verticalArrangement = Arrangement.spacedBy(Consistent.Pad.smallX),
            ) {
                item {
                    val expandState = rememberSaveable { mutableStateOf(true) }
                    activity.vm.configExpandStates.add(expandState)
                    ConfigViewItem(
                        title = "Convert to$ELLIPSES",
                        isLast = allowedArgs.isEmpty(),
                        colors = colors,
                        expandState = expandState,
                    ) { modifier, _ ->
                        SelectMimeTypeByCategory(
                            modifier = modifier,
                            allowedMimes = shareInfo!!.outMimes //! critical: allow outMimes only
                        ) { outMime = it }
                    }
                }
                items(allowedArgs.size) { i ->
                    val expandState = rememberSaveable { mutableStateOf(false) }
                    activity.vm.configExpandStates.add(expandState)
                    val isLast = i == allowedArgs.size - 1

                    ConfigViewItemFFmpeg(
                        arg = allowedArgs[i],
                        mediaInfo = mediaInfo!!,
                        isLast = isLast,
                        colors = colors,
                        expandState = expandState,
                    )
                }
            }
        }
    }
}


@Composable
fun ConfigViewItem(
    title: String,
    isLast: Boolean,
    colors: BgVariants,
    modifier: Modifier = Modifier,
    expandState: MutableState<Boolean> = rememberSaveable { mutableStateOf(false) },
    item: @Composable (modifier: Modifier, inFile: File) -> Unit
) {
    val activity = LocalActivity.current as ShareActivity
    val scrollProgress by activity.vm.configScrollProgressState
    val pad = scrollProgress.edgePad(true)
    val expand by expandState

    val inFile by activity.vm.inFileLive.observeAsState()
    val opticalPad = pad * 1.2f
    Column(
        Modifier
            .padding(
                start = pad, end = pad, top = pad,
                bottom = if (isLast) opticalPad else 0.dp
            )
            .fillMaxWidth()
            .heightIn(max = 500.dp) then modifier
    ) {
        ExpandableGroup(
            title = title,
            expandState = expandState,
            radius = Consistent.Shape.Radius.medium paddedBy pad,
            padding = Consistent.Pad.regular,
            containerColor = colors.first,
            contentBackground = colors.second
        ) { groupPad ->
            item(
                Modifier
                    .fillMaxWidth()
                    .padding(groupPad)
                    .verticalScroll(rememberScrollState()),
                inFile!!.pickFirst().finishedValue!!,
            )
        }

        AnimatedVisibility(!expand && !isLast) {
            HorizontalDivider(
                color = colors.second.copy(alpha = if (isSystemInDarkTheme()) .75f else .5f),
                modifier = Modifier.padding(horizontal = pad * .25f),
            )
        }
    }
}

@Composable
fun ConfigViewItemFFmpeg(
    arg: FFmpegArg,
    mediaInfo: MediaInformation,
    isLast: Boolean,
    colors: BgVariants,
    modifier: Modifier = Modifier,
    expandState: MutableState<Boolean> = rememberSaveable { mutableStateOf(false) },
) {
    val activity = LocalActivity.current as ShareActivity

    ConfigViewItem(
        modifier = modifier,
        title = arg.name.sentenceCase,
        isLast = isLast,
        colors = colors,
        expandState = expandState,
    ) { childModifier, inFile ->
        FFmpegArgView(
            file = inFile,
            arg = arg,
            mediaInfo = mediaInfo,
            modifier = childModifier,
            coroutineScope = activity.vm.viewModelScope,
            colors = colors
        )
    }
}
