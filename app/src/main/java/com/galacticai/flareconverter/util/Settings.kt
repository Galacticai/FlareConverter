package com.galacticai.flareconverter.util

import android.content.Context
import android.util.Log
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.models.settings.ObjectSetting
import com.galacticai.flareconverter.models.settings.Setting
import global.common.models.amount.Amount
import global.common.models.amount.affixes.Affix
import global.common.models.amount.units.BaseUnit
import global.common.models.amount.units.BinarySystem
import org.json.JSONObject
import com.galacticai.flareconverter.util.Resolution as ResolutionClass

/** Various [Setting]s and [ObjectSetting]s used by Flare Converter */
object Settings {
    private fun String.toSettingsKey() = replace("[^a-zA-Z0-9_]".toRegex(), "_")

    /** [ObjectSetting] of the last destination [MimeType] for the given mime type
     * @param fromMimeType source [MimeType] */
    class OutMime(fromMimeType: MimeType) : ObjectSetting<MimeType>(
        keyName = fromMimeType.settingKey,
        defaultObject = defaultObject(fromMimeType)
    ) {
        override suspend fun getObject(context: Context): MimeType {
            val value = get(context)
            return try {
                val json = JSONObject(value)
                MimeType.fromJson(json)
            } catch (e: Exception) {
                Log.e(
                    "Settings.OutMime",
                    "Resetting to default ($keyName). Invalid json: $value",
                    e
                )
                setObject(context, defaultObject)
                defaultObject
            }
        }

        override suspend fun setObject(context: Context, value: MimeType) =
            super.set(context, value.toJson().toString())


        companion object {
            private val MimeType.settingKey
                get() = "LastSelectedMime_${this.category.toSettingsKey()}"

            private fun defaultObject(mimeType: MimeType) =
                MimeType.Outputs.of(mimeType.category)!!.first()
        }
    }

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
            defaultObject = ResolutionClass.Zero
        ) {
            override suspend fun getObject(context: Context): ResolutionClass {
                val v = get(context)
                return ResolutionClass.fromJsonString(v)
            }
        }

        object MaxSize : Setting<Double>(
            keyName = "MaxSize",
            defaultValue = Defaults.maxSize.baseValue
        )
    }
}