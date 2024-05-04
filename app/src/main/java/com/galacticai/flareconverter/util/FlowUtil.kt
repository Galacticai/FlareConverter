package com.galacticai.flareconverter.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

object FlowUtil {
    /** like [MutableStateFlow]`.collectAsState` but for 1 value of a [Map] (by [key]) */
    @Composable
    fun <K, V> MutableStateFlow<Map<K, V>>.collectSingleAsState(
        key: K, default: V
    ): MutableState<V> {
        val flow = this
        val state = remember(key, default) {
            flow.map { it[key] ?: default }.distinctUntilChanged()
        }.collectAsState(initial = flow.value[key] ?: default)

        return remember(key, state) {
            object : MutableState<V> {
                override fun component1() = value
                override fun component2(): (V) -> Unit = { value = it }
                override var value: V
                    get() = state.value
                    set(v) {
                        flow.update {
                            it.toMutableMap().apply { this[key] = v }
                        }
                    }
            }
        }
    }
}