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

        val supportedCodecs = MediaCapabilities.Supported.supportedCodecs()
        val decoders = MediaCapabilities.Supported.decoders()
        val encoders = MediaCapabilities.Supported.encoders()

        assertNotNull("Supported codecs set should not be null", supportedCodecs)
        assertNotNull("Decoders map should not be null", decoders)
        assertNotNull("Encoders map should not be null", encoders)

        Log.d(
            "CodecIntersectionTest",
            "FFmpeg: ${ffmpegCodecs?.size}, Device: ${deviceCodecs.size}, Supported: ${supportedCodecs.size}, Decoders: ${decoders?.size}, Encoders: ${encoders?.size}"
        )

        decoders?.values?.firstOrNull()?.let {
            val canDecode = MediaCapabilities.Supported.canDecode(it)
            Log.d(
                "CodecIntersectionTest",
                "Sample decoder check for ${it.key}: canDecode=$canDecode"
            )
        }

        encoders?.values?.firstOrNull()?.let {
            val canEncode = MediaCapabilities.Supported.canEncode(it)
            Log.d(
                "CodecIntersectionTest",
                "Sample encoder check for ${it.key}: canEncode=$canEncode"
            )
        }

        decoders?.keys?.forEach {
            Log.d("CodecIntersectionTest", "+ decoder: $it")
        }

        encoders?.keys?.forEach {
            Log.d("CodecIntersectionTest", "+ encoder: $it")
        }

        val unmatchedDeviceCodecs = deviceCodecs.filter { deviceCodec ->
            val mimeKey = deviceCodec.mime.key?.lowercase() ?: ""
            val name = deviceCodec.info.name.lowercase()

            val deviceTokens = buildSet {
                addAll(mimeKey.split(Regex("[^a-z0-9]+")).filter { it.length > 1 })
                addAll(name.split(Regex("[^a-z0-9]+")).filter { it.length > 1 })
            }

            decoders?.none { (key, codec) ->
                val codecTokens = buildSet {
                    addAll(
                        key.lowercase().split(Regex("[^a-z0-9]+")).filter { it.length > 1 }
                    )
                    codec.description?.let {
                        addAll(
                            it.lowercase().split(Regex("[^a-z0-9]+")).filter { it.length > 1 }
                        )
                    }
                }
                (deviceTokens.intersect(codecTokens)).isNotEmpty()
            } ?: true
        }.map { "${it.mime.mime} (${it.info.name})" }.distinct()

        unmatchedDeviceCodecs.forEach {
            Log.d("CodecIntersectionTest", "X $it")
        }
    }
}

