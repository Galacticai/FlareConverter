package com.galacticai.flareconverter.models.ffmpeg.ff_command.stats

/** @see FFmpegStats
 * @see FFmpegStatsBuilder */
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