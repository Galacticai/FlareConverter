package com.galacticai.flareconverter.models.settings

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
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

const val SETTINGS = "settings"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS)

open class Setting<T>(
    val keyName: String,
    val defaultValue: T,
) {


    val key = when (defaultValue) {
        is Boolean -> booleanPreferencesKey(keyName)
        is Int -> intPreferencesKey(keyName)
        is Long -> longPreferencesKey(keyName)
        is Float -> floatPreferencesKey(keyName)
        is Double -> doublePreferencesKey(keyName)
        is String -> stringPreferencesKey(keyName)
        else -> throw IllegalArgumentException("Unsupported type")
    }

    @Suppress("UNCHECKED_CAST") //! set(.) will always save the value as T
    suspend fun get(context: Context): T = context
        .dataStore.data
        .firstOrNull()?.get(key) as T
        ?: defaultValue

    @Suppress("UNCHECKED_CAST") //! get(..) will always save the key as Preferences.Key<T>
    suspend fun set(context: Context, value: T) = context
        .dataStore.edit {
            val k = key as Preferences.Key<T>
            it[k] = value
        }

    suspend fun restoreDefault(context: Context) = set(context, defaultValue)


    fun <T> asState(context: Context) = SettingState(this, context)

    /** this [Setting] as a [MutableState]  (updating the state will update the [Setting] value) */
    @Composable
    fun rememberValue(saveable: Boolean = false, vararg keys: Any?): MutableState<T> {
        val context = LocalContext.current
        val setting =
            if (saveable) rememberSaveable(keys) { this.asState<T>(context) }
            else remember(keys) { this.asState<T>(context) }
        LaunchedEffect(Unit) { setting.setWithoutSaving(get(context)) }
        return setting
    }
}