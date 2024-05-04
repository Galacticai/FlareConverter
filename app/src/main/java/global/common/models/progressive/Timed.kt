package global.common.models.progressive

import global.common.util.TimeUtil
import java.util.Date

interface TimedStart {
    val start: Date
}

interface Timed : TimedStart {
    val end: Date

    val duration get() = TimeUtil.getDuration(start, end)
}