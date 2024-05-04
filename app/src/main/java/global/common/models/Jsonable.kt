package global.common.models

import org.json.JSONObject
import java.io.Serializable

/** Can be converted into a [JSONObject] and to a json [String] */
abstract class Jsonable : Serializable {
    abstract fun toJson(): JSONObject
    override fun toString() = toJson().toString()
}
