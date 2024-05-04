package com.galacticai.flareconverter.models.settings

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import java.io.Serializable

abstract class ObjectSetting<T : Serializable>(
    keyName: String,
    val defaultObject: T
) : Setting<String>(
    keyName,
    defaultObject.toString()
) {
    open suspend fun setObject(context: Context, value: T) {
        super.set(context, value.toString())
    }

    abstract suspend fun getObject(context: Context): T

    @Composable
    fun rememberObject(saveable: Boolean = false, vararg keys: Any?): MutableState<T> {
        val context = LocalContext.current
        val setting =
            if (saveable) rememberSaveable(keys) { mutableStateOf(defaultObject) }
            else remember(keys) { mutableStateOf(defaultObject) }
        LaunchedEffect(Unit) { setting.value = getObject(context) }
        return setting
    }
}