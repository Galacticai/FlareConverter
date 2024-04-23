package global.common.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/** Checkbox item with title and an optional subtitle.
 * @param modifier container [Modifier]
 * @param checkState state of the checkbox
 */
@Composable
fun CheckItem(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    checkState: Boolean? = null,
    onClick: CheckItemClicked? = null,
) {
    val isChecked =
        if (checkState == null) rememberSaveable { mutableStateOf(false) }
        else rememberSaveable(checkState) { mutableStateOf(checkState) }

    fun onClick(newState: Boolean) {
        val changeAccepted =
            onClick?.invoke(newState) //? let the callback decide
                ?: true //? accept if uncontrolled
        if (changeAccepted) isChecked.value = newState
    }

    Column(
        Modifier
            .clickable { onClick(!isChecked.value) }
            .then(modifier)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isChecked.value, onCheckedChange = { onClick(it) })
            Text(
                title, modifier = Modifier
                    .weight(1f)
                    .padding(end = 10.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
        if (subtitle != null) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 5.dp),
                text = subtitle,
                color = LocalContentColor.current.copy(.87f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckItemPreview() {
    CheckItem(
        title = "Not a very long title",
        subtitle = "Subtitle",
    )
}
/** Callback for when a check is changed
 * @param requestedCheckState newly requested state
 * @return false will cancel the change
 */
typealias CheckItemClicked = (requestedCheckState: Boolean) -> Boolean
