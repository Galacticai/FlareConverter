package com.galacticai.flareconverter.models.settings

import androidx.compose.runtime.MutableState
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.Serializable

@RunWith(AndroidJUnit4::class)
class SettingInstrumentedTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    data class TestUser(val name: String, val age: Int) : Serializable {
        override fun toString(): String = "$name,$age"
        companion object {
            fun fromString(s: String): TestUser {
                val parts = s.split(",")
                return TestUser(parts[0], parts[1].toInt())
            }
        }
    }

    private class UserObjectSetting(keyName: String) : ObjectSetting<TestUser>(
        defaultObject = TestUser("Default", 0),
        keyName = keyName,
        toObject = { TestUser.fromString(it) }
    )

    @Test
    fun testSettingGetAndSet() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val setting = Setting(42, customKeyName = "TestIntSettingKey")

        // Initial default
        assertEquals(42, setting.get(context))

        // Set value
        setting.set(context, 100)
        assertEquals(100, setting.get(context))

        // Restore default
        setting.restoreDefault(context)
        assertEquals(42, setting.get(context))
    }

    @Test
    fun testSaveInOnePlaceAndGetInAnother() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val keyName = "CrossInstanceKey"

        val settingInstance1 = Setting(10, customKeyName = keyName)
        val settingInstance2 = Setting(10, customKeyName = keyName)

        // Save value in place 1 (instance 1)
        settingInstance1.set(context, 555)

        // Get value in place 2 (instance 2) and verify it updates/reflects the saved value
        assertEquals(555, settingInstance2.get(context))
    }

    @Test
    fun testObjectSettingSaveInOnePlaceAndGetInAnother() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val objectSetting1 = UserObjectSetting("TestUserSetting_SaveAndGet")
        val objectSetting2 = UserObjectSetting("TestUserSetting_SaveAndGet")

        val newUser = TestUser("Bob", 25)
        // Save in place 1
        objectSetting1.setObject(context, newUser)

        // Get in place 2 and verify it updates/reflects the saved object
        assertEquals(newUser, objectSetting2.getObject(context))
    }

    @Test
    fun testObjectSettingGetAndSet() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val objectSetting = UserObjectSetting("TestUserSetting_GetAndSet")

        // Initial default
        assertEquals(TestUser("Default", 0), objectSetting.getObject(context))

        // Set object
        val newUser = TestUser("Alice", 30)
        objectSetting.setObject(context, newUser)
        assertEquals(newUser, objectSetting.getObject(context))

        // Test fallback on invalid stored data
        objectSetting.set(context, "invalid_data_format")
        val fallback = objectSetting.getObject(context)
        assertEquals(TestUser("Default", 0), fallback)
    }

    @Test
    fun testRememberValueAndRememberObjectInCompose() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val intSetting = Setting(100, customKeyName = "ComposeRememberIntKey")
        val userSetting = UserObjectSetting("TestUserSetting_Remember")

        lateinit var intState: MutableState<Int>
        lateinit var userState: MutableState<TestUser>

        composeTestRule.setContent {
            intState = intSetting.rememberValue()
            userState = userSetting.rememberObject()
        }

        // Wait for Compose to apply effects
        composeTestRule.waitForIdle()

        // Verify initial loaded values
        assertEquals(100, intState.value)
        assertEquals(TestUser("Default", 0), userState.value)

        // Update intState value (SettingState automatically saves to DataStore)
        composeTestRule.runOnUiThread {
            intState.value = 999
        }
        composeTestRule.waitForIdle()

        // Verify that setting was saved and can be retrieved from another place
        runBlocking {
            assertEquals(999, intSetting.get(context))
        }

        // Update userState value and persist via setObject
        val updatedUser = TestUser("Charlie", 40)
        composeTestRule.runOnUiThread {
            userState.value = updatedUser
            runBlocking {
                userSetting.setObject(context, updatedUser)
            }
        }
        composeTestRule.waitForIdle()

        // Verify that object setting was saved and can be retrieved from another place
        runBlocking {
            assertEquals(updatedUser, userSetting.getObject(context))
        }
    }
}
