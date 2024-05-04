package com.galacticai.flareconverter.models.ffmpeg.media_info

import global.common.models.Jsonable
import org.json.JSONObject

abstract class MediaJson(protected open val json: JSONObject) : Jsonable() {
    constructor(jsonRaw: String) : this(JSONObject(jsonRaw))

    override fun toJson() = JSONObject(json.toString())
}