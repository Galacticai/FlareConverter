package com.galacticai.flareconverter.models.ffmpeg.ff_command

enum class LogLevel(val key: String) {
    Quiet("quiet"),
    Panic("panic"),
    Fatal("fatal"),
    Error("error"),
    Warning("warning"),
    Info("info"),
    Verbose("verbose"),
    Debug("debug"),
    Trace("trace")
}
