package com.galacticai.flareconverter.ui.components.thumbnail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.galacticai.flareconverter.models.MediaFile
import com.galacticai.flareconverter.models.MediaFileBase
import com.galacticai.flareconverter.util.Modifiers.alphaGradient
import com.galacticai.flareconverter.util.media.FFGenerate
import global.common.models.progressive.Progressive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ThumbnailCover(
    modifier: Modifier = Modifier,
    inFile: Progressive<MediaFile>,
) {
    val file = inFile.done?.fileInfo?.file
    var image by remember(file) { mutableStateOf<MediaFileBase?>(null) }
    LaunchedEffect(inFile) {
        if (inFile !is Progressive.Done) {
            image = null
            return@LaunchedEffect
        }
        if (image != null) return@LaunchedEffect
        image = withContext(Dispatchers.IO) {
            FFGenerate.extractFrame(inFile.value)
        }
    }
    val scaleExit = 1.3f
    val animationSpec = tween<Float>(500)
    AnimatedVisibility(
        image != null,
        modifier = Modifier.fillMaxSize(),
        enter = fadeIn(animationSpec) + scaleIn(animationSpec, scaleExit),
        exit = fadeOut(animationSpec) + scaleOut(animationSpec, scaleExit),
    ) {
        AsyncImage(
            model = image!!.fileInfo.file,
            contentDescription = "Image thumbnail",
            modifier = modifier then Modifier
                .fillMaxSize()
                .alphaGradient(),
            contentScale = ContentScale.Crop
        )
    }
}