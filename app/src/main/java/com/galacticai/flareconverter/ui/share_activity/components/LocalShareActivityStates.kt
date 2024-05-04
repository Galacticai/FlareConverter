package com.galacticai.flareconverter.ui.share_activity.components

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import com.galacticai.flareconverter.models.ConvertStage
import com.galacticai.flareconverter.ui.share_activity.ShareActivity
import com.galacticai.flareconverter.ui.share_activity.ShareActivityHelpers
import global.common.ui.bounds_resolver.LocalPlacementState
import global.common.ui.bounds_resolver.PlacementState

object LocalShareActivityStates {
    val LocalConfigListState = compositionLocalOf<LazyListState> {
        error("Config scroll state is not provided")
    }
    val LocalShareActivityColors = compositionLocalOf<ShareActivityView.Colors> {
        error("ShareActivityView.Colors is not provided")
    }
    val LocalConvertStage = compositionLocalOf<ConvertStage> {
        error("ConvertStage is not provided")
    }

    @Composable
    fun Provider(content: @Composable () -> Unit) {
        val activity = LocalActivity.current as ShareActivity
        val placementState = PlacementState.remember()
        val configColumnState = rememberLazyListState()
        val colors by ShareActivityHelpers.getBgColors(placementState.ratio)
        val convertStage = activity.helpers.rememberConvertStage()

        CompositionLocalProvider(
            LocalPlacementState provides placementState,
            LocalConfigListState provides configColumnState,
            LocalShareActivityColors provides colors,
            LocalConvertStage provides convertStage,
        ) { content() }
    }
}