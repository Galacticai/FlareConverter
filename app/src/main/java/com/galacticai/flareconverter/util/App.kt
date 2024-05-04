package com.galacticai.flareconverter.util

import android.content.Context
import global.common.util.IOUtil.child
import java.io.File

/** things that never change (get once) */
object App {
    const val GITHUB = "https://github.com/Galacticai/FlareConverter"
    const val GITHUB_ISSUES = "$GITHUB/issues"
    lateinit var dir: File; private set
    val ffmpeg get() = dir.child("libffmpeg.so")
    val ffprobe get() = dir.child("libffprobe.so")

    internal fun init(context: Context) {
        dir = File(context.applicationInfo.nativeLibraryDir)
    }
}
