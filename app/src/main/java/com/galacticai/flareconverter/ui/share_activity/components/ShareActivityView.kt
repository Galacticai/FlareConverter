package com.galacticai.flareconverter.ui.share_activity.components

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.galacticai.flareconverter.models.ConvertStage
import com.galacticai.flareconverter.ui.components.MainBox
import com.galacticai.flareconverter.ui.components.expressive.ExpressiveHandle
import com.galacticai.flareconverter.ui.components.thumbnail.Thumbnail
import com.galacticai.flareconverter.ui.components.thumbnail.ThumbnailState
import com.galacticai.flareconverter.ui.share_activity.ShareActivity
import com.galacticai.flareconverter.ui.share_activity.ShareActivityHelpers
import com.galacticai.flareconverter.ui.share_activity.components.LocalShareActivityStates.LocalConfigListState
import com.galacticai.flareconverter.ui.share_activity.components.LocalShareActivityStates.LocalConvertStage
import com.galacticai.flareconverter.ui.share_activity.components.ShareActivityView.CONTENT_ABOVE_HEIGHT
import com.galacticai.flareconverter.ui.share_activity.components.ShareActivityView.CONTENT_SCROLL_ALPHA
import com.galacticai.flareconverter.ui.share_activity.components.ShareActivityView.CONTENT_SCROLL_SCALE
import com.galacticai.flareconverter.ui.share_activity.components.ShareActivityView.containerOutline
import com.galacticai.flareconverter.ui.share_activity.components.ShareActivityView.edgePad
import com.galacticai.flareconverter.ui.themes.GalacticColorScheme
import com.galacticai.flareconverter.util.Consistent
import com.galacticai.flareconverter.util.Consistent.Pad.screenHPadding
import com.galacticai.flareconverter.util.Modifiers.outsideShadow
import global.common.models.progressive.Progressive
import global.common.models.space.dp_bound.DpPlacement
import global.common.ui.ExpanderPage
import global.common.ui.bounds_resolver.BoundPlacementUtil.rememberScrollConnection
import global.common.ui.bounds_resolver.LocalPlacementState
import global.common.util.ColorUtil
import global.common.util.ColorUtil.colorInBetween
import global.common.util.ColorUtil.hsl
import global.common.util.DpUtil.onSizeChangedDp
import global.common.util.NumberUtil.inverse

@Composable
fun ShareActivityView() = MainBox {
    val safePad = WindowInsets.safeDrawing.asPaddingValues()

    var headerHeight by remember { mutableStateOf(0.dp) }
    var footerHeight by remember { mutableStateOf(0.dp) }
    val contentPad = remember(safePad, headerHeight, footerHeight) {
        safePad + PaddingValues(top = headerHeight, bottom = footerHeight)
    }

    LocalShareActivityStates.Provider {
        //! intentional: first: z=0 (behind header/footer)
        ExpanderPage(
            state = LocalPlacementState.current,
            modifier = Modifier.fillMaxSize(),
            destinationModifier = Modifier
                .fillMaxSize()
                .padding(contentPad)
                .padding(horizontal = Consistent.Pad.screenHorizontal * 0.5f),
            contentBehind = { ContentBehind(contentPad, it) },
            contentAbove = { bound, drag ->
                ContentAbove(bound, drag)
            }
        )

        AppHeader(
            Modifier
                .align(Alignment.TopCenter)
                .onSizeChangedDp { headerHeight = it.height }
        )

        ActionButtons(
            Modifier
                .align(Alignment.BottomCenter)
                .onSizeChangedDp { footerHeight = it.height }
        )

        ShareActivityHelpers.SyncArgs()
    }
}

object ShareActivityView {
    const val CONTENT_SCROLL_ALPHA = .25f
    const val CONTENT_SCROLL_SCALE = .97f
    val CONTENT_ABOVE_HEIGHT = 500.dp

    fun Float.edgePad(inner: Boolean): Dp {
        val pad = Consistent.Pad.screenHorizontal
        val effect = this * .5f
        return if (inner) pad * effect
        else pad inverse effect
    }


    val Float.containerOutline
        @Composable get() = BorderStroke(
            .5.dp,
            MaterialTheme.colorScheme.outlineVariant
                .copy(.1f + (.5f inverse this))
        )

    data class Colors(
        val bg1: Color, val bg2: Color,
        val ratioSnap: Float,
    )

    /** @return pair
     * - ratio (snapped to 0-1 + animated)
     * -  (bg1, bg2) */
    @Composable //TODO: move to ui not helpers
    fun getBgColors(ratio: Float): State<Colors> {
        val colors = MaterialTheme.colorScheme
        val ratioSnap by animateFloatAsState(
            if (ratio == 0f) 0f else 1f,
            tween(500),
        )
        return remember(colors, ratioSnap) {
            derivedStateOf {
                fun between(pair: Pair<Color, Color>) = ColorUtil.colorInBetween(
                    1 - ratioSnap, 0f, 1f,
                    pair.first, pair.second
                )

                val bg1 = between(colors.surface.copy(0f) to colors.surface)
                val bg2 = between(colors.surface.copy(.5f) to colors.surfaceVariant)
                Colors(bg1, bg2, ratioSnap)
            }
        }
    }

