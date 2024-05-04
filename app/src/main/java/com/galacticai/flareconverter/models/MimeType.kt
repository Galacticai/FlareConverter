package com.galacticai.flareconverter.models

import com.galacticai.flareconverter.models.MimeType.Companion.ANYTHING
import com.galacticai.flareconverter.util.JsonUtils
import com.galacticai.flareconverter.util.MimeTypeUtils.regex
import global.common.models.Jsonable
import global.common.util.IOUtil.mime
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/** Mime type representation
 * @param category "video", "audio", "image"... etc.
 * @param keys "*" (any), "mp4", "mp3", "jpg"... etc.
 * @param extensions possible file extensions, or [[keys]] by default */
open class MimeType(
    val category: String,
    val keys: List<String>,
    val extensions: List<String> = listOf(),
) : Jsonable() {
    /** Get the first of [keys]*/
    val key get() = keys.firstOrNull()

    /** [key] or [ANYTHING] */
    val keyOrAnything get() = keys.firstOrNull() ?: ANYTHING

    /** Get the mime type [String] as "[category]/[keys]" */
    val mime get() = "$category/$key"

    /** Get the first one out of [extensions] or empty [String] if none */
    val extension get() = extensions.firstOrNull() ?: ""

    override fun toString() = mime
    override fun toJson() = JSONObject().apply {
        put("category", category)
        put("keys", JSONArray(keys))
        put("extensions", JSONArray(extensions))
    }

    object Anything : MimeType(ANYTHING, listOf(ANYTHING))

    object AllVideos : MimeType(VIDEO, listOf(ANYTHING))
    object Avi : MimeType(VIDEO, listOf("avi", "x-msvideo", "msvideo"), listOf("avi"))
    object Flv : MimeType(VIDEO, listOf("flv", "x-flv"), listOf("flv"))
    object Webm : MimeType(VIDEO, listOf("webm"), listOf("webm"))
    object Mkv : MimeType(VIDEO, listOf("mkv", "x-matroska", "matroska"), listOf("mkv"))
    object Mp4 : MimeType(VIDEO, listOf("mp4"), listOf("mp4"))
    object Mpeg4 : MimeType(VIDEO, listOf("mpeg4", "mp4"), listOf("mp4", "m4v"))
    object M4v : MimeType(VIDEO, listOf("m4v", "x-m4v"), listOf("m4v"))
    object Mpeg : MimeType(VIDEO, listOf("mpeg", "mpg"), listOf("mpeg", "mpg"))
    object Quicktime : MimeType(VIDEO, listOf("quicktime", "mov"), listOf("mov", "qt"))
    object Ogv : MimeType(VIDEO, listOf("ogg", "ogv"), listOf("ogg", "ogv"))
    object Wmv : MimeType(VIDEO, listOf("wmv", "x-ms-wmv", "ms-wmv"), listOf("wmv"))


    object AllImages : MimeType(IMAGE, listOf(ANYTHING))
    object Gif : MimeType(IMAGE, listOf("gif"), listOf("gif"))
    object Bmp : MimeType(IMAGE, listOf("bmp", "x-ms-bmp"), listOf("bmp"))
    object Tiff : MimeType(IMAGE, listOf("tiff", "x-tiff"), listOf("tiff"))
    object Svg : MimeType(IMAGE, listOf("svg", "svg+xml"), listOf("svg"))
    object Png : MimeType(IMAGE, listOf("png"), listOf("png"))
    object Apng : MimeType(IMAGE, listOf("apng"), listOf("apng"))
    object Jpeg : MimeType(IMAGE, listOf("jpeg", "jpg"), listOf("jpeg", "jpg"))
    object Webp : MimeType(IMAGE, listOf("webp"), listOf("webp"))
    object Heic : MimeType(IMAGE, listOf("heic", "heif"), listOf("heic"))


    object AllAudio : MimeType(AUDIO, listOf(ANYTHING))
    object M4a : MimeType(AUDIO, listOf("m4a", "x-m4a", "aac"), listOf("m4a"))
    object Mp3 : MimeType(AUDIO, listOf("mp3", "mpeg", "x-mpeg", "x-mp3"), listOf("mp3"))
    object Aac : MimeType(AUDIO, listOf("aac"), listOf("aac"))
    object Wav : MimeType(AUDIO, listOf("wav", "x-wav", "wave"), listOf("wav"))
    object Ogg : MimeType(AUDIO, listOf("ogg"), listOf("ogg"))
    object Flac : MimeType(AUDIO, listOf("flac", "x-flac"), listOf("flac"))
    object Wma : MimeType(AUDIO, listOf("wma", "x-ms-wma", "ms-wma"), listOf("wma"))
    object Opus : MimeType(AUDIO, listOf("opus"), listOf("opus"))

    object Inputs {
        val videos get() = listOf(Avi, Flv, Webm, Mkv, Mp4, Mpeg4, M4v, Mpeg, Quicktime, Ogv, Wmv)
        val images get() = listOf(Gif, Bmp, Tiff, Svg, Png, Apng, Jpeg, Webp, Heic)
        val imagesAnimated get() = listOf(Gif, Apng, Webp)
        val audios get() = listOf(Mp3, M4a, Aac, Wav, Ogg, Flac, Wma, Opus)

        val animated get() = videos + imagesAnimated

        val all get() = videos + images + audios


        fun of(category: String): List<MimeType>? {
            return when (category) {
                IMAGE -> images
                VIDEO -> videos
                AUDIO -> audios
                else -> null
            }
        }
    }

    object Outputs {
        val image get() = Inputs.images - Svg
        val video get() = Inputs.videos + image + Inputs.audios
        val audio get() = Inputs.audios

        fun of(inputCategory: String): List<MimeType>? {
            return when (inputCategory) {
                IMAGE -> image
                VIDEO -> video
                AUDIO -> audio
                else -> null
            }
        }
    }

    companion object {
        const val ANYTHING = "*"
        const val VIDEO = "video"
        const val IMAGE = "image"
        const val AUDIO = "audio"
        val categories get() = listOf(IMAGE, VIDEO, AUDIO)


        fun getParts(mimeString: String?): Pair<String, String>? {
            if (mimeString == null) return null
            val match = regex.matchEntire(mimeString)
                ?: return null
            val (category, key) = match.destructured
            return category to key
        }

        fun from(
            mimeString: String?,
            allowWild: Boolean = false,
            allowUnsupported: Boolean = false
        ): MimeType? {
            val (category, key) = getParts(mimeString)
                ?: return null

            val found = Inputs.all.find {
                category == it.category && it.keys.contains(key)
            }
            if (found != null) return found

            if (!allowWild) return null
            if (key == ANYTHING) {
                return when (category) {
                    VIDEO -> AllVideos
                    IMAGE -> AllImages
                    AUDIO -> AllAudio
                    else -> Anything
                }
            }

            if (!allowUnsupported) return null
            return MimeType(category, listOf(key))
        }

        fun from(
            file: File,
            acceptAnything: Boolean = false,
            allowUnsupported: Boolean = false
        ): MimeType? {
            return from(file.mime, acceptAnything, allowUnsupported)
        }

        fun fromJson(json: JSONObject): MimeType {
            val category = json.getString("category")
            val keys = JsonUtils.arrayStrings(
                json, "keys"
            )
            val extensions = JsonUtils.arrayStrings(
                json, "extensions"
            )
            return MimeType(category, keys, extensions)
        }
    }
}

