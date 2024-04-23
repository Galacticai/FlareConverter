package global.common

import android.content.Context
import android.os.PowerManager

fun isIgnoringBatteryOptimization(context: Context): Boolean {
//    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return false
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    return powerManager.isIgnoringBatteryOptimizations(context.packageName)
}
