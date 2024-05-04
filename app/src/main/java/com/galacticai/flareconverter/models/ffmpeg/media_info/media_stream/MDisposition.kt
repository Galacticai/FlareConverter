package com.galacticai.flareconverter.models.ffmpeg.media_info.media_stream

import com.galacticai.flareconverter.models.ffmpeg.media_info.MediaJson
import global.common.util.JsonUtil.find
import org.json.JSONObject

class MDisposition : MediaJson {
    constructor(json: JSONObject) : super(json)
    constructor(jsonRaw: String) : super(jsonRaw)

    val default by lazy { json.find<Int>("default") }
    val dub by lazy { json.find<Int>("dub") }
    val original by lazy { json.find<Int>("original") }
    val comment by lazy { json.find<Int>("comment") }
    val lyrics by lazy { json.find<Int>("lyrics") }
    val karaoke by lazy { json.find<Int>("karaoke") }
    val forced by lazy { json.find<Int>("forced") }
    val hearingImpaired by lazy { json.find<Int>("hearing_impaired") }
    val visualImpaired by lazy { json.find<Int>("visual_impaired") }
    val cleanEffects by lazy { json.find<Int>("clean_effects") }
    val attachedPic by lazy { json.find<Int>("attached_pic") }
    val timedThumbnails by lazy { json.find<Int>("timed_thumbnails") }
    val nonDiegetic by lazy { json.find<Int>("non_diegetic") }
    val captions by lazy { json.find<Int>("captions") }
    val descriptions by lazy { json.find<Int>("descriptions") }
    val metadata by lazy { json.find<Int>("metadata") }
    val dependent by lazy { json.find<Int>("dependent") }
    val stillImage by lazy { json.find<Int>("still_image") }
    val multilayer by lazy { json.find<Int>("multilayer") }
}
