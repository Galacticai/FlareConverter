package com.galacticai.flareconverter.models

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import com.galacticai.flareconverter.util.MimeTypeUtils
import global.common.ActivityUtil.getParcel
import global.common.IOUtils.extensionFromMime

data class ShareInfo(
    val uri: Uri,
    val mime: String,
    val extension: String,
    val convertibleTo: List<MimeType>
) {
    companion object {
        fun getShareInfo(
            intent: Intent?,
            contentResolver: ContentResolver,
            onFail: (msg: String) -> Unit,
        ): ShareInfo? {
            if (intent == null) {
                onFail("Missing intent")
                return null
            }
            if (intent.action != Intent.ACTION_SEND) {
                onFail("Intent action required: ${Intent.ACTION_SEND}")
                return null
            }
            val uri = intent.getParcel<Uri>(Intent.EXTRA_STREAM)
            if (uri == null) {
                onFail("Missing Uri in ${Intent.EXTRA_STREAM}")
                return null
            }
            val mimeString = contentResolver.getType(uri)
            if (mimeString == null) {
                onFail("Mime type not found: $uri")
                return null
            }
            val extension = mimeString.extensionFromMime
            if (extension == null) {
                onFail("Unknown file extension of this mime type: $mimeString")
                return null
            }
            val convertibleTo = MimeTypeUtils.getConvertibleList(mimeString)
            if (convertibleTo == null) {
                onFail("Cannot convert from this mime type: $mimeString")
                return null
            }
            return ShareInfo(uri, mimeString, extension, convertibleTo)
        }
    }
}