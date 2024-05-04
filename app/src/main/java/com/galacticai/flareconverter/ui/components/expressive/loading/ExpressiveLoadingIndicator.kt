package com.galacticai.flareconverter.ui.components.expressive.loading

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.galacticai.flareconverter.ui.themes.GalacticTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveLoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    indicatorColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
) {
    val tPulse by rememberLooping(PulseGlow.keyframes())
    val tCircle by rememberLooping(SquareToCircle.keyframes())

    Box(modifier.size(size)) {
        Canvas(Modifier.fillMaxSize()) {
            PulseGlow.draw(
                this, tPulse,
                containerColor,
                radius = size / 2.25f,
            )
            SquareToCircle.draw(
                this,
                tCircle,
                containerColor,
                size = size,
            )
        }

        ContainedLoadingIndicator(
            Modifier
                .size(size / 2)
                //.offset(y= 60.dp)
                .align(Alignment.Center),
            containerColor = Color.Transparent,
            indicatorColor=indicatorColor,
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
private fun Preview() = GalacticTheme {
    ExpressiveLoadingIndicator(size = 100.dp)
}