package com.galacticai.flareconverter.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBox
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.galacticai.flareconverter.BuildConfig
import com.galacticai.flareconverter.R
import com.galacticai.flareconverter.models.ShareInfo
import com.galacticai.flareconverter.models.mimes.MimeType
import com.galacticai.flareconverter.models.mimes.MimeTypeUtils
import com.galacticai.flareconverter.ui.theme.GalacticTheme
import com.galacticai.flareconverter.util.AppDefaults.clearInputDir
import com.galacticai.flareconverter.util.AppDefaults.clearOutputDir
import com.galacticai.flareconverter.util.AppDefaults.inputDir
import com.galacticai.flareconverter.util.Consistent
import com.galacticai.flareconverter.util.settings.Setting
import global.common.IOUtils.mime
import global.common.models.FutureValue
import global.common.ui.ExpandableGroup
import global.common.ui.ExpandableGroupItemFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class ConvertActivity : ComponentActivity() {
    val vm by viewModels<ConvertActivityVM>()

    val shareInfoState = mutableStateOf<ShareInfo?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ShareActivityContent() }

        //TODO: handle with settings (delete by age)
        clearInputDir(true)
        clearOutputDir(true)
        shareInfoState.value = vm.initFile(intent, inputDir, contentResolver)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShareActivityContent() {
    GalacticTheme {
        BottomSheetScaffold(
            sheetContent = { SheetContent() },
            sheetDragHandle = { SheetTitle() },
            sheetShape = Consistent.shapeTop,
        ) { DebugInfo() }
    }
}

@Composable
private fun SheetTitle() {
    Text(
        modifier = Modifier.padding(
            horizontal = Consistent.padMedium, vertical = Consistent.padRegular,
        ),
        text = stringResource(R.string.app_name),
        fontSize = 24.sp,
    )
}


@Composable
private fun SheetContent() {
    val activity = LocalContext.current as ConvertActivity
    val vm = activity.vm

    var halt by remember { mutableStateOf(false) }
    var converting by remember { mutableStateOf(false) }

    val shareInfo by remember { activity.shareInfoState }
    val inMime by remember(shareInfo) {
        derivedStateOf {
            val m = shareInfo?.mime ?: return@derivedStateOf null
            MimeTypeUtils.getSupportedOrNew(m)
        }
    }
    //! not using ObjectSetting.rememberObject()
    //! because this one may have null parameter which cant work without this custom solution
    var outMime by remember(inMime) { mutableStateOf<MimeType?>(null) }
    LaunchedEffect(inMime) {
        val m = inMime ?: return@LaunchedEffect
        outMime = Setting.LastSelectedMime(m.mime).getObject(activity)
    }
    fun saveOutMime(v: MimeType) = runBlocking(Dispatchers.IO) {
        val m = inMime ?: return@runBlocking
        Setting.LastSelectedMime(m.mime).setObject(activity, v)
        outMime = v
    }

    val inFileFuture by vm.inFileLive.observeAsState()
    // val outFileFuture by vm.outFileLive.observeAsState()
    val inFile by remember(inFileFuture) { derivedStateOf { inFileFuture?.finishedValue } }
    // val outFile by remember(outFileFuture) { derivedStateOf { outFileFuture?.finishedValue } }
    val inFileFrameFuture by vm.inFileFrameLive.observeAsState()

    val canConvert by remember(
        halt, converting, shareInfo, inFile, inMime, outMime
    ) {
        derivedStateOf {
            !halt
                    && !converting
                    && shareInfo != null
                    && inFile != null
                    && inMime != null
                    && outMime != null
        }
    }

    Column {
        AnimatedContent(targetState = inFileFuture, label = "inFileInfo_Animation") {
            when (it) {
                is FutureValue.Running -> {
                    Log.d(ConvertActivity::class.java.simpleName, "inFileFuture: Running")
                    LinearProgressIndicator()
                    halt = true
                }

                is FutureValue.Failed.Error -> {
                    Log.d(
                        ConvertActivity::class.java.simpleName,
                        "inFileFuture: Error: ${it.error}"
                    )
                    Icon(
                        Icons.Rounded.Warning,
                        null,
                        modifier = Modifier.size(32.dp),
                        tint = colorResource(R.color.surface)
                    )
                    Text(it.error.toString())
                    halt = true
                }

                is FutureValue.Finished -> {
                    Log.d(
                        ConvertActivity::class.java.simpleName,
                        "inFileFuture: Finished: ${it.value.mime}"
                    )
                    halt = false
                }
            }
        }

        AnimatedVisibility(visible = canConvert) {
            val expandedMimesState = rememberSaveable(inMime) { mutableStateOf(inMime == null) }
            val expandedMimes by expandedMimesState

            Column(Modifier.padding(horizontal = Consistent.padRegular)) {
                Surface(
                    shape = Consistent.shape,
                    modifier = Modifier
                        .padding(vertical = Consistent.padRegular)
                        .heightIn(),
                    //TODO: click image to show info
                ) {
                    @Suppress("LocalVariableName")
                    AnimatedContent(
                        targetState = inFileFrameFuture to expandedMimes,
                        label = "inFileFrame_Animation",
                        transitionSpec = { fadeIn() togetherWith fadeOut() }
                    ) { inFileFrameFuture_expandedMimes ->
                        val (frame, expanded) = inFileFrameFuture_expandedMimes
                        when (frame) {
                            is FutureValue.Pending,
                            is FutureValue.Running -> CircularProgressIndicator()

                            is FutureValue.Failed.Error -> Text(frame.error.toString())

                            is FutureValue.Finished -> {
                                Image(
                                    rememberAsyncImagePainter(inFileFrameFuture!!.finishedValue!!.absolutePath),
                                    null,
                                    alignment = Alignment.Center,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = (if (expanded) 100 else 500).dp),
                                )
                            }
                        }
                    }
                }

                Column(Modifier.padding(Consistent.padRegular)) {
                    Text("From: ${inMime!!.mime}")
                    Text(
                        "To: " + (outMime?.mime ?: "(Not selected...)"),
                        color = colorResource(R.color.secondary)
                    )
                }

                ExpandableGroup(
                    modifier = Modifier.padding(Consistent.padRegular),
                    title = outMime?.name ?: "Select target format:",
                    expand = expandedMimesState,
                    contentHeight = 300.dp,
                    items = convertibleMimesList(shareInfo!!.mime, outMime) {
                        saveOutMime(it)
                        expandedMimesState.value = false
                    }
                )
            }
        }

        AnimatedVisibility(visible = converting) {
            LinearProgressIndicator(
                Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
        }

        Row(
            modifier = Modifier.padding(
                horizontal = Consistent.padMedium,
                vertical = Consistent.padRegular
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = { activity.finish() }) {
                Text("Cancel")
            }
            Spacer(Modifier.weight(1f))
            Button(
                enabled = canConvert,
                onClick = {
                    if (!canConvert) return@Button
                    converting = true
                    vm.convert(activity, outMime!!,
                        onStatistics = { stats ->
                            //TODO: conversion stats
                        }
                    ) {
                        converting = false
                        vm.share(activity, it.value, outMime)
                    }
                }
            ) {
                Text("Convert")
            }
        }
    }
}


