package com.galacticai.flareconverter.common

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE

@Suppress("DEPRECATION")
inline fun <reified T : Service> Context.isServiceRunning(): Boolean {
    val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
    val running = manager.getRunningServices(Integer.MAX_VALUE)
    return running.any { it.service.className == T::class.java.name }
}