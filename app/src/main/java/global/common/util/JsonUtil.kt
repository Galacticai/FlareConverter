package global.common.util

import org.json.JSONArray
import org.json.JSONObject

object JsonUtil {
    fun <T> JSONArray.toList(
        parser: (i: Int, array: JSONArray) -> T?
    ): List<T> {
        val list = mutableListOf<T>()
        for (i in 0 until this.length()) {
            val parsed = parser(i, this)
                ?: continue
            list.add(parsed)
        }
        return list
    }

    /** take a value from [JSONObject] by [key]. or null on failure */
    inline fun <reified T> JSONObject.find(key: String): T? = runCatching {
        get(key).takeUnless { it === JSONObject.NULL }?.let { it as? T }
    }.getOrNull()

    /** take a value from [JSONArray] by [index]. or null on failure */
    inline fun <reified T> JSONArray.find(index: Int): T? = runCatching {
        get(index).takeUnless { it === JSONObject.NULL }?.let { it as? T }
    }.getOrNull()
}