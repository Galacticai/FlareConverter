package global.common.ui.graphing.bar_chart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import global.common.ui.graphing.vertical
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@Composable
fun <T> BarChart(
    modifier: Modifier = Modifier,
    startAsScrolledToEnd: Boolean = false,
    style: BarChartStyle<T> = BarChartStyle(),
    parser: (T) -> BarData,
    data: Iterable<T>,
    onBarClick: ((data: T, parsed: BarData, i: Int) -> Unit)? = null,
) {
    var max by rememberSaveable { mutableFloatStateOf(Float.MIN_VALUE) }
    var min by rememberSaveable { mutableFloatStateOf(Float.MAX_VALUE) }
    val range by remember { derivedStateOf { max..min } }
    val dataParsed by remember(data) {
        derivedStateOf {
            var maxNew = max
            var minNew = min
            val parsed = data.map {
                val barData = parser(it)
                if (barData.value > maxNew) maxNew = barData.value
                if (barData.value < minNew) minNew = barData.value
                barData
            }
            max = maxNew
            min = minNew
            parsed
        }
    }
    //? Must do this otherwise it wont initialize correctly
    LaunchedEffect(data) {
        @Suppress("UNUSED_EXPRESSION")
        dataParsed //? Trigger derivedStateOf
    }

    if (max < min) {
        BarChartContent(modifier, style) {
            Icon(
                imageVector = Icons.Rounded.Warning, null,
                modifier = Modifier.align(Alignment.Center),
                tint = MaterialTheme.colorScheme.error
            )
        }
        return
    }

    val rowState = rememberLazyListState()
    suspend fun scrollToEnd() = rowState.scrollToItem(rowState.layoutInfo.totalItemsCount - 1)
    if (startAsScrolledToEnd) {
        LaunchedEffect(rowState, data) {
            if (rowState.layoutInfo.totalItemsCount > 0) scrollToEnd()
        }
    }
    var scaleValuesWidth by remember { mutableStateOf(0.dp) }

    @Composable
    fun scaleLines() {
        if (style.horizontalScale == null) return
        val ySectionHeight = style.bar.heightMax / style.horizontalScale.count
        Box {
            Column(
                modifier = Modifier
                    .height(style.bar.heightMax)
                    .fillMaxWidth()
            ) {
                @Composable
                fun line() = HorizontalDivider(
                    modifier = Modifier.padding(0.dp),
                    thickness = style.horizontalScale.thickness,
                    color = style.horizontalScale.color
                )
                line()
                repeat(style.horizontalScale.count) {
                    Spacer(modifier = Modifier.height(ySectionHeight - style.horizontalScale.thickness))
                    line()
                }
            }
        }
    }

    @Composable
    fun scaleValues() {
        val context = LocalContext.current
        val direction = LocalLayoutDirection.current
        if (style.horizontalScale == null) return
        if (style.yValue == null) return

        val ySectionHeight = style.bar.heightMax / style.horizontalScale.count
        Column(
            modifier = Modifier.height(style.bar.heightMax),
            verticalArrangement = Arrangement.Bottom
        ) {
            var maxWidth by rememberSaveable { mutableIntStateOf(0) }

            (style.horizontalScale.count downTo 1).forEach { i ->
                val value = max / style.horizontalScale.count * i
                val startPadding = style.yValue.margin.calculateStartPadding(direction)
                val endPadding = style.yValue.margin.calculateEndPadding(direction)

                Box(modifier = Modifier.height(ySectionHeight)) {
                    Surface(
                        modifier = Modifier
                            .heightIn(max = ySectionHeight)
                            .padding(
                                bottom = style.yValue.margin.calculateBottomPadding(),
                                start = startPadding,
                                end = endPadding,
                            ),
                        shape = RoundedCornerShape(
                            topStart = style.yValue.bgRadius / 2,
                            topEnd = style.yValue.bgRadius / 2,
                            bottomStart = style.yValue.bgRadius,
                            bottomEnd = style.yValue.bgRadius
                        ),
                        color = style.yValue.bgColor(value, range, i),
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(style.yValue.padding)
                                .onGloballyPositioned {
                                    val width = it.size.width
                                    if (width < maxWidth) return@onGloballyPositioned
                                    maxWidth = width
                                    val density = context.resources.displayMetrics.density
                                    scaleValuesWidth = (it.size.width / density).dp +
                                            startPadding + endPadding + 2.dp
                                },
                            text = style.yValue.format(value, range, i),
                            color = style.yValue.color(value, range, i),
                            fontSize = style.yValue.fontSize,
                            fontWeight = style.yValue.fontWeight
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun scrollToEndButton(modifier: Modifier) {
        if (style.scrollButton == null) return
        val canScroll = rowState.canScrollForward
        AnimatedVisibility(
            modifier = modifier,
            visible = canScroll
        ) {
            IconButton(
                modifier = Modifier
                    .shadow(
                        10.dp,
                        shape = CircleShape,
                        ambientColor = style.bgColor,
                        spotColor = style.bgColor,
                    ),
                onClick = { if (canScroll) MainScope().launch { scrollToEnd() } },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = style.scrollButton.bgColor,
                    contentColor = style.scrollButton.color
                ),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.padding(horizontal = 10.dp),
                )
            }
        }
    }

    @Composable
    fun bars() {
        val isLtr = LocalLayoutDirection.current == LayoutDirection.Ltr
        LazyRow(
            modifier = Modifier
                .height(style.bar.heightMax)
                .fillMaxWidth(),
            state = rowState,
            verticalAlignment = Alignment.Bottom
        ) {
            item { Spacer(modifier = Modifier.width(scaleValuesWidth)) }
            data.forEachIndexed { i, dataItem ->
                if (i > dataParsed.lastIndex) return@forEachIndexed
                val parsedItem = dataParsed[i]
                val barHeight = style.bar.heightMax * (parsedItem.value / max)

                if (i > 0) item { Spacer(modifier = Modifier.width(style.bar.spacing)) }
                item {
                    Box(
                        modifier = Modifier
                            .height(style.bar.heightMax)
                            .clickable { onBarClick?.invoke(dataItem, parsedItem, i) },
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        val bottomRadius = min(style.bar.radius / 5, 2.dp)
                        Surface(
                            modifier = Modifier.size(style.bar.width, barHeight),
                            color = style.bar.color(dataItem, range),
                            shape = RoundedCornerShape(
                                topStart = style.bar.radius, topEnd = style.bar.radius,
                                bottomStart = bottomRadius, bottomEnd = bottomRadius,
                            ),
                            border = style.bar.border
                        ) {}
                        if (style.xValue != null) {
                            Surface(
                                modifier = Modifier
                                    .vertical()
                                    .rotate(90f * (if (isLtr) -1 else 1))
                                    .heightIn(max = style.bar.heightMax)
                                    .padding(style.xValue.margin),
                                shape = RoundedCornerShape(style.xValue.bgRadius),
                                color = style.xValue.bgColor(dataItem, parsedItem, range),
                            ) {
                                Text(
                                    modifier = Modifier.padding(style.xValue.padding),
                                    text = style.xValue.format(dataItem, parsedItem, range),
                                    color = style.xValue.color(dataItem, parsedItem, range),
                                    fontSize = style.xValue.fontSize,
                                    fontWeight = style.xValue.fontWeight
                                )
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.width(style.bar.width / 2)) }
        }
    }

    BarChartContent(modifier, style) {
        scaleLines()
        bars()
        scaleValues()
        scrollToEndButton(Modifier.align(Alignment.CenterEnd))
    }
}

@Composable
private fun <T> BarChartContent(
    modifier: Modifier,
    style: BarChartStyle<T>,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(style.bar.heightMax)
            .background(style.bgColor)
            .then(modifier),
    ) { content() }
}


@Preview(showBackground = true)
@Composable
fun BarChartPreview() {
    val data = (10 downTo 0).map { it.toFloat() }
    BarChart(
        parser = { BarData(it.toString(), it) },
        data = data
    )
}