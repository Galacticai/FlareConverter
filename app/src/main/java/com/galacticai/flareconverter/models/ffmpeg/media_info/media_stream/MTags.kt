package com.galacticai.flareconverter.models.ffmpeg.media_info.media_stream

import com.galacticai.flareconverter.models.ffmpeg.media_info.MediaJson
import global.common.util.JsonUtil.find
import org.json.JSONObject
import kotlin.time.Instant

class MTags : MediaJson {
    constructor(json: JSONObject) : super(json)
    constructor(jsonRaw: String) : super(jsonRaw)

    val language by lazy { json.find<String>("language") }
    val handlerName by lazy { json.find<String>("handler_name") }

    val creationTime by lazy {
        json.find<String>("creation_time")?.runCatching {
            Instant.parse(this)
        }?.getOrNull()
    }

    val majorBrand by lazy { json.find<String>("major_brand") }
    val minorVersion by lazy { json.find<String>("minor_version") }
    val compatibleBrands by lazy { json.find<String>("compatible_brands") }
}