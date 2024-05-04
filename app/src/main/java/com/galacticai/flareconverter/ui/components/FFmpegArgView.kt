package com.galacticai.flareconverter.ui.components

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
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.automirrored.rounded.Input
import androidx.compose.material.icons.automirrored.rounded.LastPage
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.AspectRatio
import androidx.compose.material.icons.rounded.Audiotrack
import androidx.compose.material.icons.rounded.Crop
import androidx.compose.material.icons.rounded.DataUsage
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FirstPage
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.HighQuality
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Output
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ShutterSpeed
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material.icons.rounded.SurroundSound
import androidx.compose.material.icons.rounded.Tag
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Videocam
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.galacticai.flareconverter.models.MediaFile
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.models.exceptions.FFmpegArgNoView
import com.galacticai.flareconverter.models.ffmpeg.ff_command.FFmpegCommand
import com.galacticai.flareconverter.models.ffmpeg.ff_command.args.FFmpegArg
import com.galacticai.flareconverter.models.ffmpeg.media_info.MediaInfo
import com.galacticai.flareconverter.ui.components.inputs.BitrateInput
import com.galacticai.flareconverter.ui.components.inputs.resolution_input.ResolutionInput
import com.galacticai.flareconverter.ui.components.mime_type.MimeTypeSelectByCategory
import com.galacticai.flareconverter.ui.components.mime_type.MimeTypeView
import com.galacticai.flareconverter.ui.share_activity.ShareActivity
import com.galacticai.flareconverter.ui.share_activity.ShareActivityHelpers
import com.galacticai.flareconverter.ui.share_activity.components.LocalShareActivityStates.LocalShareActivityColors
import com.galacticai.flareconverter.util.Consistent
import com.galacticai.flareconverter.util.FlowUtil.collectSingleAsState
import com.galacticai.flareconverter.util.MimeTypeUtils.outMimeSetting
import com.galacticai.flareconverter.util.Resolution
import com.galacticai.flareconverter.util.Settings
import global.common.ui.ExpandableGroup
import global.common.util.IOUtil.toExtension
import org.json.JSONObject
import java.io.File

typealias FFmpegArgViewRenderer = @Composable (
    modifier: Modifier,
    info: MediaFile,
    onChange: (parsed: Map<FFmpegArg, String>) -> Unit,
) -> Unit


private val empty: FFmpegArgViewRenderer
    get() = { _, _, _ ->
        // empty = TODO
    }

private val frameCount: FFmpegArgViewRenderer
    get() = { modifier, info, onChange ->
        var last by Settings.FFmpeg.FrameCount.rememberValue(true)
        empty(modifier, info, onChange)
    }

private val inOut: FFmpegArgViewRenderer
    get() = get@{ modifier, info, onChange ->
        val activity = LocalActivity.current as ShareActivity
        val shareInfo by activity.vm.shareInfoState
        val sInfo = shareInfo ?: return@get

        var outMime by sInfo.inMime.outMimeSetting
            .rememberObject(false, sInfo)

        val inFile by activity.vm.inFileFlow.collectAsState()
        val inFileDone = remember(inFile) {
            inFile.done!!.fileInfo.file
        }
        val outFile = remember(inFileDone, outMime) {
            inFileDone.toExtension(outMime.extension)
        }

        fun parseInput() = FFmpegArg.Input.parse(arrayOf(inFileDone.absolutePath))
        fun parseOutput() = FFmpegArg.Output.parse(arrayOf(outFile.absolutePath))

        var inputCurrent by activity.vm.configFlow.collectSingleAsState(
            FFmpegArg.Input, parseInput()
        )
        var outputCurrent by activity.vm.configFlow.collectSingleAsState(
            FFmpegArg.Output, parseOutput()
        )

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
                val inputParsed = parseInput()
                val outputParsed = parseOutput()
                inputCurrent = inputParsed
                outputCurrent = outputParsed
                onChange(
                    mapOf(
                        FFmpegArg.Input to inputParsed,
                        FFmpegArg.Output to outputParsed,
                    )
                )
            }
        }
    }

private val frameRate: FFmpegArgViewRenderer
    get() = { modifier, info, onChange ->
        var last by Settings.FFmpeg.FrameRate.rememberValue(true)
        empty(modifier, info, onChange)
    }
private val sampleRate: FFmpegArgViewRenderer
    get() = { modifier, info, onChange ->
        var last by Settings.FFmpeg.SampleRate.rememberValue(true)
        empty(modifier, info, onChange)
    }
private val channels: FFmpegArgViewRenderer
    get() = { modifier, info, onChange ->
        var last by Settings.FFmpeg.Channels.rememberValue(true)
        empty(modifier, info, onChange)
    }

