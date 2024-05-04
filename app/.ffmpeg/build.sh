#!/usr/bin/env bash
set -euo pipefail

API="${1:-28}"
NDK="${ANDROID_NDK_HOME:?Set ANDROID_NDK_HOME first}"
TOOLCHAIN="$NDK/toolchains/llvm/prebuilt/linux-x86_64"

init_env() {
    case "$ARCH" in
    aarch64)
        TARGET="aarch64-linux-android"
        FFARCH=$ARCH
        CPU="armv8-a"
        ;;
    arm)
        TARGET="armv7a-linux-androideabi"
        FFARCH=$ARCH
        CPU="armv7-a"
        ;;
    x86)
        TARGET="i686-linux-android"
        FFARCH=$ARCH
        CPU="i686"
        ;;
    x86_64)
        TARGET="x86_64-linux-android"
        FFARCH=$ARCH
        CPU="x86-64"
        ;;
    esac
}

build_ffmpeg() {
    ./configure \
        --prefix="$PWD/install" \
        --target-os=android \
        --arch="$FFARCH" \
        --cpu="$CPU" \
        --enable-cross-compile \
        --cc="$TOOLCHAIN/bin/${TARGET}${API}-clang" \
        --cxx="$TOOLCHAIN/bin/${TARGET}${API}-clang++" \
        --ar="$TOOLCHAIN/bin/llvm-ar" \
        --nm="$TOOLCHAIN/bin/llvm-nm" \
        --ranlib="$TOOLCHAIN/bin/llvm-ranlib" \
        --strip="$TOOLCHAIN/bin/llvm-strip" \
        --disable-doc \
        --disable-debug \
        --disable-ffplay \
        --enable-static \
        --disable-shared \
        --enable-pic \
        --disable-asm

    make -j"$(nproc)"
    make install
}

for ARCH in aarch64 arm x86 x86_64; do
    init_env

    echo "Building FFmpeg for $ARCH..."

    rm -rf "./build/$ARCH"
    mkdir -p "./build/$ARCH"
    cp -a ./FFmpeg-n9.0.2/. "./build/$ARCH/"

    pushd "./build/$ARCH" > /dev/null

    build_ffmpeg > /dev/null 2>&1 || RESULT=$?
    RESULT="${RESULT:-0}"

    [ "$RESULT" -eq 0 ] || {
        popd > /dev/null
        echo "Build failed for $ARCH" >&2
        exit "$RESULT"
    }

    popd > /dev/null
    echo "Finished building $ARCH"
done

echo "All architectures built successfully."
