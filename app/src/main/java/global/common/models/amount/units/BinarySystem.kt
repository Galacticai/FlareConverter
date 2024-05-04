package global.common.models.amount.units

import global.common.models.amount.affixes.Affix

enum class BinaryFactor(val value: Int) {
    Base(0),
    Kibi(10),
    Mebi(20),
    Gibi(30),
    Tebi(40),
    Pebi(50),
    Exbi(60),
    Zebi(70),
    Yobi(80),
}

class BinarySystem(
    factor: Int,
    vararg affixes: Affix
) : SimpleUnitSystem(
    factor,
    (1L shl factor).toDouble(),
    *affixes
) {
    constructor(factor: BinaryFactor, vararg affixes: Affix) : this(factor.value, *affixes)

    companion object {
        fun kibi(preferLong: Boolean = false) =
            BinarySystem(BinaryFactor.Kibi, Affix.Multiplier.Binary.kibi(preferLong))

        fun mebi(preferLong: Boolean = false) =
            BinarySystem(BinaryFactor.Mebi, Affix.Multiplier.Binary.mebi(preferLong))

        fun gibi(preferLong: Boolean = false) =
            BinarySystem(BinaryFactor.Gibi, Affix.Multiplier.Binary.gibi(preferLong))

        fun tebi(preferLong: Boolean = false) =
            BinarySystem(BinaryFactor.Tebi, Affix.Multiplier.Binary.tebi(preferLong))

        fun pebi(preferLong: Boolean = false) =
            BinarySystem(BinaryFactor.Pebi, Affix.Multiplier.Binary.pebi(preferLong))

        fun exbi(preferLong: Boolean = false) =
            BinarySystem(BinaryFactor.Exbi, Affix.Multiplier.Binary.exbi(preferLong))

        fun zebi(preferLong: Boolean = false) =
            BinarySystem(BinaryFactor.Zebi, Affix.Multiplier.Binary.zebi(preferLong))

        fun yobi(preferLong: Boolean = false) =
            BinarySystem(BinaryFactor.Yobi, Affix.Multiplier.Binary.yobi(preferLong))
    }
}
