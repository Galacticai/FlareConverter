package global.common.util

import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

object TimeUtil {
    private const val S = 1000
    private const val M = 60 * S
    private const val H = 60 * M
    private const val D = 24 * H

    fun getDuration(from: Date, to: Date = Date()) =
        (to.time - from.time).toDuration(DurationUnit.MILLISECONDS)


    /** Get the unix time at the start of the day in milliseconds ([Long]) (00:00:00 of the same day)
     * - Fast version: uses the modulo operation to reduce the number of calculations */
    fun Long.atStartOfDayMSFast(timezone: TimeZone = TimeZone.getDefault()): Long =
        this - (this % D) - timezone.rawOffset

    /** Get the unix time at the start of the hour in milliseconds ([Long]) (hh:00:00 of the same hour)
     * - Fast version: uses the modulo operation to reduce the number of calculations */
    fun Long.atStartOfHourMSFast(timezone: TimeZone = TimeZone.getDefault()): Long =
        this - (this % H) - timezone.rawOffset

    /** Get the unix time at the start of the day in milliseconds ([Long]) (00:00:00 of the same day) */
    fun Long.atStartOfDayMS(zoneId: ZoneId = ZoneId.systemDefault()): Long =
        Instant.ofEpochMilli(this)
            .atZone(zoneId)
            .toLocalDate()
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli()


    /** Get the unix time at the start of the hour in milliseconds ([Long]) (hh:00:00 of the same hour) */
    fun Long.atStartOfHourMS(zoneId: ZoneId = ZoneId.systemDefault()): Long =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(this), zoneId)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)
            .toInstant()
            .toEpochMilli()

    /** Get the unix time at the start of the minute in milliseconds ([Long]) (hh:mm:00 of the same hour) */
    fun Long.atStartOfMinuteMS(zoneId: ZoneId = ZoneId.systemDefault()): Long =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(this), zoneId)
            .withSecond(0)
            .withNano(0)
            .toInstant()
            .toEpochMilli()

    /** Get the minute of the hour ([Int]) from the unix time ([Long]) */
    fun Long.getMinute(zoneId: ZoneId = ZoneId.systemDefault()): Int =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(this), zoneId)
            .minute

    /** Get the minute of the hour ([Int]) from the unix time ([Long]) */
    fun Long.getHour(zoneId: ZoneId = ZoneId.systemDefault()): Int =
        ZonedDateTime.ofInstant(Instant.ofEpochMilli(this), zoneId)
            .hour

    fun Long.fromUTC(to: ZoneId = ZoneId.systemDefault()): Long =
        convertTimezone(ZoneOffset.UTC, to)

    fun Long.toUTC(from: ZoneId = ZoneId.systemDefault()): Long =
        convertTimezone(from, ZoneOffset.UTC)

    fun Long.convertTimezone(from: ZoneId, to: ZoneId): Long =
        ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(this),
            from
        )
            .withZoneSameInstant(to)
            .toInstant()
            .toEpochMilli()


    open class DateSuffixes(
        val months: String,
        val days: String,
        val hours: String,
        val minutes: String,
        val seconds: String,
        val milliseconds: String,
    ) {
        /** Map of [java.util.Calendar] fields to [DateSuffixes] */
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

}