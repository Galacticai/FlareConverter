package com.galacticai.flareconverter

import org.json.JSONArray
import org.json.JSONObject

/** Mime type representation
 * @param category "video", "audio", "image"... etc.
 * @param name "*" (any), "mp4", "mp3", "jpg"... etc.
 * @param extensions possible file extensions, or [[name]] by default */
open class MimeType(
    val category: String,
    val name: String,
    val extensions: List<String> = listOf(name),
    val convertibleTo: List<MimeType> = emptyList(),
) : Jsonable {
    init {
        if (extensions.isEmpty()) {
            throw IllegalArgumentException("No extensions specified")
        }
    }

    /** Get the mime type [String] as "[category]/[name]" */
    val mime get() = "$category/$name"

    /** Get the first one out of [extensions] */
    val extension get() = extensions.first()

    override fun toString() = mime
    override fun toJson() = JSONObject().apply {
        put("category", category)
        put("name", name)
        put("extensions", JSONArray(extensions))
        //! use mime string to avoid cyclic references
        put("convertibleTo", JSONArray(convertibleTo.map { it.mime }))
    }

    /** All video types (video/[*]) */
    object AllVideos : MimeType(VIDEO, "*", convertibleTo = movers)
    object Avi : MimeType(
        VIDEO, "x-msvideo", listOf("avi"),
        convertibleTo = movers - Avi
    )

    object Flv : MimeType(
        VIDEO, "x-flv", listOf("flv"),
        convertibleTo = movers - Flv
    )

    object Webm : MimeType(
        VIDEO, "webm",
        convertibleTo = movers - Webm
    )

    object Mov : MimeType(
        VIDEO, "mp4", listOf("mov"),
        convertibleTo = movers - Mov
    )

    object Mp4 : MimeType(
        VIDEO, "mp4",
        convertibleTo = movers - Mp4
    )

    object Mpeg : MimeType(
        VIDEO, "mpeg", listOf("mpeg", "mpg"),
        convertibleTo = movers - Mpeg
    )

    object Ogv : MimeType(
        VIDEO, "ogg", listOf("ogv"),
        convertibleTo = movers - Ogv
    )

    object Wmv : MimeType(
        VIDEO, "x-ms-wmv", listOf("wmv"),
        convertibleTo = movers - Wmv
    )

    /** All image types (image/[*]) */
    object AllImages : MimeType(IMAGE, "*", convertibleTo = images)
    object Gif : MimeType(
        IMAGE, "gif",
        convertibleTo = videos + images - Gif
    )

    object Bmp : MimeType(
        IMAGE, "bmp",
        convertibleTo = images - Bmp
    )

    object Tiff : MimeType(
        IMAGE, "tiff",
        convertibleTo = images - Tiff
    )

    object Svg : MimeType(
        IMAGE, "svg+xml", listOf("svg"),
        convertibleTo = images - Svg
    )

    object Png : MimeType(
        IMAGE, "png",
        convertibleTo = images - Png
    )

    object Jpeg : MimeType(
        IMAGE, "jpeg", listOf("jpg", "jpeg"),
        convertibleTo = images - Jpeg
    )

    // object Aac : MimeType("audio", "aac", "aac")
    // object Flac : MimeType("audio", "flac", "flac")
    // object Ogg : MimeType("audio", "ogg", "ogg")
    // object Wav : MimeType("audio", "wav", "wav")

    companion object {
        const val VIDEO = "video"
        const val IMAGE = "image"

        val allCollectionsMap
            get() = mapOf(
                AllVideos to videos,
                AllImages to images
            )
        val all get() = videos + images

        /** Image types like [Png], [Jpeg], [Bmp]...*/
        val images get() = listOf(Gif, Bmp, Tiff, Svg, Png, Jpeg)

        /** Video types like [Mov], [Mp4], [Mpeg]...*/
        val videos get() = listOf(Avi, Flv, Webm, Mov, Mp4, Mpeg, Ogv, Wmv)

        /** Stuff that moves ([videos] + [Gif])*/
        val movers get() = videos + Gif


        val regex = Regex("""^[a-z0-9]+/(\*|[a-z0-9\-+.]+)$""", RegexOption.IGNORE_CASE)
        val String.isMimeType get() = regex.matches(this)

        /** Get the [MimeType] from the given [mimeString] */
        fun getSupported(mimeString: String): MimeType? {
            if (!mimeString.isMimeType) return null
            val parts = mimeString.split('/')
            val category = parts[0]
            val name = parts[1]
            val collection = allCollectionsMap.toList().find { it.first.category == category }
                ?: return null
            val mimes = collection.second
            return mimes.find { it.name == name }
        }


        fun fromJson(json: JSONObject): MimeType {
            val category = json.getString("category")
            val name = json.getString("name")
            val extensionsJ = json.getJSONArray("extensions")
            val extensions = mutableListOf<String>()
            for (i in 0 until extensionsJ.length()) {
                extensions.add(extensionsJ.getString(i))
            }
            val convertibleToJ = json.getJSONArray("convertibleTo")
            val convertibleTo = mutableListOf<MimeType>()
            for (i in 0 until convertibleToJ.length()) {
                val mimeString = convertibleToJ.getString(i) ?: continue
                val mimeType = getSupported(mimeString) ?: continue
                convertibleTo.add(mimeType)
            }
            return MimeType(category, name, extensions, convertibleTo)
        }
    }
}

