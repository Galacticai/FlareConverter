package global.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import global.common.models.space.dp_bound.DpPlacement.Companion.bounds
import global.common.ui.bounds_resolver.BoundPlacementUtil.rememberScrollConnection
import global.common.ui.bounds_resolver.BoundResolve
import global.common.ui.bounds_resolver.PlacementState
import global.common.util.DpUtil.toDp

/**
 * main page content (behind)
 * @param resolver place where [contentAbove] will settle while collapsed
 */
typealias ExpanderPageContentBehind = @Composable PlacementState.(
    resolver: @Composable (Modifier) -> Unit
) -> Unit

/** draggable floating content
 * @param boundModifier apply [bounds] to the content above so it moves and settles on top of [contentBehind] while expanded
 * @param dragModifier apply [draggable] to the content above so it allows dragging - _disable this to lock dragging and let the children scroll freely_
 */
typealias ExpanderPageContentAbove = @Composable PlacementState.(
    boundModifier: Modifier,
    dragModifier: Modifier,
) -> Unit

/**
 * page with [contentAbove] that expands on top of [contentBehind] (in z axis)
 * @see BoundResolve
 * @see PlacementState
 */
@Composable
fun ExpanderPage(
    modifier: Modifier = Modifier,
    state: PlacementState = PlacementState.remember(),
    vibrateOnSettle: Boolean = true,
    contentBehind: ExpanderPageContentBehind,
    contentAbove: ExpanderPageContentAbove,
) = with(state) {
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current

    fun vibrate() {
        haptic.performHapticFeedback(HapticFeedbackType.GestureThresholdActivate)
    }

    LaunchedEffect(this@with, vibrateOnSettle) {
        if (!vibrateOnSettle) return@LaunchedEffect

        var previousRatio = ratio

        snapshotFlow {
            ratio to dragDirection
        }.collect { (ratio, dragDirection) ->
            val crossedThreshold = dragDirection > 0 &&
                    previousRatio < settleThreshold &&
                    ratio >= settleThreshold
            if (crossedThreshold) vibrate()
            previousRatio = ratio
        }
    }

    Box(modifier) {
        BoundResolve(Modifier.fillMaxSize()) { destination = it }

        contentBehind { modifier ->
            BoundResolve(modifier) { source = it }
        }

        val boundModifier = Modifier.bounds { current }
        val dragModifier = Modifier.draggable(
            dragState, orientation,
            onDragStopped = {
                val isOtherSide = settle(it.toDp(density.density))
                if (isOtherSide) vibrate()
            }
        )

        contentAbove(boundModifier, dragModifier)
    }
}


@Preview(showBackground = true)
@Composable
fun ExpanderPagePreview() {
    val pad = 10.dp
    val shape = RoundedCornerShape(20.dp)

    val boundState = PlacementState.remember()
    Column(Modifier.fillMaxSize()) {
        ExpanderPage(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad / 2)
                .clip(shape)
                .background(MaterialTheme.colorScheme.surface),
            state = boundState,
            contentBehind = { resolver ->
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(pad)
                ) {
                    Box(
                        Modifier
                            .clip(shape)
                            .fillMaxWidth()
                            .weight(.6f)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer.copy(.5f),
                                shape
                            )
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.primary.copy(.5f),
                                shape
                            )
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Distance:\n$distance\nof $distanceMax",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center
                        )
                    }

                    resolver(
                        Modifier
                            .fillMaxWidth()
                            .weight(.4f)
                            .padding(horizontal = pad * 2)
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer.copy(.5f),
                                shape
                            )
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.secondary.copy(.5f),
                                shape
                            )
                    )

                }
            },
            contentAbove = { boundsModifier, dragModifier ->
                val listState = rememberLazyListState()

                val nestedScrollConnection = rememberScrollConnection(listState)

                val isExpanded = ratioAnimated.value == 1f

                Box(
                    modifier = boundsModifier then dragModifier then Modifier
                        .clip(shape)
                        .border(
                            2.dp,
                            MaterialTheme.colorScheme.secondary,
                            shape
                        ),
                ) {
                    val list = List(60) { it }
                    val itemMod = Modifier
                        .fillMaxWidth()
                        .padding(pad)
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.secondary.copy(.5f),
                            shape
                        )

                    Column {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(32.dp)
                                .then(dragModifier),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                Modifier
                                    .width(40.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(MaterialTheme.colorScheme.secondary.copy(0.5f))
                            )
                        }

                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .weight(1f)
                                .nestedScroll(nestedScrollConnection),
                            userScrollEnabled = isExpanded
                        ) {
                            item { Text("ratio = $ratio", itemMod) }
                            item { Text("ratioAnimated = ${ratioAnimated.value}", itemMod) }
                            item { Text("orientationSign = $orientationSign", itemMod) }
                            item {
                                Text(
                                    "canScrollBackward = ${listState.canScrollBackward}",
                                    itemMod
                                )
                            }
                            item {
                                Text(
                                    "canScrollForward = ${listState.canScrollForward}",
                                    itemMod
                                )
                            }
                            item { Text("isExpanded = $isExpanded", itemMod) }
                            items(list.size) { Text("Item $it", itemMod) }
                        }
                    }
                }
            }
        )


        Text(
            "${boundState.ratio}\n\n${boundState.current}",
            Modifier.height(100.dp),
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )
    }
}