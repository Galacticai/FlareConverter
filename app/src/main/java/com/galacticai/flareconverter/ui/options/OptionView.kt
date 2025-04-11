package com.galacticai.flareconverter.ui.options

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.galacticai.flareconverter.util.Consistent
import java.util.UUID


@Composable
fun NumberInputView(
    title: String,
    modifier: Modifier,
    initialValue: Int = 0,
    onChanged: ((Int) -> Unit)? = null,
    onConfirm: (Int) -> Unit,
) {
    val regex = """^\d+$""".toRegex()

    var n by remember { mutableIntStateOf(initialValue) }
    var editing by remember { mutableStateOf(false) }

    AnimatedContent(
        targetState = !editing,
        label = "NumberInputView_Animation_${UUID.randomUUID()}"
    ) { idle ->
        if (idle) Column(modifier) {
            Text(title)
            Text(n.toString())
        } else Row(modifier) {
            TextField(
                modifier = Modifier.weight(1f),
                label = { Text(title) },
                value = n.toString(),
                onValueChange = {
                    if (!regex.matches(it)) return@TextField
                    n = it.toIntOrNull() ?: 0
                    onChanged?.invoke(n)
                },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            Spacer(Modifier.width(Consistent.padRegular))
            IconButton({
                onConfirm(n)
                editing = false
            }) {
                Icon(Icons.Rounded.Done, null)
            }
        }
    }
}