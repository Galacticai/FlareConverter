package com.galacticai.flareconverter.util

import android.content.Context
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.models.settings.ObjectSetting
import com.galacticai.flareconverter.models.settings.Setting
import global.common.models.bitvalue.BitUnit
import global.common.models.bitvalue.BitUnitBase
import global.common.models.bitvalue.BitUnitExponent
import global.common.models.bitvalue.BitValue
import org.json.JSONObject

/** Various [Setting]s and [ObjectSetting]s used by Flare Converter */
object Settings {
    private fun String.toKey() = replace("[^a-zA-Z0-9_]".toRegex(), "_")

    /** [ObjectSetting] of the last destination [MimeType] for the given mime type
     * @param fromMimeType source [MimeType] */
    class OutMime(fromMimeType: MimeType) : ObjectSetting<MimeType>(
        keyName = key(fromMimeType),
        defaultObject = defaultObject(fromMimeType)
    ) {
        override suspend fun getObject(context: Context) =
            MimeType.fromJson(JSONObject(get(context)))
        override suspend fun setObject(context: Context, value: MimeType) =
            super.set(context, value.toJson().toString())

        companion object {
            private fun key(mimeType: MimeType): String {
                val category = mimeType.category.toKey()
                return "LastSelectedMime_$category"
            }

            private fun defaultObject(mimeType: MimeType) =
                MimeTypeUtils.getConvertibleList(mimeType.mime)!!.first()
        }
    }

    object FFmpeg {
        object Defaults {
            const val BIT_RATE = 1024

            // val resolution get() = -1 to -1
            val maxSize // 10 GB
                get() = BitValue(
                    10f,
                    BitUnit(BitUnitExponent.Metric.Giga, BitUnitBase.Byte)
                )
        }

        object BitRate : Setting<Int>(
            keyName = "LastBitRate",
            defaultValue = Defaults.BIT_RATE,
        )

//        object Resolution : ObjectSetting<Pair<Int, Int>>(
//            keyName = "Resolution",
//            defaultObject = Defaults.resolution
//        ) {
//            override suspend fun getObject(context: Context): Pair<Int, Int> {
//                val v = get(context) // "(first, second)"
//                val parts = v.substring(1, v.lastIndex - 1)
//                    .split(", ")
//                return parts[0].toInt() to parts[1].toInt()
//            }
//        }

        object MaxSize : Setting<BitValue>(
            keyName = "MaxSize",
            defaultValue = Defaults.maxSize
        )
    }
}