package com.galacticai.flareconverter.util

import android.content.Context
import android.content.Intent
import com.galacticai.flareconverter.models.MimeType
import global.common.ActivityUtil
import global.common.IOUtils.child
import global.common.IOUtils.copyToFile
import global.common.IOUtils.deleteChildrenRecursively
import global.common.IOUtils.mime
import global.common.IOUtils.orMkdirs
import java.io.File
import java.io.IOException

object AppDefaults {
    const val INPUT_DIR_NAME = "input"
    const val OUTPUT_DIR_NAME = "output"
    const val FILE_PROVIDER_NAME = "fileprovider"

    val Context.fileProviderUri get() = "$packageName.$FILE_PROVIDER_NAME"

    val Context.inputDir: File get() = cacheDir.child(INPUT_DIR_NAME).orMkdirs()
    val Context.outputDir: File get() = filesDir.child(OUTPUT_DIR_NAME).orMkdirs()

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


    fun Context.copyToOutputDir(file: File): File {
        val outFile = outputDir.child(file.name)
        if (!outFile.exists()) file.copyToFile(outFile) // copy if not in out
        return outFile
    }

    fun Context.openFile(file: File): Intent {
        val outFile = outputDir.child(file.name)
        if (!outFile.exists()) file.copyToFile(outFile) // copy if not in out
        canExportFile(file, orThrow = true)
        return ActivityUtil.openFile(file, this, fileProviderUri)
    }

    /** Share the converted file ([outFile])
     * @param mimeType optional mime type to use when sharing otherwise will try to infer it using [File.mime] */
    fun Context.share(outFile: File, mimeType: MimeType? = null): Intent {
        canExportFile(outFile, orThrow = true)
        val mime = mimeType?.toString() ?: outFile.mime
        return ActivityUtil.share(this, outFile, mime)
    }
}