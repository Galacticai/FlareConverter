package com.galacticai.flareconverter

import com.galacticai.flareconverter.models.FFmpegCommand
import global.common.models.Command
import org.junit.Assert.assertEquals
import org.junit.Test

class Tests {
    @Test
    fun pair() {
        val p = 1 to 5
        assertEquals("(1, 5)", p.toString())
    }

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
            .duration(1000L)

        assertEquals(
            "ffmpeg -i $input $output -vf setpts=${speed}*PTS -af atempo=${1 / speed} -t 0:0:1.0",
            cmd.toString()
        )
    }
}