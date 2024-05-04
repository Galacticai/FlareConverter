package com.galacticai.flareconverter.ui.share_activity.components

import androidx.activity.compose.LocalActivity
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.arthenica.ffmpegkit.MediaInformation
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.models.ffmpeg.FFmpegArg
import com.galacticai.flareconverter.ui.components.FFmpegArgView
import com.galacticai.flareconverter.ui.components.mime_type.MimeTypeSelectByCategory
import com.galacticai.flareconverter.ui.components.mime_type.MimeTypeView
import com.galacticai.flareconverter.ui.share_activity.ShareActivity
import com.galacticai.flareconverter.ui.share_activity.ShareActivityHelpers
import com.galacticai.flareconverter.ui.share_activity.components.ShareActivityView.edgePad
import com.galacticai.flareconverter.util.Consistent
import com.galacticai.flareconverter.util.Consistent.Shape.Radius.paddedBy
import com.galacticai.flareconverter.util.Settings
import global.common.models.progressive.Progressive
import global.common.models.space.dp_bound.DpBound
import global.common.ui.ExpandableGroup
import global.common.ui.FadeDirection
import global.common.ui.FadeEdgesContainer
import global.common.ui.bounds_resolver.IBoundPlacementState
import global.common.util.TextUtil.ELLIPSES
import global.common.util.TextUtil.sentenceCase
import java.io.File

@Composable
fun IBoundPlacementState.ConfigView(
    modifier: Modifier = Modifier,
    colors: ShareActivityView.BgVariants,
    lazyColumnState: LazyListState = rememberLazyListState(),
) {
    val activity = LocalActivity.current as ShareActivity
    val shareInfo by activity.vm.shareInfoState
    val inFile by activity.vm.inFileLive.observeAsState()
    val mediaInfo = (inFile?.convert { it.info } as? Progressive.Done)?.value

    val ready = remember(mediaInfo, shareInfo) {
        mediaInfo != null && shareInfo != null
                && source != DpBound.zero && destination != DpBound.zero
    }

    AnimatedVisibility(
        ready,
        enter = Consistent.Animation.inUp,
        exit = Consistent.Animation.outDown,
    ) {
//        if (!ready) return@AnimatedContent
        FadeEdgesContainer(
            modifier = modifier,
            size = Consistent.Pad.largeX,
            direction = FadeDirection.Vertical,
            beginAlpha = if (lazyColumnState.canScrollBackward) 1f else 0f,
            endAlpha = if (lazyColumnState.canScrollForward) 1f else 0f,
            color = colors.first
        ) {
            val sInfo = shareInfo ?: return@FadeEdgesContainer
            var outMime by Settings.OutMime(sInfo.inMime)
                .rememberObject(true, sInfo)

            val allowedArgs = ShareActivityHelpers.ffmpegAllowedArgs[
                outMime.category
            ] ?: emptyList()

            LazyColumn(
                state = lazyColumnState,
                verticalArrangement = Arrangement.spacedBy(Consistent.Pad.smallX),
            ) {
                item {
                    val expandState = rememberSaveable { mutableStateOf(true) }
                    activity.vm.configExpandStates["convert"] = expandState
                    val sInfo = shareInfo ?: return@item
                    ConfigViewItem(
                        title = "Convert to$ELLIPSES",
                        isLast = allowedArgs.isEmpty(),
                        colors = colors,
                        expandState = expandState,
                        scrollRatio = ratio,
                    ) { modifier, _ ->
                        Column(
                            modifier,
                            verticalArrangement = Arrangement.spacedBy(Consistent.Pad.smallX),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            MimeTypeView(sInfo.inMime)
                            Icon(
                                Icons.Rounded.KeyboardArrowDown,
                                "mime type from-to arrow"
                            )
                            MimeTypeSelectByCategory(
                                allowedMimes = sInfo.outMimes //! critical: allow outMimes only
                            ) { outMime = it }
                        }
                    }
                }
                items(allowedArgs.size) { i ->
                    val arg = allowedArgs[i]
                    val expandState = rememberSaveable { mutableStateOf(false) }
                    activity.vm.configExpandStates[arg.name] = expandState
                    val isLast = i == allowedArgs.size - 1

                    val mInfo = mediaInfo ?: return@items
                    ConfigViewItemFFmpeg(
                        arg = arg,
                        mediaInfo = mInfo,
                        isLast = isLast,
                        colors = colors,
                        expandState = expandState,
                        scrollRatio = ratio,
                    )
                }
            }
        }
    }
}


@Composable
fun ConfigViewItem(
    title: String,
    scrollRatio: Float,
    isLast: Boolean,
    colors: ShareActivityView.BgVariants,
    modifier: Modifier = Modifier,
    expandState: MutableState<Boolean> = rememberSaveable { mutableStateOf(false) },
    item: @Composable (modifier: Modifier, inFile: File?) -> Unit
) {
    val activity = LocalActivity.current as ShareActivity
    val pad = scrollRatio.edgePad(true)
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
                (inFile?.convert { it.file } as? Progressive.Done)?.value,
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
    scrollRatio: Float,
    isLast: Boolean,
    colors: ShareActivityView.BgVariants,
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
        scrollRatio = scrollRatio,
    ) { childModifier, inFile ->
        if (inFile != null) {
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
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    Column(
        Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Consistent.Pad.smallX),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MimeTypeView(MimeType.Png)
        Icon(
            Icons.Rounded.KeyboardArrowDown,
            "mime type from-to arrow"
        )
        MimeTypeSelectByCategory(
            allowedMimes = MimeType.Outputs.image
        ) { }
    }
}