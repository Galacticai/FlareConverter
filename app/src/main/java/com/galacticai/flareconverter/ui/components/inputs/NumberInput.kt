package com.galacticai.flareconverter.ui.components.inputs

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@Composable
fun NumberInput(
    modifier: Modifier = Modifier,
    state: TextFieldState = rememberTextFieldState(""),
    placeholder: String? = null,
    debounceMs: Int = 500,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    colors: TextFieldColors = TextFieldDefaults.colors(),
    shape: Shape = OutlinedTextFieldDefaults.shape,
    /** transform text before processing like to [Int] or something */
    transform: ((String) -> String)? = null,
    /** @return true to accept change - false to revert */
    onChange: (Double) -> Boolean
) {
    assert(debounceMs >= 0)
    val currentOnChange by rememberUpdatedState(onChange)
    val currentTransform by rememberUpdatedState(transform)

    var lastValidString by remember(state) {
        val currentText = state.text.toString()
        mutableStateOf(transform?.invoke(currentText) ?: currentText)
    }

    LaunchedEffect(state, transform) {
        val currentText = state.text.toString()
        val transformed = transform?.invoke(currentText) ?: currentText
        if (transformed != currentText) {
            state.edit { replace(0, length, transformed) }
        }
    }

    LaunchedEffect(state) {
        snapshotFlow { state.text.toString() }
            .distinctUntilChanged() //? diff only
            .drop(1) //? skip initial composition
            .let {
                if (debounceMs > 0)
                    it.debounce(debounceMs.milliseconds)
                else it
            }
            .collect {
                val str = currentTransform?.invoke(it) ?: it
                if (str != it) {
                    state.edit { replace(0, length, str) }
                    return@collect
                }

                val n = str.toDoubleOrNull() ?: return@collect
                if (n == lastValidString.toDoubleOrNull()) {
                    lastValidString = str
                    return@collect
                }

                val accepted = currentOnChange(n)
                if (accepted) {
                    lastValidString = str
                } else {
                    state.edit {
                        replace(0, length, lastValidString)
                    }
                }
            }
    }

    OutlinedTextField(
        modifier = modifier,
        state = state,
        placeholder = { Text(placeholder ?: state.text.toString()) },
        leadingIcon = leading,
        trailingIcon = trailing,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        lineLimits = TextFieldLineLimits.SingleLine,
        inputTransformation = transform?.let { t ->
            InputTransformation {
                val original = asCharSequence().toString()
                val transformed = t(original)
                if (transformed != original) {
                    replace(0, length, transformed)
                }
            }
        },
        colors = colors,
        shape = shape
    )
}
