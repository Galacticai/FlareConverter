package com.galacticai.flareconverter.util

import java.io.Serializable


open class Resolution(val width: Int, val height: Int) : Serializable {
    object QVGA : Resolution(320, 240)
    object VGA : Resolution(640, 480)
    object HD : Resolution(1280, 720)
    object FHD : Resolution(1920, 1080)
    object QHD : Resolution(2560, 1440)
    object UHD : Resolution(3840, 2160)

    override fun toString() = "${width}x${height}"

    private fun readResolve(): Any = FHD

}