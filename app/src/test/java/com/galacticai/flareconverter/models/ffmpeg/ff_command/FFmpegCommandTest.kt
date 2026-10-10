package com.galacticai.flareconverter.models.ffmpeg.ff_command

import com.galacticai.flareconverter.models.ffmpeg.ff_command.FFmpegCommand.Companion.toCommand
import com.galacticai.flareconverter.models.ffmpeg.ff_command.args.FFmpegArg
import com.galacticai.flareconverter.util.App
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File

class FFmpegCommandTest {

    @Before
    fun setUp() = try {
        val field = App::class.java.getDeclaredField("dir")
        field.isAccessible = true
        field.set(null, File("/fake/path"))
    } catch (_: Throwable) {
    }

    @Test
    fun testFFmpegCommandFormattingAndSorting() {
        val argsMap = mapOf(
            FFmpegArg.Output to "output.avi",
            FFmpegArg.Input to "input.mp4",
            FFmpegArg.Resolution to "203x360",
        )

        val cmd = argsMap.toCommand()
        val tokens = cmd.tokens

        assertEquals(
            listOf(
                App.ffmpeg.absolutePath,
                "-hide_banner",
                "-stats",
                "-v", "error",
                "-i", "input.mp4",
                "-s", "203x360",
                "output.avi"
            ),
            tokens
        )

        assertEquals(
            "${App.ffmpeg.absolutePath} -hide_banner -stats -v error -i input.mp4 -s 203x360 output.avi",
            cmd.toString()
        )
    }

    @Test
    fun testOutputArgumentDoesNotAddDash() {
        val cmd = FFmpegCommand()
            .hideBanner()
            .input("input.mp4")
            .resolution(203, 360)
            .output("output.avi")

        val tokens = cmd.tokens

        assertEquals(
            listOf(
                App.ffmpeg.absolutePath,
                "-hide_banner",
                "-i", "input.mp4",
                "-s", "203x360",
                "output.avi"
            ),
            tokens
        )
    }

    @Test
    fun testArgOrderSortingWhenAddedOutOfOrder() {
        val cmd = FFmpegCommand()
            .output("output.avi")
            .resolution(203, 360)
            .frameRate(30)
            .input("input.mp4")
            .hideBanner()

        val tokens = cmd.tokens

        assertEquals(
            listOf(
                App.ffmpeg.absolutePath,
                "-hide_banner",
                "-i", "input.mp4",
                "-r", "30",
                "-s", "203x360",
                "output.avi"
            ),
            tokens
        )
    }

    @Test
    fun testMultipleAfterInputArgumentsPlacedBeforeOutput() {
        val argsMap = mapOf(
            FFmpegArg.Output to "output.mp4",
            FFmpegArg.Preset to "fast",
            FFmpegArg.CodecVideo to "libx264",
            FFmpegArg.Input to "input.mkv",
            FFmpegArg.BitrateVideo to "2000k"
        )

        val cmd = argsMap.toCommand()
        val tokens = cmd.tokens

        assertEquals(
            listOf(
                App.ffmpeg.absolutePath,
                "-hide_banner",
                "-stats",
                "-v", "error",
                "-i", "input.mkv",
                "-b:v", "2000k",
                "-c:v", "libx264",
                "-preset", "fast",
                "output.mp4"
            ),
            tokens
        )
    }

    @Test
    fun testFFmpegArgNameUsesObjectNameNotCompanion() {
        assertEquals("Version", FFmpegArg.Version.name)
    }
}