private fun convertibleMimesList(
    mime: String,
    outMime: MimeType?,
    onClick: (MimeType) -> Unit
): List<ExpandableGroupItemFactory> {
    val convertibleList = MimeTypeUtils.getConvertibleList(mime)
        ?: return emptyList()
    return convertibleList.map {
        return@map { padding, _ ->
            val isSelected by remember(outMime) { derivedStateOf { outMime?.mime == it.mime } }
            AnimatedContent(
                targetState = isSelected,
                label = "MimeListItem_Animation"
            ) { selected ->
                Surface(
                    modifier = if (selected) Modifier
                    else Modifier.clickable { onClick(it) },
                    color = colorResource(
                        if (selected) R.color.secondaryContainer
                        else R.color.background
                    ),
                ) {
                    Row(
                        modifier = Modifier.padding(padding),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val iconMod = Modifier
                            .size(48.dp)
                            .padding(Consistent.padRegular)
                        Icon(
                            if (it.category == MimeType.IMAGE) Icons.Rounded.AccountBox
                            else Icons.Rounded.PlayArrow,
                            null,
                            modifier = iconMod,
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(it.extension)
                            Row {
                                val color = LocalContentColor.current.copy(.8f)
                                val size = 10.sp
                                Text(
                                    it.category,
                                    fontWeight = FontWeight.Bold,
                                    color = color, fontSize = size, lineHeight = size / 2,
                                )
                                Text(
                                    " — ${it.name}",
                                    color = color, fontSize = size, lineHeight = size / 2,
                                )
                            }
                        }
                        if (selected) {
                            Icon(
                                Icons.Rounded.Check, null,
                                modifier = iconMod,
                                tint = colorResource(R.color.secondary),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DebugInfo() {
    if (!BuildConfig.DEBUG) return
    val activity = LocalContext.current as ConvertActivity
    val inFile by activity.vm.inFileLive.observeAsState()
    val outFile by activity.vm.outFileLive.observeAsState()
    val inFileFrame by activity.vm.inFileFrameLive.observeAsState()

    Column {
        Text("DEBUG:", fontSize = 18.sp, color = colorResource(R.color.primary))

        Spacer(Modifier.height(20.dp))

        val ie = inFile as? FutureValue.Failed.Error
        if (ie != null) {
            Text(ie.error.toString())
            Spacer(Modifier.height(10.dp))
        }
        val oe = outFile as? FutureValue.Failed.Error
        if (oe != null) {
            Text(oe.error.toString())
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(10.dp))

        val ir = inFile as? FutureValue.Running
        if (ir != null) {
            Text("inFile: running")
            Spacer(Modifier.height(10.dp))
        }
        val or = outFile as? FutureValue.Running
        if (or != null) {
            Text("outFile: running")
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(10.dp))

        Text(inFile?.finishedValue?.absolutePath ?: "No input file")
        Spacer(Modifier.height(10.dp))
        Text(outFile?.finishedValue?.absolutePath ?: "No output file")

        Spacer(Modifier.height(10.dp))

        Text(inFileFrame?.finishedValue?.absolutePath ?: "No frame image")
        Text("$inFileFrame")
        if (inFileFrame is FutureValue.Failed.Error) {
            val e = (inFileFrame as FutureValue.Failed.Error).error
            Text(e.toString())
        }
    }
}