package com.galacticai.flareconverter.ui.components

import android.util.Size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.arthenica.ffmpegkit.Chapter
import com.arthenica.ffmpegkit.MediaInformation
import com.arthenica.ffmpegkit.StreamInformation
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.models.exceptions.FFmpegArgNoView
import com.galacticai.flareconverter.models.ffmpeg.FFmpegArg
import com.galacticai.flareconverter.ui.components.inputs.BitrateInput
import com.galacticai.flareconverter.ui.components.inputs.NumberInput
import com.galacticai.flareconverter.ui.components.inputs.resolution_input.ResolutionInput
import com.galacticai.flareconverter.ui.share_activity.ShareActivityHelpers
import com.galacticai.flareconverter.ui.share_activity.components.BgVariants
import com.galacticai.flareconverter.ui.share_activity.components.getColors
import com.galacticai.flareconverter.util.Consistent
import com.galacticai.flareconverter.util.MediaUtils.mainStream
import com.galacticai.flareconverter.util.Resolution
import com.galacticai.flareconverter.util.Settings
import global.common.TextUtil.sentenceCase
import global.common.ui.ExpandableGroup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.json.JSONObject
import java.io.File

@Composable
fun FFmpegArgView(
    arg: FFmpegArg,
    file: File,
    mediaInfo: MediaInformation,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier,
    colors: BgVariants,
) {

    when (arg) {
        FFmpegArg.FrameCount -> {
            var last by Settings.FFmpeg.FrameCount.rememberValue(true)
            NumberInput(
                placeholder = FFmpegArg.FrameCount.toString(),
                transform = { it.toIntOrNull()?.toString() ?: "0" },
                colors = TextFieldDefaults.colors().copy(
                    focusedContainerColor = colors.first.copy(alpha = .5f),
                    unfocusedContainerColor = colors.first,
                ),
                modifier = Modifier.fillMaxWidth() then modifier,
                shape = Consistent.Shape.Rounded.pill
            ) {
                last = it.toInt()
                true
            }
        }

        FFmpegArg.FrameRate -> {
            var last by Settings.FFmpeg.FrameRate.rememberValue(true)
            NumberInput(
                placeholder = FFmpegArg.FrameRate.toString(),
                transform = { it.toIntOrNull()?.toString() ?: "0" },
                colors = TextFieldDefaults.colors().copy(
                    focusedContainerColor = colors.first.copy(alpha = .5f),
                    unfocusedContainerColor = colors.first,
                ),
                modifier = Modifier.fillMaxWidth() then modifier,
                shape = Consistent.Shape.Rounded.pill
            ) {
                last = it.toInt()
                true
            }
        }

        FFmpegArg.SampleRate -> {
            var last by Settings.FFmpeg.SampleRate.rememberValue(true)
            NumberInput(
                placeholder = FFmpegArg.SampleRate.toString(),
                transform = { it.toIntOrNull()?.toString() ?: "0" },
                colors = TextFieldDefaults.colors().copy(
                    focusedContainerColor = colors.first.copy(alpha = .5f),
                    unfocusedContainerColor = colors.first,
                ),
                modifier = Modifier.fillMaxWidth() then modifier,
                shape = Consistent.Shape.Rounded.pill
            ) {
                last = it.toInt()
                true
            }
        }

        FFmpegArg.Channels -> {
            var last by Settings.FFmpeg.Channels.rememberValue(true)
            NumberInput(
                placeholder = FFmpegArg.Channels.toString(),
                transform = { it.toIntOrNull()?.toString() ?: "0" },
                colors = TextFieldDefaults.colors().copy(
                    focusedContainerColor = colors.first.copy(alpha = .5f),
                    unfocusedContainerColor = colors.first,
                ),
                modifier = Modifier.fillMaxWidth() then modifier,
                shape = Consistent.Shape.Rounded.pill
            ) {
                last = it.toInt()
                true
            }
        }
//            FFmpegArg.Bitrate -> {}

        FFmpegArg.BitrateVideo -> {
            var last by Settings.FFmpeg.BitrateVideo.rememberValue(true)
            BitrateInput(
                modifier = modifier,
                bpsLast = last,
                mediaInfo = mediaInfo,
                colors = colors
            ) { last = it.baseValue }
        }

        FFmpegArg.BitrateAudio -> {
            var last by Settings.FFmpeg.BitrateAudio.rememberValue(true)
            BitrateInput(
                modifier = modifier,
                bpsLast = last,
                mediaInfo = mediaInfo,
                colors = colors
            ) { last = it.baseValue }
        }

        FFmpegArg.CodecVideo -> {}
        FFmpegArg.CodecAudio -> {}
        FFmpegArg.Resolution -> {
            Column {
                // only used as context
                var last by Settings.FFmpeg.Resolution.rememberObject(true)

                val stream = remember(mediaInfo) {
                    mediaInfo.firstStream!! //! not null: already checked upon init
                }

                ResolutionInput(
                    Modifier.fillMaxWidth() then modifier,
                    input = Size(stream.width.toInt(), stream.height.toInt()),
                    maxBoxSize = 300.dp,
                ) {
                    last = Resolution(
                        it.width, it.height,
                        last.interlaced, *last.names
                    )
                }
            }
        }

        FFmpegArg.DurationByString -> {}
        FFmpegArg.DurationByMs -> {}
        FFmpegArg.Format -> {}
        FFmpegArg.StartTimeByMs -> {}
        FFmpegArg.StartTimeByString -> {}
        FFmpegArg.EndTimeByMs -> {}
        FFmpegArg.EndTimeByString -> {}
        FFmpegArg.Scale -> {}
        FFmpegArg.Crop -> {}
        FFmpegArg.Speed -> {}
        FFmpegArg.Pitch -> {}
        FFmpegArg.MetadataByString -> {}
        FFmpegArg.MetadataByPairs -> {}
        FFmpegArg.MetadataByMap -> {}
        FFmpegArg.Preset -> {}
//            FFmpegArg.Crf -> {}
        FFmpegArg.MaxSizeByAmount -> {}
        FFmpegArg.MaxSizeByString -> {}

//            FFmpegArg.Version -> {
//                val cmd = FFmpegCommand().version().build()
//                val result by produceState(cmd) {
//                    try {
//                        FFmpegKit.executeAsync(cmd) {
//                            value = it.output
//                        }
//                    } catch (_: Exception) {
//                        value = "Unknown version"
//                    }
//                }
//                val versions by remember(result) {
//                    derivedStateOf {
//                        result.lineSequence()
//                            .filterNot {
//                                it.trim().isEmpty() ||
//                                        it.contains("configuration:") ||
//                                        it.contains("built with")
//                            }.joinToString("\n")
//                    }
//                }
//                Text(versions)
//                //! intentional: skip parse or the whole command would collapse to this version call
//                //onParse(FFmpegCommand().version())
//            }
//        FFmpegArg.Help -> {}
//        FFmpegArg.Input -> {}
        else -> throw FFmpegArgNoView(arg) //? failsafe: won't happen unless UI messes up
    }
}


@Preview(showSystemUi = true)
@Composable
fun ArgViewPreview() {
    val colors by getColors(0f)
    val allowedArgs = ShareActivityHelpers.ffmpegAllowedArgs[MimeType.VIDEO]!!
    LazyColumn(
        state = rememberLazyListState(),
        verticalArrangement = Arrangement.spacedBy(Consistent.Pad.smallX),
    ) {
        items(allowedArgs.size) { i ->
            val arg = allowedArgs[i]

            Column {
                ExpandableGroup(arg.name.sentenceCase) { groupPad ->
                    FFmpegArgView(
                        file = File("stuff.mp4"),
                        arg = arg,
                        mediaInfo = MediaInformation(
                            JSONObject()
                                .put("filename", "stuff.mp4")
                                .put("bit_rate", "1000000"),
                            listOf<StreamInformation>(),
                            listOf<Chapter>()
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(groupPad)
                            .verticalScroll(rememberScrollState()),
                        coroutineScope = CoroutineScope(Dispatchers.IO),
                        colors = colors
                    )
                }
            }
        }
    }
}
