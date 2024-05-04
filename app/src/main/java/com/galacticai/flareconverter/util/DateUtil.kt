package com.galacticai.flareconverter.util

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

object DateUtil {
    const val DATE_FORMAT = "yyyy/MM/dd"
    const val TIME_FORMAT = "HH:mm"
    const val TIME_SECONDS_FORMAT = "$TIME_FORMAT:ss"

    fun Date.format(time: Boolean = true, seconds: Boolean = false): String {
        val parts = mutableListOf(DATE_FORMAT)
        if (time) parts.add(if (seconds) TIME_SECONDS_FORMAT else TIME_FORMAT)
        val format = parts.joinToString(" ")
        val formatter = DateTimeFormatter.ofPattern(format)
        return toInstant().atZone(ZoneId.systemDefault()).format(formatter)
    }
}