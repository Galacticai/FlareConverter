package global.common.ui.dialogs.quick_input

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> QuickInput(
    inputData: InputData<T>,
    modifier: Modifier = Modifier
) {
    var value by rememberSaveable { inputData.state }
    Row(modifier) {
        TextField(
            modifier = Modifier.weight(1f),
            value = value.toString(),
            onValueChange = {
                if (!inputData.regex.matches(it)) return@TextField
                value = inputData.converter(it)
                inputData.onChange?.invoke(value)
            },
            keyboardOptions = KeyboardOptions(keyboardType = inputData.keyboardType)
        )
        Spacer(Modifier.width(10.dp))
        IconButton(onClick = { inputData.onConfirm?.invoke(value) }) {
            Icon(Icons.Rounded.Check, null)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> QuickInputModal(inputData: InputData<T>) {
    ModalBottomSheet(onDismissRequest = inputData.onDismiss ?: {}) {
        QuickInput(inputData, Modifier.padding(10.dp))
    }
}