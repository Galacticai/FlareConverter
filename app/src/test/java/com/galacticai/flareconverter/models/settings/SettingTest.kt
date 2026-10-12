package com.galacticai.flareconverter.models.settings

import org.junit.Assert.assertEquals
import org.junit.Test

class SettingTest {

    private class BooleanSetting : Setting<Boolean>(true)
    private class IntSetting : Setting<Int>(10)
    private class LongSetting : Setting<Long>(100L)
    private class FloatSetting : Setting<Float>(1.5f)
    private class DoubleSetting : Setting<Double>(2.5)
    private class StringSetting : Setting<String>("hello")

    @Test
    fun testKeyNameInferenceAndCustom() {
        val defaultSetting = Setting(true)
        assertEquals("Setting.Setting", defaultSetting.keyName)

        val customSetting = Setting(123, customKeyName = "CustomKeyName")
        assertEquals("CustomKeyName", customSetting.keyName)

        val subclassSetting = IntSetting()
        assertEquals("Setting.IntSetting", subclassSetting.keyName)
    }

    @Test
    fun testKeyTypes() {
        val bSetting = BooleanSetting()
        assertEquals("Setting.BooleanSetting", bSetting.key.name)

        val iSetting = IntSetting()
        assertEquals("Setting.IntSetting", iSetting.key.name)

        val lSetting = LongSetting()
        assertEquals("Setting.LongSetting", lSetting.key.name)

        val fSetting = FloatSetting()
        assertEquals("Setting.FloatSetting", fSetting.key.name)

        val dSetting = DoubleSetting()
        assertEquals("Setting.DoubleSetting", dSetting.key.name)

        val sSetting = StringSetting()
        assertEquals("Setting.StringSetting", sSetting.key.name)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testUnsupportedTypeThrowsException() {
        Setting(Any())
    }
}
