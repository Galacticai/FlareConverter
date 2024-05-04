package com.galacticai.flareconverter.ui.share_activity.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BlurCircular
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.galacticai.flareconverter.R
import com.galacticai.flareconverter.util.Consistent
import global.common.ui.DimenUtils.paddedSize

@Composable
fun AppHeader(modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .fillMaxWidth()
            .paddedSize(
                height = Consistent.Size.header,
                pad = PaddingValues(
                    horizontal = Consistent.Pad.regular,
                    vertical = Consistent.Pad.small
                )
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        //TODO
        Icon(
            Icons.Rounded.BlurCircular,
            null,
            modifier = Modifier.size(32.dp)
        )
        Text(
            stringResource(R.string.app_name),
            fontSize = Consistent.Text.header * 1.25
        )
        //TODO
        Icon(
            Icons.Rounded.BlurCircular,
            null,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun AppHeaderPreview() {
    Column {
        AppHeader()
        Card(
            Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
        ) {
            Text(
                text = "stuff  stuff  stuff  stuff  stuff  stuff  stuff  stuff  stuff  stuff  stuff  stuff  stuff  stuff" +
                        " stuff  stuff  stuff  stuff  stuff  stuff  stuff  stuff  stuff  stuff  stuff  stuff  stuff  stuff" +
                        " stuff  stuff  stuff  stuff  stuff  stuff  stuff  stuff  stuff  stuff  stuff  stuff  stuff  stuff",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                textAlign = TextAlign.Center,
                fontSize = 18.sp
            )
        }
    }
}