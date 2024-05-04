package com.galacticai.flareconverter.models.ffmpeg.media_info.format

/**
 *  - `D..` = Demuxing supported
 *  - `.E.` = Muxing supported
 *  - `..d` = Is a device
 */
data class Format(
    val demuxIsSupported: Boolean,
    val muxIsSupported: Boolean,
    val isDevice: Boolean,
    /** index */
    val key: String,
    val description: String,
) {
    companion object {
        /** example: `DEd name          description description` */
        val regex = Regex("""^([D ])([E ])([d ])\s+(\w+)\s+(.+)$""")
        fun from(raw: String) = try {
            val (D, E, d, key, description) = regex.matchEntire(raw)!!.destructured
            Format(
                D == "D",
                E == "E",
                d == "d",
                key, description,
            )
        } catch (_: Exception) {
            null
        }
    }
}
