package com.galacticai.flareconverter.ui.components.mime_type

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.galacticai.flareconverter.models.mimes.MimeType
import com.galacticai.flareconverter.util.Consistent
import com.galacticai.flareconverter.util.media.MediaCapabilities
import global.common.ui.ExpandIcon
import global.common.ui.SplitMenu
import global.common.util.TextUtil.sentenceCase
import global.common.util.TextUtil.upper

object MimeTypeSelect {
    val menuShape = Consistent.Shape.Rounded.all
    val contentSpacing = Consistent.Pad.smallX
    fun contentPad(isStartButton: Boolean): PaddingValues {
        val regular = Consistent.Pad.regular
        val moderate = Consistent.Pad.moderate
        val start = if (isStartButton) regular else moderate
        val end = if (isStartButton) moderate else regular * .5f
        return PaddingValues(start = start, end = end)
    }
}

/** select [MimeType] by category and type ([SplitButtonLayout]) */
@Composable
fun MimeTypeSelectByCategory(
    modifier: Modifier = Modifier,
    /** allowed mime categories */
    allowedMimes: Set<MimeType> = MediaCapabilities.Supported.mimes(),
    onSelect: (MimeType) -> Unit
) {
    val categories by remember {
        derivedStateOf {
            allowedMimes.map { it.category }.toSet()
        }
    }
    assert(categories.isNotEmpty())
    val categoriesSet = MimeType.CATEGORIES.toSet()
    for (category in categories) {
        assert(categoriesSet.contains(category))
    }

    val selectedCategoryState = rememberSaveable(categories) {
        mutableStateOf(categories.first())
    }
    var selectedCategory by selectedCategoryState
    val selectedMimeState = remember { mutableStateOf<MimeType?>(null) }
    var selectedMime by selectedMimeState

    /** mimes in current category x intersected with [allowedMimes] */
    val categoryMimes by remember(selectedMime) {
        derivedStateOf<Set<MimeType>> {
            val current = selectedMime
                ?: return@derivedStateOf emptySet()
            val baseMimes = MediaCapabilities.Supported.outputsOf(current)
            baseMimes?.intersect(allowedMimes).orEmpty()
        }
    }

    val openCategoryState = remember { mutableStateOf(false) }
    var openCategory by openCategoryState

    val openMimeState = remember { mutableStateOf(false) }
    var openMime by openMimeState

    LaunchedEffect(selectedMime) {
        selectedMime?.let { onSelect(it) }
    }

    val categoryProps = SplitMenu.Props(
        menuShape = MimeTypeSelect.menuShape,
        items = categories,
        openState = openCategoryState,
        selectedState = selectedCategoryState,
        renderSelected = {
            MimeIcon(it)
            Text(it.sentenceCase)
        },
        leadingIcon = { MimeIcon(it) },
        text = { Text(it.sentenceCase) },
        contentPadding = MimeTypeSelect.contentPad(true),
        contentSpacing = MimeTypeSelect.contentSpacing,
    ) { openCategory = !openCategory }

    val mimeProps = SplitMenu.Props(
        menuShape = MimeTypeSelect.menuShape,
        items = categoryMimes,
        openState = openMimeState,
        selectedState = selectedMimeState,
        renderSelected = {
            if (it == null) return@Props
            Text(it.key.upper)
            ExpandIcon.ArrowMinus(openMime)
        },
        text = {
            if (it == null) return@Props
            Text(it.key.upper)
        },
        contentPadding = MimeTypeSelect.contentPad(false),
        contentSpacing = MimeTypeSelect.contentSpacing,
    ) { openMime = !openMime }

    SplitMenu.Buttons(
        modifier,
        categoryProps to mimeProps
    )
}

/** select [MimeType] single [OutlinedButton] */
@Composable
fun MimeTypeSelect(
    modifier: Modifier = Modifier,
    menuModifier: Modifier = Modifier,
    mimes: Set<MimeType>,
    onSelect: (MimeType) -> Unit,
) {
    assert(mimes.isNotEmpty())

    val openState = remember { mutableStateOf(false) }
    val selectedState = remember(mimes) { mutableStateOf(mimes.first()) }
    var open by openState
    var selected by selectedState

    LaunchedEffect(selected) {
        onSelect(selected)
    }

    val props = SplitMenu.Props(
        menuModifier = menuModifier,
        menuShape = MimeTypeSelect.menuShape,
        items = mimes,
        openState = openState,
        selectedState = selectedState,
        renderSelected = {
            Text(it.key!!.upper)
            ExpandIcon.ArrowMinus(open)
        },
        text = { Text(it.key!!.upper) },
        contentPadding = MimeTypeSelect.contentPad(true),
        contentSpacing = MimeTypeSelect.contentSpacing,
    ) { open = !open }

    OutlinedButton(
        modifier = modifier,
        onClick = { open = !open },
        contentPadding = PaddingValues(
            start = Consistent.Pad.medium,
            end = Consistent.Pad.regular * .75f
        )
    ) { SplitMenu.ButtonContent(props) }
}


@Preview(showSystemUi = true)
@Composable
private fun MimeTypeSelectByCategoryPreview() {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(
            Modifier.padding(Consistent.Pad.large),
            verticalArrangement = Arrangement.spacedBy(Consistent.Pad.regular),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            var selected by remember { mutableStateOf<MimeType>(MimeType.Mp4) }
            MimeTypeSelect(
                mimes = setOf(
                    MimeType.Avi, MimeType.Flv, MimeType.Webm,
                    MimeType.Mkv, MimeType.Mp4, MimeType.Mpeg4, MimeType.M4v,
                    MimeType.Mpeg, MimeType.Quicktime, MimeType.Ogv, MimeType.Wmv,
                )
            ) {
                selected = it
            }
            Text("selected : $selected")

            var selectedByCat by remember { mutableStateOf<MimeType>(MimeType.Mp4) }
            MimeTypeSelectByCategory { selectedByCat = it }
            Text("selected by category : $selectedByCat")

        }
    }
}
