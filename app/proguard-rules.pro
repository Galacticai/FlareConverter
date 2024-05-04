# Prevent FFmpegKit classes from being obfuscated or stripped
-keep class com.arthenica.ffmpegkit.** { *; }
-dontwarn com.arthenica.ffmpegkit.**

# Keep FFmpeg native library entry points
-keep class org.ffmpeg.** { *; }
-dontwarn org.ffmpeg.**

# Keep smart-exception classes used by FFmpegKit
-keep class com.arthenica.smartexception.** { *; }
-dontwarn com.arthenica.smartexception.**
