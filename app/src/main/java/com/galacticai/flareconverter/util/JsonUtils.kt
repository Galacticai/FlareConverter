package com.galacticai.flareconverter.util

import org.json.JSONArray
import org.json.JSONObject

object JsonUtils {
    fun <T> array(
        arrayJ: JSONArray,
        parser: (index: Int) -> T
    ): List<T> {
        val list = mutableListOf<T>()
        for (i in 0 until arrayJ.length()) {
            list.add(parser(i))
        }
        return list.toList()
    }

    fun <T> array(
        obj: JSONObject, arrayKey: String,
        parser: (arrayJ: JSONArray, index: Int) -> T
    ): List<T> {
        val arrayJ = obj.getJSONArray(arrayKey)
        return array(arrayJ) {
            parser(arrayJ, it)
        }
    }

    fun arrayStrings(
        obj: JSONObject, arrayKey: String
    ): List<String> {
        return array(obj, arrayKey) { a, i ->
            a.getString(i)
        }
    }

    fun arrayStrings(arrayJ: JSONArray) =
        array(arrayJ) { arrayJ.getString(it) }
}