    @SuppressLint("ModifierFactoryExtensionFunction") //! intentional: avoid accidental foreign imports
    @Composable
    fun shadowAbove(): Modifier {
        val ratio = LocalPlacementState.current.ratio
        val isDark = GalacticColorScheme.isDark()
        val colorScheme = MaterialTheme.colorScheme
        val color by remember(colorScheme, isDark, ratio) {
            derivedStateOf {
                colorInBetween(
                    .5f, 0f, 1f,
                    colorScheme.inverseSurface, colorScheme.primaryContainer,
                ).hsl(
                    lightness = .15f,
                    alpha = (if (isDark) 1f else .5f) * ratio
                )
            }
        }
        return Modifier.outsideShadow(
            elevation = Consistent.Pad.small * ratio,
            shape = Consistent.Shape.Rounded.all,
            color = color,
        )
    }
}

@Composable
private fun BoxWithConstraintsScope.ContentBehind(
    safePad: PaddingValues,
    resolver: @Composable (Modifier) -> Unit
) {
    val activity = LocalActivity.current as ShareActivity
    val placementState = LocalPlacementState.current
    val inFile by activity.vm.inFileFlow.collectAsState()
    val outFile by activity.vm.outFileFlow.collectAsState()
    val isDark = GalacticColorScheme.isDark()
    val ratio = placementState.ratio

    val thumbnailState = ThumbnailState.remember(
        Thumbnail.Files(inFile, outFile)
    )
    Thumbnail(
        thumbnailState,
        modifier = Modifier.align(Alignment.TopCenter)
            .fillMaxWidth().heightIn(max = 650.dp),
        coverModifier = Modifier
            .blur(Consistent.Pad.regular)
            .graphicsLayer {
                alpha = (1f - (ratio inverse CONTENT_SCROLL_ALPHA)) *
                        (if (isDark) .25f else .5f)

                //? initial = big, final = 1f
                val s = 1f + (1f - ratio) * (1f - CONTENT_SCROLL_SCALE)
                scaleX = s; scaleY = s
            },
        contentModifier = Modifier.fillMaxWidth()
            .padding(safePad).screenHPadding()
            .graphicsLayer {
                //? initial = 1f, final = smaller
                alpha = 1 - (ratio inverse CONTENT_SCROLL_ALPHA)
                val s = 1 - (ratio inverse CONTENT_SCROLL_SCALE)
                scaleX = s; scaleY = s
            },
    )

    val contentAboveHeight = remember(maxHeight) {
        val half = maxHeight / 2
        if (CONTENT_ABOVE_HEIGHT > half) half
        else CONTENT_ABOVE_HEIGHT
    }

    val pad = remember(ratio) {
        ratio.edgePad(false) to ratio.edgePad(true)
    }

    resolver(
        Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth().height(contentAboveHeight)
            .padding(
                start = pad.first, end = pad.first,
                bottom = safePad.calculateBottomPadding()
            )
    )
}

@SuppressLint("ModifierParameter")
@Composable
private fun BoxWithConstraintsScope.ContentAbove(
    boundModifier: Modifier, dragModifier: Modifier,
) {
    val activity = LocalActivity.current as ShareActivity
    val convertStage = LocalConvertStage.current
    val placementState = LocalPlacementState.current
    val configListState = LocalConfigListState.current
    val ratio = placementState.ratio

    val colors by ShareActivityView.getBgColors(ratio)
    val inFile by activity.vm.inFileFlow.collectAsState()
    val hide = inFile !is Progressive.Done
            || placementState.source == DpPlacement.zero
            || placementState.destination == DpPlacement.zero

    val cardAlphaAnimated by animateFloatAsState(
        if (hide) 0f else 1f,
        tween(500),
        label = "card alpha"
    )


    val scrollConnection = placementState.rememberScrollConnection(configListState)
    val modifier = boundModifier then
            Modifier.nestedScroll(scrollConnection) then
            dragModifier then //! intentional: drag must be AFTER nested scroll
            ShareActivityView.shadowAbove() then
            Modifier.alpha(cardAlphaAnimated)

    AnimatedVisibility(
        visible = convertStage <= ConvertStage.Config,
        enter = Consistent.Animation.inUp, exit = Consistent.Animation.outDown,
    ) {
        Card(
            modifier = modifier.align(Alignment.BottomCenter),
            shape = Consistent.Shape.Rounded.all,
            colors = CardDefaults.cardColors(
                colors.bg1,
                MaterialTheme.colorScheme.onBackground, //! required: transparent bg causes fg to go black
            ),
            border = BorderStroke(
                .5.dp,
                MaterialTheme.colorScheme.primary.copy(.5f * ratio),
            )
        ) {
            ExpressiveHandle(
                colors.bg2, border = colors.ratioSnap.containerOutline,
                onClickLabel = "toggle expand options"
            ) {
                placementState.animateRatio()
            }

            AnimatedContent(
                targetState = hide,
                transitionSpec = { Consistent.Animation.inUpOutDown },
            ) {
                if (it) ConfigViewSkeleton()
                else ConfigView()
            }
        }
    }
}