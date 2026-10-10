package com.galacticai.flareconverter.models.ffmpeg.ff_command.stats

enum class FFmpegStatsProgress {
    /** continue processing */
    Continue,

    /** last bit of progress (finished after this) */
    End
}