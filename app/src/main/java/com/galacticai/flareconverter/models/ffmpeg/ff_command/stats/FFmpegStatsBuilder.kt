package com.galacticai.flareconverter.models.ffmpeg.ff_command.stats

import global.common.util.TextUtil.pascalCase

/**
 * build [FFmpegStats] from
 * @see FFmpegStatKey
 * @see FFmpegStats */
class FFmpegStatsBuilder(
    val historyMax: Int = 5,
    /** called upon completing stats lines (after detecting [FFmpegStatKey.Progress]) */
    val onProgress: suspend (FFmpegStats) -> Unit
) {
    private val _raw = mutableMapOf<FFmpegStatKey, String>()
    val raw get() = _raw.toMap()
    private val _history = mutableListOf<FFmpegStats>()
    val history get() = _history.toList()

    private suspend fun pushStats() {
        val stats = FFmpegStats(raw)
        _history.add(stats)
        if (_history.size > historyMax) _history.removeAt(0)
        onProgress(stats)
    }

    /** @return first = this instance  |  second = progress line (last) */
    suspend fun receiveLine(line: String): FFmpegStatsBuilder = try {
        if (line.isBlank()) return this
        val (keyRaw, valueRaw) = line.split("=", limit = 2)
        val key = FFmpegStatKey.valueOf(keyRaw.pascalCase)
        _raw[key] = valueRaw
        if (key == FFmpegStatKey.Progress) pushStats()
        return this
    } catch (_: Throwable) {
        return this
    }
}