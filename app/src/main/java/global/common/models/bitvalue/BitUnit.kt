package global.common.models.bitvalue

open class BitUnit(val exponent: BitUnitExponent, val base: BitUnitBase) {
    val name
        get() = ShortLongName(
            "${exponent.name.short}${base.name.short}",
            "${exponent.name.long}${base.name.long}"
        )
    val toBitMultiplier get() = exponent.toBaseMultiplier * base.toBitMultiplier

    override fun toString(): String = name.short
    class Basic(exponent: BitUnitExponent) : BitUnit(exponent.basic(), BitUnitBase.Bit)
    class BasicByte(exponent: BitUnitExponent) : BitUnit(exponent.basic(), BitUnitBase.Byte)

}