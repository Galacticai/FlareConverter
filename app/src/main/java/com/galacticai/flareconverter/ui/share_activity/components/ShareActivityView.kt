package com.galacticai.flareconverter.ui.share_activity.components

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.galacticai.flareconverter.models.ffmpeg.FFmpegArg
import com.galacticai.flareconverter.ui.components.dialog.InitFileConvertDialog
import com.galacticai.flareconverter.ui.components.expressive.ExpressiveHandle
import com.galacticai.flareconverter.ui.components.thumbnail.Thumbnail
import com.galacticai.flareconverter.ui.share_activity.ShareActivity
import com.galacticai.flareconverter.ui.share_activity.ShareActivityHelpers
import com.galacticai.flareconverter.ui.share_activity.ShareActivityVM
import com.galacticai.flareconverter.ui.share_activity.components.ShareActivityView.ContentAbove
import com.galacticai.flareconverter.ui.share_activity.components.ShareActivityView.ContentBehind
import com.galacticai.flareconverter.ui.themes.ThemedScaffold
import com.galacticai.flareconverter.util.Consistent
import com.galacticai.flareconverter.util.Consistent.Pad.screenHPadding
import com.galacticai.flareconverter.util.MimeTypeUtils.outMimeSetting
import global.common.models.space.dp_bound.DpPlacement
import global.common.ui.ExpanderPage
import global.common.ui.bounds_resolver.BoundPlacementUtil.rememberScrollConnection
import global.common.ui.bounds_resolver.PlacementState
import global.common.util.ColorUtil.colorInBetween
import global.common.util.NumberUtil.inverse
import kotlinx.coroutines.flow.update

@Composable
fun ShareActivityView() {
    ThemedScaffold(
        topBar = { AppHeader() },
        bottomBar = { ActionButtons() }
    ) { scaffoldPadding ->
        val boundState = PlacementState.remember()
        ExpanderPage(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding),
            state = boundState,
            contentBehind = { ContentBehind(it) },
            contentAbove = { bound, drag ->
                ContentAbove(bound, drag)
            }
        )

        InitFileConvertDialog()
        ShareActivityView.SyncArgs(boundState)
    }
}

object ShareActivityView {
    /** bg1 -> bg2 */
    typealias Bg = Pair<Color, Color>

    const val CONTENT_SCROLL_ALPHA = .25f
    const val CONTENT_SCROLL_SCALE = .97f

    fun Float.edgePad(inner: Boolean): Dp {
        val pad = Consistent.Pad.screenHorizontal
        val effect = this * .5f
        return if (inner) pad * effect
        else pad inverse effect
    }

    @Composable
    fun getBgColors(progress: () -> Float): State<Bg> {
        val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
        val surface = MaterialTheme.colorScheme.surface

        return remember(surfaceVariant, surface) {
            derivedStateOf {
                val p = progress()
                fun between(pair: Bg) = colorInBetween(
                    1 - p, 0f, 1f,
                    pair.first, pair.second
                )

                val bg = between(surfaceVariant to surface)
                val fg = between(surface to surfaceVariant)
                bg to fg
            }
        }
    }


    @Composable
    fun PlacementState.ContentBehind(resolver: @Composable (Modifier) -> Unit) {
        val activity = LocalActivity.current as ShareActivity
        val inFile by activity.vm.inFileLive.collectAsState()
        val shareInfo by activity.vm.shareInfoState

        Column(
            verticalArrangement = Arrangement.spacedBy(Consistent.Pad.regular),
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    alpha = 1 - (ratio inverse CONTENT_SCROLL_ALPHA)
                    val s = 1 - (ratio inverse CONTENT_SCROLL_SCALE)
                    scaleX = s; scaleY = s
                },
        ) {
            Thumbnail(
                modifier = Modifier
                    .height(400.dp)
                    .screenHPadding(),
                inFile = inFile.convert { it.file },
                mimeCategory = shareInfo?.inMime?.category
            )

            resolver(Modifier.fillMaxSize())
        }
    }

    @SuppressLint("ModifierParameter")
    @Composable
    fun PlacementState.ContentAbove(boundModifier: Modifier, dragModifier: Modifier) {
        val hide = source == DpPlacement.zero || destination == DpPlacement.zero

        val colors by getBgColors { ratio }
        val pad = ratio.edgePad(false) to ratio.edgePad(true)

        val alphaAnimated by animateFloatAsState(
            if (hide) 0f else 1f,
            tween(500),
            label = "alpha"
        )

        val lazyColumnState = rememberLazyListState()
        val scrollConnection = rememberScrollConnection(lazyColumnState)
        val modifier = boundModifier then
                Modifier.nestedScroll(scrollConnection) then
                dragModifier then //! intentional: drag must be AFTER nested scroll
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = pad.first)
                    .graphicsLayer {
                        clip = true
                        shape = Consistent.Shape.Rounded.all
                        shadowElevation = pad.second.toPx()
                        alpha = alphaAnimated
                    }
                    .border(
                        .5.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = ratio),
                        Consistent.Shape.Rounded.all,
                    )

        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(colors.first),
        ) {
            ExpressiveHandle(colors.second, "toggle expand options") {
                this@ContentAbove.animateRatio(if (ratio == 0f) 1f else 0f)
            }
            ConfigView(lazyColumnState)
        }
    }


    /**
     * sync:
     * - [ShareActivityVM.allowedArgsState]
     * - [ShareActivityVM.configExpandFlow] entries
     *
     * based on [ShareActivityVM.shareInfoState]
     */
    @Composable
    fun SyncArgs(boundState: PlacementState) {
        val activity = LocalActivity.current as ShareActivity
        val shareInfo by activity.vm.shareInfoState
        if (shareInfo == null) return
        val configExpandFlow = activity.vm.configExpandFlow
        var allowedArgs by activity.vm.allowedArgsState

        val outMime by shareInfo!!.inMime.outMimeSetting
            .rememberObject(false, shareInfo)

        LaunchedEffect(outMime.category) {
            val args = ShareActivityHelpers.ffmpegAllowedArgs[
                outMime.category
            ] ?: emptyList()

            allowedArgs = args

            configExpandFlow.update {
                val entries = args.map { it.name }
                entries.associateWith {
                    //! intentional: keep FFmpegArg.Input expanded
                    it == FFmpegArg.Input.name
                }
            }
        }

        LaunchedEffect(configExpandFlow) {
            var previous = emptyMap<String, Boolean>()

            configExpandFlow.collect {
                val isExpandedAny = it.entries.firstOrNull { (key, isExpanded) ->
                    key != FFmpegArg.Input.name //! intentional: skip FFmpegArg.Input
                            && isExpanded
                            && previous[key] != true
                }

                //? expanded child => expand parent
                if (isExpandedAny != null) {
                    boundState.animateRatio(1f)
                }

                previous = it
            }
        }
    }
}