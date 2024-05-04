package global.common.models.amount.units

import global.common.models.amount.affixes.Affix

abstract class SimpleUnitSystem(
    val factor: Int,
    multiplier: Double,
    vararg affixes: Affix
) : SimpleUnit(multiplier, *affixes)
