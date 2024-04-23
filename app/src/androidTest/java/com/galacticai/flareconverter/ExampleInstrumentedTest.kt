package com.galacticai.flareconverter

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.galacticai.flareconverter.ui.theme.GalacticTheme
import org.json.JSONObject
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun useAppContext() {
        composeTestRule.setContent {
            val mp4 by remember { mutableStateOf(MimeType.Mp4) }

            val mp4Json = mp4.toJson()
            val mp4JsonString = mp4Json.toString()
            val mp4Json2 = JSONObject(mp4JsonString)
            val mp4JsonString2 = mp4Json2.toString()
            assert(mp4JsonString == mp4JsonString2)
            val mp4FromJson = MimeType.fromJson(mp4Json2)
            val mp4j by remember { mutableStateOf(mp4FromJson) }

            assert(mp4.convertibleTo.size == mp4j.convertibleTo.size)
            mp4.convertibleTo.forEachIndexed { i, it ->
                val j = mp4j.convertibleTo[i]
                assert(it.mime == j.mime)
            }

            GalacticTheme {
                mp4.convertibleTo.forEach {
                    Text(it.mime)
                }
                mp4j.convertibleTo.forEach {
                    Text(it.mime)
                }
            }
        }
    }
}