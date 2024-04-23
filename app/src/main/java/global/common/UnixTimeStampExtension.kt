package global.common

import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.TimeZone


private const val s = 1000
private const val m = 60 * s
private const val h = 60 * m
private const val d = 24 * h

/** Get the unix time at the start of the day in milliseconds ([Long]) (00:00:00 of the same day)
 * - Fast version: uses the modulo operation to reduce the number of calculations */
fun Long.atStartOfDayMSFast(timezone: TimeZone = TimeZone.getDefault()): Long =
    this - (this % d) - timezone.rawOffset

/** Get the unix time at the start of the hour in milliseconds ([Long]) (hh:00:00 of the same hour)
 * - Fast version: uses the modulo operation to reduce the number of calculations */
fun Long.atStartOfHourMSFast(timezone: TimeZone = TimeZone.getDefault()): Long =
    this - (this % h) - timezone.rawOffset

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
