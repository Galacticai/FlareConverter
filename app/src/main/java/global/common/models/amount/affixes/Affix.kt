package global.common.models.amount.affixes

enum class AffixType {
    /** Before the value */
    Prefix,

    /** After the value */
    Suffix
}

data class Affix(
    val short: String,
    val long: String,
    val type: AffixType = AffixType.Suffix,
    val preferLong: Boolean = false
) {
    /**
     * ⚠️ Data loss: [type] & [preferLong]
     * will be taken from [right] only
     *
     * @return [Affix] with [short] & [long] extended,
     * and the [type] & [preferLong] of [right]
     */
    operator fun plus(right: Affix): Affix = Affix(
        this.short + right.short,
        this.long + right.long,
        right.type,
        right.preferLong
    )

    override fun toString(): String = if (preferLong) long else short

    object Currency {
        fun USD(preferLong: Boolean = false) = pre("$", "USD", preferLong)
        fun EUR(preferLong: Boolean = false) = pre("€", "EUR", preferLong)
        fun GBP(preferLong: Boolean = false) = pre("£", "GBP", preferLong)
        fun JPY(preferLong: Boolean = false) = pre("¥", "JPY", preferLong)
        fun KRW(preferLong: Boolean = false) = pre("₩", "KRW", preferLong)
        fun CNY(preferLong: Boolean = false) = pre("¥", "CNY", preferLong)
        fun INR(preferLong: Boolean = false) = pre("₹", "INR", preferLong)
        fun RUB(preferLong: Boolean = false) = pre("₽", "RUB", preferLong)
        fun CHF(preferLong: Boolean = false) = pre("₣", "CHF", preferLong)
        fun PHP(preferLong: Boolean = false) = pre("₱", "PHP", preferLong)
    }

    object Multiplier {
        object Metric {
            fun quecto(preferLong: Boolean = false) = suf("q", "Quecto", preferLong)
            fun ronto(preferLong: Boolean = false) = suf("r", "Ronto", preferLong)
            fun yocto(preferLong: Boolean = false) = suf("y", "Yocto", preferLong)
            fun zepto(preferLong: Boolean = false) = suf("z", "Zepto", preferLong)
            fun atto(preferLong: Boolean = false) = suf("a", "Atto", preferLong)
            fun femto(preferLong: Boolean = false) = suf("f", "Femto", preferLong)
            fun pico(preferLong: Boolean = false) = suf("p", "Pico", preferLong)
            fun nano(preferLong: Boolean = false) = suf("n", "Nano", preferLong)
            fun micro(preferLong: Boolean = false) = suf("μ", "Micro", preferLong)
            fun milli(preferLong: Boolean = false) = suf("m", "Milli", preferLong)
            fun centi(preferLong: Boolean = false) = suf("c", "Centi", preferLong)
            fun deci(preferLong: Boolean = false) = suf("d", "Deci", preferLong)
            fun deca(preferLong: Boolean = false) = suf("da", "Deca", preferLong)
            fun hecto(preferLong: Boolean = false) = suf("h", "Hecto", preferLong)
            fun kilo(preferLong: Boolean = false) = suf("k", "Kilo", preferLong)
            fun mega(preferLong: Boolean = false) = suf("M", "Mega", preferLong)
            fun giga(preferLong: Boolean = false) = suf("G", "Giga", preferLong)
            fun tera(preferLong: Boolean = false) = suf("T", "Tera", preferLong)
            fun peta(preferLong: Boolean = false) = suf("P", "Peta", preferLong)
            fun exa(preferLong: Boolean = false) = suf("E", "Exa", preferLong)
            fun zetta(preferLong: Boolean = false) = suf("Z", "Zetta", preferLong)
            fun yotta(preferLong: Boolean = false) = suf("Y", "Yotta", preferLong)
            fun ronna(preferLong: Boolean = false) = suf("R", "Ronna", preferLong)
            fun quetta(preferLong: Boolean = false) = suf("Q", "Quetta", preferLong)
        }

        object Binary {
            fun kibi(preferLong: Boolean = false) = suf("Ki", "Kibi", preferLong)
            fun mebi(preferLong: Boolean = false) = suf("Mi", "Mebi", preferLong)
            fun gibi(preferLong: Boolean = false) = suf("Gi", "Gibi", preferLong)
            fun tebi(preferLong: Boolean = false) = suf("Ti", "Tebi", preferLong)
            fun pebi(preferLong: Boolean = false) = suf("Pi", "Pebi", preferLong)
            fun exbi(preferLong: Boolean = false) = suf("Ei", "Exbi", preferLong)
            fun zebi(preferLong: Boolean = false) = suf("Zi", "Zebi", preferLong)
            fun yobi(preferLong: Boolean = false) = suf("Yi", "Yobi", preferLong)
        }
    }

    companion object {
        val Empty = Affix("", "")

        internal fun pre(symbol: String, name: String, preferLong: Boolean = false) =
            Affix(symbol, name, AffixType.Prefix, preferLong)

        internal fun suf(symbol: String, name: String, preferLong: Boolean = false) =
            Affix(symbol, name, AffixType.Suffix, preferLong)

        fun gram(preferLong: Boolean = false) = suf("g", "Gram", preferLong)
        fun meter(preferLong: Boolean = false) = suf("m", "Meter", preferLong)
        fun second(preferLong: Boolean = false) = suf("s", "Second", preferLong)
        fun ampere(preferLong: Boolean = false) = suf("A", "Ampere", preferLong)
        fun kelvin(preferLong: Boolean = false) = suf("K", "Kelvin", preferLong)
        fun mole(preferLong: Boolean = false) = suf("mol", "Mole", preferLong)
        fun candela(preferLong: Boolean = false) = suf("cd", "Candela", preferLong)
        fun hertz(preferLong: Boolean = false) = suf("Hz", "Hertz", preferLong)
        fun newton(preferLong: Boolean = false) = suf("N", "Newton", preferLong)
        fun pascal(preferLong: Boolean = false) = suf("Pa", "Pascal", preferLong)
        fun joule(preferLong: Boolean = false) = suf("J", "Joule", preferLong)
        fun watt(preferLong: Boolean = false) = suf("W", "Watt", preferLong)
        fun coulomb(preferLong: Boolean = false) = suf("C", "Coulomb", preferLong)
        fun volt(preferLong: Boolean = false) = suf("V", "Volt", preferLong)
        fun farad(preferLong: Boolean = false) = suf("F", "Farad", preferLong)
        fun ohm(preferLong: Boolean = false) = suf("Ω", "Ohm", preferLong)
        fun siemens(preferLong: Boolean = false) = suf("S", "Siemens", preferLong)
        fun weber(preferLong: Boolean = false) = suf("Wb", "Weber", preferLong)
        fun tesla(preferLong: Boolean = false) = suf("T", "Tesla", preferLong)
        fun henry(preferLong: Boolean = false) = suf("H", "Henry", preferLong)
        fun lumen(preferLong: Boolean = false) = suf("lm", "Lumen", preferLong)
        fun lux(preferLong: Boolean = false) = suf("lx", "Lux", preferLong)
        fun minute(preferLong: Boolean = false) = suf("m", "Minute", preferLong)
        fun hour(preferLong: Boolean = false) = suf("h", "Hour", preferLong)
        fun day(preferLong: Boolean = false) = suf("d", "Day", preferLong)
        fun week(preferLong: Boolean = false) = suf("w", "Week", preferLong)
        fun year(preferLong: Boolean = false) = suf("y", "Year", preferLong)
        fun inch(preferLong: Boolean = false) = suf("in", "Inch", preferLong)
        fun foot(preferLong: Boolean = false) = suf("ft", "Foot", preferLong)
        fun yard(preferLong: Boolean = false) = suf("yd", "Yard", preferLong)
        fun mile(preferLong: Boolean = false) = suf("mi", "Mile", preferLong)
        fun pound(preferLong: Boolean = false) = suf("lb", "Pound", preferLong)
        fun ounce(preferLong: Boolean = false) = suf("oz", "Ounce", preferLong)
        fun gallon(preferLong: Boolean = false) = suf("gal", "Gallon", preferLong)
        fun liter(preferLong: Boolean = false) = suf("L", "Liter", preferLong)
        fun celsius(preferLong: Boolean = false) = suf("°C", "Celsius", preferLong)
        fun fahrenheit(preferLong: Boolean = false) = suf("°F", "Fahrenheit", preferLong)
        fun byte(preferLong: Boolean = false) = suf("B", "Byte", preferLong)
        fun bit(preferLong: Boolean = false) = suf("b", "Bit", preferLong)
        fun pixel(preferLong: Boolean = false) = suf("px", "Pixel", preferLong)
    }
}
