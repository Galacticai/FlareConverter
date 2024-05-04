package com.galacticai.flareconverter.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.galacticai.flareconverter.ui.themes.v1.GalacticColorFamilyV1
import com.galacticai.flareconverter.util.Consistent
import global.common.ui.DimenUtils.paddedSize
import global.common.util.TextUtil.ELLIPSES
import androidx.compose.material.icons.outlined.Info as InfoOutlined
import androidx.compose.material.icons.rounded.Info as InfoFilled


object InfoBox {
    data class Config(
        /** null = no icon */
        val icon: ImageVector?,
        val containerColor: Color? = null,
        val contentColor: Color? = null,
        /** 0 = no icon */
        val iconSize: Dp = 24.dp,
    ) {
        @Composable
        fun Icon() {
            if (icon == null || iconSize <= 0.dp) return
            val pad = when (icon) {
                //? custom bottom pad for warning icons (visual center)
                Icons.Rounded.Warning,
                Icons.Rounded.WarningAmber -> PaddingValues(bottom = Consistent.Pad.smallXX)

                else -> PaddingValues()
            }
            Icon(
                modifier = Modifier.paddedSize(
                    DpSize(iconSize, iconSize),
                    pad
                ),
                imageVector = icon,
                contentDescription = "Info box icon",
            )
        }

        @Composable
        fun colors(): CardColors {
            val bg = containerColor ?: Color.Transparent
            val fg = contentColor ?: LocalTextStyle.current.color
            return CardDefaults.cardColors().copy(
                bg, fg,
            )
        }

        companion object {
            val neutral
                @Composable get() = Config(Icons.Rounded.InfoFilled)
            val info
                @Composable get() = Config(
                    Icons.Outlined.InfoOutlined,
                    GalacticColorFamilyV1.current.primary.colorContainer,
                    GalacticColorFamilyV1.current.primary.onColorContainer,
                )
            val warn
                @Composable get() = Config(
                    Icons.Rounded.WarningAmber,
                    GalacticColorFamilyV1.current.warning.colorContainer,
                    GalacticColorFamilyV1.current.warning.onColorContainer,
                )
            val warnOutline @Composable get() = warn.copy(icon = Icons.Rounded.WarningAmber)
            val error
                @Composable get() = Config(
                    Icons.Rounded.Close,
                    GalacticColorFamilyV1.current.error.colorContainer,
                    GalacticColorFamilyV1.current.error.onColorContainer,
                )
            val success
                @Composable get() = Config(
                    Icons.Rounded.Check,
                    GalacticColorFamilyV1.current.success.colorContainer,
                    GalacticColorFamilyV1.current.success.onColorContainer,
                )
        }
    }
}

@Composable
fun InfoBox(
    modifier: Modifier = Modifier,
    config: InfoBox.Config = InfoBox.Config.info,
    cornerRadius: Dp = Consistent.Pad.medium,
    elevation: CardElevation = CardDefaults.cardElevation(),
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    var height by remember { mutableStateOf(0.dp) }
    val isBig = remember(height, cornerRadius) {
        height / 3 > cornerRadius
    }

    val cornerRadius by animateDpAsState(
        targetValue =
                if (isBig) cornerRadius else height / 2,
        animationSpec = tween(250),
        label = "infoBoxCornerRadius"
    )
    val verticalAlignment = remember(isBig) {
        if (isBig) Alignment.Top
        else Alignment.CenterVertically
    }

    Card(
        modifier = Modifier
            .animateContentSize()
            .onSizeChanged {
                height = with(density) { it.height.toDp() }
            } then modifier,
        colors = config.colors(),
        shape = RoundedCornerShape(cornerRadius),
        elevation = elevation,
    ) {
        Row(
            Modifier.padding(
                top = Consistent.Pad.regular,
                bottom = Consistent.Pad.regular,
                start = Consistent.Pad.regular,
                end = Consistent.Pad.regular * 1.5f,
            ),
            verticalAlignment = verticalAlignment,
            horizontalArrangement = Arrangement.spacedBy(Consistent.Pad.small),
        ) {
            config.Icon()
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InfoBoxPreviewNeutral() {
    InfoBox(config = InfoBox.Config.neutral) {
        Text(
            "Something very mild and useless to say really",
            textAlign = TextAlign.Justify
        )
    }
}

@Preview
@Composable
private fun InfoBoxPreviewInfo() {
    InfoBox(config = InfoBox.Config.info) {
        Text(
            "Just letting you know that you should sleep probably. No worries though.",
            textAlign = TextAlign.Justify
        )
    }
}

@Preview
@Composable
private fun InfoBoxPreviewWarn() {
    InfoBox(config = InfoBox.Config.warn) {
        Text("This might cause severe headaches$ELLIPSES")
    }
}

@Preview
@Composable
private fun InfoBoxPreviewError() {
    InfoBox(config = InfoBox.Config.error) {
        Text("Error while running the runner that runs")
    }
}

@Preview
@Composable
private fun InfoBoxPreviewSuccess() {
    InfoBox(config = InfoBox.Config.success) {
        Text("Task finished successfully")
    }
}


@Preview
@Composable
private fun InfoBoxPreviewCustom() {
    InfoBox(
        config = InfoBox.Config(
            icon = Icons.Rounded.Android,
            containerColor = GalacticColorFamilyV1.current.secondary.color,
            contentColor = GalacticColorFamilyV1.current.secondary.onColor,
        )
    ) {
        Text("Android is on the door, open it.")
    }
}

@Preview
@Composable
private fun InfoBoxPreviewCustomNoIcon() {
    InfoBox(
        config = InfoBox.Config(
            icon = null,
            containerColor = GalacticColorFamilyV1.current.primary.color,
            contentColor = GalacticColorFamilyV1.current.primary.onColor,
        )
    ) {
        Text("Why would you even use icons")
    }
}