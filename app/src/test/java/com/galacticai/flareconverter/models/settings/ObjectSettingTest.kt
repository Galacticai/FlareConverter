package com.galacticai.flareconverter.models.settings

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.Serializable

class ObjectSettingTest {

    data class SampleData(val id: Int, val name: String) : Serializable {
        override fun toString(): String = "$id:$name"
        companion object {
            fun fromString(s: String): SampleData {
                val parts = s.split(":")
                if (parts.size != 2) throw IllegalArgumentException("Invalid format")
                return SampleData(parts[0].toInt(), parts[1])
            }
        }
    }

    private class TestObjectSetting(defaultObj: SampleData) : ObjectSetting<SampleData>(
        defaultObject = defaultObj,
        keyName = "TestObjectSettingKey",
        fromObject = { it.toString() },
        toObject = { SampleData.fromString(it) }
    )

    @Test
    fun testObjectSettingSerializationAndDeserialization() {
        val initial = SampleData(1, "Flare")
        val setting = TestObjectSetting(initial)

        assertEquals("TestObjectSettingKey", setting.keyName)
        assertEquals("1:Flare", setting.defaultObject.toString())

        val serialized = setting.fromObject(initial)
        assertEquals("1:Flare", serialized)

        val deserialized = setting.toObject(serialized)
        assertEquals(initial, deserialized)
    }
}
