package global.common.util

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.core.content.FileProvider
import global.common.util.IOUtil.mime
import java.io.File

object ActivityUtil {
    private val Context.fileProviderUriDefault: String get() = "${packageName}.fileprovider"

    inline fun <reified T : Parcelable> Intent.getParcel(key: String): T? = when {
        Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    inline fun <reified T : Parcelable> Bundle.getParcel(key: String): T? = when {
        Build.VERSION.SDK_INT >= 33 -> getParcelable(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelable(key) as? T
    }


    private fun Context.prepareFileIntent(
        action: String,
        file: File,
        mime: String? = file.mime,
        fileProviderUri: String = fileProviderUriDefault
    ): Intent {
        val uri = FileProvider.getUriForFile(this, fileProviderUri, file)
        return Intent(action).apply {
            setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(uri, mime)
        }
    }

    /** Opens a file with the default app associated with the file type */
    fun Context.openFile(
        file: File,
        mime: String? = file.mime,
        fileProviderUri: String = fileProviderUriDefault
    ): Intent {
        val intent = prepareFileIntent(Intent.ACTION_VIEW, file, mime, fileProviderUri)
        startActivity(intent)
        return intent
    }

    /** Share a [file] */
    fun Context.shareFile(
        file: File,
        mime: String? = file.mime,
        fileProviderUri: String = fileProviderUriDefault,
        title: String = "Share via",
    ): Intent {
        val send = prepareFileIntent(Intent.ACTION_SEND, file, mime, fileProviderUri)
        val chooser = Intent.createChooser(send, title)
        startActivity(chooser)
        return chooser
    }


    /** Saves a [file] using the default android file save handler */
    fun Context.saveFile(
        file: File,
        mime: String? = file.mime,
        fileProviderUri: String = fileProviderUriDefault,
        title: String = "Save as ($mime)",
    ): Intent {
        val send = prepareFileIntent(Intent.ACTION_CREATE_DOCUMENT, file, mime, fileProviderUri)
        val chooser = Intent.createChooser(send, title)
        startActivity(chooser)
        return chooser
    }
}