package com.galacticai.flareconverter.models.ffmpeg.media_info.media_stream

import com.galacticai.flareconverter.models.ffmpeg.media_info.MediaJson
import global.common.util.JsonUtil.find
import global.common.util.JsonUtil.toList
import org.json.JSONArray
import org.json.JSONObject

class MStreamComponent : MediaJson {
    constructor(json: JSONObject) : super(json)
    constructor(jsonRaw: String) : super(jsonRaw)

    val nbTiles by lazy { json.find<Int>("nb_tiles") }
    val horizontalOffset by lazy { json.find<Int>("horizontal_offset") }
    val verticalOffset by lazy { json.find<Int>("vertical_offset") }
    val width by lazy { json.find<Int>("width") }
    val height by lazy { json.find<Int>("height") }
    val codedWidth by lazy { json.find<Int>("coded_width") }
    val codedHeight by lazy { json.find<Int>("coded_height") }
    val subcomponents by lazy {
        json.find<JSONArray>("subcomponents")?.runCatching {
            this.toList { i, arr ->
                val value = arr.find<JSONObject>(i)
                    ?: return@toList null
                SubComponent(value)
            }
        }?.getOrNull()
    }
    val sideDataList by lazy {
        json.find<JSONArray>("side_data_list")?.runCatching {
            this.toList { i, arr ->
                val value = arr.find<JSONObject>(i)
                    ?: return@toList null
                SideData(value)
            }
        }?.getOrNull()
    }


    data class SubComponent(private val json: JSONObject) {
        val streamIndex by lazy { json.find<Int>("stream_index") }
        val tileHorizontalOffset by lazy { json.find<Long>("tile_horizontal_offset") }
        val tileVerticalOffset by lazy { json.find<Long>("tile_vertical_offset") }
    }

    data class SideData(private val json: JSONObject) {
        val sideDataType by lazy { json.find<String>("side_data_type") }
        val size by lazy { json.find<Int>("size") }
    }
}