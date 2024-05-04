package com.galacticai.flareconverter.util

import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.models.ffmpeg.ff_command.args.FFmpegArg
import com.galacticai.flareconverter.models.settings.ObjectSetting
import com.galacticai.flareconverter.models.settings.Setting
import com.galacticai.flareconverter.ui.themes.models.ColorSchemeSpec
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
                    MimeType.Outputs.of(mimeType.category)!!.first()
        }
    }

    object Theme : ObjectSetting<ColorSchemeSpec>(
        keyName = "Theme",
        defaultObject = ColorSchemeSpec(false, null),
        toObject = { ColorSchemeSpec.fromJson(it) },
        fromObject = { it.toJson().toString() },
    )

    object ConfigEnabled : ObjectSetting<Array<FFmpegArg>>(
        keyName = "ConfigEnabled",
        defaultObject = FFmpegArg.entries.toTypedArray(),
        toObject = {
            JSONArray(it).toList { i, array ->
                FFmpegArg.from(array.getInt(i))
            }.toTypedArray()
        },
        fromObject = { JSONArray(it.map { arg -> arg.id }).toString() },
    )

    object FFmpeg {
        object Defaults {
            val maxSize // 10 GB
                get() = Amount(
                    10.0,
                    BinarySystem.gibi() and BaseUnit(Affix.byte())
                )

//
//            val bitrates
//                get():List<BitValue> {
//                    fun map(i: Int) = (i + 1) * 100 * BitUnitExponent.Binary.Kibi.toBaseMultiplier
//                    val kb = List(20) { map(it) } +
//                            List(20) { map(it * 10) } +
//                            List(20) { map(it * 100) }
//                    return kb.map {
//                        BitValue(
//                            it,
//                            BitUnit.BasicByte(BitUnitExponent.Binary.Kibi)
//                        )
//                    }
//                }
        }

        /** 0 = pick from file info */
        object FrameCount : Setting<Int>(
            keyName = "FrameCount",
            defaultValue = 0,
        )

        /** 0 = pick from file info */
        object FrameRate : Setting<Int>(
            keyName = "FrameRate",
            defaultValue = 0,
        )

        /** 0 = pick from file info */
        object SampleRate : Setting<Int>(
            keyName = "SampleRate",
            defaultValue = 0,
        )

        /** 0 = pick from file info */
        object Channels : Setting<Int>(
            keyName = "Channels",
            defaultValue = 0,
        )

        /** null = pick from file info */
        object Bitrate : Setting<Double>(
            keyName = "BitrateOverall",
            defaultValue = 0.0,
        )

        /** 0 = pick from file info */
        object BitrateVideo : Setting<Double>(
            keyName = "BitrateVideo",
            defaultValue = 0.0,
        )

        /** 0 = pick from file info */
        object BitrateAudio : Setting<Double>(
            keyName = "BitRateBitrateAudio",
            defaultValue = 0.0,
        )

        object Resolution : ObjectSetting<ResolutionClass>(
            keyName = "Resolution",
            defaultObject = ResolutionClass.zero,
            toObject = { ResolutionClass.fromJsonString(it) },
            fromObject = { it.toJson().toString() }
        )

        object MaxSize : Setting<Double>(
            keyName = "MaxSize",
            defaultValue = Defaults.maxSize.baseValue
        )
    }
}