package com.galacticai.flareconverter.util

import com.galacticai.flareconverter.models.MimeType

object MimeTypeUtils {
    val MimeType.dashed get() = "$category — $keys" //? yes I write em dash, not AI

    val regex = Regex(
        """^(?<category>\*|[a-z0-9\-+.]+)/(?<key>\*|[a-z0-9\-+.]+)$""",
        RegexOption.IGNORE_CASE
    )
    val String.isMimeType get() = regex.matches(this)

    /** Get the [Settings.OutMime] for this [MimeType] */
    val MimeType.outMimeSetting get() = Settings.OutMime(this)
}