#include <jni.h>
#include <string>

extern "C" {
#include <libavutil/avutil.h>
#include <libavformat/avformat.h>
#include <libavcodec/avcodec.h>
#include <libavcodec/codec_desc.h>
}

class MediaInfo {
    long width;
    long height;
    long bitrate;
    long frameCount;
    int frameRate;

};
//
//extern "C" JNIEXPORT MediaInfo JNICALL
//Java_com_galacticai_flareconverter_bridge_FFmpegBridge_getMediaInfo(
//        JNIEnv *env, jobject, jstring path
//) {
//    AVFormatContext *ctx = nullptr;
//
//    char *pathChar = (char *) env->GetStringUTFChars(path, nullptr);
//
//    if (avformat_open_input(&ctx, pathChar, nullptr, nullptr) < 0)
//        return {};
//
//    if (avformat_find_stream_info(ctx, nullptr) < 0) {
//        avformat_close_input(&ctx);
//        return {};
//    }
//
//    for (unsigned int i = 0; i < ctx->nb_streams; i++) {
//        AVCodecParameters *codecpar = ctx->streams[i]->codecpar;
//
//        if (codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
//            int width = codecpar->width;
//            int height = codecpar->height;
//
//            avformat_close_input(&ctx);
//
//            // width and height are your resolution
//            return width * 100000 + height;
//        }
//    }
//
//
//    return {};
//}
