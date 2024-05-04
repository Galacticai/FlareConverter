package com.galacticai.flareconverter.models.exceptions

/**
 * FFmpeg operation failed
 * @see IllegalStateException
 */
sealed class FFCommandFailed : IllegalStateException {
    constructor(executable: String) : super("$executable $MESSAGE")

    constructor(executable: String, target: String) :
            super("$executable $MESSAGE: $target")

    constructor(executable: String, target: Class<*>, element: String) :
            super("$executable $MESSAGE: (${target.name}) $element")

    companion object {
        private const val MESSAGE = "operation failed"
    }
}