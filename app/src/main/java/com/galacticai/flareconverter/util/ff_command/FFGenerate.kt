package com.galacticai.flareconverter.util.ff_command

import androidx.annotation.FloatRange
import com.galacticai.flareconverter.models.MediaFile
import com.galacticai.flareconverter.models.MediaFileBase
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.models.ffmpeg.ff_command.FFmpegCommand
import global.common.util.IOUtil.toExtension

object FFGenerate {
    suspend fun extractFrame(
        media: MediaFile,
        @FloatRange(0.0, 1.0)
        from: Float = .5f
    ): MediaFileBase? = try {
        when (media.mime.category) {
            MimeType.AUDIO -> return null
            MimeType.IMAGE -> return MediaFileBase(media.fileInfo.file, media.mime)
        }

        val mimeNew = MimeType.Jpeg
        val frame = media.fileInfo.file.toExtension(mimeNew.extension)

        val msTotal = media.mainStreamMime?.duration?.inWholeMilliseconds ?: 0L
        val msFrame = (msTotal * from).toLong()

        FFmpegCommand().hideBanner()
            .apply { if (msFrame > 0) startTime(msFrame) }
            .input(media.fileInfo.file.absolutePath)
            .frameCount(1)
            .output(frame.absolutePath)
            .run(onFail = { throw it }) { }

        MediaFileBase(frame, MimeType.Jpeg)
    } catch (ex: Throwable) {
        ex.printStackTrace()
        null
    }
}

