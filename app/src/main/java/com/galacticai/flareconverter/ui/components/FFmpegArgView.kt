package com.galacticai.flareconverter.ui.components

import android.util.Size
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arthenica.ffmpegkit.Chapter
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.MediaInformation
import com.arthenica.ffmpegkit.StreamInformation
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.models.exceptions.FFmpegArgNoView
import com.galacticai.flareconverter.models.ffmpeg.FFmpegArg
import com.galacticai.flareconverter.models.ffmpeg.FFmpegCommand
import com.galacticai.flareconverter.ui.components.inputs.BitrateInput
import com.galacticai.flareconverter.ui.components.inputs.resolution_input.ResolutionInput
import com.galacticai.flareconverter.ui.components.mime_type.MimeTypeSelectByCategory
import com.galacticai.flareconverter.ui.components.mime_type.MimeTypeView
import com.galacticai.flareconverter.ui.share_activity.ShareActivity
import com.galacticai.flareconverter.ui.share_activity.ShareActivityHelpers
import com.galacticai.flareconverter.ui.share_activity.components.ShareActivityView
import com.galacticai.flareconverter.util.Consistent
import com.galacticai.flareconverter.util.FlowUtil.collectSingleAsState
import com.galacticai.flareconverter.util.MediaUtils.mainStream
import com.galacticai.flareconverter.util.MimeTypeUtils.outMimeSetting
import com.galacticai.flareconverter.util.Resolution
import com.galacticai.flareconverter.util.Settings
import global.common.ui.ExpandableGroup
import global.common.util.IOUtil.toExtension
import org.json.JSONObject

typealias FFmpegArgViewRenderer = @Composable (
    modifier: Modifier,
    mediaInfo: MediaInformation,
    colors: ShareActivityView.Bg,
    onChange: (parsed: String) -> Unit,
) -> Unit


private val empty: FFmpegArgViewRenderer
    get() = { _, _, _, _ ->
        // empty = TODO
    }

private val frameCount: FFmpegArgViewRenderer
    get() = { modifier, mediaInfo, colors, onChange ->
        var last by Settings.FFmpeg.FrameCount.rememberValue(true)
        empty(modifier, mediaInfo, colors, onChange)
    }

private val input: FFmpegArgViewRenderer
    get() = get@{ modifier, mediaInfo, colors, onChange ->
        val activity = LocalActivity.current as ShareActivity
        val shareInfo by activity.vm.shareInfoState
        val sInfo = shareInfo ?: return@get

        var outMimeSaved by sInfo.inMime.outMimeSetting
            .rememberObject(false, sInfo)

        val inFile by activity.vm.inFileLive.collectAsState()
        val inFileDone = remember(inFile) {
            inFile.done!!.file
        }
        val outFile = remember(inFileDone, sInfo) {
            inFileDone.toExtension(outMimeSaved.extension)
        }

        fun parse() = FFmpegArg.Input.parseCmd(
            arrayOf(inFileDone.absolutePath, outFile.absolutePath)
        )

        var inputCurrent by activity.vm.configFlow.collectSingleAsState(
            FFmpegArg.Input, parse()
        )

        var outMime by shareInfo!!.inMime.outMimeSetting
            .rememberObject(false, shareInfo)

        Column(
            modifier,
            verticalArrangement = Arrangement.spacedBy(Consistent.Pad.smallX),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            MimeTypeView(sInfo.inMime)
            Icon(
                Icons.Rounded.KeyboardArrowDown,
                "mime type from-to arrow"
            )
            MimeTypeSelectByCategory(
                allowedMimes = sInfo.outMimes //! critical: allow outMimes only
            ) {
                outMime = it
                inputCurrent = parse()
            }
        }
    }

private val frameRate: FFmpegArgViewRenderer
    get() = { modifier, mediaInfo, colors, onChange ->
        var last by Settings.FFmpeg.FrameRate.rememberValue(true)
        empty(modifier, mediaInfo, colors, onChange)
    }
private val sampleRate: FFmpegArgViewRenderer
    get() = { modifier, mediaInfo, colors, onChange ->
        var last by Settings.FFmpeg.SampleRate.rememberValue(true)
        empty(modifier, mediaInfo, colors, onChange)
    }
private val channels: FFmpegArgViewRenderer
    get() = { modifier, mediaInfo, colors, onChange ->
        var last by Settings.FFmpeg.Channels.rememberValue(true)
        empty(modifier, mediaInfo, colors, onChange)
    }

