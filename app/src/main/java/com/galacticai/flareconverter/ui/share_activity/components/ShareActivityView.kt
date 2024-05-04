package com.galacticai.flareconverter.ui.share_activity.components

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.galacticai.flareconverter.ui.components.FloatingElevatingBox
import com.galacticai.flareconverter.ui.components.thumbnail.Thumbnail
import com.galacticai.flareconverter.ui.share_activity.ShareActivity
import com.galacticai.flareconverter.ui.themes.ThemedScaffold
import com.galacticai.flareconverter.util.Consistent
import com.galacticai.flareconverter.util.Consistent.Pad.screenHPadding
import global.common.NumberUtils.inverse
import global.common.models.FutureValue.Companion.pickFirst
import global.common.ui.StaticHandle
import global.common.ui.colors.colorInBetween
import global.common.ui.elevated_expandable.DpBounds

/** bg1 -> bg2 */
typealias BgVariants = Pair<Color, Color>

const val CONTENT_SCROLL_ALPHA = .25f
const val CONTENT_SCROLL_SCALE = .97f

fun Float.edgePad(inner: Boolean): Dp {
    val pad = Consistent.Pad.screenHorizontal
    val effect = this * .5f
    return if (inner) pad * effect
    else pad inverse effect
}

@Composable
fun ShareActivityView(modifier: Modifier = Modifier) {
    val activity = LocalActivity.current as ShareActivity
    val density = LocalDensity.current

    val inFilePair by activity.vm.inFileLive.observeAsState()
    val shareInfo by activity.vm.shareInfoState

    var configSectionBoundsCollapsed by remember { mutableStateOf<DpBounds?>(null) }
    var configSectionBoundsExpanded by remember { mutableStateOf<DpBounds?>(null) }
    var configScrollProgress by activity.vm.configScrollProgressState

    val lazyColumnState = rememberLazyListState()
    //    val isAtTop by remember { derivedStateOf { !lazyColumnState.canScrollBackward } }

    ThemedScaffold(
        modifier,
        topBar = { AppHeader() },
        bottomBar = { ActionButtons() }
    ) { pad ->
        Box(Modifier.padding(pad)) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Consistent.Pad.regular),
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = 1 - (configScrollProgress inverse CONTENT_SCROLL_ALPHA)
                        val s = 1 - (configScrollProgress inverse CONTENT_SCROLL_SCALE)
                        scaleX = s
                        scaleY = s
                    },
            ) {

                Thumbnail(
                    modifier = Modifier
                        .height(400.dp)
                        .screenHPadding(),
                    inFile = inFilePair?.pickFirst(),
                    mimeCategory = shareInfo?.inMime?.category
                )

                DpBounds.Resolve(
                    Modifier.fillMaxSize(),
                    onResult = { configSectionBoundsCollapsed = it }
                )
            }


            AnimatedVisibility(
                shareInfo != null,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                DpBounds.Resolve(
                    Modifier.fillMaxSize(),
                    onResult = { configSectionBoundsExpanded = it }
                ) {
                    // ElevatedExpandable(
                    //      bounds = configSectionBoundsCollapsed!! to configSectionBoundsExpanded!!,
                    ////    scrollLock = !isAtTop
                    // ) { modifier, progress, boundsCurrent ->
                    FloatingElevatingBox(
                        bounds = configSectionBoundsCollapsed ?: DpBounds.zero,
                        expandedHeight =
                            configSectionBoundsExpanded?.size?.y
                                ?: configSectionBoundsCollapsed?.size?.y
                                ?: 0.dp,
                        canScrollBackward = { lazyColumnState.canScrollBackward }
                    ) { scrollProgress ->
                        if (configScrollProgress != scrollProgress) {
                            configScrollProgress = scrollProgress
                        }

                        val colors by getColors(scrollProgress)
                        val padOuter = scrollProgress.edgePad(false)
                        val padInner = scrollProgress.edgePad(true)
                        Card(
                            colors = CardDefaults.cardColors(colors.first),
                            modifier = Modifier
                                .fillMaxSize()
                                // .bounds(boundsCurrent)
                                .padding(horizontal = padOuter)
                                .border(
                                    width = .5.dp,
                                    shape = Consistent.Shape.Rounded.all,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = scrollProgress),
                                )
                                .graphicsLayer {
                                    clip = true
                                    shape = Consistent.Shape.Rounded.all
                                    shadowElevation = with(density) { padInner.toPx() }
                                } then modifier,
                        ) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = padInner)
                            ) {
                                StaticHandle(
                                    modifier = Modifier.align(Alignment.Center),
                                    100.dp, Consistent.Pad.smallX,
                                    shape = Consistent.Shape.Rounded.all,
                                    color = colors.second
                                )

                                val expanded = activity.vm.configExpandStates.any { it.value };

                                TextButton(
                                    modifier = Modifier.align(Alignment.CenterEnd),
                                    onClick = {
                                        for (state in activity.vm.configExpandStates) {
                                            state.value = !expanded
                                        }
                                    }) { Text("Toggle all") }
                            }

                            ConfigView(colors = colors, lazyColumnState = lazyColumnState)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun getColors(progress: Float): State<BgVariants> {
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val surface = MaterialTheme.colorScheme.surface

    //TODO: maybe not going to use this whole thing...

    fun between(pair: BgVariants) = colorInBetween(
        1 - progress, 0f, 1f,
        pair.first, pair.second
    )
    return remember(progress, surfaceVariant, surface) {
        derivedStateOf {
            val bg = between(surfaceVariant to surface)
            val fg = between(surface to surfaceVariant)
            bg to fg
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//private fun ShareActivityPreview() {
//    GalacticTheme {
//        ShareActivityContent(
//            info = ShareInfo(
//                uri = "file.png".toUri(),
//                mimeRaw = "image/png",
//                extension = "png",
//                inMime = MimeType.Png,
//                outMimes = MimeType.Outputs.image,
//            ),
//            inFileLive = MutableLiveData(
//                FutureValue.Finished(File("stuff.stuff"))
//            ),
//        )
//    }
//}
