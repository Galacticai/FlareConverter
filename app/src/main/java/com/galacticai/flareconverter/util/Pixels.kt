package com.galacticai.flareconverter.util

import global.common.util.NumberUtil.nearest

enum class Pixels(val p: Int) {
    P144(144),
    P240(240),
    P256(256),
    P360(360),
    P480(480),
    P640(640),
    P720(720),
    P768(768),
    P1080(1080),
    P1280(1280),
    P1366(1366),
    P1440(1440),
    P1920(1920),
    P2160(2160),
    P2560(2560),
    P3840(3840),
    P4320(4320),
    P7680(7680);


    override fun toString() = p.toString()

    companion object {
        fun from(p: Int): Pixels? = entries.find { it.p == p }
        fun nearest(p: Int): Pixels =
            from(entries.map { it.p }.nearest(p))!!
    }
}