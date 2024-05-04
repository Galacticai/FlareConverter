package global.common.models.amount

import global.common.models.amount.units.AmountUnit
import java.text.DecimalFormat

typealias ValueFormatter = (Double) -> String

data class Amount(
    val value: Double,
    val unit: AmountUnit,
    private val formatterCustom: ValueFormatter? = null
) {
    val baseValue: Double = unit.toBase(value)

    private val format: ValueFormatter
        get() = formatterCustom ?: DEFAULT_VALUE_FORMATTER

    fun toUnit(targetUnit: AmountUnit): Amount {
        val unitValue = targetUnit.toUnit(baseValue)
        return Amount(unitValue, targetUnit, formatterCustom)
    }

    fun toString(separator: String): String =
        unit.toString(format(value), separator)

    override fun toString(): String = toString(" ")

    companion object {
        val DEFAULT_VALUE_FORMATTER: ValueFormatter = { v ->
            DecimalFormat("0.##").format(v)
        }
    }
}

fun Amount.toDouble(): Double = baseValue
