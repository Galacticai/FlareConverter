package com.galacticai.flareconverter.util.ff_command

import com.galacticai.flareconverter.models.ffmpeg.ff_command.FFmpegCommand
import com.galacticai.flareconverter.models.ffmpeg.media_info.codec.Codec
import com.galacticai.flareconverter.models.ffmpeg.media_info.codec.CodecType
import com.galacticai.flareconverter.models.ffmpeg.media_info.format.Format
import kotlinx.coroutines.runBlocking

/** things that never change (get once) across the runtime (like supported formats ...etc...) */
object FFStatic {
    /**
     * key = [com.galacticai.flareconverter.models.ffmpeg.media_info.format.Format.key]
     * @see [com.galacticai.flareconverter.models.ffmpeg.ff_command.args.FFmpegArg.Formats]
     */
    val FORMATS_AVAILABLE by lazy {
        FFmpegCommand().hideBanner().formats()
            .getSupportedBy(
                "---" //? this is how ffmpeg outputs it...
            ) { Format.from(it) }
            ?.associateBy { it.key }
    }

    /**
     * key = [com.galacticai.flareconverter.models.ffmpeg.media_info.codec.Codec.key]
     * @see [com.galacticai.flareconverter.models.ffmpeg.ff_command.args.FFmpegArg.Codecs]
     */
    val CODECS_AVAILABLE by lazy {
        FFmpegCommand().hideBanner().codecs()
            .getSupportedBy(
                "-------" //? this is how ffmpeg outputs it...
            ) { Codec.from(it) }
            ?.associateBy { it.key }
    }

    /** ⚠️ images are also categorized as [com.galacticai.flareconverter.models.ffmpeg.media_info.codec.CodecType.Video] */
    fun getCodecsOf(type: CodecType) =
            CODECS_AVAILABLE?.filter { it.value.type == type }

    /** ⚠️ blocks main thread - will only be used for tiny operations */
    private fun <T> FFmpegCommand.getSupportedBy(
        valueSplit: String, map: (line: String) -> T
    ) = runBlocking {
        //! intentional: runBlocking - very lightweight so it's ok to run on main thread
        this@getSupportedBy.run { it.output }
    }?.split(valueSplit)
        ?.getOrNull(1) // second part = actual values
        ?.split('\n')
        ?.filter { it.isNotEmpty() }
        ?.mapNotNull { map(it) }
}