package com.galacticai.flareconverter.util

import android.content.Context
import global.common.IOUtils.child
import global.common.IOUtils.deleteChildrenRecursively
import global.common.IOUtils.orMkdirs
import java.io.File

object AppDefaults {
    const val INPUT_DIR_NAME = "input"
    const val OUTPUT_DIR_NAME = "output"
    const val FILE_PROVIDER_NAME = "fileprovider"

    val Context.fileprovider get() = "$packageName.$FILE_PROVIDER_NAME"

    val Context.inputDir: File get() = cacheDir.child(INPUT_DIR_NAME).orMkdirs()
    val Context.outputDir: File get() = filesDir.child(OUTPUT_DIR_NAME).orMkdirs()

    fun Context.clearInputDir(recreate: Boolean = false) =
        inputDir.deleteChildrenRecursively(recreate)

    fun Context.clearOutputDir(recreate: Boolean = false) =
        outputDir.deleteChildrenRecursively(recreate)

    fun Context.canShareFile(file: File) =
        file.exists() &&
                file.isFile &&
                file.parentFile?.absolutePath == outputDir.absolutePath
}