private val resolution: FFmpegArgViewRenderer
    get() = { modifier, mediaInfo, colors, onChange ->
        Column {
            val activity = LocalActivity.current as ShareActivity
            var saved by Settings.FFmpeg.Resolution.rememberObject(true)
            var current by activity.vm.configFlow.collectSingleAsState(
                FFmpegArg.Resolution,
                FFmpegArg.Resolution.parseCmd(
                    arrayOf(saved.width, saved.height)
                )
            )

            val stream = remember(mediaInfo) {
                mediaInfo.mainStream!! //! not null: already checked upon init
            }

            val inputSize = remember(stream) {
                Size(stream.width.toInt(), stream.height.toInt())
            }

            ResolutionInput(
                Modifier.fillMaxWidth() then modifier,
                input = inputSize,
                maxBoxSize = 300.dp,
            ) {
                saved = Resolution(
                    it.width, it.height,
                    saved.interlaced, *saved.names
                )
                val cmd = FFmpegArg.Resolution.parseCmd(
                    arrayOf(it.width, it.height)
                )
                current = cmd
                onChange(cmd)
            }
        }
    }

private val bitrateVideo: FFmpegArgViewRenderer
    get() = { modifier, mediaInfo, colors, onChange ->
        var last by Settings.FFmpeg.BitrateVideo.rememberValue(true)
        BitrateInput(
            modifier = modifier,
            bpsLast = last,
            mediaInfo = mediaInfo,
            colors = colors
        ) {
            last = it.baseValue
            onChange(it.baseValue.toLong().toString())
        }
    }
private val bitrateAudio: FFmpegArgViewRenderer
    get() = { modifier, mediaInfo, colors, onChange ->
        var last by Settings.FFmpeg.BitrateAudio.rememberValue(true)
        BitrateInput(
            modifier = modifier,
            bpsLast = last,
            mediaInfo = mediaInfo,
            colors = colors
        ) {
            last = it.baseValue
            onChange(it.baseValue.toLong().toString())
        }
    }

val version: FFmpegArgViewRenderer
    get() = { _, _, _, _ ->
        val cmd = FFmpegCommand().version().build()
        val result by produceState(cmd) {
            try {
                FFmpegKit.executeAsync(cmd) {
                    value = it.output
                }
            } catch (_: Exception) {
                value = "Unknown version"
            }
        }
        val versions = remember(result) {
            result.lineSequence()
                .filterNot {
                    it.trim().isEmpty() ||
                            it.contains("configuration:") ||
                            it.contains("built with")
                }.joinToString("\n")
        }
        Text(versions)
    }

@get:Composable
val FFmpegArg.View
    get(): FFmpegArgViewRenderer = when (this) {
        FFmpegArg.Input -> input
        FFmpegArg.FrameCount -> frameCount
        FFmpegArg.FrameRate -> frameRate
        FFmpegArg.SampleRate -> sampleRate
        FFmpegArg.Channels -> channels
        // FFmpegArg.Bitrate -> empty //TODO
        FFmpegArg.BitrateVideo -> bitrateVideo
        FFmpegArg.BitrateAudio -> bitrateAudio
        FFmpegArg.CodecVideo -> empty //TODO
        FFmpegArg.CodecAudio -> empty //TODO
        FFmpegArg.Resolution -> resolution
        FFmpegArg.DurationByString -> empty //TODO
        FFmpegArg.DurationByMs -> empty //TODO
        FFmpegArg.Format -> empty //TODO
        FFmpegArg.StartTimeByMs -> empty //TODO
        FFmpegArg.StartTimeByString -> empty //TODO
        FFmpegArg.EndTimeByMs -> empty //TODO
        FFmpegArg.EndTimeByString -> empty //TODO
        FFmpegArg.Scale -> empty //TODO
        FFmpegArg.Crop -> empty //TODO
        FFmpegArg.Speed -> empty //TODO
        FFmpegArg.Pitch -> empty //TODO
        FFmpegArg.MetadataByString -> empty //TODO
        FFmpegArg.MetadataByPairs -> empty //TODO
        FFmpegArg.MetadataByMap -> empty //TODO
        FFmpegArg.Preset -> empty //TODO
        // FFmpegArg.Crf empty
        FFmpegArg.MaxSizeByAmount -> empty //TODO
        FFmpegArg.MaxSizeByString -> empty //TODO
        FFmpegArg.Version -> version
        // FFmpegArg.Help -> {}
        else -> throw FFmpegArgNoView(this) //? failsafe: won't happen unless UI messes up
    }


@Preview(showSystemUi = true)
@Composable
fun ArgViewPreview() {
    val colors by ShareActivityView.getBgColors { 0f }
    val allowedArgs = ShareActivityHelpers.ffmpegAllowedArgs[MimeType.VIDEO]!!
    LazyColumn(
        state = rememberLazyListState(),
        verticalArrangement = Arrangement.spacedBy(Consistent.Pad.smallX),
    ) {
        items(allowedArgs.size) { i ->
            val arg = allowedArgs[i]

            Column {
                ExpandableGroup(arg.toString()) { groupPad ->
                    arg.View(
                        Modifier
                            .fillMaxWidth()
                            .padding(groupPad)
                            .verticalScroll(rememberScrollState()),
                        MediaInformation(
                            JSONObject()
                                .put("filename", "stuff.mp4")
                                .put("bit_rate", "1000000"),
                            listOf<StreamInformation>(),
                            listOf<Chapter>()
                        ),
                        colors,
                        { })
                }
            }
        }
    }
}
