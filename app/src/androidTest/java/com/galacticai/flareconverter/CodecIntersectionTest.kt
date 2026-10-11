package com.galacticai.flareconverter

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.galacticai.flareconverter.util.media.MediaCapabilities
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CodecIntersectionTest {

    @Test
    fun testDynamicCodecIntersection() {
        val ffmpegCodecs = MediaCapabilities.FFmpeg.codecs
        val deviceCodecs = MediaCapabilities.Device.codecs

        assertNotNull("FFmpeg codecs should not be null", ffmpegCodecs)
        assertNotNull("Device codecs should not be null", deviceCodecs)

        // Dynamically intersect using MediaCapabilities.Intersect.codecs(...)
        val intersectedCodecs = MediaCapabilities.Intersect.codecs(null)
        val intersectedEncoders = MediaCapabilities.Intersect.codecs(true)
        val intersectedDecoders = MediaCapabilities.Intersect.codecs(false)

        assertNotNull("Intersected codecs should not be null", intersectedCodecs)
        assertNotNull("Intersected encoders should not be null", intersectedEncoders)
        assertNotNull("Intersected decoders should not be null", intersectedDecoders)

        Log.d(
            "CodecIntersectionTest",
            "FFmpeg: ${ffmpegCodecs?.size}, Device: ${deviceCodecs.size}, Intersected: ${intersectedCodecs?.size}, Enc: ${intersectedEncoders?.size}, Dec: ${intersectedDecoders?.size}"
        )

        intersectedCodecs?.keys?.forEach {
            Log.d("CodecIntersectionTest", "+ $it")
        }

        val unmatchedDeviceCodecs = deviceCodecs.filter { deviceCodec ->
            val mimeKey = deviceCodec.mime.key?.lowercase() ?: ""
            val name = deviceCodec.info.name.lowercase()

            val deviceTokens = buildSet {
                addAll(mimeKey.split(Regex("[^a-z0-9]+")).filter { it.length > 1 })
                addAll(name.split(Regex("[^a-z0-9]+")).filter { it.length > 1 })
            }

            intersectedCodecs?.none { (key, codec) ->
                val codecTokens = buildSet {
                    addAll(key.lowercase().split(Regex("[^a-z0-9]+")).filter { it.length > 1 })
                    addAll(
                        codec.description.lowercase().split(Regex("[^a-z0-9]+"))
                            .filter { it.length > 1 })
                }
                (deviceTokens.intersect(codecTokens)).isNotEmpty()
            } ?: true
        }.map { "${it.mime.mime} (${it.info.name})" }.distinct()

        unmatchedDeviceCodecs.forEach {
            Log.d("CodecIntersectionTest", "X $it")
        }
    }
}

/* log:
FFmpeg: 539, Device: 103, Intersected: 216, Enc: 29, Dec: 216
+ 8bps
+ amv
+ apv
+ argo
+ av1
+ avs
+ bethsoftvid
+ binkvideo
+ bmv_video
+ cavs
+ cdgraphics
+ cdtoons
+ cdxl
+ cmv
+ cpia
+ cri
+ dds
+ dsicinvideo
+ dvvideo
+ ffv1
+ flashsv
+ flashsv2
+ flic
+ flv1
+ gdv
+ h263
+ h264
+ hevc
+ hnm4video
+ idcin
+ indeo4
+ indeo5
+ interplayvideo
+ ipu
+ jv
+ kgv1
+ kmvc
+ mad
+ magicyuv
+ mdec
+ mmvideo
+ mobiclip
+ motionpixels
+ mpeg1video
+ mpeg2video
+ mpeg4
+ msa1
+ msmpeg4v1
+ msmpeg4v2
+ msmpeg4v3
+ mss1
+ mss2
+ msvideo1
+ mts2
+ mvc1
+ mvc2
+ mxpeg
+ paf_video
+ pdv
+ prores_raw
+ prosumer
+ qtrle
+ rawvideo
+ rl2
+ roq
+ rpza
+ rtv1
+ sanm
+ sga
+ simbiosis_imx
+ smackvideo
+ smvjpeg
+ svq1
+ svq3
+ tgq
+ tgv
+ thp
+ tiertexseqvideo
+ tqi
+ utvideo
+ vc1
+ vc1image
+ vmdvideo
+ vmix
+ vmnc
+ vnull
+ vp3
+ vp4
+ vp5
+ vp6
+ vp6a
+ vp6f
+ vp7
+ vp8
+ vp9
+ vvc
+ wcmv
+ wmv1
+ wmv2
+ wmv3
+ wmv3image
+ ws_vqa
+ yop
+ zerocodec
+ zmbv
+ aac
+ aac_latm
+ ac3
+ adpcm_4xm
+ adpcm_adx
+ adpcm_afc
+ adpcm_agm
+ adpcm_aica
+ adpcm_argo
+ adpcm_ct
+ adpcm_dtk
+ adpcm_ea
+ adpcm_ea_maxis_xa
+ adpcm_ea_r1
+ adpcm_ea_r2
+ adpcm_ea_r3
+ adpcm_ea_xas
+ adpcm_g722
+ adpcm_g726
+ adpcm_g726le
+ adpcm_ima_acorn
+ adpcm_ima_alp
+ adpcm_ima_amv
+ adpcm_ima_apc
+ adpcm_ima_apm
+ adpcm_ima_cunning
+ adpcm_ima_dat4
+ adpcm_ima_dk3
+ adpcm_ima_dk4
+ adpcm_ima_ea_eacs
+ adpcm_ima_ea_sead
+ adpcm_ima_iss
+ adpcm_ima_moflex
+ adpcm_ima_mtf
+ adpcm_ima_oki
+ adpcm_ima_qt
+ adpcm_ima_rad
+ adpcm_ima_smjpeg
+ adpcm_ima_ssi
+ adpcm_ima_wav
+ adpcm_ima_ws
+ adpcm_ima_xbox
+ adpcm_ms
+ adpcm_mtaf
+ adpcm_psx
+ adpcm_sanyo
+ adpcm_sbpro_2
+ adpcm_sbpro_3
+ adpcm_sbpro_4
+ adpcm_swf
+ adpcm_thp
+ adpcm_thp_le
+ adpcm_vima
+ adpcm_xa
+ adpcm_xmd
+ adpcm_yamaha
+ adpcm_zork
+ amr_nb
+ amr_wb
+ ape
+ avc
+ dolby_e
+ eac3
+ flac
+ gsm
+ gsm_ms
+ ilbc
+ mp1
+ mp2
+ mp3
+ mp3adu
+ mp4als
+ opus
+ pcm_alaw
+ pcm_bluray
+ pcm_dvd
+ pcm_f16le
+ pcm_f24le
+ pcm_f32be
+ pcm_f32le
+ pcm_f64be
+ pcm_f64le
+ pcm_lxf
+ pcm_mulaw
+ pcm_s16be
+ pcm_s16be_planar
+ pcm_s16le
+ pcm_s16le_planar
+ pcm_s24be
+ pcm_s24daud
+ pcm_s24le
+ pcm_s24le_planar
+ pcm_s32be
+ pcm_s32le
+ pcm_s32le_planar
+ pcm_s64be
+ pcm_s64le
+ pcm_s8
+ pcm_s8_planar
+ pcm_sga
+ pcm_u16be
+ pcm_u16le
+ pcm_u24be
+ pcm_u24le
+ pcm_u32be
+ pcm_u32le
+ pcm_u8
+ pcm_vidc
+ sbc
+ vorbis
+ text
*/