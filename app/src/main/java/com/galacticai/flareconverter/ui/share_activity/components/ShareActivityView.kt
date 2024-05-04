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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.galacticai.flareconverter.ui.components.dialog.InitFileConvertDialog
import com.galacticai.flareconverter.ui.components.expressive.ExpressiveHandle
import com.galacticai.flareconverter.ui.components.thumbnail.Thumbnail
import com.galacticai.flareconverter.ui.share_activity.ShareActivity
import com.galacticai.flareconverter.ui.share_activity.components.ShareActivityView.ContentAbove
import com.galacticai.flareconverter.ui.share_activity.components.ShareActivityView.ContentBehind
import com.galacticai.flareconverter.ui.themes.ThemedScaffold
import com.galacticai.flareconverter.util.Consistent
import com.galacticai.flareconverter.util.Consistent.Pad.screenHPadding
import global.common.models.space.dp_bound.DpBound
import global.common.ui.bounds_resolver.IBoundPlacementState
import global.common.ui.colors.colorInBetween
import global.common.ui.expander_page.ExpanderPage
import global.common.util.NumberUtil.inverse

@Composable
fun ShareActivityView() {
    ThemedScaffold(
        topBar = { AppHeader() },
        bottomBar = { ActionButtons() }
    ) { scaffoldPadding ->
        ExpanderPage(
            scope = IBoundPlacementState.remember(.33f),
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding),
            contentBehind = {
                ContentBehind(it)
            },
            contentAbove = { bound, drag ->
                ContentAbove(bound, drag)
            }
        )
        InitFileConvertDialog()
    }
}

object ShareActivityView {
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
    fun getBgColors(progress: Float): State<BgVariants> {
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


    @Composable
    fun IBoundPlacementState.ContentBehind(resolver: @Composable (Modifier) -> Unit) {
        val activity = LocalActivity.current as ShareActivity
        val inFile by activity.vm.inFileLive.observeAsState()
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
                inFile = inFile!!.convert { it.file },
                mimeCategory = shareInfo?.inMime?.category
            )

            resolver(Modifier.fillMaxSize())
        }
    }

    @SuppressLint("ModifierParameter")
    @Composable
    fun IBoundPlacementState.ContentAbove(boundModifier: Modifier, dragModifier: Modifier) {
        val hide = source == DpBound.zero || destination == DpBound.zero
        val alphaTarget = if (hide) 0f else 1f
        val alphaAnimated by animateFloatAsState(
            alphaTarget,
            tween(500),
            label = "alpha"
        )

        val colors by getBgColors(ratio)
        val pad = ratio.edgePad(false) to ratio.edgePad(true)

        val modifier = boundModifier then dragModifier then Modifier
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
            colors = CardDefaults.cardColors(colors.first),
            modifier = modifier,
        ) {
            ExpressiveHandle(colors.second, "toggle expand options") {
                this@ContentAbove.animateRatio(if (ratio == 0f) 1f else 0f)
            }
            ConfigView(colors = colors)
        }
    }
}