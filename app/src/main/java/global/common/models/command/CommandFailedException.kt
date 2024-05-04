package global.common.models.command

class CommandFailedException(
    val command: String,
    val exitCode: Int,
    val error: String,
) : Exception(
    buildString {
        append("Command failed with exit code $exitCode")
        if (error.isNotBlank()) {
            append(": ")
            append(error.trim())
        }
        append("\nCommand: ")
        append(command)
    }
)