package com.galacticai.flareconverter.models.settings

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import java.io.Serializable

abstract class ObjectSetting<T : Serializable?>(
    keyName: String,
    val defaultObject: T,
    val fromObject: (T) -> String = { it.toString() },
    val toObject: (String) -> T,
) : Setting<String>(
    keyName,
    defaultObject.toString()
) {
    suspend fun setObject(context: Context, value: T) {
        super.set(context, value.toString())
    }

    suspend fun getObject(context: Context): T {
        val v = get(context)
        return try {
            toObject(v)
        } catch (e: Throwable) {
            Log.e(
                "ObjectSetting - ${javaClass.simpleName}",
                "Resetting to default ($keyName). Invalid json: $v",
                e
            )
            setObject(context, defaultObject)
            defaultObject
        }
    }

    @Composable
    fun rememberObject(saveable: Boolean = false, vararg keys: Any?): MutableState<T> {
        val context = LocalContext.current
        val setting =
                if (saveable) rememberSaveable(
                    *keys,
                    saver = Saver(
                        save = { it.value.toString() },
                        restore = { mutableStateOf(defaultObject) }
                    )
                ) { mutableStateOf(defaultObject) }
                else remember(*keys) { mutableStateOf(defaultObject) }

        LaunchedEffect(Unit) { setting.value = getObject(context) }
        return setting
    }
}