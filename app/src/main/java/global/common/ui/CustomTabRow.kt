package global.common.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

@Composable
fun CustomTabRow(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(50),
    padding: Dp = 5.dp,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    selectedContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    textColor: Color = Color.Unspecified,
    textSelectedColor: Color = Color.Unspecified,
    initialSelectedIndex: Int = 0,
    titles: List<String>,
    onSelected: (i: Int) -> Unit
) {
    CustomTabRow(
        modifier,
        shape,
        padding,
        containerColor,
        selectedContainerColor,
        initialSelectedIndex,
        items = titles.map { title ->
            @Composable { selected ->
                Text(
                    text = title,
                    color = if (selected) textSelectedColor else textColor,
                )
            }
        },
        onSelected,
    )
}

@Composable
fun CustomTabRow(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(50),
    insetPadding: Dp = 5.dp,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    selectedContainerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    initialSelectedIndex: Int = 0,
    items: List<@Composable (selected: Boolean) -> Unit>,
    onSelected: (i: Int) -> Unit
) {

    var selectedIndex by remember {
        mutableIntStateOf(
            min(items.lastIndex, max(initialSelectedIndex, 0))
        )
    }
    TabRow(
        modifier = Modifier
            .clip(shape)
            .then(modifier),
        containerColor = containerColor,
        selectedTabIndex = selectedIndex,
        indicator = { },
    ) {
        if (items.isEmpty()) {
            Tab(
                selected = true,
                onClick = { },
                text = {
                    Text("Empty tab row...", color = selectedContainerColor)
                }
            )
            return@TabRow
        }
        for ((i, item) in items.withIndex()) {
            val selected = selectedIndex == i
            AnimatedContent(
                targetState = selected,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "tabAnimation$i"
            ) {
                Surface(
                    modifier = Modifier.padding(
                        top = insetPadding, bottom = insetPadding,
                        start = if (i == 0) insetPadding else insetPadding / 2,
                        end = if (i == items.lastIndex) insetPadding else insetPadding / 2
                    ),
                    shape = shape,
                    color = if (it) selectedContainerColor else containerColor,
                ) {
                    Tab(
                        selected = selected,
                        onClick = {
                            selectedIndex = i
                            onSelected(i)
                        },
                        text = {
                            item(it)
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun CustomTabRowPreview1() {
    CustomTabRow(titles = listOf("Home", "ToDo", "Settings")) {}
}

@Preview
@Composable
fun CustomTabRowPreview2() {
    CustomTabRow(
        items = listOf(
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Rounded.Home, null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "Home",
                    )
                }
            },
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Rounded.CheckCircle, null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "ToDo",
                    )
                }
            },
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Rounded.Settings, null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "Settings",
                    )
                }
            },
        )
    ) {}
}

@Preview
@Composable
fun CustomTabRowPreview3() {
    CustomTabRow(
        items = listOf()
    ) {}
}