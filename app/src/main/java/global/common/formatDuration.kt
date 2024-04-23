package global.common

import java.util.Calendar
import kotlin.time.Duration

open class DateSuffixes(
    val months: String,
    val days: String,
    val hours: String,
    val minutes: String,
    val seconds: String,
    val milliseconds: String,
) {
    /** Map of [Calendar] fields to [DateSuffixes] */
    val ofCalendar = mapOf(
        Calendar.MONTH to months,
        Calendar.DAY_OF_MONTH to days,
        Calendar.HOUR_OF_DAY to hours,
        Calendar.MINUTE to minutes,
        Calendar.SECOND to seconds,
        Calendar.MILLISECOND to milliseconds
    )

    data object Default : DateSuffixes(
        months = "mo",
        days = "d",
        hours = "h",
        minutes = "m",
        seconds = "s",
        milliseconds = "ms",
    )
}

/** Format a [Duration] into a string with [suffixes]
 * - ⚠️ Month = 30 days
 * - Example: `1y, 2mo, 3d ..`
 *
 * @param suffixes Time suffixes
 * @param joint Parts separator
 * @param partsCount Max number of parts to return
 * @param valueFormat Format the value of each part (Useful for localization)
 */
fun Duration.format(
    suffixes: DateSuffixes = DateSuffixes.Default,
    joint: String = ", ",
    partsCount: Int = Int.MAX_VALUE,
    valueFormat: (Int) -> String = { it.toString() }
): String = listOf(
    (inWholeDays / 30).toInt() to suffixes.months,
    (inWholeDays % 30).toInt() to suffixes.days,
    (inWholeHours % 24).toInt() to suffixes.hours,
    (inWholeMinutes % 60).toInt() to suffixes.minutes,
    (inWholeSeconds % 60).toInt() to suffixes.seconds,
    (inWholeMilliseconds % 1000).toInt() to suffixes.milliseconds
)
    .filter { it.first > 0 }
    .take(partsCount)
    .joinToString(joint) {
        "${valueFormat(it.first)}${it.second}"
    }
