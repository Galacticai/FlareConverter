package com.galacticai.flareconverter

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.galacticai.flareconverter.util.media.MediaCapabilities
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        val value = MediaCapabilities.Device.CODECS
        assert(value.isNotEmpty())
    }
}

