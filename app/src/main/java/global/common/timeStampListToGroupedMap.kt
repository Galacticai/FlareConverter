package global.common

import java.util.Calendar

enum class TimeGroupingLevel(val calendarField: Int) {
    Seconds(Calendar.SECOND),
    Minutes(Calendar.MINUTE),
    Hours(Calendar.HOUR_OF_DAY),
    Days(Calendar.DAY_OF_MONTH),
    Months(Calendar.MONTH),
    Years(Calendar.YEAR),
}


///** Resets all [Calendar] fields up to and including the provided [field]
// *
// * Example:
// * - in: Reset up to [Calendar.MINUTE]:
// * - out: (hour):(minute):00.000
// */
//private fun Calendar.resetFieldsUpTo(field: Int) {
//    val fieldsToReset = TimeGroupingLevel.entries.map { it.calendarField }
//    for (i in 0 until fieldsToReset.indexOf(field) + 1) {
//        this[fieldsToReset[i]] = 0
//    }
//}

/** Group time stamps ([Collection]<[Long]>) using [TimeGroupingLevel] ([level])
 * @param level The [TimeGroupingLevel] to use (ex: [TimeGroupingLevel.Days] will group by day)
 * @return A [Map] where each key ([Long]) is the zero'd time stamp of the selected [level] and the value is the [List]<[Long]> of time stamps in that group
 */
fun Collection<Long>.groupByTime(
    level: TimeGroupingLevel,
    sort: Boolean = true
): Map<Long, List<Long>> {
    val calendar = Calendar.getInstance()

    return (if (sort) this.sorted() else this).groupBy { timestamp ->
        calendar.timeInMillis = timestamp
        truncateCalendar(calendar, level)
        calendar.timeInMillis
    }
}

/** Truncates the calendar based on the provided [TimeGroupingLevel] */
private fun truncateCalendar(calendar: Calendar, level: TimeGroupingLevel) {
    for (entry in TimeGroupingLevel.entries) {
        if (level.calendarField >= entry.calendarField) {
            calendar[entry.calendarField] = 0
        }
    }
}