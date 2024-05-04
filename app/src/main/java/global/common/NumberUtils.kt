package global.common

import androidx.compose.ui.unit.Dp
import kotlin.math.abs

object NumberUtils {
    infix fun Float.inverse(y: Float) = this * (1f - y)
    infix fun Dp.inverse(y: Float) = this * (1f - y)

    inline fun <reified T : Number> numberToGeneric(f: Double): T? {
        return when (T::class) {
            Double::class -> f as T
            Float::class -> f.toFloat() as T
            Long::class -> f.toLong() as T
            Int::class -> f.toInt() as T
            Byte::class -> f.toInt().toByte() as T
            Short::class -> f.toInt().toShort() as T
            else -> null
        }
    }

    inline fun <reified T : Number> genericToNumber(input: String): T? {
        return when (T::class) {
            Double::class -> input.toDoubleOrNull() as T?
            Float::class -> input.toFloatOrNull() as T?
            Long::class -> input.toLongOrNull() as T?
            Int::class -> input.toIntOrNull() as T?
            Byte::class -> input.toByteOrNull() as T?
            Short::class -> input.toShortOrNull() as T?
            else -> null
        }
    }

    /** @return nearest value to [number] within this number [List] | or 0 if invalid */
    inline fun <reified N : Number> List<N>.nearest(number: N): N {
        if (this.isEmpty()) return numberToGeneric(0.0)!!

        val target = number.toDouble()
        var nearest = this[0].toDouble()
        var delta = abs(nearest - target)

        val numbers = this.drop(1).map { it.toDouble() }
        for (number in numbers) {
            val deltaCurrent = abs(number - target)
            if (deltaCurrent > delta) continue
            delta = deltaCurrent
            nearest = number
        }

        return numberToGeneric(nearest)!!
    }
}