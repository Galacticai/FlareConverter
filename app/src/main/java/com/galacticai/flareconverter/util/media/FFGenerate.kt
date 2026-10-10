package com.galacticai.flareconverter.util.media

import androidx.annotation.FloatRange
import com.galacticai.flareconverter.models.MediaFile
import com.galacticai.flareconverter.models.MediaFileBase
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.models.exceptions.FFmpegFailed
import com.galacticai.flareconverter.models.ffmpeg.ff_command.FFmpegCommand
import com.galacticai.flareconverter.util.Pixels
import global.common.util.IOUtil.toExtension

object FFGenerate {
    suspend fun extractFrame(
        media: MediaFile,
        @FloatRange(0.0, 1.0)
        from: Float = .5f,
    ): MediaFileBase? = try {
        when (media.mime.category) {
            MimeType.AUDIO -> return null
            MimeType.IMAGE -> return MediaFileBase(media.fileInfo.file, media.mime)
        }

        val mimeNew = MimeType.Jpeg
        val frame = media.fileInfo.file.toExtension(mimeNew.extension)

        if (frame.exists()) {
            //? skip: when the ui reloads (theme changed / screen rotated / or whatever)
            return MediaFileBase(frame, mimeNew)
        }

        val msTotal = media.mainStreamMime?.duration?.inWholeMilliseconds ?: 0L
        val msFrame = (msTotal * from).toLong()

        val resolution = media.mainStreamMime?.resolution
            ?.scale(.5f)
            ?.maxHeight(Pixels.P1080.p)
            ?.maxWidth(Pixels.P1080.p)
            ?: throw FFmpegFailed("Invalid ${MimeType.VIDEO} file: unknown resolution")

        FFmpegCommand().hideBanner()
            .apply { if (msFrame > 0) startTime(msFrame) }
            .input(media.fileInfo.file.absolutePath)
            .resolution(resolution.width, resolution.height)
            .frameCount(1)
            .output(frame.absolutePath)
            .run(onFail = { throw it }) { }

        MediaFileBase(frame, mimeNew)
    } catch (ex: Throwable) {
        ex.printStackTrace()
        null
    }
}

