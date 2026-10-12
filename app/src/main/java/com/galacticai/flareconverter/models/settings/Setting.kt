package com.galacticai.flareconverter.models.settings

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull

open class Setting<T>(
    val defaultValue: T,
    customKeyName: String? = null,
) {
    val keyName: String = customKeyName
        ?: (this::class.simpleName ?: defaultValue?.javaClass?.simpleName)
            ?.let { "Setting.$it" }
        ?: throw IllegalArgumentException(
            "Unable to determine a usable key. you must specify `keyName` property for this call"
        )

    @Suppress("UNCHECKED_CAST") //! T is always one of the below types
    val key: Preferences.Key<T> = when (defaultValue) {
        is Boolean -> booleanPreferencesKey(keyName) as Preferences.Key<T>
        is Int -> intPreferencesKey(keyName) as Preferences.Key<T>
        is Long -> longPreferencesKey(keyName) as Preferences.Key<T>
        is Float -> floatPreferencesKey(keyName) as Preferences.Key<T>
        is Double -> doublePreferencesKey(keyName) as Preferences.Key<T>
        is String -> stringPreferencesKey(keyName) as Preferences.Key<T>
        else -> throw IllegalArgumentException("Unsupported type")
    }

    suspend fun get(context: Context): T =
            context.dataStore.data.firstOrNull()?.get(key)
                ?: defaultValue

    suspend fun set(context: Context, value: T) {
        context.dataStore.edit { it[key] = value }
    }

    suspend fun restoreDefault(context: Context) = set(context, defaultValue)


    fun asState(context: Context) = SettingState(this, context)

    /** this [Setting] as a [MutableState]  (updating the state will update the [Setting] value) */
    @Composable
    fun rememberValue(saveable: Boolean = false, vararg inputs: Any?): MutableState<T> {
        val context = LocalContext.current
        val setting = if (saveable) {
            rememberSaveable(
                *inputs,
                saver = Saver(
                    save = { it.value },
                    restore = { asState(context).apply { setWithoutSaving(it) } }
                )
            ) {
                asState(context)
            }
        } else {
            remember(*inputs) { asState(context) }
        }
        LaunchedEffect(setting) { setting.setWithoutSaving(get(context)) }
        return setting
    }

    companion object {
        const val SETTINGS = "settings"
        val Context.dataStore by preferencesDataStore(name = SETTINGS)
    }
}