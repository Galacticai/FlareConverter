package global.common

import android.content.Context
import android.content.Intent
import android.net.Uri

fun Context.openURL(url: String) = openURL(Uri.parse(url))
fun Context.openURL(uri: Uri) =
    startActivity(Intent(Intent.ACTION_VIEW, uri).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    })