/*
FFmpeg: 539, Device: 103, Supported: 104, Decoders: 216, Encoders: 29
Sample decoder check for 8bps: canDecode=true, canInput=true
Sample encoder check for amv: canEncode=true, canOutput=true
+ decoder: 8bps
+ decoder: amv
+ decoder: apv
+ decoder: argo
+ decoder: av1
+ decoder: avs
+ decoder: bethsoftvid
+ decoder: binkvideo
+ decoder: bmv_video
+ decoder: cavs
+ decoder: cdgraphics
+ decoder: cdtoons
+ decoder: cdxl
+ decoder: cmv
+ decoder: cpia
+ decoder: cri
+ decoder: dds
+ decoder: dsicinvideo
+ decoder: dvvideo
+ decoder: ffv1
+ decoder: flashsv
+ decoder: flashsv2
+ decoder: flic
+ decoder: flv1
+ decoder: gdv
+ decoder: h263
+ decoder: h264
+ decoder: hevc
+ decoder: hnm4video
+ decoder: idcin
+ decoder: indeo4
+ decoder: indeo5
+ decoder: interplayvideo
+ decoder: ipu
+ decoder: jv
+ decoder: kgv1
+ decoder: kmvc
+ decoder: mad
+ decoder: magicyuv
+ decoder: mdec
+ decoder: mmvideo
+ decoder: mobiclip
+ decoder: motionpixels
+ decoder: mpeg1video
+ decoder: mpeg2video
+ decoder: mpeg4
+ decoder: msa1
+ decoder: msmpeg4v1
+ decoder: msmpeg4v2
+ decoder: msmpeg4v3
+ decoder: mss1
+ decoder: mss2
+ decoder: msvideo1
+ decoder: mts2
+ decoder: mvc1
+ decoder: mvc2
+ decoder: mxpeg
+ decoder: paf_video
+ decoder: pdv
+ decoder: prores_raw
+ decoder: prosumer
+ decoder: qtrle
+ decoder: rawvideo
+ decoder: rl2
+ decoder: roq
+ decoder: rpza
+ decoder: rtv1
+ decoder: sanm
+ decoder: sga
+ decoder: simbiosis_imx
+ decoder: smackvideo
+ decoder: smvjpeg
+ decoder: svq1
+ decoder: svq3
+ decoder: tgq
+ decoder: tgv
+ decoder: thp
+ decoder: tiertexseqvideo
+ decoder: tqi
+ decoder: utvideo
+ decoder: vc1
+ decoder: vc1image
+ decoder: vmdvideo
+ decoder: vmix
+ decoder: vmnc
+ decoder: vnull
+ decoder: vp3
+ decoder: vp4
+ decoder: vp5
+ decoder: vp6
+ decoder: vp6a
+ decoder: vp6f
+ decoder: vp7
+ decoder: vp8
+ decoder: vp9
+ decoder: vvc
+ decoder: wcmv
+ decoder: wmv1
+ decoder: wmv2
+ decoder: wmv3
+ decoder: wmv3image
+ decoder: ws_vqa
+ decoder: yop
+ decoder: zerocodec
+ decoder: zmbv
+ decoder: aac
+ decoder: aac_latm
+ decoder: ac3
+ decoder: adpcm_4xm
+ decoder: adpcm_adx
+ decoder: adpcm_afc
+ decoder: adpcm_agm
+ decoder: adpcm_aica
+ decoder: adpcm_argo
+ decoder: adpcm_ct
+ decoder: adpcm_dtk
+ decoder: adpcm_ea
+ decoder: adpcm_ea_maxis_xa
+ decoder: adpcm_ea_r1
+ decoder: adpcm_ea_r2
+ decoder: adpcm_ea_r3
+ decoder: adpcm_ea_xas
+ decoder: adpcm_g722
+ decoder: adpcm_g726
+ decoder: adpcm_g726le
+ decoder: adpcm_ima_acorn
+ decoder: adpcm_ima_alp
+ decoder: adpcm_ima_amv
+ decoder: adpcm_ima_apc
+ decoder: adpcm_ima_apm
+ decoder: adpcm_ima_cunning
+ decoder: adpcm_ima_dat4
+ decoder: adpcm_ima_dk3
+ decoder: adpcm_ima_dk4
+ decoder: adpcm_ima_ea_eacs
+ decoder: adpcm_ima_ea_sead
+ decoder: adpcm_ima_iss
+ decoder: adpcm_ima_moflex
+ decoder: adpcm_ima_mtf
+ decoder: adpcm_ima_oki
+ decoder: adpcm_ima_qt
+ decoder: adpcm_ima_rad
+ decoder: adpcm_ima_smjpeg
+ decoder: adpcm_ima_ssi
+ decoder: adpcm_ima_wav
+ decoder: adpcm_ima_ws
+ decoder: adpcm_ima_xbox
+ decoder: adpcm_ms
+ decoder: adpcm_mtaf
+ decoder: adpcm_psx
+ decoder: adpcm_sanyo
+ decoder: adpcm_sbpro_2
+ decoder: adpcm_sbpro_3
+ decoder: adpcm_sbpro_4
+ decoder: adpcm_swf
+ decoder: adpcm_thp
+ decoder: adpcm_thp_le
+ decoder: adpcm_vima
+ decoder: adpcm_xa
+ decoder: adpcm_xmd
+ decoder: adpcm_yamaha
+ decoder: adpcm_zork
+ decoder: amr_nb
+ decoder: amr_wb
+ decoder: ape
+ decoder: avc
+ decoder: dolby_e
+ decoder: eac3
+ decoder: flac
+ decoder: gsm
+ decoder: gsm_ms
+ decoder: ilbc
+ decoder: mp1
+ decoder: mp2
+ decoder: mp3
+ decoder: mp3adu
+ decoder: mp4als
+ decoder: opus
+ decoder: pcm_alaw
+ decoder: pcm_bluray
+ decoder: pcm_dvd
+ decoder: pcm_f16le
+ decoder: pcm_f24le
+ decoder: pcm_f32be
+ decoder: pcm_f32le
+ decoder: pcm_f64be
+ decoder: pcm_f64le
+ decoder: pcm_lxf
+ decoder: pcm_mulaw
+ decoder: pcm_s16be
+ decoder: pcm_s16be_planar
+ decoder: pcm_s16le
+ decoder: pcm_s16le_planar
+ decoder: pcm_s24be
+ decoder: pcm_s24daud
+ decoder: pcm_s24le
+ decoder: pcm_s24le_planar
+ decoder: pcm_s32be
+ decoder: pcm_s32le
+ decoder: pcm_s32le_planar
+ decoder: pcm_s64be
+ decoder: pcm_s64le
+ decoder: pcm_s8
+ decoder: pcm_s8_planar
+ decoder: pcm_sga
+ decoder: pcm_u16be
+ decoder: pcm_u16le
+ decoder: pcm_u24be
+ decoder: pcm_u24le
+ decoder: pcm_u32be
+ decoder: pcm_u32le
+ decoder: pcm_u8
+ decoder: pcm_vidc
+ decoder: sbc
+ decoder: vorbis
+ decoder: text
+ encoder: amv
+ encoder: dvvideo
+ encoder: ffv1
+ encoder: flashsv
+ encoder: flashsv2
+ encoder: flv1
+ encoder: h263
+ encoder: h264
+ encoder: hevcFFmpeg: 539, Device: 103, Supported: 104, Decoders: 216, Encoders: 29
Sample decoder check for 8bps: canDecode=true, canInput=true
Sample encoder check for amv: canEncode=true, canOutput=true
+ decoder: 8bps
+ decoder: amv
+ decoder: apv
+ decoder: argo
+ decoder: av1
+ decoder: avs
+ decoder: bethsoftvid
+ decoder: binkvideo
+ decoder: bmv_video
+ decoder: cavs
+ decoder: cdgraphics
+ decoder: cdtoons
+ decoder: cdxl
+ decoder: cmv
+ decoder: cpia
+ decoder: cri
+ decoder: dds
+ decoder: dsicinvideo
+ decoder: dvvideo
+ decoder: ffv1
+ decoder: flashsv
+ decoder: flashsv2
+ decoder: flic
+ decoder: flv1
+ decoder: gdv
+ decoder: h263
+ decoder: h264
+ decoder: hevc
+ decoder: hnm4video
+ decoder: idcin
+ decoder: indeo4
+ decoder: indeo5
+ decoder: interplayvideo
+ decoder: ipu
+ decoder: jv
+ decoder: kgv1
+ decoder: kmvc
+ decoder: mad
+ decoder: magicyuv
+ decoder: mdec
+ decoder: mmvideo
+ decoder: mobiclip
+ decoder: motionpixels
+ decoder: mpeg1video
+ decoder: mpeg2video
+ decoder: mpeg4
+ decoder: msa1
+ decoder: msmpeg4v1
+ decoder: msmpeg4v2
+ decoder: msmpeg4v3
+ decoder: mss1
+ decoder: mss2
+ decoder: msvideo1
+ decoder: mts2
+ decoder: mvc1
+ decoder: mvc2
+ decoder: mxpeg
+ decoder: paf_video
+ decoder: pdv
+ decoder: prores_raw
+ decoder: prosumer
+ decoder: qtrle
+ decoder: rawvideo
+ decoder: rl2
+ decoder: roq
+ decoder: rpza
+ decoder: rtv1
+ decoder: sanm
+ decoder: sga
+ decoder: simbiosis_imx
+ decoder: smackvideo
+ decoder: smvjpeg
+ decoder: svq1
+ decoder: svq3
+ decoder: tgq
+ decoder: tgv
+ decoder: thp
+ decoder: tiertexseqvideo
+ decoder: tqi
+ decoder: utvideo
+ decoder: vc1
+ decoder: vc1image
+ decoder: vmdvideo
+ decoder: vmix
+ decoder: vmnc
+ decoder: vnull
+ decoder: vp3
+ decoder: vp4
+ decoder: vp5
+ decoder: vp6
+ decoder: vp6a
+ decoder: vp6f
+ decoder: vp7
+ decoder: vp8
+ decoder: vp9
+ decoder: vvc
+ decoder: wcmv
+ decoder: wmv1
+ decoder: wmv2
+ decoder: wmv3
+ decoder: wmv3image
+ decoder: ws_vqa
+ decoder: yop
+ decoder: zerocodec
+ decoder: zmbv
+ decoder: aac
+ decoder: aac_latm
+ decoder: ac3
+ decoder: adpcm_4xm
+ decoder: adpcm_adx
+ decoder: adpcm_afc
+ decoder: adpcm_agm
+ decoder: adpcm_aica
+ decoder: adpcm_argo
+ decoder: adpcm_ct
+ decoder: adpcm_dtk
+ decoder: adpcm_ea
+ decoder: adpcm_ea_maxis_xa
+ decoder: adpcm_ea_r1
+ decoder: adpcm_ea_r2
+ decoder: adpcm_ea_r3
+ decoder: adpcm_ea_xas
+ decoder: adpcm_g722
+ decoder: adpcm_g726
+ decoder: adpcm_g726le
+ decoder: adpcm_ima_acorn
+ decoder: adpcm_ima_alp
+ decoder: adpcm_ima_amv
+ decoder: adpcm_ima_apc
+ decoder: adpcm_ima_apm
+ decoder: adpcm_ima_cunning
+ decoder: adpcm_ima_dat4
+ decoder: adpcm_ima_dk3
+ decoder: adpcm_ima_dk4
+ decoder: adpcm_ima_ea_eacs
+ decoder: adpcm_ima_ea_sead
+ decoder: adpcm_ima_iss
+ decoder: adpcm_ima_moflex
+ decoder: adpcm_ima_mtf
+ decoder: adpcm_ima_oki
+ decoder: adpcm_ima_qt
+ decoder: adpcm_ima_rad
+ decoder: adpcm_ima_smjpeg
+ decoder: adpcm_ima_ssi
+ decoder: adpcm_ima_wav
+ decoder: adpcm_ima_ws
+ decoder: adpcm_ima_xbox
+ decoder: adpcm_ms
+ decoder: adpcm_mtaf
+ decoder: adpcm_psx
+ decoder: adpcm_sanyo
+ decoder: adpcm_sbpro_2
+ decoder: adpcm_sbpro_3
+ decoder: adpcm_sbpro_4
+ decoder: adpcm_swf
+ decoder: adpcm_thp
+ decoder: adpcm_thp_le
+ decoder: adpcm_vima
+ decoder: adpcm_xa
+ decoder: adpcm_xmd
+ decoder: adpcm_yamaha
+ decoder: adpcm_zork
+ decoder: amr_nb
+ decoder: amr_wb
+ decoder: ape
+ decoder: avc
+ decoder: dolby_e
+ decoder: eac3
+ decoder: flac
+ decoder: gsm
+ decoder: gsm_ms
+ decoder: ilbc
+ decoder: mp1
+ decoder: mp2
+ decoder: mp3
+ decoder: mp3adu
+ decoder: mp4als
+ decoder: opus
+ decoder: pcm_alaw
+ decoder: pcm_bluray
+ decoder: pcm_dvd
+ decoder: pcm_f16le
+ decoder: pcm_f24le
+ decoder: pcm_f32be
+ decoder: pcm_f32le
+ decoder: pcm_f64be
+ decoder: pcm_f64le
+ decoder: pcm_lxf
+ decoder: pcm_mulaw
+ decoder: pcm_s16be
+ decoder: pcm_s16be_planar
+ decoder: pcm_s16le
+ decoder: pcm_s16le_planar
+ decoder: pcm_s24be
+ decoder: pcm_s24daud
+ decoder: pcm_s24le
+ decoder: pcm_s24le_planar
+ decoder: pcm_s32be
+ decoder: pcm_s32le
+ decoder: pcm_s32le_planar
+ decoder: pcm_s64be
+ decoder: pcm_s64le
+ decoder: pcm_s8
+ decoder: pcm_s8_planar
+ decoder: pcm_sga
+ decoder: pcm_u16be
+ decoder: pcm_u16le
+ decoder: pcm_u24be
+ decoder: pcm_u24le
+ decoder: pcm_u32be
+ decoder: pcm_u32le
+ decoder: pcm_u8
+ decoder: pcm_vidc
+ decoder: sbc
+ decoder: vorbis
+ decoder: text
+ encoder: amv
+ encoder: dvvideo
+ encoder: ffv1
+ encoder: flashsv
+ encoder: flashsv2
+ encoder: flv1
+ encoder: h263
+ encoder: h264
+ encoder: hevc
+ encoder: magicyuv
+ encoder: mpeg1video
+ encoder: mpeg2video
+ encoder: mpeg4
+ encoder: msvideo1
+ encoder: pdv
+ encoder: qtrle
+ encoder: rawvideo
+ encoder: roq
+ encoder: rpza
+ encoder: svq1
+ encoder: utvideo
+ encoder: vnull
+ encoder: vp8
+ encoder: wmv1
+ encoder: wmv2
+ encoder: zmbv
+ encoder: aac
+ encoder: flac
+ encoder: opus
+ encoder: magicyuv
+ encoder: mpeg1video
+ encoder: mpeg2video
+ encoder: mpeg4
+ encoder: msvideo1
+ encoder: pdv
+ encoder: qtrle
+ encoder: rawvideo
+ encoder: roq
+ encoder: rpza
+ encoder: svq1
+ encoder: utvideo
+ encoder: vnull
+ encoder: vp8
+ encoder: wmv1
+ encoder: wmv2
+ encoder: zmbv
+ encoder: aac
+ encoder: flac
+ encoder: opus
*/