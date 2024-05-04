package global.common.models.command

import java.io.File

data class RunConfig(
    val environment: Map<String, String>? = null,
    val workingDir: File? = null,
)