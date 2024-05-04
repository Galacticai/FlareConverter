package global.common.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Deprecated("TODO")//TODO: not really deprecated but let's not use it for now
@Composable
fun Expandable(
    modifier: Modifier = Modifier,
    radius: Dp = 20.dp,
    onExpand: ((Boolean) -> Unit)? = null,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    header: @Composable (padding: PaddingValues) -> Unit,
    content: @Composable (padding: PaddingValues) -> Unit,
) {
//    var expanded by remember { mutableStateOf(false) }
//
//    Card(
//        modifier = modifier,
//        colors = colors,
//        elevation = elevation,
//        border = border,
//        shape = RoundedCornerShape(radius),
//    ) {
//        header()
//        Card() {
//            content(PaddingValues(Consistent.Pad.regular))
//        }
//    }
}

@Preview
@Composable
private fun Preview() {
//    Expandable() { Text("Stuff", Modifier.padding(it)) }
}