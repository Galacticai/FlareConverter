package com.galacticai.flareconverter.models.mimes

import com.galacticai.flareconverter.util.media.MediaCapabilities
import global.common.models.Jsonable
import global.common.util.IOUtil.mime
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/** Mime type representation
 * @param category "video", "audio", "image"... etc.
 * @param key "*" (any), "mp4", "mp3", "jpg"... etc.
 * @param extension possible file extension */
open class MimeType(
    val category: String,
    val key: String,
    val extension: String,
) : Jsonable() {

    /** Get [key] with its synonyms ([MimeSynonyms.keysByCategory]) */
    val keys: Set<String> by lazy {
        setOf(key) + MimeSynonyms.keysByCategory[category]?.get(key).orEmpty()
    }

    /** Get [extension] with its synonyms ([MimeSynonyms.extensionsByCategory]) */
    val extensions: Set<String> by lazy {
        setOf(extension) + MimeSynonyms.extensionsByCategory[category]?.get(key).orEmpty()
    }

    /** Get the mime type [String] as "[category]/[keys]" */
    val mime get() = "$category/$key"


    val isImage get() = category == IMAGE
    val isVideo get() = category == VIDEO
    val isAudio get() = category == AUDIO
    val isAnimated: Boolean
        get() {
            if (category == VIDEO) return true
            val animatedKeys = ANIMATED.flatMap { it.keys }.toSet()
            return keys.any { it in animatedKeys }
        }

    val outMimes get() = MediaCapabilities.Supported.outputsOf(this)

    override fun toString() = mime
    override fun toJson() = JSONObject().apply {
        put("category", category)
        put("key", key)
        put("extensions", JSONArray(extensions))
    }

    object Anything : MimeType(ANYTHING, ANYTHING, "")
    object AllVideos : MimeType(VIDEO, ANYTHING, "")
    object Avi : MimeType(VIDEO, "avi", "avi")
    object Flv : MimeType(VIDEO, "flv", "flv")
    object Webm : MimeType(VIDEO, "webm", "webm")
    object Mkv : MimeType(VIDEO, "mkv", "mkv")
    object Mp4 : MimeType(VIDEO, "mp4", "mp4")
    object Mpeg4 : MimeType(VIDEO, "mpeg4", "mp4")
    object M4v : MimeType(VIDEO, "m4v", "m4v")
    object Mpeg : MimeType(VIDEO, "mpeg", "mpeg")
    object Quicktime : MimeType(VIDEO, "quicktime", "mov")
    object Ogv : MimeType(VIDEO, "ogg", "ogg")
    object Wmv : MimeType(VIDEO, "wmv", "wmv")

    object AllImages : MimeType(IMAGE, ANYTHING, "")
    object Gif : MimeType(IMAGE, "gif", "gif")
    object Bmp : MimeType(IMAGE, "bmp", "bmp")
    object Tiff : MimeType(IMAGE, "tiff", "tiff")
    object Svg : MimeType(IMAGE, "svg", "svg")
    object Png : MimeType(IMAGE, "png", "png")
    object Apng : MimeType(IMAGE, "apng", "apng")
    object Jpeg : MimeType(IMAGE, "jpeg", "jpeg")
    object Webp : MimeType(IMAGE, "webp", "webp")
    object Heic : MimeType(IMAGE, "heic", "heic")

    object AllAudio : MimeType(AUDIO, ANYTHING, "")
    object M4a : MimeType(AUDIO, "m4a", "m4a")
    object Mp3 : MimeType(AUDIO, "mp3", "mp3")
    object Aac : MimeType(AUDIO, "aac", "aac")
    object Wav : MimeType(AUDIO, "wav", "wav")
    object Ogg : MimeType(AUDIO, "ogg", "ogg")
    object Flac : MimeType(AUDIO, "flac", "flac")
    object Wma : MimeType(AUDIO, "wma", "wma")
    object Opus : MimeType(AUDIO, "opus", "opus")


    companion object {
        const val ANYTHING = "*"
        const val IMAGE = "image"
        const val VIDEO = "video"
        const val AUDIO = "audio"

        val CATEGORIES = listOf(IMAGE, VIDEO, AUDIO)
        val ANIMATED = listOf(MimeType.Gif, MimeType.Apng, MimeType.Webp)
        val HAS_STREAM_GROUP = setOf(Mp4, Mpeg4, M4v, Quicktime, Heic)

        val REGEX = Regex(
            """^(?<category>\*|[a-z0-9\-+.]+)/(?<key>\*|[a-z0-9\-+.]+)$""",
            RegexOption.IGNORE_CASE
        )

        fun getParts(mimeString: String?): Pair<String, String>? {
            if (mimeString == null) return null
            val match = REGEX.matchEntire(mimeString)
                ?: return null
            val (category, key) = match.destructured
            return category to key
        }

        /** ⚠️ also parses unsupported mimes */
        fun from(mimeString: String?): MimeType? {
            val (category, key) = getParts(mimeString)
                ?: return null

            if (key == ANYTHING) {
                return when (category) {
                    VIDEO -> AllVideos
                    IMAGE -> AllImages
                    AUDIO -> AllAudio
                    else -> Anything
                }
            }

            val extension = MimeSynonyms.getExtensionsByKey(category, key)
                ?.firstOrNull() ?: ""
            return MimeType(category, key, extension)
        }

        fun from(file: File) = from(file.mime)

        fun fromJson(json: JSONObject): MimeType {
            val category = json.getString("category")
            val key = json.getString("key")
            val extension = json.getString("extension")
            return MimeType(category, key, extension)
        }
    }
}
