package global.common

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

fun hasNotificationPermission(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(context, "android.permission.POST_NOTIFICATIONS") ==
            PackageManager.PERMISSION_GRANTED
}