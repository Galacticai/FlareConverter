package global.common.models.bitvalue

open class BitUnitBase(
    val name: ShortLongName,
    val toBitMultiplier: Int,
) {
    override fun toString(): String = name.short

    object Bit : BitUnitBase(ShortLongName("b", "Bit"), 1)
    object Byte : BitUnitBase(ShortLongName("B", "Byte"), 8)
}