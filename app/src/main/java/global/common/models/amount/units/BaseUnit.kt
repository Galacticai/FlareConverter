package global.common.models.amount.units

import global.common.models.amount.affixes.Affix

/** [SimpleUnit] with a multiplier=1 */
class BaseUnit(vararg affixes: Affix) : SimpleUnit(1.0, *affixes) {
    companion object {
        fun gram(preferLong: Boolean = false) = BaseUnit(Affix.gram(preferLong))
        fun meter(preferLong: Boolean = false) = BaseUnit(Affix.meter(preferLong))
        fun second(preferLong: Boolean = false) = BaseUnit(Affix.second(preferLong))
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
        fun minute(preferLong: Boolean = false) = BaseUnit(Affix.minute(preferLong))
        fun hour(preferLong: Boolean = false) = BaseUnit(Affix.hour(preferLong))
        fun day(preferLong: Boolean = false) = BaseUnit(Affix.day(preferLong))
        fun week(preferLong: Boolean = false) = BaseUnit(Affix.week(preferLong))
        fun year(preferLong: Boolean = false) = BaseUnit(Affix.year(preferLong))
        fun inch(preferLong: Boolean = false) = BaseUnit(Affix.inch(preferLong))
        fun foot(preferLong: Boolean = false) = BaseUnit(Affix.foot(preferLong))
        fun yard(preferLong: Boolean = false) = BaseUnit(Affix.yard(preferLong))
        fun mile(preferLong: Boolean = false) = BaseUnit(Affix.mile(preferLong))
        fun pound(preferLong: Boolean = false) = BaseUnit(Affix.pound(preferLong))
        fun ounce(preferLong: Boolean = false) = BaseUnit(Affix.ounce(preferLong))
        fun gallon(preferLong: Boolean = false) = BaseUnit(Affix.gallon(preferLong))
        fun liter(preferLong: Boolean = false) = BaseUnit(Affix.liter(preferLong))
        fun celsius(preferLong: Boolean = false) = BaseUnit(Affix.celsius(preferLong))
        fun fahrenheit(preferLong: Boolean = false) = BaseUnit(Affix.fahrenheit(preferLong))
        fun byte(preferLong: Boolean = false) = BaseUnit(Affix.byte(preferLong))
        fun bit(preferLong: Boolean = false) = BaseUnit(Affix.bit(preferLong))
        fun pixel(preferLong: Boolean = false) = BaseUnit(Affix.pixel(preferLong))
    }
}
