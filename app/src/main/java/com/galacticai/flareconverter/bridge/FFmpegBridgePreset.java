package com.galacticai.flareconverter.bridge;

import org.bytedeco.javacpp.annotation.Platform;
import org.bytedeco.javacpp.annotation.Properties;
import org.bytedeco.javacpp.tools.InfoMap;
import org.bytedeco.javacpp.tools.InfoMapper;

@Properties(
        target = "com.galacticai.flareconverter.bridge",
        global = "com.galacticai.flareconverter.bridge.global",

        value = {
                @Platform(
                        value = "android-arm",
                        cinclude = "ffmpeg_all.h",
                        includepath = {
                                "src/main/cpp/ffmpeg",
                                "src/main/cpp/ffmpeg/include"
                        },
                        linkpath = "src/main/cpp/ffmpeg/lib/armeabi-v7a",
                        link = {"avdevice", "avfilter", "avformat", "avcodec", "swresample", "swscale", "avutil", "z", "log", "m"},
                        preload = {"avutil", "swresample", "swscale", "avcodec", "avformat", "avfilter", "avdevice"}
                ),

                @Platform(
                        value = "android-arm64",
                        cinclude = "ffmpeg_all.h",
                        includepath = {"src/main/cpp/ffmpeg", "src/main/cpp/ffmpeg/include"},
                        linkpath = "src/main/cpp/ffmpeg/lib/arm64-v8a",
                        link = {"avdevice", "avfilter", "avformat", "avcodec", "swresample", "swscale", "avutil", "z", "log", "m"},
                        preload = {"avutil", "swresample", "swscale", "avcodec", "avformat", "avfilter", "avdevice"}
                ),

                @Platform(
                        value = "android-x86",
                        cinclude = "ffmpeg_all.h",
                        includepath = {"src/main/cpp/ffmpeg", "src/main/cpp/ffmpeg/include"},
                        linkpath = "src/main/cpp/ffmpeg/lib/x86",
                        link = {"avdevice", "avfilter", "avformat", "avcodec", "swresample", "swscale", "avutil", "z", "log", "m"},
                        preload = {"avutil", "swresample", "swscale", "avcodec", "avformat", "avfilter", "avdevice"}
                ),

                @Platform(
                        value = "android-x86_64",
                        cinclude = "ffmpeg_all.h",
                        includepath = {"src/main/cpp/ffmpeg", "src/main/cpp/ffmpeg/include"},
                        linkpath = "src/main/cpp/ffmpeg/lib/x86_64",
                        link = {"avdevice", "avfilter", "avformat", "avcodec", "swresample", "swscale", "avutil", "z", "log", "m"},
                        preload = {"avutil", "swresample", "swscale", "avcodec", "avformat", "avfilter", "avdevice"}
                )
        }
)
public class FFmpegBridgePreset implements InfoMapper {
    @Override
    public void map(InfoMap infoMap) {
        //! intentional: empty (all)
    }
}
