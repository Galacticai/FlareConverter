package com.galacticai.flareconverter.util.settings

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
import com.galacticai.flareconverter.models.mimes.MimeType
import com.galacticai.flareconverter.models.mimes.MimeTypeUtils
import com.galacticai.flareconverter.models.mimes.MimeTypeUtils.isMimeType
import kotlinx.coroutines.flow.firstOrNull
import org.json.JSONObject

const val SETTINGS = "settings"
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = SETTINGS)

open class Setting<T>(
    val keyName: String,
    val defaultValue: T,
) {
    companion object {
        val entries
            get() = listOf<Setting<*>>(
                // might remove
            )

        suspend fun restoreAll(c: Context) = entries.forEach { it.restoreDefault(c) }
    }

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

    /** [ObjectSetting] of the last destination [MimeType] for the given mime type
     * @param mime source mime type */
    class LastSelectedMime(mime: String) : ObjectSetting<MimeType>(
        keyName = "LastSelectedMime_${mime.split('/')[0]}",
        defaultObject = MimeTypeUtils.getConvertibleList(mime)!!.first()
    ) {
        init {
            assert(mime.isMimeType)
        }

        override suspend fun getObject(context: Context) =
            MimeType.fromJson(JSONObject(get(context)))
    }
}