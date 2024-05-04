package com.galacticai.flareconverter.models

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import global.common.util.ActivityUtil.getParcel
import global.common.util.IOUtil.extensionFromMime


data class ShareInfo(
    val uri: Uri,
    /** raw mime type from [ContentResolver] */
    val mimeRaw: String,
    val extension: String,
    /** mime represented with supported [MimeType] */
    val inMime: MimeType,
    /** allowed [MimeType]s for the given [inMime] */
    val outMimes: List<MimeType>,
) {
    fun toMime(
        mime: MimeType,
        onFail: ((msg: String) -> Unit)? = null
    ): ShareInfo? = copy(
        extension = mime.extension,
        mimeRaw = mime.mime,
        inMime = mime,
        outMimes = MimeType.Outputs.of(inMime.category)
            ?: return run {
                onFail?.invoke("Cannot convert from this type: $inMime")
                null
            }
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
            if (uri == null) {
                onFail("Missing Uri in intent extra stream")
                return null
            }
            val mimeString = contentResolver.getType(uri)
            if (mimeString == null) {
                onFail("Unknown file type: $uri")
                return null
            }
            val extension = mimeString.extensionFromMime
            if (extension == null) {
                onFail("Unknown file extension: $mimeString")
                return null
            }
            val inMime = MimeType.from(mimeString)
            if (inMime == null) {
                onFail("Unsupported file type: $mimeString")
                return null
            }
            val outMimes = MimeType.Outputs.of(inMime.category)
            if (outMimes == null) {
                onFail("Cannot convert from this type: $inMime")
                return null
            }
            return ShareInfo(uri, mimeString, extension, inMime, outMimes)
        }
    }
}
