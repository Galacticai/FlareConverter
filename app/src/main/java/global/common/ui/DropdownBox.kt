package global.common.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class RenderType { Button, List }

@Composable
fun <T> SelectBox(
    modifier: Modifier = Modifier,
    items: List<T>,
    selectedIndexInitial: Int? = null,
    shape: Shape? = null,
    menuShape: Shape? = null,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    menuContainerColor: Color = MaterialTheme.colorScheme.surface,
    shadowElevation: Dp = 5.dp,
    placeholder: @Composable () -> Unit = { Text("Select…") },
    renderItem: @Composable (
        item: T, i: Int,
        selected: Boolean,
        renderType: RenderType
    ) -> Unit,
    onSelected: (T) -> Unit,
) {
    if (selectedIndexInitial != null)
        assert(selectedIndexInitial >= 0 && selectedIndexInitial < items.size)

    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(selectedIndexInitial ?: -1) }
    val selectedItem by remember(selectedIndex, items) {
        derivedStateOf { items.getOrNull(selectedIndex) }
    }

    var width by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    Box(modifier.onSizeChanged { width = it.width }) {
        Button(
            colors = colors,
            shape = shape ?: ButtonDefaults.shape,
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (selectedItem == null) {
                placeholder()
            } else {
                renderItem(selectedItem!!, selectedIndex, true, RenderType.Button)
            }
        }

        DropdownMenu(
            shape = menuShape ?: RoundedCornerShape(10.dp),
            expanded = expanded,
            shadowElevation = shadowElevation,
            containerColor = menuContainerColor,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(density) { width.toDp() })
                .heightIn(max = 400.dp),
        ) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = {
                        renderItem(item, index, selectedIndex == index, RenderType.List)
                    },
                    onClick = {
                        selectedIndex = index
                        onSelected(item)
                        expanded = false
                    },
                )
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun SelectBoxPreview() {
    val items = listOf("Apple", "Banana", "Orange")
    var selected by remember { mutableStateOf<String?>(null) }

    SelectBox(
        modifier = Modifier.padding(horizontal = 40.dp, vertical = 20.dp),
        items = items,
//        selectedIndexInitial = 0,
        placeholder = { Text("Select an item") },
        renderItem = { item, _, selected, _ ->
            Text(
                item,
                modifier = Modifier.fillMaxWidth(),
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        },
        onSelected = { selected = it },
        shape = RoundedCornerShape(10.dp),
    )
}
