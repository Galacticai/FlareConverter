package global.common.util

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

object AndroidUtil {

    fun restartApp(context: AppCompatActivity, startupActivity: Class<out AppCompatActivity>) {
        context.startActivity(Intent(context, startupActivity))
        context.finishAffinity()
    }
    
    object Intents {

        fun Context.openURL(url: String) = openURL(Uri.parse(url))
        fun Context.openURL(uri: Uri) =
            startActivity(Intent(Intent.ACTION_VIEW, uri).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
    }

    fun hasNotificationPermission(context: Context): Boolean {
        val current = ActivityCompat.checkSelfPermission(
            context,
            "android.permission.POST_NOTIFICATIONS"
        )
        return current == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("ObsoleteSdkInt")
    fun isIgnoringBatteryOptimization(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true //? battery optimization didn't exist before M (= true)
        }
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }


    object Services {
        fun isOwnServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            @Suppress("DEPRECATION")
            for (service in manager.getRunningServices(Int.MAX_VALUE))
                if (serviceClass.name == service.service.className)
                    return true
            return false
        }

        @Suppress("DEPRECATION")
        inline fun <reified T : Service> Context.isServiceRunning(): Boolean {
            val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val running = manager.getRunningServices(Integer.MAX_VALUE)
            return running.any { it.service.className == T::class.java.name }
        }
    }
}