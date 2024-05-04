package com.galacticai.flareconverter.models.ffmpeg.media_info.media_stream

import com.galacticai.flareconverter.models.ffmpeg.media_info.MediaJson
import global.common.models.amount.Amount
import global.common.models.amount.units.BaseUnit
import global.common.util.JsonUtil.find
import org.json.JSONObject
import kotlin.time.Duration.Companion.milliseconds

class MFormat : MediaJson {
    constructor(json: JSONObject) : super(json)
    constructor(jsonRaw: String) : super(jsonRaw)

    val filename by lazy { json.find<String>("filename") }
    val nbStreams by lazy { json.find<Int>("nb_streams") }
    val nbPrograms by lazy { json.find<Int>("nb_programs") }
    val nbStreamGroups by lazy { json.find<Int>("nb_stream_groups") }
    val formatName by lazy { json.find<String>("format_name") }
    val formatLongName by lazy { json.find<String>("format_long_name") }
    val startTime by lazy { json.find<String>("start_time")?.toFloatOrNull() }
    val size by lazy {
        json.find<String>("size")?.toDoubleOrNull()?.runCatching {
            Amount(this, BaseUnit.byte())
        }?.getOrNull()
    }
    val bitRate by lazy {
        json.find<String>("bit_rate")?.toDoubleOrNull()?.runCatching {
            Amount(this, BaseUnit.bitPerSecond())
        }?.getOrNull()
    }
    val duration by lazy {
        json.find<String>("duration")?.toDoubleOrNull()?.runCatching {
            (this * 1000).toLong().milliseconds
        }?.getOrNull()
    }
    val probeScore by lazy { json.find<Int>("probe_score") }
    val tags by lazy {
        json.find<JSONObject>("tags")?.runCatching {
            MTags(this)
        }?.getOrNull()
    }
}