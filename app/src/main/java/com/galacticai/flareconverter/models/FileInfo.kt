package com.galacticai.flareconverter.models

import android.system.Os
import android.system.StructStat
import global.common.models.amount.Amount
import global.common.models.amount.units.BaseUnit
import global.common.models.amount.units.BinarySystem
import java.io.File
import java.util.Date

data class FileInfo(
    val file: File,
) {
    val stat: StructStat by lazy { Os.stat(file.absolutePath) }


    @Deprecated("IOUtil > stream copy > does not copy dates so the date will always be *now* not the real file date")
    val createdAt by lazy { Date(stat.st_ctime * 1000L) }

    @Deprecated("IOUtil > stream copy > does not copy dates so the date will always be *now* not the real file date")
    val modifiedAt by lazy { Date(stat.st_mtime * 1000L) }

    val size by lazy {
        Amount(stat.st_size.toDouble(), BaseUnit.byte())
            .toUnit(BinarySystem.mebi() and BaseUnit.byte())
    }
}
