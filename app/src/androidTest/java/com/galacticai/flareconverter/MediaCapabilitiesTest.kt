package com.galacticai.flareconverter

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.galacticai.flareconverter.models.mimes.MimeType
import com.galacticai.flareconverter.util.media.MediaCapabilities
import com.galacticai.flareconverter.util.media.tokenize
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MediaCapabilitiesTest {

    @Test
    fun testSupportedFormatsMimes() {
        val ffmpegFormats = MediaCapabilities.FFmpeg.formats
        val deviceCodecs = MediaCapabilities.Device.codecs

        assertNotNull("FFmpeg formats should not be null", ffmpegFormats)
        assertNotNull("Device codecs should not be null", deviceCodecs)
        assertTrue(
            "FFmpeg formats should not be empty (got ${ffmpegFormats?.size} formats)",
            ffmpegFormats!!.isNotEmpty()
        )

        val supportedMimes = MediaCapabilities.Supported.mimes()
        assertNotNull("Supported mimes should not be null", supportedMimes)
        assertTrue(
            "Should have supported mimes"
                    + "(got ${supportedMimes.size}):"
                    + "${supportedMimes.take(5)}",
            supportedMimes.isNotEmpty()
        )

        Log.d(
            "MediaCapabilitiesTest",
            "FFmpeg formats: ${ffmpegFormats.size}, Device codecs: ${deviceCodecs.size}, Supported mimes: ${supportedMimes.size}"
        )

        // Log supported mimes
        supportedMimes.forEach {
            Log.d("MediaCapabilitiesTest", "+ mime: ${it.mime} (${it.keys})")
        }
    }

    @Test
    fun testSupportedMimesByCategory() {
        val videoMimes = MediaCapabilities.Supported.mimesByCategory(MimeType.VIDEO)
        val audioMimes = MediaCapabilities.Supported.mimesByCategory(MimeType.AUDIO)
        val imageMimes = MediaCapabilities.Supported.mimesByCategory(MimeType.IMAGE)

        assertNotNull("Video mimes should not be null", videoMimes)
        assertNotNull("Audio mimes should not be null", audioMimes)
        assertNotNull("Image mimes should not be null", imageMimes)

        Log.d(
            "MediaCapabilitiesTest",
            "Video: ${videoMimes.size}, Audio: ${audioMimes.size}, Image: ${imageMimes.size}"
        )

        // Verify categories are correct
        videoMimes.forEach { assertTrue("Video mime", it.category == MimeType.VIDEO) }
        audioMimes.forEach { assertTrue("Audio mime", it.category == MimeType.AUDIO) }
        imageMimes.forEach { assertTrue("Image mime", it.category == MimeType.IMAGE) }
    }

    @Test
    fun testMimeTypeFromDynamic() {
        // Test that MimeType.from() works with dynamic format keys
        val ffmpegFormats = MediaCapabilities.FFmpeg.formats ?: return

        // Pick a known format key
        val formatKey = ffmpegFormats.keys.firstOrNull() ?: return

        // Test creating mime types for each category
        for (category in MimeType.CATEGORIES) {
            MediaCapabilities.Supported.getMime("$category/$formatKey")
                ?.also { mime ->
                    assertEquals("Category should match", category, mime.category)
                    assertTrue("Keys should contain format key", mime.keys.contains(formatKey))
                }
        }
    }

    @Test
    fun testMimeTypeConstructor() {
        // Test that MimeType constructor works dynamically
        val mime = MimeType(MimeType.VIDEO, "test_format", "test")

        assertEquals("Category should be video", MimeType.VIDEO, mime.category)
        assertEquals("Key should be test_format", "test_format", mime.key)
        assertEquals("Extension should be test", "test", mime.extension)
        assertEquals("Mime string should be video/test_format", "video/test_format", mime.mime)
    }

    @Test
    fun testTokenize() {
        // Test the tokenize extension function
        assertEquals(setOf("mp4"), "mp4".tokenize())
        assertEquals(setOf("my", "format"), "my_format".tokenize())
        assertEquals(setOf("h", "264"), "h.264".tokenize())
        assertEquals(emptySet<String>(), "".tokenize())
        assertEquals(setOf("a"), "a".tokenize()) // single char kept
    }

    @Test
    fun testSupportedMimesAreFromFFmpegFormats() {
        val ffmpegFormats = MediaCapabilities.FFmpeg.formats ?: return
        val supportedMimes = MediaCapabilities.Supported.mimes()

        // Every supported mime should have at least one key that matches an FFmpeg format key
        for (mime in supportedMimes) {
            val hasMatchingFormat = mime.keys.any { mimeKey ->
                ffmpegFormats.keys.any { formatKey ->
                    formatKey.tokenize().any { token -> mimeKey.tokenize().contains(token) }
                }
            }
            assertTrue("MimeType ${mime.mime} should match FFmpeg format", hasMatchingFormat)
        }
    }

}
