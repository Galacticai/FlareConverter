package com.galacticai.flareconverter.common

import android.app.ActivityManager
import android.content.Context

fun isOwnServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    @Suppress("DEPRECATION")
    for (service in manager.getRunningServices(Int.MAX_VALUE))
        if (serviceClass.name == service.service.className)
            return true
    return false
}
