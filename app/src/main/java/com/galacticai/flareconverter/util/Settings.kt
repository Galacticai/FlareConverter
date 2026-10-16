package com.galacticai.flareconverter.util

import com.galacticai.flareconverter.models.ffmpeg.ff_command.args.FFArg
import com.galacticai.flareconverter.models.ffmpeg.ff_command.args.FFmpegArg
import com.galacticai.flareconverter.models.mimes.MimeType
import com.galacticai.flareconverter.models.settings.ObjectSetting
import com.galacticai.flareconverter.models.settings.Setting
import com.galacticai.flareconverter.ui.themes.models.ColorSchemeSpec
import com.galacticai.flareconverter.util.media.MediaCapabilities
import global.common.models.amount.Amount
import global.common.models.amount.affixes.Affix
import global.common.models.amount.units.BaseUnit
import global.common.models.amount.units.BinarySystem
import global.common.util.JsonUtil.toList
import org.json.JSONArray
import org.json.JSONObject
import com.galacticai.flareconverter.util.Resolution as ResolutionClass

/** Various [Setting]s and [ObjectSetting]s used by Flare Converter */
object Settings {
    private fun String.toSettingsKey() = replace("[^a-zA-Z0-9_]".toRegex(), "_")

    /** [ObjectSetting] of the last destination [MimeType] for the given mime type
     * @param fromMimeType source [MimeType] */
    class OutMime(fromMimeType: MimeType) : ObjectSetting<MimeType>(
        keyName = fromMimeType.settingKey,
        defaultObject = defaultObject(fromMimeType),
        toObject = { MimeType.fromJson(JSONObject(it)) },
        fromObject = { it.toJson().toString() },
    ) {
        companion object {
            private val MimeType.settingKey
                get() = "LastSelectedMime_${this.category.toSettingsKey()}"

            private fun defaultObject(mimeType: MimeType) =
                    MediaCapabilities.Supported.mimesByCategory(mimeType.category).first()
        }
    }

    object Theme : ObjectSetting<ColorSchemeSpec>(
        defaultObject = ColorSchemeSpec(false, null),
        toObject = { ColorSchemeSpec.fromJson(it) },
        fromObject = { it.toJson().toString() },
    )

    object ConfigEnabled : ObjectSetting<Array<FFmpegArg>>(
        defaultObject = FFArg.all<FFmpegArg>().values.toTypedArray(),
        toObject = {
            JSONArray(it).toList { i, arr ->
                FFArg.from<FFmpegArg>(arr.getString(i))
            }.toTypedArray()
        },
        fromObject = {
            JSONArray(it.map { arg -> arg.id }).toString()
        },
    )

    object FFmpeg {
        object Defaults {
            val maxSize // 10 GB
                get() = Amount(
                    10.0,
                    BinarySystem.gibi() and BaseUnit(Affix.byte())
                )
        }

        /** 0 = pick from file info */
        object FrameCount : Setting<Int>(0)

        /** 0 = pick from file info */
        object FrameRate : Setting<Int>(0)

        /** 0 = pick from file info */
        object SampleRate : Setting<Int>(0)

        /** 0 = pick from file info */
        object Channels : Setting<Int>(0)

        /** 0 = pick from file info */
        object BitrateVideo : Setting<Double>(0.0)

        /** 0 = pick from file info */
        object BitrateAudio : Setting<Double>(0.0)

        object Resolution : ObjectSetting<ResolutionClass>(
            defaultObject = ResolutionClass.zero,
            toObject = { ResolutionClass.fromJsonString(it) },
            fromObject = { it.toJson().toString() }
        )

        object MaxSize : Setting<Double>(Defaults.maxSize.baseValue)
    }
}