package com.galacticai.flareconverter.models.ffmpeg.ff_command.stats

import global.common.models.amount.Amount
import global.common.models.amount.units.BaseUnit
import global.common.models.amount.units.BinarySystem
import global.common.models.amount.units.MetricSystem
import global.common.util.TextUtil.pascalCase
import kotlin.time.Duration
import kotlin.time.Duration.Companion.microseconds

enum class FFmpegStatKey(val key: String) {
    /** processed frames */
    Frame("frame"),

    /** frame processing speed per second */
    Fps("fps"),

    /** average output bitrate */
    Bitrate("bitrate"),

    /** current output size */
    Size("total_size"),

    /** ETA */
    OutTimeUs("out_time_us"),

    /** frames duplicated (cumulative) */
    DupFrames("dup_frames"),

    /** frames dropped (cumulative) */
    DropFrames("drop_frames"),

    /** processing speed (multiplier) (relative to normal playback) */
    Speed("speed"),

    /** continue or end of progress */
    Progress("progress"),
}

enum class FFmpegStatsProgress {
    /** continue processing */
    Continue,

    /** last bit of progress (finished after this) */
    End
}

class FFmpegStats(
    private val raw: Map<FFmpegStatKey, String>
) {
    val frame by lazy { raw[FFmpegStatKey.Frame]?.toIntOrNull() }
    val fps by lazy { raw[FFmpegStatKey.Fps]?.toFloatOrNull() }
    val bitrate by lazy {
        val v = raw[FFmpegStatKey.Bitrate]
            ?.replace(Regex("[^\\d.]"), "")
            ?.toDoubleOrNull()
            ?: return@lazy null
        Amount(v, MetricSystem.kilo() and BaseUnit.bitPerSecond())
            .toUnit(BinarySystem.mebi() and BaseUnit.bitPerSecond())
    }
    val size by lazy {
        val v = raw[FFmpegStatKey.Size]
            ?.toDoubleOrNull()
            ?: return@lazy null
        Amount(v, BaseUnit.byte())
            .toUnit(BinarySystem.mebi() and BaseUnit.byte())
    }
    val outTime by lazy {
        raw[FFmpegStatKey.OutTimeUs]
            ?.toLongOrNull()
            ?.microseconds
    }
    val dupFrames by lazy { raw[FFmpegStatKey.DupFrames]?.toIntOrNull() }
    val dropFrames by lazy { raw[FFmpegStatKey.DropFrames]?.toIntOrNull() }
    val speed by lazy {
        raw[FFmpegStatKey.Speed]
            ?.replace(Regex("[^\\d.]"), "")
            ?.toFloatOrNull()
    }
    val progressFlag by lazy {
        raw[FFmpegStatKey.Progress]?.let {
            FFmpegStatsProgress.valueOf(it.pascalCase)
        }
    }

    fun getProgress(duration: Duration): Float? {
        val processedUs = outTime?.inWholeMicroseconds ?: return null
        val totalUs = duration.inWholeMicroseconds
        if (totalUs <= 0) return null
        return (processedUs.toFloat() / totalUs).coerceIn(0f, 1f)
    }
}

class FFmpegStatsBuilder(
    /** called upon completing stats lines (after detecting [FFmpegStatKey.Progress]) */
    val onProgress: suspend (FFmpegStats) -> Unit
) {
    private val _raw = mutableMapOf<FFmpegStatKey, String>()
    val raw get() = _raw.toMap()

    /** @return first = this instance  |  second = progress line (last) */
    suspend fun append(line: String): FFmpegStatsBuilder = try {
        if (line.isBlank()) return this
        val (keyRaw, valueRaw) = line.split("=", limit = 2)
        val key = FFmpegStatKey.valueOf(keyRaw.pascalCase)
        _raw[key] = valueRaw
        if (key == FFmpegStatKey.Progress) {
            onProgress(FFmpegStats(raw))
        }
        return this
    } catch (_: Throwable) {
        return this
    }
}


/*                    VIDEO
frame=4801
fps=600.02
stream_0_0_q=28.0
bitrate= 764.1kbits/s
total_size=18612272
out_time_us=194861859
out_time_ms=194861859
out_time=00:03:14.861859
dup_frames=0
drop_frames=0
speed=24.4x
progress=continue
frame=5302
fps=626.59
stream_0_0_q=-1.0
bitrate= 751.9kbits/s
total_size=19936996
out_time_us=212137506
out_time_ms=212137506
out_time=00:03:32.137506
dup_frames=0
drop_frames=0
speed=25.1x
progress=end
*/

/*                    IMAGE
frame=0
fps=0.00
stream_0_0_q=0.0
bitrate=N/A
total_size=0
out_time_us=N/A
out_time_ms=N/A
out_time=N/A
dup_frames=0
drop_frames=0
speed=N/A
progress=continue
frame=1
fps=0.75
stream_0_0_q=-0.0
bitrate=N/A
total_size=N/A
out_time_us=1000000
out_time_ms=1000000
out_time=00:00:01.000000
dup_frames=0
drop_frames=0
speed=0.747x
progress=end
*/

/*                    AUDIO
bitrate= 149.3kbits/s
total_size=49807360
out_time_us=2668202667
out_time_ms=2668202667
out_time=00:44:28.202667
dup_frames=0
drop_frames=0
speed= 445x
progress=continue
bitrate= 150.0kbits/s
total_size=52218142
out_time_us=2784810667
out_time_ms=2784810667
out_time=00:46:24.810667
dup_frames=0
drop_frames=0
speed= 445x
progress=end
*/