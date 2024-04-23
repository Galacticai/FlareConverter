package com.galacticai.flareconverter.models.mimes

import com.galacticai.flareconverter.models.mimes.MimeType.Companion.animated
import com.galacticai.flareconverter.models.mimes.MimeType.Quicktime
import global.common.IOUtils.extensionFromMime
import global.common.IOUtils.mime
import java.io.File

object MimeTypeUtils {

    private val MimeType.defaultConvertibleList
        get() = when (this.category) {
            "image" -> MimeType.imagesOut - this
            "video" -> animated - this
            else -> emptyList()
        }
    private val MimeType.defaultConvertibleMapping
        get() = this.mime to this.defaultConvertibleList

    /** Map of each [MimeType.mime] and the list of [MimeType] that it can be converted to */
    private val convertibleMap: Map<String, List<MimeType>>
        get() = mapOf(
            MimeType.AllVideos.mime to animated,
            MimeType.AllImages.mime to MimeType.imagesOut,
            MimeType.Gif.mime to (MimeType.allOut - MimeType.Gif),
        ) + (MimeType.all - MimeType.Gif).map { it.defaultConvertibleMapping }

    /** Extra mappings for [convertibleMap] for mime types that correspond to existing ones but have a different category/name
     * - Key is the unmentioned mime type
     * - Value is the supported mime type existing within [MimeType] objects
     *
     * Example:
     * - "video/mp4" is not mentioned here but it is supported, and it corresponds to [Quicktime] */
    private val convertibleKeyMappings: Map<String, String>
        get() = mapOf(
            "${MimeType.VIDEO}/mp4" to Quicktime.mime,
        )

    /** Get a list of [MimeType]s that can be the destination from the given [mime]
     * - Like "video/mp4" can be converted to all [animated] */
    fun getConvertibleList(mime: String): List<MimeType>? {
        if (!mime.isMimeType) return null
        return convertibleMap[mime]
            ?: convertibleMap[convertibleKeyMappings[mime]]
    }

    fun File.getConvertibleList(): List<MimeType>? {
        val m = this.mime ?: return null
        return getConvertibleList(m)
    }

    val regex = Regex("""^[a-z0-9]+/(\*|[a-z0-9\-+.]+)$""", RegexOption.IGNORE_CASE)
    val String.isMimeType get() = regex.matches(this)

    fun getMimeParts(mimeString: String): Pair<String, String>? {
        if (!mimeString.isMimeType) return null
        val parts = mimeString.split('/')
        return parts[0] to parts[1]
    }

    /** Get the [MimeType] from the given [mimeString]
     * @param orNew or create a new one*/
    fun getSupported(mimeString: String, orNew: Boolean = false): MimeType? {
        val (category, name) = getMimeParts(mimeString)
            ?: return null
        return MimeType.all
            .find { it.category == category && it.name == name }
            ?: if (orNew) MimeType(category, name)
            else null
    }

    fun getSupportedOrNew(mimeString: String): MimeType? {
        val (category, name) = getMimeParts(mimeString)
            ?: return null
        return MimeType.all
            .find { it.category == category && it.name == name }
            ?: MimeType(category, name)
    }

    /** Get the [MimeType] from the given [mimeString] */
    fun get(mimeString: String): MimeType? {
        val (category, name) = getMimeParts(mimeString)
            ?: return null
        val extension = mimeString.extensionFromMime
        return if (extension == null) MimeType(category, name)
        else MimeType(category, name, extension)
    }
}