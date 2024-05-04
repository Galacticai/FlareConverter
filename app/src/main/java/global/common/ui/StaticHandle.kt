package global.common.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import global.common.ui.DimenUtils.paddedSize

@Composable
fun StaticHandle(
    modifier: Modifier = Modifier,
    width: Dp,
    height: Dp,
    color: Color = MaterialTheme.colorScheme.surface,
    shape: Shape = CardDefaults.shape
) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier.paddedSize(
                width, height,
                PaddingValues(height * 3)
            ),
            colors = CardDefaults.cardColors().copy(
                containerColor = color
            ),
            shape = shape,
        ) { }
    }
}