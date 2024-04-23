package global.common.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties

@Composable
fun <T> CustomDropdownMenu(
    list: List<T>,
    defaultSelected: T,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = MaterialTheme.typography.bodyLarge.fontSize,
    fontWeight: FontWeight = FontWeight.SemiBold,
    color: Color = MaterialTheme.colorScheme.primary,
    colorBG: Color = MaterialTheme.colorScheme.background,
    radius: Dp = 20.dp,
    padding: Dp = 20.dp,
    itemText: (T) -> String = { it.toString() },
    onSelected: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        color = colorBG,
        modifier = Modifier
            .border(
                border = BorderStroke(1.dp, color),
                shape = RoundedCornerShape(radius)
            )
            .then(modifier),
        shape = RoundedCornerShape(radius),
        onClick = { expanded = !expanded }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
        ) {
            Text(
                text = itemText(defaultSelected),
                color = color,
                fontSize = fontSize,
                fontWeight = fontWeight,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = null,
                tint = color
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            properties = PopupProperties(
                focusable = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            ),
            modifier = Modifier
                .background(colorBG)
                .padding(2.dp)
                .fillMaxWidth(.4f)
        ) {
            list.forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = itemText(it),
                            color = color,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    onClick = {
                        expanded = false
                        onSelected(it)
                    }
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun CustomDropdownMenuPreview() {
    CustomDropdownMenu(
        list = listOf("Option 1", "Option 2", "Option 3"),
        defaultSelected = "Option 1",
        onSelected = { }
    )
}