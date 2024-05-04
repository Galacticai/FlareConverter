package com.galacticai.flareconverter.ui.share_activity.components

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.galacticai.flareconverter.models.MediaFile
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.models.ffmpeg.ff_command.args.FFmpegArg
import com.galacticai.flareconverter.ui.components.Expandable
import com.galacticai.flareconverter.ui.components.View
import com.galacticai.flareconverter.ui.components.expressive.rememberExpressiveRadius
import com.galacticai.flareconverter.ui.components.getIcon
import com.galacticai.flareconverter.ui.components.mime_type.MimeTypeSelectByCategory
import com.galacticai.flareconverter.ui.components.mime_type.MimeTypeView
import com.galacticai.flareconverter.ui.share_activity.ShareActivity
import com.galacticai.flareconverter.ui.share_activity.components.LocalShareActivityStates.LocalConfigListState
import com.galacticai.flareconverter.ui.share_activity.components.LocalShareActivityStates.LocalShareActivityColors
import com.galacticai.flareconverter.ui.share_activity.components.ShareActivityView.containerOutline
import com.galacticai.flareconverter.ui.share_activity.components.ShareActivityView.edgePad
import com.galacticai.flareconverter.util.Consistent
import com.galacticai.flareconverter.util.Consistent.Shape.Radius.paddedBy
import com.galacticai.flareconverter.util.FlowUtil.collectSingleAsState
import com.galacticai.flareconverter.util.Settings
import global.common.ui.FadeDirection
import global.common.ui.Skeleton
import global.common.ui.bounds_resolver.LocalPlacementState
import global.common.ui.fadeEdges
import global.common.util.IterableUtil.pickRandom
import kotlinx.coroutines.flow.update
import java.io.File

@Composable
fun ConfigView() {
    val activity = LocalActivity.current as ShareActivity
    val placementState = LocalPlacementState.current
    val configListState = LocalConfigListState.current
    val configShown by activity.vm.configShownFlow.collectAsState()
    val configEnabled by Settings.ConfigEnabled.rememberObject()
    val inFile by activity.vm.inFileFlow.collectAsState()

    val entries = remember(configShown, configEnabled) {
        val configEnabledSet = configEnabled.toSet()
        configShown.sortedBy { it in configEnabledSet }
    }

    val padMax = 1f.edgePad(true)

    LazyColumn(
        state = configListState,
        verticalArrangement = Arrangement.spacedBy(padMax),
        modifier = Modifier.padding(vertical = padMax).fadeEdges(
            size = Consistent.Pad.largeX,
            direction = FadeDirection.Vertical,
            beginAlpha = if (configListState.canScrollBackward) 1f else 0f,
            endAlpha = if (configListState.canScrollForward) 1f else 0f,
        )
    ) {
        items(
            count = entries.size, key = { entries[it].name },
        ) { i ->
            val entry = entries[i]
            val isLast = i == configShown.size - 1
            val info = inFile.done ?: return@items
            entry.ConfigViewItem(
                expandStateKey = entry.name,
                info = info,
                isLast = isLast,
                scrollRatio = placementState.ratio,
            )
        }
    }
}

@Composable
fun ConfigViewSkeleton() {
    Column(
        verticalArrangement = Arrangement.spacedBy(Consistent.Pad.regular),
    ) {
        repeat(5) {
            Skeleton(Modifier.fillMaxWidth().height(Consistent.Pad.medium))
        }
    }
}

