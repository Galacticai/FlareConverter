package com.galacticai.flareconverter.ui.themes.models

import global.common.models.Jsonable
import org.json.JSONObject

data class ColorSchemeSpec(
    val isDynamic: Boolean,
    /** null = auto */
    val isDark: Boolean?,
    /** [isDynamic] = contrast is specified by system not by [ColorSchemeSpec] */
    val contrast: ColorContrast = ColorContrast.Regular,
) : Jsonable() {
    override fun toJson() = JSONObject().apply {
        put("isDynamic", isDynamic)
        put("isDark", isDark)
        put("contrast", contrast.ordinal)
    }

    companion object {
        fun fromJson(json: JSONObject) = ColorSchemeSpec(
            isDynamic = json.getBoolean("isDynamic"),
            isDark = json.opt("isDark") as? Boolean,
            contrast = ColorContrast.entries[json.getInt("contrast")],
        )

        fun fromJson(json: String) = fromJson(JSONObject(json))
    }
}