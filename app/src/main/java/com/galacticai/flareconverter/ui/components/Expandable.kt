package com.galacticai.flareconverter.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Devices
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.galacticai.flareconverter.ui.components.expressive.ExpressiveRadius
import com.galacticai.flareconverter.ui.components.expressive.rememberExpressiveRadius
import com.galacticai.flareconverter.ui.themes.GalacticTheme
import com.galacticai.flareconverter.util.Consistent
import global.common.ui.ExpandIcon

object Expandable {
    typealias ContentConditional<T> = @Composable T.(
        expanded: Boolean,
        modifierRecommended: Modifier
    ) -> Unit

    typealias Content<T> = @Composable T.(
        modifierRecommended: Modifier
    ) -> Unit

    object Defaults {
        val enter = fadeIn() + expandVertically()
        val exit = shrinkVertically() + fadeOut()

        private val colors @Composable get() = MaterialTheme.colorScheme
        val headerBackground @Composable get() = colors.surface to colors.surface.copy(.5f)
        val contentBackground @Composable get() = colors.background.copy(.01f) to colors.surface
    }
}


@Composable
fun Expandable(
    modifier: Modifier = Modifier,
    expandState: MutableState<Boolean> = rememberSaveable { mutableStateOf(false) },

    enter: EnterTransition = Expandable.Defaults.enter,
    exit: ExitTransition = Expandable.Defaults.exit,

    headerBorder: BorderStroke? = null,
    contentBorder: BorderStroke? = null,
    headerBackground: Pair<Color, Color> = Expandable.Defaults.headerBackground,
    contentBackground: Pair<Color, Color> = Expandable.Defaults.contentBackground,
    radiusRange: ExpressiveRadius.Absolute = Consistent.Shape.Radius.expressiveDp,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(Consistent.Pad.smallX),

    icon: Expandable.ContentConditional<RowScope>? = null,
    title: Expandable.ContentConditional<RowScope>,
    showExpandIcon: Boolean = true,

    content: Expandable.Content<ColumnScope>,
) = Column(
    modifier.animateContentSize(),
    verticalArrangement = verticalArrangement,
) {
    var expand by expandState
    var radius by remember { mutableStateOf(radiusRange.neutral) }
    val radiusMiddleFactor by animateFloatAsState(
        if (expand) .5f else 1f
    )
    val radiusMiddle = remember(radius, radiusMiddleFactor) {
        //! intentional: radius is already animated
        if (expand) radius * radiusMiddleFactor else radius
    }
    val expressive = rememberExpressiveRadius(radiusRange) {
        radius = it as Dp
    }
    val headerShape = remember(radiusRange, radius, radiusMiddle) {
        RoundedCornerShape(
            topStart = radius, topEnd = radius,
            bottomStart = radiusMiddle, bottomEnd = radiusMiddle,
        )
    }
    val contentShape = remember(radiusRange, radiusMiddle) {
        RoundedCornerShape(
            //! intentional: follow header animation only on the top half near the header
            topStart = radiusMiddle, topEnd = radiusMiddle,
            bottomStart = radiusRange.neutral, bottomEnd = radiusRange.neutral,
        )
    }

    val headerBgAnimated by animateColorAsState(
        if (expand) headerBackground.second else headerBackground.first
    )
    val contentBgAnimated by animateColorAsState(
        if (expand) contentBackground.second else contentBackground.first
    )

    Card(
        colors = CardDefaults.cardColors(headerBgAnimated),
        shape = headerShape,
        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
                then expressive.modifierPress,
        border = headerBorder,
    ) {
        Box(
            Modifier.clickable { expand = !expand }
        ) {
            Row(
                Modifier.fillMaxSize().padding(
                    horizontal = Consistent.Pad.regular,
                    vertical = Consistent.Pad.smallX,
                ),
                horizontalArrangement = Arrangement.spacedBy(Consistent.Pad.regular),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                icon?.invoke(
                    this@Row, expand,
                    Modifier.padding(start = Consistent.Pad.smallX, end = Consistent.Pad.smallXX)
                )
                title(
                    expand,
                    Modifier.weight(1f)
                        .padding(vertical = Consistent.Pad.smallX).let {
                            if (icon == null) it.padding(start = Consistent.Pad.smallX)
                            else it
                        }
                )

                if (showExpandIcon) ExpandIcon.Custom(
                    expand,
                    iconOpen = Icons.Rounded.KeyboardArrowDown,
                    iconClose = Icons.Rounded.Remove,
                )
            }
        }
    }

    AnimatedVisibility(
        expand,
        enter = enter, exit = exit,
    ) {
        Card(
            Modifier.fillMaxWidth(),
            shape = contentShape,
            colors = CardDefaults.cardColors().copy(contentBgAnimated),
            border = contentBorder,
        ) {
            content(
                Modifier.padding(
                    vertical = Consistent.Pad.regular,
                    horizontal = Consistent.Pad.moderate
                )
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun Preview() = GalacticTheme {
    val headerBackground = MaterialTheme.colorScheme.surface.let {
        it.copy(.25f) to it.copy(.5f)
    }
    val contentBackground = MaterialTheme.colorScheme.surface.let {
        it.copy(.25f) to it.copy(.5f)
    }
    val headerBorder = BorderStroke(.5.dp, headerBackground.second.copy(.8f))
    val contentBorder = BorderStroke(.5.dp, contentBackground.second.copy(.8f))
    Column {
        repeat(2) { i ->
            Expandable(
                expandState = rememberSaveable { mutableStateOf(true) },
                modifier = Modifier.fillMaxWidth().padding(Consistent.Pad.regular),
                headerBackground = headerBackground,
                contentBackground = contentBackground,
                headerBorder = headerBorder,
                contentBorder = contentBorder,
                icon = if (i == 0) null else { expanded, modifier ->
                    Icon(Icons.Rounded.Devices, "", modifier)
                },
                title = { expanded, modifier ->
                    val weight by animateIntAsState(
                        if (expanded) FontWeight.Light.weight
                        else FontWeight.SemiBold.weight
                    )
                    Text("Expandable header", modifier, fontWeight = FontWeight(weight))
                },
            ) { modifier ->
                Text(
                    text = "This is the expandable content.",
                    modifier = modifier,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}