@Composable
fun ConfigViewItem(
    expandStateKey: String,
    icon: ImageVector,
    title: String,
    scrollRatio: Float,
    isLast: Boolean,
    modifier: Modifier = Modifier,
    onEnabled: (Boolean) -> Unit,
    item: @Composable (modifier: Modifier, inFile: File?) -> Unit,
) {
    val activity = LocalActivity.current as ShareActivity
    val colors = LocalShareActivityColors.current
    val inFile by activity.vm.inFileFlow.collectAsState()
    val expandState = activity.vm.configExpandFlow
        .collectSingleAsState(expandStateKey, false)

    val padHorizontal = scrollRatio.edgePad(true)

    val radiusRange = Consistent.Shape.Radius.expressiveDp.let {
        it.copy(
            neutral = it.neutral paddedBy padHorizontal,
            clicked = it.clicked paddedBy padHorizontal,
        )
    }
    val headerBackground = colors.bg2.let {
        it.copy(.25f) to it.copy(.5f)
    }
    val contentBackground = colors.bg2.copy(.1f) to Color.Transparent
    val headerBorder = colors.ratioSnap.containerOutline
    val contentBorder = BorderStroke(
        .5.dp,
        MaterialTheme.colorScheme.outlineVariant.copy(.5f)
    )

    Expandable(
        Modifier.fillMaxWidth().heightIn(max = 500.dp)
            .padding(horizontal = padHorizontal)
                then modifier,
        radiusRange = radiusRange,
        expandState = expandState,
        headerBorder = headerBorder,
        contentBorder = contentBorder,
        headerBackground = headerBackground,
        contentBackground = contentBackground,
        icon = { _, modifier ->
            ConfigIcon(
                modifier,
                icon, colorKey = expandStateKey,
                enabledDefault = true, //TODO: Settings.ArgsEnabled
                onEnabled,
            )
        },
        title = { expanded, modifier ->
            val weight by animateIntAsState(
                if (expanded) FontWeight.Light.weight
                else FontWeight.SemiBold.weight
            )
            Text(title, modifier, fontWeight = FontWeight(weight))
        },
    ) { modifier ->
        item(
            modifier.fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            inFile.done?.fileInfo?.file,
        )
    }
}

@Composable
fun ConfigIcon(
    modifier: Modifier,
    icon: ImageVector, colorKey: String,
    enabledDefault: Boolean,
    onEnabled: (Boolean) -> Unit,
) {
    var enabled by remember { mutableStateOf(enabledDefault) }
    val colorsLocal = LocalShareActivityColors.current
    val colors = MaterialTheme.colorScheme

    /** first = container  |  second = content */
    val colorPair = remember(colorKey) {
        listOf(
            colors.primaryContainer to colors.primary,
            colors.primaryContainer to colors.onPrimaryContainer,
            colors.secondaryContainer to colors.secondary,
            colors.secondaryContainer to colors.onSecondaryContainer,
            colors.tertiaryContainer to colors.tertiary,
            colors.tertiaryContainer to colors.onTertiaryContainer,
        ).pickRandom(colorKey)
    }

    val containerColorAnimated by animateColorAsState(
        if (enabled) colorPair.first.copy(.25f * colorsLocal.ratioSnap)
        else MaterialTheme.colorScheme.surface.copy(colorsLocal.ratioSnap)
    )

    val expressive = rememberExpressiveRadius(Consistent.Shape.Radius.expressive)

    Card(
        onClick = {
            enabled = !enabled
            onEnabled(enabled)
        },
        modifier = modifier then expressive.modifierPress,
        shape = expressive.shape,
        colors = CardDefaults.cardColors(
            containerColor = containerColorAnimated,
            contentColor = colorPair.second,
        ),
        border = BorderStroke(
            .5.dp,
            colorPair.second.copy(.25f * colorsLocal.ratioSnap)
        )
    ) {
        Icon(
            icon, "$this",
            Modifier.padding(Consistent.Pad.regular),
        )
    }
}

/** specifically for [FFmpegArg] */
@Composable
fun FFmpegArg.ConfigViewItem(
    modifier: Modifier = Modifier,
    info: MediaFile,
    scrollRatio: Float,
    isLast: Boolean,
    expandStateKey: String,
) {
    val activity = LocalActivity.current as ShareActivity
    val configFlow = activity.vm.configFlow
    var configEnabled by Settings.ConfigEnabled.rememberObject()
    ConfigViewItem(
        modifier = modifier,
        isLast = isLast,
        expandStateKey = expandStateKey,
        scrollRatio = scrollRatio,
        icon = this.getIcon(),
        title = this.toString(),
        onEnabled = {
            configEnabled = configEnabled.toMutableSet().apply {
                if (it) add(this@ConfigViewItem)
                else remove(this@ConfigViewItem)
            }.toTypedArray()
        }
    ) { childModifier, inFile ->
        if (inFile == null) return@ConfigViewItem
        View(childModifier, info) {
            configFlow.update {
                val map = it.toMutableMap()
                for ((arg, value) in map) map[arg] = value
                map
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun Preview() {
    Column(
        Modifier.padding(10.dp).fillMaxWidth(),
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
