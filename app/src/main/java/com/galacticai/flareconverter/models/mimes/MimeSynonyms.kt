package com.galacticai.flareconverter.models.mimes

import global.common.models.Synonyms

object MimeSynonyms {
    /** key = [MimeType.category]  |  value = [MimeSynonyms] of [MimeType.extension] */
    val extensionsByCategory: Map<String, Synonyms<String>> = mapOf(
        //? add more synonyms
        MimeType.IMAGE to Synonyms(
            setOf("gif"),
            setOf("bmp"),
            setOf("tiff", "tif"),
            setOf("svg"),
            setOf("png", "apng"),
            setOf("jpg", "jpeg"),
            setOf("webp"),
            setOf("heic", "heif"),
        ),

        MimeType.VIDEO to Synonyms(
            setOf("avi"),
            setOf("flv"),
            setOf("webm"),
            setOf("mkv"),
            setOf("mp4", "m4v"),
            setOf("mpeg", "mpg"),
            setOf("mov", "qt"),
            setOf("ogg", "ogv"),
            setOf("wmv"),
            setOf("3gp"),
            setOf("yuv"),
            setOf("m2v"),
        ),

        MimeType.AUDIO to Synonyms(
            setOf("m4a"),
            setOf("mp3"),
            setOf("aac"),
            setOf("wav"),
            setOf("ogg"),
            setOf("flac"),
            setOf("wma"),
            setOf("opus"),
            setOf("3gp"),
            setOf("ac3"),
            setOf("eac3"),
            setOf("dts"),
        )
    )

    /** key = [MimeType.category]  |  value = [MimeSynonyms] of [MimeType.key] */
    val keysByCategory: Map<String, Synonyms<String>> = mapOf(
        MimeType.IMAGE to Synonyms(
            setOf("gif", "gif89a"),
            setOf("bmp", "x-ms-bmp", "bitmap"),
            setOf("tiff", "x-tiff", "tif"),
            setOf("svg", "svg+xml"),
            setOf("png", "apng"),
            setOf("jpg", "jpeg"),
            setOf("webp", "webp_image"),
            setOf("heic", "heif", "hevc"),
        ),

        MimeType.VIDEO to Synonyms(
            setOf("avi", "x-msvideo", "msvideo"),
            setOf("flv", "x-flv"),
            setOf("webm"),
            setOf("mkv", "x-matroska", "matroska"),
            setOf("mpeg", "mpg"),
            setOf("quicktime", "mov", "qt"),
            setOf("ogg", "ogv"),
            setOf("wmv", "x-ms-wmv", "ms-wmv"),
            setOf("3gpp", "h263"),
            setOf("raw", "rawvideo"),
            setOf("h264", "avc", "avc1", "mpeg4_avc"),
            setOf("h265", "hevc", "hvc1", "hev1"),
            setOf("vp8", "vp08"),
            setOf("vp9", "vp09"),
            setOf("av1", "av01"),
            setOf("mpeg2video", "mpeg2", "m2v"),
            setOf("mp4", "mpeg4", "mp4v", "m4v", "x-m4v", "mpeg4video"),
            setOf("mjpeg", "motion_jpeg"),
            setOf("yuv", "yuv420p", "i420", "yv12"),
        ),

        MimeType.AUDIO to Synonyms(
            setOf("mp3", "mpeg", "x-mpeg", "x-mp3", "mpeg1audio", "mpeg_audio"),
            setOf("aac", "aac_lc", "m4a", "x-m4a", "mp4a", "mpeg4_aac"),
            setOf("wav", "x-wav", "wave"),
            setOf("ogg"),
            setOf("flac", "x-flac", "free_lossless_audio_codec"),
            setOf("wma", "x-ms-wma", "ms-wma"),
            setOf("opus", "libopus"),
            setOf("amr", "3gpp", "amrnb", "amr_nb", "amrwb", "amr_wb"),
            setOf("pcm", "g711", "mlaw", "alaw", "mulaw", "pcm_alaw", "pcm_mulaw"),
            setOf("ima", "adpcm"),
            setOf("vorbis", "ogg_vorbis"),
            setOf("ac3", "dolby_digital"),
            setOf("eac3", "ec3", "dolby_digital_plus"),
            setOf("dts", "dca"),
        ),
    )

    private fun expandKeysToExtensions(category: String, keyExtension: Pair<String, String>) =
            keysByCategory[category]!![keyExtension.first]!! to
                    extensionsByCategory[category]!![keyExtension.second]!!

    /**
     * - key = [MimeType.category]
     * - value = set of [Pair]s =>
     *     - first = equivalent to [MimeType.keys]
     *     - second = equivalent to [MimeType.extensions]
     */
    private val keysToExtensions: Map<String, List<Pair<Set<String>, Set<String>>>> = mapOf(
        MimeType.IMAGE to listOf(
            "gif" to "gif",
            "bmp" to "bmp",
            "tiff" to "tif",
            "svg" to "svg",
            "png" to "png",
            "jpeg" to "jpg",
            "webp" to "webp",
            "heic" to "heic",
        ),
        MimeType.VIDEO to listOf(
            "avi" to "avi",
            "flv" to "flv",
            "webm" to "webm",
            "mkv" to "mkv",
            "mpeg" to "mpg",
            "quicktime" to "mov",
            "ogg" to "ogv",
            "wmv" to "wmv",
            "3gpp" to "3gp",
            "raw" to "yuv",
            "h264" to "mp4",
            "h265" to "mp4",
            "vp8" to "webm",
            "vp9" to "webm",
            "av1" to "webm",
            "mpeg2video" to "m2v",
            "mp4" to "mp4",
            "mjpeg" to "avi",
            "yuv" to "yuv",
        ),
        MimeType.AUDIO to listOf(
            "mp3" to "mp3",
            "aac" to "m4a",
            "wav" to "wav",
            "ogg" to "ogg",
            "flac" to "flac",
            "wma" to "wma",
            "opus" to "opus",
            "amr" to "3gp",
            "g711" to "wav",
            "ima" to "wav",
            "vorbis" to "ogg",
            "ac3" to "ac3",
            "eac3" to "eac3",
            "dts" to "dts",
        ),
    ).mapValues { (category, pairs) ->
        pairs.map { pair ->
            expandKeysToExtensions(category, pair)
        }
    }

    fun getKeysByExtension(category: String, extension: String): Set<String>? {
        val keyExtensionPairs = keysToExtensions[category]
        val keys = keyExtensionPairs?.find { (_, extensions) ->
            extension in extensions
        }?.first
        return keys
    }

    fun getExtensionsByKey(category: String, key: String): Set<String>? {
        val keyExtensionPairs = keysToExtensions[category]
        val extensions = keyExtensionPairs?.find { (keys, _) ->
            key in keys
        }?.second
        return extensions
    }
}