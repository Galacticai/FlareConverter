package com.galacticai.flareconverter.ui.components.thumbnail_v1.components.thumbnail_renderer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.flareconverter.ui.components.thumbnail_v1.Thumbnail
import com.galacticai.flareconverter.ui.components.thumbnail_v1.ThumbnailV1
import com.galacticai.flareconverter.ui.components.thumbnail_v1.ThumbnailV1Scope
import com.galacticai.flareconverter.ui.components.thumbnail_v1.components.InfoSkeleton
import com.galacticai.flareconverter.ui.themes.GalacticTheme
import global.common.models.progressive.Progressive
import global.common.ui.animtion.AnimatedText
import java.io.File
import java.util.Date

class RunningThumbnailRenderer(
    private val targetFuture: Progressive.Running<File>
) : ThumbnailRenderer {
    @Composable
    override fun ThumbnailV1Scope.Preview() {
        Thumbnail.CenterBox {
            val size = 100.dp
            val mod = Modifier.size(size)
            val progress = targetFuture.progress
                ?: return@CenterBox CircularProgressIndicator(
                    mod,
                    color = colors.main,
                    trackColor = colors.secondary
                )

            CircularWavyProgressIndicator(
                modifier = mod,
                color = colors.main,
                trackColor = colors.secondary,
                progress = { progress },
                wavelength = size / 2.5f,
                stroke = Stroke(size.value / 5, cap = StrokeCap.Round)
            )
            if (showPercent) {
                val percent = (progress * 100).toInt()
                if (percent < 100) {
                    AnimatedText("${percent}%") { c, modifier ->
                        Text(
                            c.toString(),
                            modifier = modifier,
                            color = colors.main,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }

    @Composable
    override fun ThumbnailV1Scope.Info() {
        InfoSkeleton()//TODO
    }
}


@Preview(showBackground = true)
@Composable
private fun RunningThumbnailV1RendererPreview() = GalacticTheme {
    val sliderState = rememberSliderState(.5f)
    val future = Progressive.Running<File>(
        Progressive.Running.Type.Main,
        sliderState.value,
        null, Date(),
    )
    LaunchedEffect(sliderState.value) {
        future.progress = sliderState.value
    }

    Column(Modifier.padding(10.dp)) {
        ThumbnailV1(
            Modifier
                .aspectRatio(1f)
                .padding(20.dp),
            inFile = future
        )

        Slider(sliderState)
    }
}