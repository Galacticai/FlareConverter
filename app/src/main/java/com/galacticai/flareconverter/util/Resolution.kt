package com.galacticai.flareconverter.util

import global.common.NumberUtils.nearest
import global.common.models.Jsonable
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.sqrt


open class Resolution(
    val width: Int,
    val height: Int,
    val interlaced: Boolean = false,
    vararg val names: String
) : Jsonable() {
    val widthXHeight get() = "${width}x${height}"
    val name get() = names.firstOrNull() ?: widthXHeight
    val heightPI get() = "$height${if (interlaced) "i" else "p"}"
    val aspectRatio get() = width.toFloat() / height
    val pixelCount get() = width * height
    val diagonal get() = sqrt((width * width + height * height).toDouble())


    /** flip [width] and [height] */
    fun rotate() = Resolution(height, width)

    override fun toJson() = JSONObject().apply {
        put("width", width)
        put("height", height)
        put("interlaced", interlaced)
        put("names", JSONArray(names.toList()))
    }


    /** unspecified */
    object Zero : Resolution(0, 0)

    companion object {
        fun fromJson(json: JSONObject) = Resolution(
            json.getInt("width"),
            json.getInt("height"),
            json.getBoolean("interlaced"),
            *JsonUtils.arrayStrings(json, "names").toTypedArray()
        )

        fun fromJsonString(json: String) = fromJson(JSONObject(json))

        fun fromWidth(width: Int, ratio: Float) =
            Resolution(width, (width / ratio).toInt())

        fun fromHeight(height: Int, ratio: Float) =
            Resolution(height, (height / ratio).toInt())

        val r7680x4320p
            get() = Resolution(
                Pixels.P7680.p, Pixels.P4320.p,
                false, "8K", "UHD"
            )

        val r3840x2160p
            get() = Resolution(
                Pixels.P3840.p, Pixels.P2160.p,
                false, "4K", "UHD"
            )

        val r2560x1440p
            get() = Resolution(
                Pixels.P2560.p, Pixels.P1440.p,
                false, "2K", "QHD"
            )

        val r1920x1080p
            get() = Resolution(
                Pixels.P1920.p, Pixels.P1080.p,
                false, "FHD"
            )

        val r1366x768p
            get() = Resolution(
                Pixels.P1366.p, Pixels.P768.p,
                false, "HD", "WXGA"
            )

        val r1280x720p
            get() = Resolution(
                Pixels.P1280.p, Pixels.P720.p,
                false, "HD"
            )

        val r640x480p
            get() = Resolution(
                Pixels.P640.p, Pixels.P480.p,
                false, "SD", "VGA"
            )

        val r360x240p get() = Resolution(Pixels.P360.p, Pixels.P240.p)
        val r256x144p get() = Resolution(Pixels.P256.p, Pixels.P144.p)
    }
}

enum class Pixels(val p: Int) {
    P144(144),
    P240(240),
    P256(256),
    P360(360),
    P480(480),
    P640(640),
    P720(720),
    P768(768),
    P1080(1080),
    P1280(1280),
    P1366(1366),
    P1440(1440),
    P1920(1920),
    P2160(2160),
    P2560(2560),
    P3840(3840),
    P4320(4320),
    P7680(7680);


    override fun toString() = p.toString()

    companion object {
        fun from(p: Int): Pixels? = Pixels.entries.find { it.p == p }
        fun nearest(p: Int): Pixels =
            from(Pixels.entries.map { it.p }.nearest(p))!!
    }
}
