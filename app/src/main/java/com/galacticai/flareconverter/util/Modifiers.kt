package com.galacticai.flareconverter.util

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativePaint
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.galacticai.flareconverter.ui.themes.GalacticTheme
import com.galacticai.flareconverter.util.Modifiers.outsideShadow
import com.galacticai.flareconverter.util.Modifiers.radiusBy
import global.common.ui.DimenUtils.toDp
import kotlin.math.ceil

object Modifiers {
    fun Modifier.onPress(
        key: Any? = null,
        onChange: (Boolean) -> Unit
    ) = this.pointerInput(key ?: Unit) {
        awaitEachGesture {
            awaitFirstDown(
                requireUnconsumed = false,
                pass = PointerEventPass.Initial
            )
            onChange(true)
            try {
                do {
                    val event = awaitPointerEvent(pass = PointerEventPass.Initial)
                } while (event.changes.any { it.pressed })
            } finally {
                onChange(false)
            }
        }
    }

    @Composable
    fun Modifier.expandIf(
        isExpanded: Boolean,
        delta: DpSize,
        spec: AnimationSpec<Dp> = Consistent.Animation.getSpring(),
    ): Modifier {
        val density = LocalDensity.current
        var size by remember { mutableStateOf<DpSize?>(null) }

        val deltaWidthAnimated by animateDpAsState(
            targetValue = if (isExpanded) delta.width else 0.dp,
            animationSpec = spec,
            label = "expandWidth",
        )

        val deltaHeightAnimated by animateDpAsState(
            targetValue = if (isExpanded) delta.height else 0.dp,
            animationSpec = spec,
            label = "expandHeight",
        )

        val sizeModifier = remember(isExpanded, size, deltaWidthAnimated, deltaHeightAnimated) {
            size?.let {
                var mod: Modifier = Modifier
                if (deltaWidthAnimated > 0.dp) {
                    mod = mod.requiredWidth(it.width + deltaWidthAnimated)
                }
                if (deltaHeightAnimated > 0.dp) {
                    mod = mod.requiredHeight(it.height + deltaHeightAnimated)
                }
                mod
            } ?: Modifier
        }

        return this.onSizeChanged {
            if (deltaWidthAnimated > 0.dp || deltaHeightAnimated > 0.dp)
                return@onSizeChanged
            size = it.toDp(density)
        } then sizeModifier
    }

    fun Modifier.expandOnPress(
        expansion: DpSize,
        expressiveSpring: AnimationSpec<Dp>,
        onPressed: ((Boolean) -> Unit)? = null,
    ): Modifier = composed {
        var isPressed by remember { mutableStateOf(false) }
        this
            .onPress(Unit) {
                isPressed = it
                onPressed?.invoke(it)
            }
            .expandIf(
                isPressed,
                expansion, expressiveSpring
            )
    }

    /**
     * this = container radius
     * @param containerPadding container padding
     * @return child radius
     */
    fun Dp.radiusBy(
        containerPadding: PaddingValues,
        layoutDirection: LayoutDirection
    ): Dp {
        val maxPadding = with(containerPadding) {
            maxOf(
                calculateTopPadding(),
                calculateBottomPadding(),
                calculateEndPadding(layoutDirection),
                calculateStartPadding(layoutDirection)
            )
        }
        return (this - maxPadding).coerceAtLeast(0.dp)
    }

    /** @see radiusBy */
    infix fun Dp.radiusBy(containerPadding: PaddingValues) =
        this.radiusBy(containerPadding, LayoutDirection.Ltr)


    /** @param brush gradient brush (black = opaque) */
    fun Modifier.alphaGradient(
        brush: Brush = Brush.verticalGradient(
            listOf(Color.Black, Color.Transparent)
        )
    ) = this
        .graphicsLayer {
            compositingStrategy = CompositingStrategy.Offscreen
        }
        .drawWithCache {
            onDrawWithContent {
                drawContent()
                drawRect(brush, blendMode = BlendMode.DstIn)
            }
        }


    fun Outline.toPath() = Path().apply {
        when (this@toPath) {
            is Outline.Rectangle -> addRect(rect)
            is Outline.Rounded -> addRoundRect(roundRect)
            is Outline.Generic -> addPath(path)
        }
    }


    /**
     * better than compose built-in shadow
     * - draw shadow outside the [shape] so the background can be translucent with no issues
     */
    fun Modifier.outsideShadow(
        elevation: Dp,
        shape: Shape,
        color: Color = Color.Black.copy(alpha = 0.25f),
    ): Modifier = drawWithCache {
        if (elevation <= 0f.dp || color.alpha == 0f) {
            return@drawWithCache onDrawWithContent { drawContent() }
        }
        val elevationPx = (elevation * 2).toPx()
        val outline = shape.createOutline(size, layoutDirection, this)
        val path = outline.toPath()
        val outlineExtra =
            shape.createOutline(Size(size.width + 1L, size.height + 1L), layoutDirection, this)
        val pathExtra = outlineExtra.toPath()


        val shadowPaint = Paint().apply {
            nativePaint.setShadowLayer(
                elevationPx,
                0f,
                elevationPx * 0.35f,
                color.toArgb(),
            )
        }
        val padding = ceil(elevationPx * 3f + elevationPx)
        val layerBounds = Rect(
            left = -padding,
            top = -padding,
            right = size.width + padding,
            bottom = size.height + padding,
        )
        onDrawWithContent {
            drawIntoCanvas {
                it.apply {
                    saveLayer(layerBounds, Paint())
                    clipPath(pathExtra, ClipOp.Difference)
                    drawPath(path, shadowPaint)
                    restore()
                }
            }
            drawContent()
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun Custom() = GalacticTheme {
    Box(
        Modifier.background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        Box(Modifier.background(MaterialTheme.colorScheme.secondaryContainer))
        Card(
            Modifier
                .padding(60.dp)
                .size(400.dp)
                .outsideShadow(20.dp, Consistent.Shape.Rounded.all),
            colors = CardDefaults.cardColors().copy(
                MaterialTheme.colorScheme.secondaryContainer.copy(.25f)
            ),
            shape = Consistent.Shape.Rounded.all,
        ) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(
                        "bg alpha = 0.5\n\n"
                                + "custom shadow"
                                + "\n\n(respects shape)"
                                + "\n(transparency supported)",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Androidx() = GalacticTheme {
    Box(
        Modifier.background(MaterialTheme.colorScheme.primaryContainer),
    ) {
        Card(
            Modifier
                .padding(60.dp)
                .size(400.dp)
                .shadow(20.dp, Consistent.Shape.Rounded.all),
            colors = CardDefaults.cardColors().copy(
                MaterialTheme.colorScheme.secondaryContainer.copy(.25f)
            ),
            shape = Consistent.Shape.Rounded.all,
        ) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text(
                    "bg alpha = 0.5\n\n"
                            + "shadow by google"
                            + "\n\n(draws behind)"
                            + "\n(transparency reveals\nshadow artifacts)",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
