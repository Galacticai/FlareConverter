package com.galacticai.flareconverter.models

import global.common.models.Jsonable
import org.json.JSONArray
import org.json.JSONObject

/** Mime type representation
 * @param category "video", "audio", "image"... etc.
 * @param name "*" (any), "mp4", "mp3", "jpg"... etc.
 * @param extensions possible file extensions, or [[name]] by default */
open class MimeType(
    val category: String,
    val name: String,
    vararg val extensions: String,
) : Jsonable() {

    /** Get the mime type [String] as "[category]/[name]" */
    val mime get() = "$category/$name"
    val parts get() = category to name

    /** Get the first one out of [extensions] */
    val extension get() = extensions.first()

    override fun toString() = mime
    override fun toJson() = JSONObject().apply {
        put("category", category)
        put("name", name)
        put("extensions", JSONArray(extensions))
    }

    object Anything : MimeType("*", "*")

    /** All video types (video/[*]) */
    object AllVideos : MimeType(VIDEO, "*")
    object Avi : MimeType(VIDEO, "x-msvideo", "avi")
    object Flv : MimeType(VIDEO, "x-flv", "flv")
    object Webm : MimeType(VIDEO, "webm", "webm")
    object Quicktime : MimeType(VIDEO, "quicktime", "mp4", "mov")
    object Mpeg : MimeType(VIDEO, "mpeg", "mpeg", "mpg")
    object Ogv : MimeType(VIDEO, "ogg", "ogg", "ogv")
    object Wmv : MimeType(VIDEO, "x-ms-wmv", "wmv")
    object AllImages : MimeType(IMAGE, "*")
    object Gif : MimeType(IMAGE, "gif", "gif")
    object Bmp : MimeType(IMAGE, "bmp", "bmp")
    object Tiff : MimeType(IMAGE, "tiff", "tiff")
    object Svg : MimeType(IMAGE, "svg+xml", "svg")
    object Png : MimeType(IMAGE, "png", "png")
    object Apng : MimeType(IMAGE, "apng", "apng")
    object Jpeg : MimeType(IMAGE, "jpeg", "jpeg", "jpg")

    // object Aac : MimeType("audio", "aac", "aac")
    // object Flac : MimeType("audio", "flac", "flac")
    // object Ogg : MimeType("audio", "ogg", "ogg","oga")
    // object Wav : MimeType("audio", "wav", "wav")

    companion object {
        const val VIDEO = "video"
        const val IMAGE = "image"

        /** All listed mime types in this class  */
        val all get() = videos + images

        /** [all] except types that cannot be the output of a conversion */
        val allOut get() = videos + imagesOut

        /** Image types like [Png], [Jpeg], [Bmp]...etc. */
        val images get() = listOf(Gif, Bmp, Tiff, Svg, Png, Apng, Jpeg)

        /** [images] except types that cannot be the output of a conversion
         * - Example: Can't convert to [Svg] */
        val imagesOut get() = images - Svg

        /** Video types like [Avi], [Mpeg], [Webm]...etc. */
        val videos get() = listOf(Avi, Flv, Webm, Quicktime, Mpeg, Ogv, Wmv)

        /** All types that move ([videos] + [Gif] + [Apng]) */
        val animated get() = videos + Gif + Apng

        fun fromJson(json: JSONObject): MimeType {
            val category = json.getString("category")
            val name = json.getString("name")
            val extensionsJ = json.getJSONArray("extensions")
            val extensions = mutableListOf<String>()
            for (i in 0 until extensionsJ.length()) {
                extensions.add(extensionsJ.getString(i))
            }
            return MimeType(category, name, *extensions.toTypedArray())
        }
    }
}

