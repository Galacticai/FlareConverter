package com.galacticai.flareconverter.models.ffmpeg.media_info.media_stream


class MScore<T>(
    val value: T,
    val scores: MutableList<MScoreType> = mutableListOf()
) {
    val score get() = scores.sumOf { it.score }

    operator fun plus(score: MScoreType) = this.apply {
        scores.add(score)
    }

    override fun equals(other: Any?): Boolean =
        other is MScore<*>
                && value == other.value
                && score == other.score

    override fun hashCode(): Int {
        var result = value?.hashCode() ?: 0
        result = 31 * result + score
        return result
    }
}