private val resolution: FFmpegArgViewRenderer
    get() = { modifier, info, onChange ->
        Column {
            val activity = LocalActivity.current as ShareActivity
            var saved by Settings.FFmpeg.Resolution.rememberObject(true)
            var current by activity.vm.configFlow.collectSingleAsState(
                FFmpegArg.Resolution,
                FFmpegArg.Resolution.parse(
                    arrayOf(saved.width, saved.height)
                )
            )

            val resolution = remember(info) {
                info.mainStreamMime!!.resolution!! //! not null: already checked upon init
            }

            ResolutionInput(
                Modifier.fillMaxWidth() then modifier,
                input = resolution,
                maxBoxSize = 300.dp,
            ) {
                saved = Resolution(it.width, it.height)
                val cmd = FFmpegArg.Resolution.parse(
                    arrayOf(it.width, it.height)
                )
                current = cmd
                onChange(mapOf(FFmpegArg.Resolution to cmd))
            }
        }
    }

private val bitrateVideo: FFmpegArgViewRenderer
    get() = { modifier, info, onChange ->
        var last by Settings.FFmpeg.BitrateVideo.rememberValue(true)
        val colors = LocalShareActivityColors.current
        BitrateInput(
            modifier = modifier,
            bpsLast = last,
            info = info,
            colors = colors //TODO: detach colors type because the input is unrelated to activity
        ) {
            last = it.baseValue
            onChange(
                mapOf(
                    FFmpegArg.BitrateVideo to it.baseValue.toLong().toString()
                )
            )
        }
    }
private val bitrateAudio: FFmpegArgViewRenderer
    get() = { modifier, info, onChange ->
        val colors = LocalShareActivityColors.current
        var last by Settings.FFmpeg.BitrateAudio.rememberValue(true)
        BitrateInput(
            modifier = modifier,
            bpsLast = last,
            info = info,
            colors = colors //TODO: detach colors type because the input is unrelated to activity
        ) {
            last = it.baseValue
            onChange(
                mapOf(
                    FFmpegArg.BitrateAudio to it.baseValue.toLong().toString()
                )
            )
        }
    }

val version: FFmpegArgViewRenderer
    get() = { _, _, _ ->
        val cmd = FFmpegCommand().version().build()
        val result by produceState(cmd) {
            try {
                FFmpegCommand().version().run {
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
        FFmpegArg.Input -> inOut
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


fun FFmpegArg.getIcon(): ImageVector = when (this) {
    FFmpegArg.Version -> Icons.Outlined.Info
    FFmpegArg.Help -> Icons.AutoMirrored.Rounded.Help
    FFmpegArg.Input -> Icons.AutoMirrored.Rounded.Input
    FFmpegArg.Output -> Icons.Rounded.Output
    FFmpegArg.FrameCount -> Icons.Rounded.ShutterSpeed
    FFmpegArg.FrameRate -> Icons.Rounded.Speed
    FFmpegArg.SampleRate -> Icons.Rounded.GraphicEq
    FFmpegArg.Channels -> Icons.Rounded.SurroundSound
    FFmpegArg.Bitrate -> Icons.Rounded.DataUsage
    FFmpegArg.BitrateVideo -> Icons.Rounded.HighQuality
    FFmpegArg.BitrateAudio -> Icons.Rounded.Audiotrack
    FFmpegArg.CodecVideo -> Icons.Rounded.Videocam
    FFmpegArg.CodecAudio -> Icons.Rounded.Audiotrack
    FFmpegArg.Resolution -> Icons.Rounded.Crop
    FFmpegArg.DurationByString,
    FFmpegArg.DurationByMs -> Icons.Rounded.Timer

    FFmpegArg.Format -> Icons.Rounded.Extension
    FFmpegArg.StartTimeByMs,
    FFmpegArg.StartTimeByString -> Icons.Rounded.FirstPage

    FFmpegArg.EndTimeByMs,
    FFmpegArg.EndTimeByString -> Icons.AutoMirrored.Rounded.LastPage

    FFmpegArg.Scale -> Icons.Rounded.AspectRatio
    FFmpegArg.Crop -> Icons.Rounded.Crop
    FFmpegArg.Speed -> Icons.Rounded.FastForward
    FFmpegArg.Pitch -> Icons.Rounded.Tune
    FFmpegArg.MetadataByString,
    FFmpegArg.MetadataByPairs,
    FFmpegArg.MetadataByMap -> Icons.Rounded.Tag

    FFmpegArg.Preset -> Icons.Rounded.Tune
    FFmpegArg.Crf -> Icons.Rounded.Star
    FFmpegArg.MaxSizeByAmount,
    FFmpegArg.MaxSizeByString -> Icons.Rounded.Storage

    else -> Icons.Rounded.Settings
}

@Preview(showSystemUi = true)
@Composable
fun ArgViewPreview() {
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
                        MediaFile(
                            File("stuff.png"),
                            MimeType.Png,
                            MediaInfo(JSONObject())
                        ),
                    ) { }
                }
            }
        }
    }
}
