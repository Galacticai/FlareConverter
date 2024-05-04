package global.common

import java.time.Duration
import java.util.Date

object TimeUtils {
    fun getDuration(from: Date, to: Date = Date()): Duration {
        return Duration.between(from.toInstant(), to.toInstant())
    }
}