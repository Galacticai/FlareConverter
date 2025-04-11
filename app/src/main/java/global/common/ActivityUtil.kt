package global.common

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.core.content.FileProvider
import com.galacticai.flareconverter.util.AppDefaults.fileProviderUri
import java.io.File

object ActivityUtil {
    inline fun <reified T : Parcelable> Intent.getParcel(key: String): T? = when {
        Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    inline fun <reified T : Parcelable> Bundle.getParcel(key: String): T? = when {
        Build.VERSION.SDK_INT >= 33 -> getParcelable(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelable(key) as? T
    }


    /** Opens a file with the default app associated with the file type */
    fun openFile(
        file: File,
        context: Context,
        fileProviderUri: String = "${context.packageName}.fileprovider"
    ): Intent {
        val uri = FileProvider.getUriForFile(context, fileProviderUri, file)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, context.contentResolver.getType(uri))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(intent)
        return intent
    }

    /** Share a [file] */
    fun share(
        context: Context,
        file: File,
        mime: String? = null,
        title: String = "Share via"
    ): Intent {
        val uri = FileProvider.getUriForFile(context, context.fileProviderUri, file)
        val send = Intent(Intent.ACTION_SEND).apply {
            type = mime
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            putExtra(Intent.EXTRA_STREAM, uri)
        }
        val chooser = Intent.createChooser(send, title)
        context.startActivity(chooser)
        return chooser
    }
}