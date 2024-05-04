package global.common.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SplitButton
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object SplitMenu {
    data class Props<T>(
        val buttonModifier: Modifier = Modifier.Companion,
        val menuModifier: Modifier = Modifier.Companion,
        val contentSpacing: Dp = 5.dp,
        val menuShape: Shape = RectangleShape,
        val contentPadding: PaddingValues? = null,

        val items: Iterable<T>,

        val openState: MutableState<Boolean>,
        val selectedState: MutableState<T>,

        val renderSelected: @Composable RowScope.(T) -> Unit,
        val leadingIcon: @Composable ((T) -> Unit)? = null,
        val trailingIcon: @Composable ((T) -> Unit)? = null,
        val text: @Composable (T) -> Unit,

        val onClick: () -> Unit,
    )

    /** @see androidx.compose.material3.SplitButton */
    @Composable
    fun <T1, T2> Buttons(
        modifier: Modifier = Modifier,
        pair: Pair<Props<T1>, Props<T2>>,
    ) {
        SplitButton(
            modifier = modifier,
            leadingButton = {
                val pad = pair.first.contentPadding
                    ?: SplitButtonDefaults.leadingButtonContentPaddingFor(
                        SplitButtonDefaults.SmallContainerHeight
                    )

                SplitButtonDefaults.LeadingButton(
                    pair.first.onClick,
                    contentPadding = pad,
                ) { ButtonContent(pair.first) }
            },

            trailingButton = {
                val pad = pair.second.contentPadding
                    ?: SplitButtonDefaults.trailingButtonContentPaddingFor(
                        SplitButtonDefaults.SmallContainerHeight
                    )

                SplitButtonDefaults.TrailingButton(
                    pair.second.onClick,
                    contentPadding = pad
                ) { ButtonContent(pair.second) }
            }
        )
    }

    /** menu + label */
    @Composable
    fun <T> ButtonContent(props: Props<T>) {
        var open by props.openState
        var selected by props.selectedState

        AnimatedContent(targetState = selected, label = "ItemAndMenu") {
            DisplayRow(isMenu = false, selected = false, spacing = props.contentSpacing) {
                props.renderSelected(this, it)
            }
        }

        DropdownMenu(
            shape = props.menuShape,
            modifier = props.menuModifier,
            expanded = open,
            onDismissRequest = { open = false },
        ) {
            props.items.forEach {
                val isSelected = it == selected
                val itemModifier = Modifier.fillMaxWidth() then
                        if (!isSelected) Modifier
                        else Modifier.background(
                            MaterialTheme.colorScheme.primaryContainer
                                .copy(alpha = 0.5f)
                        )
                DropdownMenuItem(
                    modifier = itemModifier,
                    leadingIcon = props.leadingIcon?.let { icon -> { icon(it) } },
                    trailingIcon = props.trailingIcon?.let { icon -> { icon(it) } },
                    text = {
                        DisplayRow(isMenu = true, isSelected, spacing = props.contentSpacing) {
                            props.text(it)
                        }
                    },
                    onClick = {
                        selected = it
                        open = false
                    },
                )
            }
        }
    }

    /** like icon + text  */
    @Composable
    fun DisplayRow(
        isMenu: Boolean,
        selected: Boolean,
        iconSize: Dp = 24.dp,
        spacing: Dp = 5.dp,
        children: @Composable RowScope.() -> Unit
    ) {
        Row(
            modifier = Modifier.animateContentSize(),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            children()
            if (isMenu && selected) {
                Box(
                    Modifier.weight(1f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        Icons.Rounded.Check, "Selected",
                        tint = LocalContentColor.current,
                        modifier = Modifier.size(iconSize)
                    )
                }
            }
        }
    }
}