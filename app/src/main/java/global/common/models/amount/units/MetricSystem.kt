package global.common.models.amount.units

import global.common.models.amount.affixes.Affix
import kotlin.math.pow

enum class MetricFactor(val value: Int) {
    Quecto(-30),
    Ronto(-27),
    Yocto(-24),
    Zepto(-21),
    Atto(-18),
    Femto(-15),
    Pico(-12),
    Nano(-9),
    Micro(-6),
    Milli(-3),
    Centi(-2),
    Deci(-1),
    Base(0),
    Deca(1),
    Hecto(2),
    Kilo(3),
    Mega(6),
    Giga(9),
    Tera(12),
    Peta(15),
    Exa(18),
    Zetta(21),
    Yotta(24),
    Ronna(27),
    Quetta(30),
}

class MetricSystem(
    factor: Int,
    vararg affixes: Affix
) : SimpleUnitSystem(
    factor,
    10.0.pow(factor),
    *affixes
) {
    constructor(factor: MetricFactor, vararg affixes: Affix) : this(factor.value, *affixes)

    companion object {
        fun quecto(preferLong: Boolean = false) =
            MetricSystem(MetricFactor.Quecto, Affix.Multiplier.Metric.quecto(preferLong))

        fun ronto(preferLong: Boolean = false) =
            MetricSystem(MetricFactor.Ronto, Affix.Multiplier.Metric.ronto(preferLong))

        fun yocto(preferLong: Boolean = false) =
            MetricSystem(MetricFactor.Yocto, Affix.Multiplier.Metric.yocto(preferLong))

        fun zepto(preferLong: Boolean = false) =
            MetricSystem(MetricFactor.Zepto, Affix.Multiplier.Metric.zepto(preferLong))

        fun atto(preferLong: Boolean = false) =
            MetricSystem(MetricFactor.Atto, Affix.Multiplier.Metric.atto(preferLong))

        fun femto(preferLong: Boolean = false) =
            MetricSystem(MetricFactor.Femto, Affix.Multiplier.Metric.femto(preferLong))

        fun pico(preferLong: Boolean = false) =
            MetricSystem(MetricFactor.Pico, Affix.Multiplier.Metric.pico(preferLong))

        fun nano(preferLong: Boolean = false) =
            MetricSystem(MetricFactor.Nano, Affix.Multiplier.Metric.nano(preferLong))

        fun micro(preferLong: Boolean = false) =
            MetricSystem(MetricFactor.Micro, Affix.Multiplier.Metric.micro(preferLong))

        fun milli(preferLong: Boolean = false) =
            MetricSystem(MetricFactor.Milli, Affix.Multiplier.Metric.milli(preferLong))

        fun centi(preferLong: Boolean = false) =
            MetricSystem(MetricFactor.Centi, Affix.Multiplier.Metric.centi(preferLong))

        fun deci(preferLong: Boolean = false) =
            MetricSystem(MetricFactor.Deci, Affix.Multiplier.Metric.deci(preferLong))

        fun deca(preferLong: Boolean = false) =
            MetricSystem(MetricFactor.Deca, Affix.Multiplier.Metric.deca(preferLong))

        fun hecto(preferLong: Boolean = false) =
            MetricSystem(MetricFactor.Hecto, Affix.Multiplier.Metric.hecto(preferLong))

        fun kilo(preferLong: Boolean = false) =
            MetricSystem(MetricFactor.Kilo, Affix.Multiplier.Metric.kilo(preferLong))

        fun mega(preferLong: Boolean = false) =
            MetricSystem(MetricFactor.Mega, Affix.Multiplier.Metric.mega(preferLong))

        fun giga(preferLong: Boolean = false) =
            MetricSystem(MetricFactor.Giga, Affix.Multiplier.Metric.giga(preferLong))

        fun tera(preferLong: Boolean = false) =
            MetricSystem(MetricFactor.Tera, Affix.Multiplier.Metric.tera(preferLong))

        fun peta(preferLong: Boolean = false) =
            MetricSystem(MetricFactor.Peta, Affix.Multiplier.Metric.peta(preferLong))

        fun exa(preferLong: Boolean = false) =
            MetricSystem(MetricFactor.Exa, Affix.Multiplier.Metric.exa(preferLong))

        fun zetta(preferLong: Boolean = false) =
            MetricSystem(MetricFactor.Zetta, Affix.Multiplier.Metric.zetta(preferLong))

        fun yotta(preferLong: Boolean = false) =
            MetricSystem(MetricFactor.Yotta, Affix.Multiplier.Metric.yotta(preferLong))

        fun ronna(preferLong: Boolean = false) =
            MetricSystem(MetricFactor.Ronna, Affix.Multiplier.Metric.ronna(preferLong))

        fun quetta(preferLong: Boolean = false) =
            MetricSystem(MetricFactor.Quetta, Affix.Multiplier.Metric.quetta(preferLong))
    }
}
