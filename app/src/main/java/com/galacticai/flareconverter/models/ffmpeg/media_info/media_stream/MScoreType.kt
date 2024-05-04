package com.galacticai.flareconverter.models.ffmpeg.media_info.media_stream

enum class MScoreType(val score: Int) {
    /** first stream */
    IsFirst(50),

    /** input is the same codec type */
    IsSameCategory(100),

    /** input is image while codec is video (video is also used for images) */
    MaybeSameCategory(50),

    /** stream has default disposition */
    IsDefault(30),

    /** stream has original disposition */
    IsOriginal(20),

    /** stream is part of a stream group component */
    IsStreamGroup(40),
}
