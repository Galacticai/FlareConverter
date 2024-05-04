package com.galacticai.flareconverter

import android.content.Context
import androidx.startup.Initializer
import com.galacticai.flareconverter.util.App

class AppInit : Initializer<Unit> {
    override fun create(context: Context) {
        App.init(context.applicationContext)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}