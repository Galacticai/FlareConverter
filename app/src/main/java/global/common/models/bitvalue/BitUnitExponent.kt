package global.common.models.bitvalue

import kotlin.math.pow

abstract class BitUnitExponent(
    val name: ShortLongName,
    val exponent: Int,
    val multiplier: Float,
) : Comparable<Int> {
    abstract fun basic(): BitUnitExponent
    abstract fun entries(): List<BitUnitExponent>

    /** Multiplier to convert a value to base exponent (example: [Metric.Basic]) */
    val toBaseMultiplier get() = multiplier.pow(exponent)

    override fun toString(): String = name.short
    override fun compareTo(other: Int): Int = exponent.compareTo(other)

    open class Metric(name: ShortLongName, exponent: Int) : BitUnitExponent(name, exponent, 1000f) {
        override fun basic() = Basic
        override fun entries(): List<BitUnitExponent> =
            listOf(Basic, Kilo, Mega, Giga, Tera, Peta, Exa, Zetta, Yotta)

        object Basic : Metric(ShortLongName("", ""), 0)
        object Kilo : Metric(ShortLongName("K", "Kilo"), 1)
        object Mega : Metric(ShortLongName("M", "Mega"), 2)
        object Giga : Metric(ShortLongName("G", "Giga"), 3)
        object Tera : Metric(ShortLongName("T", "Tera"), 4)
        object Peta : Metric(ShortLongName("P", "Peta"), 5)
        object Exa : Metric(ShortLongName("E", "Exa"), 6)
        object Zetta : Metric(ShortLongName("Z", "Zetta"), 7)
        object Yotta : Metric(ShortLongName("Y", "Yotta"), 8)
    }

    open class Binary(name: ShortLongName, exponent: Int) : BitUnitExponent(name, exponent, 1024f) {
        override fun basic() = Metric.Basic
        override fun entries(): List<BitUnitExponent> =
            listOf(Basic, Kibi, Mebi, Gibi, Tebi, Pebi, Exbi, Zebi, Yobi)

        object Basic : Binary(ShortLongName("", ""), 0)
        object Kibi : Binary(ShortLongName("Ki", "Kibi"), 1)
        object Mebi : Binary(ShortLongName("Mi", "Mebi"), 2)
        object Gibi : Binary(ShortLongName("Gi", "Gibi"), 3)
        object Tebi : Binary(ShortLongName("Ti", "Tebi"), 4)
        object Pebi : Binary(ShortLongName("Pi", "Pebi"), 5)
        object Exbi : Binary(ShortLongName("Ei", "Exbi"), 6)
        object Zebi : Binary(ShortLongName("Zi", "Zebi"), 7)
        object Yobi : Binary(ShortLongName("Yi", "Yobi"), 8)
    }
}