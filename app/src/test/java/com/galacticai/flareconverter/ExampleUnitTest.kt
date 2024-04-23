package com.galacticai.flareconverter

import com.galacticai.flareconverter.models.FFmpegCommand
import global.common.models.Command
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration
import java.time.temporal.ChronoUnit

class Tests {
    @Test
    fun command() {
        val cmd = Command(
            "bin",
            Command.Argument(
                Command.Argument.Key("help", Command.Argument.PREFIX_DOUBLE),
                "more"
            ),
            Command.Argument(
                Command.Argument.Key("a", Command.Argument.PREFIX),
                500.toString(), "big"
            )
        )
        assertEquals("bin --help more -a 500 big", cmd.toString())
    }

    @Test
    fun commandFFmpeg() {
        val cmd = FFmpegCommand()
        assertEquals("ffmpeg", cmd.toString())

        val speed = 2f
        val input = "in.avi"
        val output = "out.mp4"

        cmd.io(input, output)
            .speedVideoAudio(speed)
            .duration(Duration.of(1000L, ChronoUnit.MILLIS))

        assertEquals(
            "ffmpeg -i $input $output -vf setpts=${speed}*PTS -af atempo=${1 / speed} -t 0:0:1.0",
            cmd.toString()
        )
    }
}