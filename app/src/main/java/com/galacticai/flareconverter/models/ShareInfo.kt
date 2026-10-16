package com.galacticai.flareconverter.models

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import com.galacticai.flareconverter.models.mimes.MimeType
import com.galacticai.flareconverter.util.media.MediaCapabilities
import global.common.util.ActivityUtil.getParcel
import global.common.util.IOUtil.extensionFromMime


data class ShareInfo(
    val uri: Uri,
    /** raw mime type from [ContentResolver] */
    val mimeRaw: String,
    val extension: String,
    /** mime represented with supported [com.galacticai.flareconverter.models.mimes.MimeType] */
    val inMime: MimeType,
    /** allowed [MimeType]s for the given [inMime] */
    val outMimes: Set<MimeType>,
) {
    fun toMime(
        mime: MimeType,
        onFail: ((msg: String) -> Unit)? = null,
    ): ShareInfo? = copy(
        extension = mime.extension,
        mimeRaw = mime.mime,
        inMime = mime,
        outMimes = MediaCapabilities.Supported.outputsOf(mime)
            ?: return run {
                onFail?.invoke("Cannot convert from this type: $inMime")
                null
            },
    )

    companion object {
        fun from(
            intent: Intent?,
            contentResolver: ContentResolver,
            onFail: (msg: String) -> Unit,
        ): ShareInfo? {
            if (intent == null) {
                onFail("Missing intent")
                return null
            }
            if (intent.action != Intent.ACTION_SEND) {
                onFail("Intent required: ${Intent.ACTION_SEND}")
                return null
            }
            val uri = intent.getParcel<Uri>(Intent.EXTRA_STREAM)
                ?: return run {
                    onFail("Missing Uri in intent extra stream")
                    null
                }
            val mimeString = contentResolver.getType(uri)
                ?: return run {
                    onFail("Unknown file type: $uri")
                    null
                }
            val extension = mimeString.extensionFromMime
                ?: return run {
                    onFail("Unknown file extension: $mimeString")
                    null
                }
            val inMime = MediaCapabilities.Supported.getMime(mimeString)
                ?: return run {
                    onFail(
                        "Unsupported file type: $mimeString"
                                + "\nSupported mimes:\n"
                                + MediaCapabilities.Supported.mimes().joinToString("\n") { it.mime }
                    )
                    null
                }
            val outMimes = MediaCapabilities.Supported.outputsOf(inMime)
                ?: return run {
                    onFail("Cannot convert from this type: $inMime")
                    null
                }
            return ShareInfo(uri, mimeString, extension, inMime, outMimes)
        }
    }
}
