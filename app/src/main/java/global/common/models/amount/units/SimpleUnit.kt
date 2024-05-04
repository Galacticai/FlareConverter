package global.common.models.amount.units

import global.common.models.amount.affixes.Affix

/**
 * Simplified [AmountUnit] implementation having a simple [multiplier]
 * which is used to convert from/to base unit
 */
open class SimpleUnit(
    val multiplier: Double,
    vararg affixes: Affix
) : AmountUnit(
    toUnit = { v -> v / multiplier },
    toBase = { v -> v * multiplier },
    affixes = affixes
)
