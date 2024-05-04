package global.common.models.command

sealed interface ProcessEvent {
    data class OutputLine(val line: String) : ProcessEvent
    data class ErrorLine(val line: String) : ProcessEvent
    data class Completed(val exitCode: Int) : ProcessEvent
}