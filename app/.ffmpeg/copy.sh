cd /mnt/Storage/Projects/repos/FlareConverter || {
    echo "cd failed"
    exit 1
}

FFMPEG_BUILD="app/.ffmpeg/build"
FFMPEG_DEST="app/src/main/cpp/ffmpeg"
BIN_DEST="app/src/main/jniLibs"

rm -rf "$FFMPEG_DEST" "$BIN_DEST"

mkdir -p "$FFMPEG_DEST/include"
mkdir -p "$FFMPEG_DEST/lib"/{arm64-v8a,armeabi-v7a,x86,x86_64}
mkdir -p "$BIN_DEST"/{arm64-v8a,armeabi-v7a,x86,x86_64}

##! headers are ABI-independent
#cp -a "$FFMPEG_BUILD/arm64-v8a/install/include/." \
#      "$FFMPEG_DEST/include/"

for ARCH in arm64-v8a armeabi-v7a x86 x86_64; do
#    # static libs
#    cp "$FFMPEG_BUILD/$ARCH/install/lib/"*.a \
#       "$FFMPEG_DEST/lib/$ARCH/"

    # standalone
    cp "$FFMPEG_BUILD/$ARCH/install/bin/ffmpeg" \
       "$BIN_DEST/$ARCH/libffmpeg.so"

    cp "$FFMPEG_BUILD/$ARCH/install/bin/ffprobe" \
       "$BIN_DEST/$ARCH/libffprobe.so"
done

chmod 755 "$BIN_DEST"/*/libffmpeg.so
chmod 755 "$BIN_DEST"/*/libffprobe.so
