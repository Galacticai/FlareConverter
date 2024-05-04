package com.galacticai.flareconverter.util

import android.content.Context
import global.common.IOUtils.child
import global.common.IOUtils.copyToFile
import global.common.IOUtils.deleteChildrenRecursively
import global.common.IOUtils.orMkdirs
import java.io.File
import java.io.IOException

object AppDefaults {
    val Context.fileProviderUri get() = "$packageName.fileprovider"
    val Context.inputDir: File get() = filesDir.child("input").orMkdirs()
    val Context.outputDir: File get() = filesDir.child("output").orMkdirs()

    fun Context.clearInputDir(recreate: Boolean = false) =
        inputDir.deleteChildrenRecursively(recreate)

    fun Context.clearOutputDir(recreate: Boolean = false) =
        outputDir.deleteChildrenRecursively(recreate)

    /** @return true if it is a file and it exists in the output directory ([outputDir]) */
    @Throws(IOException::class)
    fun Context.canExportFile(file: File, orThrow: Boolean = false): Boolean {
        val isExportable = file.exists() &&
                file.isFile &&
                file.parentFile?.absolutePath == outputDir.absolutePath
        if (!isExportable && orThrow) {
            throw IOException("File cannot be exported: $file. It must be a file in the output directory ($outputDir)")
        }
        return isExportable
    }


    /** Prepare a [file] to be exported outside the app */
    fun Context.exportFile(file: File): File {
        val outFile = outputDir.child(file.name)
        if (!outFile.exists()) file.copyToFile(outFile) // copy if not in out
        return outFile
    }
}