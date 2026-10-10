package com.galacticai.flareconverter.util

import global.common.models.Jsonable
import org.json.JSONObject
import kotlin.math.roundToInt
import kotlin.math.sqrt

data class Resolution(
    val width: Int,
    val height: Int
) : Jsonable(), Comparable<Resolution> {
    val widthXHeight get() = "${width}x${height}"
    val aspectRatio get() = width.toFloat() / height
    val pixelCount get() = width * height
    val diagonal get() = sqrt((width * width + height * height).toDouble())

    fun scale(ratio: Float): Resolution = copy(
        width = (width * ratio).roundToInt(),
        height = (height * ratio).roundToInt(),
    )

    fun maxHeight(value: Int): Resolution =
            if (height <= value) this
            else scale(value.toFloat() / height)

    fun maxWidth(value: Int): Resolution =
            if (width <= value) this
            else scale(value.toFloat() / width)

    override fun toJson() = JSONObject().apply {
        put("width", width)
        put("height", height)
    }

    override fun compareTo(other: Resolution): Int =
            pixelCount.compareTo(other.pixelCount)

    operator fun plus(other: Resolution) = copy(
        width = width + other.width,
        height = height + other.height,
    )

    operator fun minus(other: Resolution) = copy(
        width = width - other.width,
        height = height - other.height,
    )

    operator fun times(other: Resolution) = copy(
        width = width * other.width,
        height = height * other.height,
    )

    operator fun div(other: Resolution) = copy(
        width = if (other.width == 0) 0 else (width / other.width),
        height = if (other.height == 0) 0 else (height / other.height),
    )

    companion object {
        val zero get() = Resolution(0, 0)

        fun fromJson(json: JSONObject) = Resolution(
            json.getInt("width"),
            json.getInt("height"),
        )

        fun fromJsonString(json: String) = fromJson(JSONObject(json))

        fun fromWidth(width: Int, ratio: Float) =
                Resolution(width, (width / ratio).toInt())

        fun fromHeight(height: Int, ratio: Float) =
                Resolution(height, (height / ratio).toInt())
    }
}

//@Deprecated("use the other one")
//open class ResolutionV1(
//    val width: Int,
//    val height: Int,
//    val interlaced: Boolean = false,
//    vararg val names: String
//) : Jsonable() {
//    val widthXHeight get() = "${width}x${height}"
//    val name get() = names.firstOrNull() ?: widthXHeight
//    val heightPI get() = "$height${if (interlaced) "i" else "p"}"
//    val aspectRatio get() = width.toFloat() / height
//    val pixelCount get() = width * height
//    val diagonal get() = sqrt((width * width + height * height).toDouble())
//
//    override fun toString() = widthXHeight
//
//    /** flip [width] and [height] */
//    fun rotate() = Resolution(height, width)
//
//    override fun toJson() = JSONObject().apply {
//        put("width", width)
//        put("height", height)
//        put("interlaced", interlaced)
//        put("names", JSONArray(names.toList()))
//    }
//
//
//    /** unspecified */
//    object Zero : ResolutionV1(0, 0)
//
//    companion object {
//        fun fromJson(json: JSONObject) = ResolutionV1(
//            json.getInt("width"),
//            json.getInt("height"),
//            json.getBoolean("interlaced"),
//            *JsonUtils.arrayStrings(json, "names").toTypedArray()
//        )
//
//        fun fromJsonString(json: String) = fromJson(JSONObject(json))
//
//        fun fromWidth(width: Int, ratio: Float) =
//            ResolutionV1(width, (width / ratio).toInt())
//
//        fun fromHeight(height: Int, ratio: Float) =
//            ResolutionV1(height, (height / ratio).toInt())
//
//        val r7680x4320p
//            get() = ResolutionV1(
//                Pixels.P7680.p, Pixels.P4320.p,
//                false, "8K", "UHD"
//            )
//
//        val r3840x2160p
//            get() = ResolutionV1(
//                Pixels.P3840.p, Pixels.P2160.p,
//                false, "4K", "UHD"
//            )
//
//        val r2560x1440p
//            get() = ResolutionV1(
//                Pixels.P2560.p, Pixels.P1440.p,
//                false, "QHD", "2K"
//            )
//
//        val r1920x1080p
//            get() = ResolutionV1(
//                Pixels.P1920.p, Pixels.P1080.p,
//                false, "FHD"
//            )
//
//        val r1366x768p
//            get() = ResolutionV1(
//                Pixels.P1366.p, Pixels.P768.p,
//                false, "HD", "WXGA"
//            )
//
//        val r1280x720p
//            get() = ResolutionV1(
//                Pixels.P1280.p, Pixels.P720.p,
//                false, "HD"
//            )
//
//        val r640x480p
//            get() = ResolutionV1(
//                Pixels.P640.p, Pixels.P480.p,
//                false, "SD", "VGA"
//            )
//
//        val r360x240p get() = ResolutionV1(Pixels.P360.p, Pixels.P240.p)
//        val r256x144p get() = ResolutionV1(Pixels.P256.p, Pixels.P144.p)
//    }
//}

