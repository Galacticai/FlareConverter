package com.galacticai.flareconverter.models

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SynonyMapTest {

    @Test
    fun testSynonyMapOperations() {
        val synMap = SynonyMap(
            setOf("3gpp", "amr", "amrnb"),
            setOf("g711", "mlaw", "alaw")
        )

        assertTrue(synMap.contains("amr"))
        assertTrue(synMap.contains("mlaw"))
        assertEquals(setOf("3gpp", "amr", "amrnb"), synMap["amrnb"])
        assertEquals(emptySet<String>(), synMap["unknown"])
    }
}
