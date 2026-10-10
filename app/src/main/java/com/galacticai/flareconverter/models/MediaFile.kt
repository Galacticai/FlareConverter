package com.galacticai.flareconverter.models

import com.galacticai.flareconverter.models.ffmpeg.media_info.MediaInfo
import com.galacticai.flareconverter.models.ffmpeg.media_info.media_stream.ParsedMediaStream
import com.galacticai.flareconverter.models.ffmpeg.media_info.media_stream.ParsedMediaStream.Companion.main
import com.galacticai.flareconverter.models.ffmpeg.media_info.media_stream.ParsedMediaStreamsMainMap
import com.galacticai.flareconverter.models.ffmpeg.media_info.media_stream.ParsedMediaStreamsMap
import com.galacticai.flareconverter.util.MimeTypeUtils.codecType
import com.galacticai.flareconverter.util.media.FFInfo.parseMediaStreams
import java.io.File


open class MediaFileBase(
    val fileInfo: FileInfo,
    val mime: MimeType,
) {
    constructor(file: File, mime: MimeType)
            : this(FileInfo(file), mime)
}

class MediaFile(
    fileInfo: FileInfo, mime: MimeType,
    val mediaInfo: MediaInfo,
) : MediaFileBase(fileInfo, mime) {

    constructor(file: File, mime: MimeType, mediaInfo: MediaInfo)
            : this(FileInfo(file), mime, mediaInfo)

    val streams: ParsedMediaStreamsMap? by lazy {
        runCatching {
            mediaInfo.parseMediaStreams(mime.category)
        }.getOrNull()
    }
    val mainStream: ParsedMediaStreamsMainMap? by lazy { streams?.main }
    val mainStreamMime: ParsedMediaStream? by lazy {
        mime.codecType?.let { mainStream?.get(it) }
    }
}