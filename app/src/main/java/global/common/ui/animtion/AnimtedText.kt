package global.common.ui.animtion

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

val animatedTextTransition
    get() = (fadeIn(tween(250)) +
            scaleIn(
                initialScale = 0.7f,
                animationSpec = tween(250)
            ) +
            slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = tween(250)
            ))
        .togetherWith(
            fadeOut(tween(150))
        )

@Composable
fun AnimatedText(
    text: String,
    modifier: Modifier = Modifier,
    content: @Composable (Char, Modifier) -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        text.forEachIndexed { index, character ->
            key(index) {
                AnimatedContent(
                    targetState = character,
                    transitionSpec = { animatedTextTransition },
                    label = "character-$index"
                ) { animatedCharacter ->
                    content(animatedCharacter, Modifier.animateContentSize())
                }
            }
        }
    }
}

