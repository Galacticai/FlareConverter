package global.common.models

import org.json.JSONObject

/** Can be converted into a [JSONObject] */
interface Jsonable {
    fun toJson(): JSONObject
}

/** Can be converted into a [String] through [Jsonable.toJson] */
abstract class JsonableString : Jsonable {
    fun toJsonString(): String = toJson().toString()
}
