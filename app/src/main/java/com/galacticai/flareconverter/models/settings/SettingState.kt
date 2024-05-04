package com.galacticai.flareconverter.models.settings

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class SettingState<T>(
    val setting: Setting<T>,
    private val context: Context
) : MutableState<T> {
    private val state = mutableStateOf(setting.defaultValue)
    private var silent = false
    override var value: T
        get() = state.value
        set(v) {
            if (v == state.value) return
            if (!silent) runBlocking(Dispatchers.IO) { setting.set(context, v) }
            state.value = v
        }

    /** Set the [MutableState.value] without calling [Setting.set] */
    fun setWithoutSaving(v: T) {
        silent = true
        value = v
        silent = false
    }

    override fun component1() = state.component1()
    override fun component2() = state.component2()
}