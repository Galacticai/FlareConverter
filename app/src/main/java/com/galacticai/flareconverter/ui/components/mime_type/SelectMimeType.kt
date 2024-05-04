package com.galacticai.flareconverter.ui.components.mime_type

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SplitButton
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
import com.galacticai.flareconverter.models.MimeType
import com.galacticai.flareconverter.util.Consistent
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

/** select [MimeType] by category and type ([SplitButton]) */
@Composable
fun MimeTypeSelectByCategory(
    modifier: Modifier = Modifier,
    /** allowed mime categories */
    allowedMimes: List<MimeType> = MimeType.Inputs.all,
    onSelect: (MimeType) -> Unit
) {
    val categories by remember {
        derivedStateOf {
            allowedMimes.map { it.category }.toSet()
        }
    }
    assert(categories.isNotEmpty())
    val categoriesSet = MimeType.categories.toSet()
    for (category in categories) {
        assert(categoriesSet.contains(category))
    }

    val selectedCategoryState = rememberSaveable(categories) {
        mutableStateOf(categories.first())
    }
    var selectedCategory by selectedCategoryState

    /** mimes in current category x intersected with [allowedMimes] */
    val categoryMimes by remember {
        derivedStateOf {
            MimeType.Inputs.of(selectedCategory)!!
                .intersect(allowedMimes.toSet())
        }
    }

    val openCategoryState = remember { mutableStateOf(false) }
    var openCategory by openCategoryState

    val openMimeState = remember { mutableStateOf(false) }
    val selectedMimeState = remember(categoryMimes) {
        mutableStateOf(categoryMimes.first())
    }
    var openMime by openMimeState
    var selectedMime by selectedMimeState

    LaunchedEffect(selectedMime) {
        onSelect(selectedMime)
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
            Text(it.key!!.upper)
            ExpandIcon.ArrowMinus(openMime)
        },
        text = { Text(it.key!!.upper) },
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
    mimes: List<MimeType>,
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
            MimeTypeSelect(mimes = MimeType.Inputs.videos) {
                selected = it
            }
            Text("selected : $selected")

            var selectedByCat by remember { mutableStateOf<MimeType>(MimeType.Mp4) }
            MimeTypeSelectByCategory { selectedByCat = it }
            Text("selected by category : $selectedByCat")
        }
    }
}
