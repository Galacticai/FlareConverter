package global.common.ui.dialogs.quick_input

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.KeyboardType
import global.common.SimpleRegex
import global.common.SimpleRegex.bounded

abstract class InputData<T>(
    val title: String? = null,
    val onDismiss: (() -> Unit)? = null
) {
    abstract val initial: T
    abstract val onChange: ((T) -> Unit)?
    abstract val onConfirm: ((T) -> Unit)?
    abstract val converter: (String) -> T
    abstract val keyboardType: KeyboardType
    abstract val regex: Regex
    abstract val state: MutableState<T>

    class TextInputData(
        override val initial: String = "",
        title: String? = null,
        onDismiss: (() -> Unit)? = null,
        override val onChange: ((String) -> Unit)? = null,
        override val onConfirm: ((String) -> Unit)? = null,
    ) : InputData<String>(title, onDismiss) {
        override val converter: (String) -> String = { it }
        override val keyboardType = KeyboardType.Text
        override val regex = SimpleRegex.anything
        override val state = mutableStateOf(initial)
    }

    class IntInputData(
        override val initial: Int = 0,
        title: String? = null,
        onDismiss: (() -> Unit)? = null,
        override val onChange: ((Int) -> Unit)? = null,
        override val onConfirm: ((Int) -> Unit),
        override val converter: (String) -> Int = String::toInt,
    ) : InputData<Int>(title, onDismiss) {
        override val keyboardType = KeyboardType.Number
        override val regex = SimpleRegex.numberWhole.bounded
        override val state = mutableIntStateOf(initial)
    }

    class FloatInputData(
        override val initial: Float = 0f,
        title: String? = null,
        onDismiss: (() -> Unit)? = null,
        override val onChange: ((Float) -> Unit)? = null,
        override val onConfirm: ((Float) -> Unit)? = null,
        override val converter: (String) -> Float = String::toFloat,
    ) : InputData<Float>(title, onDismiss) {
        override val keyboardType = KeyboardType.Decimal
        override val regex = SimpleRegex.numberScientific.bounded
        override val state = mutableFloatStateOf(initial)
    }

    class DoubleInputData(
        override val initial: Double = 0.0,
        title: String? = null,
        onDismiss: (() -> Unit)? = null,
        override val onChange: ((Double) -> Unit)? = null,
        override val onConfirm: ((Double) -> Unit)? = null,
        override val converter: (String) -> Double = String::toDouble,
    ) : InputData<Double>(title, onDismiss) {
        override val keyboardType = KeyboardType.Decimal
        override val regex = SimpleRegex.numberScientific.bounded
        override val state = mutableDoubleStateOf(initial)
    }
}