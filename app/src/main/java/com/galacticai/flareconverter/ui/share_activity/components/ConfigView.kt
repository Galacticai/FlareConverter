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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arthenica.ffmpegkit.MediaInformation
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.models.ffmpeg.FFmpegArg
import com.galacticai.flareconverter.ui.components.View
import com.galacticai.flareconverter.ui.components.mime_type.MimeTypeSelectByCategory
import com.galacticai.flareconverter.ui.components.mime_type.MimeTypeView
import com.galacticai.flareconverter.ui.share_activity.ShareActivity
import com.galacticai.flareconverter.ui.share_activity.components.ShareActivityView.edgePad
import com.galacticai.flareconverter.ui.share_activity.components.ShareActivityView.getBgColors
import com.galacticai.flareconverter.util.Consistent
import com.galacticai.flareconverter.util.Consistent.Shape.Radius.paddedBy
import com.galacticai.flareconverter.util.FlowUtil.collectSingleAsState
import global.common.models.progressive.Progressive
import global.common.models.space.dp_bound.DpPlacement
import global.common.ui.ExpandableGroup
import global.common.ui.FadeDirection
import global.common.ui.FadeEdgesContainer
import global.common.ui.bounds_resolver.PlacementState
import java.io.File

@Composable
fun PlacementState.ConfigView(
    lazyColumnState: LazyListState = rememberLazyListState()
) {
    val colors by getBgColors { ratio }
    val activity = LocalActivity.current as ShareActivity
    val shareInfo by activity.vm.shareInfoState
    val inFile by activity.vm.inFileLive.collectAsState()
    val mediaInfo = (inFile.convert { it.info } as? Progressive.Done)?.value
    val allowedArgs by activity.vm.allowedArgsState

    val visible = remember(mediaInfo, shareInfo) {
        mediaInfo != null && shareInfo != null
                && source != DpPlacement.zero && destination != DpPlacement.zero
    }

    AnimatedVisibility(
        visible,
        enter = Consistent.Animation.inUp,
        exit = Consistent.Animation.outDown,
    ) {
        FadeEdgesContainer(
            size = Consistent.Pad.largeX,
            direction = FadeDirection.Vertical,
            beginAlpha = if (lazyColumnState.canScrollBackward) 1f else 0f,
            endAlpha = if (lazyColumnState.canScrollForward) 1f else 0f,
            color = colors.first
        ) {
            if (shareInfo == null) return@FadeEdgesContainer

            LazyColumn(
                state = lazyColumnState,
                verticalArrangement = Arrangement.spacedBy(Consistent.Pad.smallX),
            ) {
                items(allowedArgs.size) { i ->
                    val arg = allowedArgs[i]

                    val expandStateKey = arg.name
                    val isLast = i == allowedArgs.size - 1
                    val mInfo = mediaInfo ?: return@items
                    arg.ConfigViewItem(
                        expandStateKey = expandStateKey,
                        mediaInfo = mInfo,
                        isLast = isLast, colors = colors,
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
    colors: ShareActivityView.Bg,
    modifier: Modifier = Modifier,
    expandStateKey: String,
    item: @Composable (modifier: Modifier, inFile: File?) -> Unit
) {
    val activity = LocalActivity.current as ShareActivity
    val inFile by activity.vm.inFileLive.collectAsState()
    val expandState = activity.vm.configExpandFlow
        .collectSingleAsState(expandStateKey, false)
    var expand by expandState

    val pad = scrollRatio.edgePad(true)
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
                (inFile.convert { it.file } as? Progressive.Done)?.value,
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

/** specifically for [FFmpegArg] */
@Composable
fun FFmpegArg.ConfigViewItem(
    modifier: Modifier = Modifier,
    mediaInfo: MediaInformation,
    scrollRatio: Float,
    isLast: Boolean,
    colors: ShareActivityView.Bg,
    expandStateKey: String,
) {
    val activity = LocalActivity.current as ShareActivity
    var config by activity.vm.configFlow.collectSingleAsState(this, "")
    ConfigViewItem(
        modifier = modifier,
        title = this.toString(),
        isLast = isLast,
        colors = colors,
        expandStateKey = expandStateKey,
        scrollRatio = scrollRatio,
    ) { childModifier, inFile ->
        if (inFile == null) return@ConfigViewItem
        View(childModifier, mediaInfo, colors) {
            config = it
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