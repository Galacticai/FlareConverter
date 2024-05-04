package com.galacticai.flareconverter.models.ffmpeg.media_info

import com.galacticai.flareconverter.models.ffmpeg.media_info.media_stream.MFormat
import com.galacticai.flareconverter.models.ffmpeg.media_info.media_stream.MStream
import com.galacticai.flareconverter.models.ffmpeg.media_info.media_stream.MStreamGroup
import global.common.util.JsonUtil.find
import global.common.util.JsonUtil.toList
import org.json.JSONArray
import org.json.JSONObject

/**
 * ⚠️ for now only supports the following command (max)
 * ```bash
 * ffprobe \
 *     -v quiet \
 *     -print_format json=compact=1 \
 *     -show_streams -show_stream_groups -show_format \
 *     -i file.ext
 * ```
 * @see MFormat
 * @see MStream
 * @see MStreamGroup
 */
class MediaInfo : MediaJson {
    constructor(json: JSONObject) : super(json)
    constructor(jsonRaw: String) : super(jsonRaw)

    val streamGroups by lazy {
        json.find<JSONArray>("stream_groups")?.runCatching {
            this.toList { i, arr ->
                val value = arr.find<JSONObject>(i)
                    ?: return@toList null
                MStreamGroup(value)
            }
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

    val format by lazy {
        json.find<JSONObject>("format")?.runCatching {
            MFormat(this)
        }?.getOrNull()
    }
}