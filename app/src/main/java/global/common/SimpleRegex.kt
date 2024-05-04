package global.common

/** Simple [Regex]s and utilities
 *
 * Why did I make this? Because I could */
object SimpleRegex {
    const val ANYTHING = ".*"
    const val SOMETHING = ".+"
    private const val NUMBER_SIGN = "[-+]?"
    const val NUMBER_WHOLE = """$NUMBER_SIGN\d+"""
    const val NUMBER_DECIMAL = """$NUMBER_SIGN\d*\.?\d+"""
    const val NUMBER_SCIENTIFIC = """$NUMBER_DECIMAL([eE]$NUMBER_DECIMAL)?"""


    val anything get() = ANYTHING.toRegex()
    val something get() = SOMETHING.toRegex()
    val numberWhole get() = NUMBER_WHOLE.toRegex()
    val numberDecimal get() = NUMBER_DECIMAL.toRegex()
    val numberScientific get() = NUMBER_SCIENTIFIC.toRegex()


    /** `^`[Regex]`$` */
    val Regex.bounded get() = "^$pattern$".toRegex()

    /** [Regex]`$` */
    val Regex.ending get() = "$pattern$".toRegex()

    /** `^`[Regex] */
    val Regex.starting get() = "^$pattern".toRegex()
}