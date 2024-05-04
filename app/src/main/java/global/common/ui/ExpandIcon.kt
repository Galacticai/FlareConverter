package global.common.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object ExpandIcon {
    /** fade + scale in/out */
    val transition
        get() = (scaleIn() + fadeIn()) togetherWith (fadeOut() + scaleOut())

    @Composable
    fun Custom(
        open: Boolean,
        modifier: Modifier = Modifier,
        /** opening icon shown when NOT [open] */
        iconOpen: ImageVector = Icons.Rounded.KeyboardArrowDown,
        /** closing icon shown when [open] */
        iconClose: ImageVector = Icons.Rounded.KeyboardArrowUp,
        size: Dp = 24.dp,
        contentDescription: String = "Expand toggle",
    ) {
        AnimatedContent(
            targetState = open,
            transitionSpec = { transition },
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(size) then modifier,
            label = contentDescription,
        ) { isOpen ->
            Icon(
                imageVector = if (isOpen) iconClose else iconOpen,
                contentDescription = contentDescription,
            )
        }
    }


    @Composable
    fun Plus(
        open: Boolean,
        modifier: Modifier = Modifier,
        size: Dp = 24.dp,
        contentDescription: String = "Expand"
    ) {
        Custom(
            open,
            modifier,
            iconOpen = Icons.Rounded.Add,
            iconClose = Icons.Rounded.Remove,
            size,
            contentDescription
        )
    }

    @Composable
    fun Arrow(
        open: Boolean,
        modifier: Modifier = Modifier,
        size: Dp = 24.dp,
        contentDescription: String = "Expand"
    ) {
        Custom(
            open,
            modifier,
            iconOpen = Icons.Rounded.KeyboardArrowDown,
            iconClose = Icons.Rounded.KeyboardArrowUp,
            size,
            contentDescription
        )
    }

    @Composable
    fun ArrowMinus(
        open: Boolean,
        modifier: Modifier = Modifier,
        size: Dp = 24.dp,
        contentDescription: String = "Expand"
    ) {
        Custom(
            open,
            modifier,
            iconOpen = Icons.Rounded.KeyboardArrowDown,
            iconClose = Icons.Rounded.Remove,
            size,
            contentDescription
        )
    }
}


@Preview(showSystemUi = true)
@Composable
fun ArrowPreview() {
    var open by remember { mutableStateOf(false) }
    val text = if (open) "Collapse" else "Expand"
    Column(
        Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { open = !open },
            contentPadding = PaddingValues(start = 18.dp, end = 10.dp)
        ) {
            AnimatedContent(open) {
                Row(
                    Modifier.animateContentSize(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text)
                    ExpandIcon.Arrow(
                        open = it,
                        contentDescription = text
                    )
                }
            }
        }
        Button(
            onClick = { open = !open },
            contentPadding = PaddingValues(start = 18.dp, end = 10.dp)
        ) {
            AnimatedContent(open) {
                Row(
                    Modifier.animateContentSize(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text)
                    ExpandIcon.Plus(
                        it,
                        contentDescription = text
                    )
                }
            }
        }
    }
}