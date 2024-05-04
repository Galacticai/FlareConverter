package com.galacticai.flareconverter.models.ffmpeg.media_info.media_stream

import com.galacticai.flareconverter.models.ffmpeg.media_info.MediaJson
import global.common.models.amount.Amount
import global.common.models.amount.units.BaseUnit
import global.common.util.JsonUtil.find
import org.json.JSONObject
import kotlin.time.Duration.Companion.milliseconds

class MStream : MediaJson {
    constructor(json: JSONObject) : super(json)
    constructor(jsonRaw: String) : super(jsonRaw)

    val index by lazy { json.find<Int>("index") }
    val id by lazy { json.find<String>("id") }
    val codecName by lazy { json.find<String>("codec_name") }
    val codecLongName by lazy { json.find<String>("codec_long_name") }
    val profile by lazy { json.find<String>("profile") }
    val codecType by lazy { json.find<String>("codec_type") }
    val codecTagString by lazy { json.find<String>("codec_tag_string") }
    val codecTag by lazy { json.find<String>("codec_tag") }
    val width by lazy { json.find<Int>("width") }
    val height by lazy { json.find<Int>("height") }
    val codedWidth by lazy { json.find<Int>("coded_width") }
    val codedHeight by lazy { json.find<Int>("coded_height") }
    val hasBFrames by lazy { json.find<Int>("has_b_frames") }
    val pixFmt by lazy { json.find<String>("pix_fmt") }
    val level by lazy { json.find<Int>("level") }
    val colorRange by lazy { json.find<String>("color_range") }
    val colorSpace by lazy { json.find<String>("color_space") }
    val colorTransfer by lazy { json.find<String>("color_transfer") }
    val colorPrimaries by lazy { json.find<String>("color_primaries") }
    val chromaLocation by lazy { json.find<String>("chroma_location") }
    val viewIdsAvailable by lazy { json.find<String>("view_ids_available") }
    val viewPosAvailable by lazy { json.find<String>("view_pos_available") }
    val rFrameRate by lazy { json.find<String>("r_frame_rate") }
    val avgFrameRate by lazy { json.find<String>("avg_frame_rate") }
    val timeBase by lazy { json.find<String>("time_base") }
    val startPts by lazy { json.find<Int>("start_pts") }
    val startTime by lazy { json.find<String>("start_time") }
    val nbFrames by lazy { json.find<String>("nb_frames") }
    val extradataSize by lazy { json.find<Int>("extradata_size") }
    val mimeCodecString by lazy { json.find<String>("mime_codec_string") }
    val sampleAspectRatio by lazy {
        json.find<String>("sample_aspect_ratio")?.runCatching {
            val parts = this.split(":").mapNotNull { i -> i.toIntOrNull() }
            parts[0] to parts[1]
        }?.getOrNull()
    }
    val displayAspectRatio by lazy {
        json.find<String>("display_aspect_ratio")?.runCatching {
            val parts = this.split(":").mapNotNull { i -> i.toIntOrNull() }
            parts[0] to parts[1]
        }?.getOrNull()
    }
    val fieldOrder by lazy { json.find<String>("field_order") }
    val isAvc by lazy { json.find<Boolean>("is_avc") }
    val nalLengthSize by lazy { json.find<Int>("nal_length_size") }
    val durationTs by lazy { json.find<Int>("duration_ts") }
    val duration by lazy {
        json.find<String>("duration")?.toDoubleOrNull()?.runCatching {
            (this * 1000).toLong().milliseconds
        }?.getOrNull()
    }

    val sampleRate by lazy { json.find<String>("sample_rate")?.toLongOrNull() }
    val bitRate by lazy {
        json.find<String>("bit_rate")?.toDoubleOrNull()?.runCatching {
            Amount(this, BaseUnit.bitPerSecond())
        }?.getOrNull()
    }

    val bitsPerRawSample by lazy { json.find<Long>("bits_per_raw_sample") }
    val tags by lazy {
        json.find<JSONObject>("tags")?.runCatching {
            MTags(this)
        }?.getOrNull()
    }
    val disposition by lazy {
        json.find<JSONObject>("disposition")?.runCatching {
            MDisposition(this)
        }?.getOrNull()
    }
}

