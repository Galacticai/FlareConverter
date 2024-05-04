cmake_minimum_required(VERSION 3.22.1)

project("flareconverter")

add_library(flareconverter SHARED
        native-lib.cpp
)

set(FFMPEG_ROOT
        "${CMAKE_CURRENT_LIST_DIR}/ffmpeg"
)

set(FFMPEG_INCLUDE_DIR
        "${FFMPEG_ROOT}/include"
)

set(FFMPEG_LIB_DIR
        "${FFMPEG_ROOT}/lib/${ANDROID_ABI}"
)

target_include_directories(flareconverter PRIVATE
        "${FFMPEG_INCLUDE_DIR}"
)

target_link_libraries(flareconverter
        "-Wl,--start-group"

        "${FFMPEG_LIB_DIR}/libavfilter.a"
        "${FFMPEG_LIB_DIR}/libavdevice.a"
        "${FFMPEG_LIB_DIR}/libavformat.a"
        "${FFMPEG_LIB_DIR}/libavcodec.a"
        "${FFMPEG_LIB_DIR}/libswresample.a"
        "${FFMPEG_LIB_DIR}/libswscale.a"
        "${FFMPEG_LIB_DIR}/libavutil.a"

        "-Wl,--end-group"

        "-Wl,-z,notext"

        android
        log
        z
        dl
        m
)
