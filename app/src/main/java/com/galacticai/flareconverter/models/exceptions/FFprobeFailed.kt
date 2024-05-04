package com.galacticai.flareconverter.models.exceptions

/**
 * FFmpeg operation failed
 * @see IllegalStateException
 */
class FFprobeFailed : IllegalStateException {
    constructor() : super(MESSAGE)

    constructor(target: String) :
            super("$MESSAGE: $target")

    constructor(target: Class<*>, element: String) :
            super("$MESSAGE: (${target.name}) $element")

    companion object {
        private const val MESSAGE = "FFprobe operation failed"
    }
}