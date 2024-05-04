package com.galacticai.flareconverter.models.ffmpeg.media_info.media_stream

import com.galacticai.flareconverter.models.ffmpeg.media_info.MediaJson
import global.common.util.JsonUtil.find
import global.common.util.JsonUtil.toList
import org.json.JSONArray
import org.json.JSONObject

class MStreamGroup : MediaJson {
    constructor(json: JSONObject) : super(json)
    constructor(jsonRaw: String) : super(jsonRaw)

    val idby by lazy { json.find<String>("id") }
    val indexby by lazy { json.find<Int>("index") }
    val nbStreamsby by lazy { json.find<Int>("nb_streams") }
    val typeby by lazy { json.find<String>("type") }
    val components by lazy {
        json.find<JSONArray>("components")?.runCatching {
            this.toList { i, arr ->
                val value = arr.find<JSONObject>(i)
                    ?: return@toList null
                MStreamComponent(value)
            }
        }?.getOrNull()
    }
    val disposition by lazy {
        json.find<JSONObject>("disposition")?.runCatching {
            MDisposition(this)
        }?.getOrNull()
    }
    val streams by lazy {
        json.find<JSONArray>("streams")?.runCatching {
            this.toList { i, arr ->
                val value = arr.find<JSONObject>(i)
                    ?: return@toList null
                MStream(value)
            }
        }?.getOrNull()
    }
}