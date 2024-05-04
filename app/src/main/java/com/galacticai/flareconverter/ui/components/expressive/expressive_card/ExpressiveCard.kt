package com.galacticai.flareconverter.ui.components.expressive.expressive_card

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.galacticai.flareconverter.util.Consistent
import global.common.ui.ExpressiveInteraction
import global.common.ui.ExpressiveInteraction.isInteracting

@Suppress("UNCHECKED_CAST") //! intentional: type is checked by sealed class (can only be `Dp` or `Float`)
@Composable
fun ExpressiveCard(
    modifier: Modifier = Modifier,
    radius: ExpressiveRadius<*> = Consistent.Shape.Radius.expressiveDp,
    animationSpec: AnimationSpec<*>? = null,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    var isCardPressed by remember { mutableStateOf(false) }

    val targetValue = if (isCardPressed) radius.clicked else radius.neutral
    val shape = when (radius) {
        is ExpressiveRadius.Absolute -> {
            val spec = (animationSpec as? AnimationSpec<Dp>)
                ?: ExpressiveInteraction.getExpressiveSpring(true)
            val cardRadius by animateDpAsState(
                targetValue as Dp,
                animationSpec = spec,
                label = "expressive card radius dp"
            )
            RoundedCornerShape(cardRadius.coerceAtLeast(0.dp))
        }

        is ExpressiveRadius.Percent -> {
            val spec = (animationSpec as? AnimationSpec<Float>)
                ?: tween()
            val cardRadius by animateFloatAsState(
                targetValue as Float,
                animationSpec = spec,
                label = "expressive card radius percent"
            )
            RoundedCornerShape(cardRadius.coerceIn(0f..100f))
        }
    }

    Card(
        Modifier.isInteracting {
            isCardPressed = !isCardPressed
        } then modifier,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        content = content,
    )
}

@Preview(showBackground = true)
@Composable
private fun ExpressiveCardPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ExpressiveCard(
            radius = Consistent.Shape.Radius.expressive,
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        ) {
            Text(
                text = "Percent radius card",
                modifier = Modifier.padding(24.dp)
            )
        }

        ExpressiveCard(
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        ) {
            Text(
                text = "Dp radius card",
                modifier = Modifier.padding(24.dp)
            )
        }
    }
}