package com.galacticai.flareconverter.models.exceptions

import com.galacticai.flareconverter.models.ffmpeg.FFmpegArg

/**
 * The selected FFmpegArg has is not viewable in the given context
 * @see IllegalStateException
 */
class FFmpegArgNoView : IllegalStateException {
    constructor() : super(MESSAGE)

    constructor(target: String) :
            super("$MESSAGE: $target")

    constructor(target: Class<*>, element: String) :
            super("$MESSAGE: (${target.name}) $element")

    constructor(arg: FFmpegArg) : super("$MESSAGE: $arg")

    companion object {
        private const val MESSAGE =
            "The selected FFmpegArg has is not viewable in the given context"
    }
}