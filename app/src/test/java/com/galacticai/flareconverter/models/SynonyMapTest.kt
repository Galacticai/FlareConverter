package com.galacticai.flareconverter.models

import global.common.models.Synonyms
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SynonyMapTest {

    @Test
    fun testSynonyMapOperations() {
        val synMap = Synonyms(
            setOf("3gpp", "amr", "amrnb"),
            setOf("g711", "mlaw", "alaw")
        )

        assertTrue(synMap.contains("amr"))
        assertTrue(synMap.contains("mlaw"))
        assertEquals(setOf("3gpp", "amr", "amrnb"), synMap["amrnb"])
        assertEquals(null, synMap["unknown"])
    }
}
