package com.galacticai.flareconverter.models.exceptions

/** Activity or fragment was launched with an invalid intent or arguments
 * @see IllegalStateException */
class InvalidLaunchCommand : IllegalStateException {
    constructor() : super(MESSAGE)

    constructor(target: String) :
            super("$MESSAGE: $target")

    constructor(target: Class<*>, element: String) :
            super("$MESSAGE: (${target.name}) $element")

    companion object {
        private const val MESSAGE =
            "Activity or fragment was launched with an invalid intent or arguments"
    }
}