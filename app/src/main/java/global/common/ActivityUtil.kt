package global.common

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable

object ActivityUtil {
    inline fun <reified T : Parcelable> Intent.getParcel(key: String): T? = when {
        Build.VERSION.SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    inline fun <reified T : Parcelable> Bundle.getParcel(key: String): T? = when {
        Build.VERSION.SDK_INT >= 33 -> getParcelable(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelable(key) as? T
    }
}