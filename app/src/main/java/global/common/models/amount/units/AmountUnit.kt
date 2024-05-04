package global.common.models.amount.units

import global.common.models.amount.affixes.Affix
import global.common.models.amount.affixes.AffixType

typealias UnitMultiplier = (Double) -> Double
typealias UnitSimple = (preferLong: Boolean) -> AmountUnit

open class AmountUnit(
    val toUnit: UnitMultiplier,
    val toBase: UnitMultiplier,
    vararg affixes: Affix
) {
    val prefixes: List<Affix>
    val suffixes: List<Affix>
    val allAffixes: List<Affix> get() = prefixes + suffixes

    init {
        val (p, s) = splitPrefixSuffix(*affixes)
        prefixes = p
        suffixes = s
    }

    /**
     * Parse [prefixes] and [suffixes] around the [value]
     * @return "[prefixes] [separator] [value] [separator] [suffixes]"
     */
    fun toString(value: String, separator: String = ""): String {
        val sb = StringBuilder()

        if (prefixes.isNotEmpty()) {
            for (prefix in prefixes) {
                sb.append(prefix.toString())
            }
            sb.append(separator)
        }

        sb.append(value)

        if (suffixes.isNotEmpty()) {
            sb.append(separator)
            for (suffix in suffixes) {
                sb.append(suffix.toString())
            }
        }

        return sb.toString()
    }

    /** [toString] is for debugging... Use [toString(String, String)] instead */
    override fun toString(): String = toString("..")

    fun <U : AmountUnit> and(unit: U, neutral: Boolean = false): AmountUnit {
        return AmountUnit(
            { v ->
                val toThis = this.toUnit(v)
                if (neutral) toThis else unit.toUnit(toThis)
            },
            { v ->
                val toThis = this.toBase(v)
                if (neutral) toThis else unit.toBase(toThis)
            },
            *(allAffixes + unit.allAffixes).toTypedArray()
        )
    }

    infix fun <U : AmountUnit> per(unit: U) = this.per(
        unit,
        neutral = true,
        preferLong = false
    )

    fun <U : AmountUnit> per(
        unit: U,
        neutral: Boolean = true,
        preferLong: Boolean = false
    ): AmountUnit {
        return AmountUnit(
            { v ->
                val toThis = this.toUnit(v)
                if (neutral) toThis else toThis / unit.toUnit(v)
            },
            { v ->
                val toThis = this.toBase(v)
                if (neutral) toThis else toThis / unit.toBase(v)
            },
            *(allAffixes + listOf(
                Affix(
                    "/",
                    " per ",
                    preferLong = preferLong
                )
            ) + unit.allAffixes).toTypedArray()
        )
    }


    /** Use [and] while neutralizing [right]'s conversion */
    infix fun and(right: AmountUnit): AmountUnit = and(right, true)

    /** Use [and] while respecting [right]'s conversion */
    operator fun plus(right: AmountUnit): AmountUnit = and(right, false)

    /** Use [per] while neutralizing [right]'s conversion */
    operator fun div(right: AmountUnit): AmountUnit = per(right, true)

    /** Use [per] while respecting [right]'s conversion */
    operator fun rem(right: AmountUnit): AmountUnit = per(right, false)

    companion object {
        fun splitPrefixSuffix(vararg affixes: Affix): Pair<List<Affix>, List<Affix>> {
            val prefixes = mutableListOf<Affix>()
            val suffixes = mutableListOf<Affix>()
            for (affix in affixes) {
                when (affix.type) {
                    AffixType.Prefix -> prefixes.add(affix)
                    AffixType.Suffix -> suffixes.add(affix)
                }
            }
            return Pair(prefixes, suffixes)
        }

        fun gram(preferLong: Boolean = false) = BaseUnit(Affix.gram(preferLong))
        fun meter(preferLong: Boolean = false) = BaseUnit(Affix.meter(preferLong))
        fun ampere(preferLong: Boolean = false) = BaseUnit(Affix.ampere(preferLong))
        fun kelvin(preferLong: Boolean = false) = BaseUnit(Affix.kelvin(preferLong))
        fun mole(preferLong: Boolean = false) = BaseUnit(Affix.mole(preferLong))
        fun candela(preferLong: Boolean = false) = BaseUnit(Affix.candela(preferLong))
        fun hertz(preferLong: Boolean = false) = BaseUnit(Affix.hertz(preferLong))
        fun newton(preferLong: Boolean = false) = BaseUnit(Affix.newton(preferLong))
        fun pascal(preferLong: Boolean = false) = BaseUnit(Affix.pascal(preferLong))
        fun joule(preferLong: Boolean = false) = BaseUnit(Affix.joule(preferLong))
        fun watt(preferLong: Boolean = false) = BaseUnit(Affix.watt(preferLong))
        fun coulomb(preferLong: Boolean = false) = BaseUnit(Affix.coulomb(preferLong))
        fun volt(preferLong: Boolean = false) = BaseUnit(Affix.volt(preferLong))
        fun farad(preferLong: Boolean = false) = BaseUnit(Affix.farad(preferLong))
        fun ohm(preferLong: Boolean = false) = BaseUnit(Affix.ohm(preferLong))
        fun siemens(preferLong: Boolean = false) = BaseUnit(Affix.siemens(preferLong))
        fun weber(preferLong: Boolean = false) = BaseUnit(Affix.weber(preferLong))
        fun tesla(preferLong: Boolean = false) = BaseUnit(Affix.tesla(preferLong))
        fun henry(preferLong: Boolean = false) = BaseUnit(Affix.henry(preferLong))
        fun lumen(preferLong: Boolean = false) = BaseUnit(Affix.lumen(preferLong))
        fun lux(preferLong: Boolean = false) = BaseUnit(Affix.lux(preferLong))
        fun second(preferLong: Boolean = false) = BaseUnit(Affix.second(preferLong))

        const val MINUTE = 60.0
        const val HOUR = MINUTE * 60
        const val DAY = 24 * HOUR
        const val WEEK = 7 * DAY
        const val YEAR = 365 * DAY

        fun minute(preferLong: Boolean = false) = SimpleUnit(MINUTE, Affix.minute(preferLong))
        fun hour(preferLong: Boolean = false) = SimpleUnit(HOUR, Affix.hour(preferLong))
        fun day(preferLong: Boolean = false) = SimpleUnit(DAY, Affix.day(preferLong))
        fun week(preferLong: Boolean = false) = SimpleUnit(WEEK, Affix.week(preferLong))
        fun year(preferLong: Boolean = false) = SimpleUnit(YEAR, Affix.year(preferLong))

        fun wattHour(preferLong: Boolean = false): AmountUnit =
            watt(preferLong) and hour(preferLong)

        object Currency {
            fun USD(preferLong: Boolean = false) = BaseUnit(Affix.Currency.USD(preferLong))
            fun EUR(preferLong: Boolean = false) = BaseUnit(Affix.Currency.EUR(preferLong))
            fun GBP(preferLong: Boolean = false) = BaseUnit(Affix.Currency.GBP(preferLong))
            fun JPY(preferLong: Boolean = false) = BaseUnit(Affix.Currency.JPY(preferLong))
            fun KRW(preferLong: Boolean = false) = BaseUnit(Affix.Currency.KRW(preferLong))
            fun CNY(preferLong: Boolean = false) = BaseUnit(Affix.Currency.CNY(preferLong))
            fun INR(preferLong: Boolean = false) = BaseUnit(Affix.Currency.INR(preferLong))
            fun RUB(preferLong: Boolean = false) = BaseUnit(Affix.Currency.RUB(preferLong))
            fun CHF(preferLong: Boolean = false) = BaseUnit(Affix.Currency.CHF(preferLong))
            fun PHP(preferLong: Boolean = false) = BaseUnit(Affix.Currency.PHP(preferLong))
        }
    }
}
