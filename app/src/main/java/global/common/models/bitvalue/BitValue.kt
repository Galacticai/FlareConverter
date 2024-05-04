package global.common.models.bitvalue

import java.text.NumberFormat
import kotlin.math.abs

open class BitValue(
    val value: Float,
    val unit: BitUnit
) {
    init {
        assert(value >= 0f)
    }

    fun toString(maxFractionDigits: Int): String {
        val formatted = NumberFormat.getInstance().apply {
            maximumFractionDigits = maxFractionDigits
        }.format(value)
        return "$formatted ${name.short}"
    }

    override fun toString(): String = toString(2)
    fun toLongString() = "$value ${name.long}"
    val name = ShortLongName(
        "${unit.exponent.name.short}${unit.base.name.short}",
        "${unit.exponent.name.long}${unit.base.name.long}"
    )
    val bits get() = value * unit.toBitMultiplier
    val bytes get() = valueToUnit(BitUnit.BasicByte(unit.exponent))
    fun valueToUnit(toUnit: BitUnit) =
        bits / toUnit.toBitMultiplier

    fun valueToBase(toBase: BitUnitBase) =
        (bits / unit.toBitMultiplier * unit.base.toBitMultiplier) / toBase.toBitMultiplier

    fun toUnit(toUnit: BitUnit) = BitValue(
        valueToUnit(toUnit),
        toUnit
    )

    fun toBase(toBase: BitUnitBase) = BitValue(
        valueToBase(toBase),
        BitUnit(unit.exponent, toBase)
    )

    fun toNearestUnit(
        exponents: List<BitUnitExponent> = unit.exponent.entries(),
        newBase: BitUnitBase = unit.base
    ): BitValue {
        val sortedExponents = exponents.sortedBy { it.exponent }

        var nearestExponent = exponents.first()
        var minDifference = Float.MAX_VALUE
        val b: Float = bits
        for (unit in sortedExponents) {
            val difference = abs(b - unit.toBaseMultiplier * 10)
            if (difference >= minDifference) continue
            minDifference = difference
            nearestExponent = unit
        }
        val nearestUnit = BitUnit(nearestExponent, newBase)
        val valueInNearestUnit = valueToUnit(nearestUnit)

        return BitValue(valueInNearestUnit, nearestUnit)
    }
}