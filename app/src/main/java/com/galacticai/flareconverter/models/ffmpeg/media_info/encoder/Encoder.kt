package com.galacticai.flareconverter.models.ffmpeg.media_info.encoder

/**
 *  -  `V.....` = Video
 *  -  `A.....` = Audio
 *  -  `S.....` = Subtitle
 *  -  `.F....` = Frame-level multithreading
 *  -  `..S...` = Slice-level multithreading
 *  -  `...C..` = Codec is experimental
 *  -  `....B.` = Supports draw_horiz_band
 *  -  `.....D` = Supports direct rendering method 1
 */
data class Encoder(
    /** `!.....` */
    val type: EncoderType,
    /** `.F....` */
    val frameMultiThreading: Boolean,
    /** `..S...` */
    val sliceMultiThreading: Boolean,
    /** `...C..` */
    val isExperimental: Boolean,
    /** `....B.` */
    val supportsDrawHorizBand: Boolean,
    /** `.....D` */
    val supportsDirectRendering: Boolean,
    /** index */
    val key: String,
    val description: String,
) {
    companion object {
        /** example: `...... name          description description`*/
        val regex = Regex("""^([VAS.])([F.])([S.])([C.])([B.])([D.])\s+(\w+)\s*(.*)$""")

        /** @param raw `DE!ILS     key     description may have spaces too` */
        @JvmStatic
        fun from(raw: String) = try {
            val (typeRaw, F, S, C, B, D, key, description) = regex.matchEntire(raw.trim())!!.destructured
            val type = EncoderType.from(typeRaw)!!
            Encoder(
                type,
                F == "F",
                S == "S",
                C == "C",
                B == "B",
                D == "D",
                key, description
            )
        } catch (_: Throwable) {
            null
        }
    }
}

