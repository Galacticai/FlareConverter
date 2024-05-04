package com.galacticai.flareconverter.models.ffmpeg.media_info.codec

/**
 *  - D..... = Decoding supported
 *  - .E.... = Encoding supported
 *  - ..V... = Video codec
 *  - ..A... = Audio codec
 *  - ..S... = Subtitle codec
 *  - ..D... = Data codec
 *  - ..T... = Attachment codec
 *  - ...I.. = Intra frame-only codec
 *  - ....L. = Lossy compression
 *  - .....S = Lossless compression
 */
data class Codec(
    /** `D.....` */
    val canDecode: Boolean,
    /** `.E....` */
    val canEncode: Boolean,
    /** `..!...` */
    val type: CodecType,
    /** `...I..` */
    val intraFrameOnly: Boolean,
    /** `....L.` */
    val lossy: Boolean,
    /** `.....S` */
    val lossless: Boolean,
    /** index */
    val key: String,
    val description: String,
) {
    companion object {
        /** example: `DEAILS name          description description`*/
        val regex = Regex("""^([D.])([E.])([AVSDT.])([I.])([L.])([S.])\s+(\w+)\s*(.*)$""")

        /** @param raw `DE!ILS     key     description may have spaces too` */
        @JvmStatic
        fun from(raw: String) = try {
            val (D, E, typeRaw, I, L, S, key, description) = regex.matchEntire(raw.trim())!!.destructured
            val type = CodecType.from(typeRaw)!!
            Codec(
                D == "D",
                E == "E",
                type,
                I == "I",
                L == "L",
                S == "S",
                key, description
            )
        } catch (_: Exception) {
            null
        }
    }
}

