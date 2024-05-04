package global.common.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

typealias ExpandableGroupItemFactory =
        @Composable (itemPadding: PaddingValues) -> Unit

@Composable
fun ExpandableGroup(
    title: String,
    modifier: Modifier = Modifier,
    expandState: MutableState<Boolean> = rememberSaveable { mutableStateOf(false) },
    onExpandChanged: ((Boolean) -> Unit)? = null,
    withDivider: Boolean = true,
    radius: Dp = 20.dp,
    padding: Dp = 20.dp,
    contentHeight: Dp? = null,
    titleFontWeightExpanded: FontWeight = FontWeight.W100,
    titleFontWeightCollapsed: FontWeight = FontWeight.W500,
    contentBackground: Color = MaterialTheme.colorScheme.background,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    item: ExpandableGroupItemFactory
) {
    ExpandableGroup(
        title,
        modifier,
        expandState,
        onExpandChanged,
        withDivider,
        radius,
        padding,
        contentHeight,
        titleFontWeightExpanded,
        titleFontWeightCollapsed,
        contentBackground,
        containerColor,
        listOf(item)
    )
}

@Composable
fun ExpandableGroup(
    title: String,
    modifier: Modifier = Modifier,
    expandState: MutableState<Boolean> = rememberSaveable { mutableStateOf(false) },
    onExpandChanged: ((Boolean) -> Unit)? = null,
    withDivider: Boolean = true,
    radius: Dp = 20.dp,
    padding: Dp = 20.dp,
    contentHeight: Dp? = null,
    titleFontWeightExpanded: FontWeight = FontWeight.W100,
    titleFontWeightCollapsed: FontWeight = FontWeight.W900,
    contentBackground: Color = MaterialTheme.colorScheme.background,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    items: List<ExpandableGroupItemFactory>
) {
    var expand by expandState
    val roundedCorner = RoundedCornerShape(radius)
    fun roundedCornerNested(minus: Dp) = RoundedCornerShape(radius - minus)

    Surface(
        color = containerColor,
        shape = roundedCorner,
        modifier = Modifier
            .fillMaxWidth() then modifier,
    ) {
        Column {
            Surface(
                onClick = {
                    expand = !expand
                    onExpandChanged?.invoke(expand)
                },

                color = containerColor,
                shape = roundedCorner,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxWidth()
                ) {
                    AnimatedContent(
                        expand,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        contentAlignment = Alignment.Center,
                        label = "headerExpansionState",
                    ) {
                        Text(
                            text = title,
                            fontWeight = if (it) titleFontWeightExpanded else titleFontWeightCollapsed,
                            fontSize = 18.sp,
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    ExpandIcon.Custom(
                        expand,
                        iconClose = Icons.Rounded.Remove,
                    )
                }
            }
            if (items.isNotEmpty())
                Surface(
                    color = contentBackground,
                    shape = roundedCornerNested(2.dp),
                    modifier = Modifier.padding(
                        bottom = 2.dp,
                        start = 2.dp,
                        end = 2.dp
                    ),
                ) {
                    AnimatedVisibility(
                        visible = expand,
                        enter = expandVertically(),
                    ) {
                        @Composable
                        fun content(
                            i: Int,
                            itemView: ExpandableGroupItemFactory,
                            itemPadding: PaddingValues
                        ) {
                            if (withDivider && i > 0 && i < items.size) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = padding),
                                    color = containerColor
                                )
                            }
                            itemView(itemPadding)
                        }

                        fun itemPadding(i: Int): PaddingValues {
                            val top = if (i == 0) padding else padding / 2
                            val bottom = if (i == items.lastIndex) padding else padding / 2
                            return PaddingValues(
                                start = padding, end = padding,
                                top = top,
                                bottom = bottom
                            )
                        }

                        if (items.size == 1) {
                            Box(
                                modifier = if (contentHeight == null) Modifier
                                else Modifier.heightIn(max = contentHeight)
                            ) {
                                content(0, items[0], itemPadding(0))
                            }
                        } else {
                            if (contentHeight == null) {
                                Column {
                                    items.forEachIndexed { i, itemView ->
                                        content(i, itemView, itemPadding(i))
                                    }
                                }
                            } else {
                                LazyColumn(modifier = Modifier.heightIn(max = contentHeight)) {
                                    items.forEachIndexed { i, itemView ->
                                        item { content(i, itemView, itemPadding(i)) }
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewExpandableGroup() {
    val expandState = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .height(500.dp)
            .padding(10.dp)
    ) {
        ExpandableGroup(
            title = "Title", contentHeight = 100.dp,
            expandState = expandState,
            items = listOf(
                { Text("Item 1", Modifier.padding(it)) },
                { Text("Item 2", Modifier.padding(it)) },
                { Text("Item 3", Modifier.padding(it)) },
                { Text("Item 4", Modifier.padding(it)) },
                { Text("Item 5", Modifier.padding(it)) },
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        ExpandableGroup(
            title = "Title",
            expandState = expandState,
            items = listOf(
                { Text("Item 1", Modifier.padding(it)) },
                { Text("Item 2", Modifier.padding(it)) },
                { Text("Item 3", Modifier.padding(it)) },
                { Text("Item 4", Modifier.padding(it)) },
                { Text("Item 5", Modifier.padding(it)) },
            )
        )
    }
}