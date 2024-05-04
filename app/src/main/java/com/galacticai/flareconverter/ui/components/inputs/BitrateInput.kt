package com.galacticai.flareconverter.ui.components.inputs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.galacticai.flareconverter.models.MediaFile
import com.galacticai.flareconverter.ui.share_activity.components.ShareActivityView
import com.galacticai.flareconverter.util.Consistent
import global.common.models.amount.Amount
import global.common.models.amount.units.BaseUnit
import global.common.models.amount.units.MetricSystem
import kotlin.math.floor

@Composable
fun BitrateInput(
    modifier: Modifier = Modifier,
    info: MediaFile,
    colors: ShareActivityView.Colors,
    /** last used value to also include in the list (b/s) */
    bpsLast: Double? = null,
    onSelect: (amount: Amount) -> Unit
) {
    val bps = BaseUnit.bitPerSecond()
    val mbps = MetricSystem.mega() and bps

    val amountMedia = info.mainStreamMime!!.bitrateAmount!!
    val bpsInitial = remember(bpsLast, amountMedia) {
        bpsLast ?: amountMedia.baseValue
    }
    var bpsCurrent by rememberSaveable(bpsInitial) {
        mutableDoubleStateOf(bpsInitial)
    }
    //! intentional: Amount is not saveable
    val amountCurrent = remember(bpsCurrent) {
        Amount(bpsCurrent, bps).toUnit(mbps)
    }

    val inputState = rememberTextFieldState(amountCurrent.value.toString())

    LaunchedEffect(amountCurrent) {
        val value = amountCurrent.value.toString()
        if (inputState.text == value) return@LaunchedEffect
        inputState.setTextAndPlaceCursorAtEnd(value)
    }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(Consistent.Pad.regular)) {
        Text(
            "Amount of data per second",
            Modifier.padding(horizontal = Consistent.Pad.regular)
        )

        NumberInput(
            state = inputState,
            leading = {
                IconButton(
                    onClick = { bpsCurrent = bpsInitial },
                    modifier = Modifier.padding(start = Consistent.Pad.smallX)
                ) {
                    Icon(Icons.Rounded.Refresh, contentDescription = "Reset")
                }
            },
            trailing = {
                Text(
                    mbps.toString(""),
                    Modifier.padding(horizontal = Consistent.Pad.medium)
                )
            },
            colors = TextFieldDefaults.colors().copy(
                focusedContainerColor = colors.bg1.copy(alpha = .5f),
                unfocusedContainerColor = colors.bg1,
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = Consistent.Shape.Rounded.pill
        ) {
            bpsCurrent = mbps.toBase(it)
            onSelect(amountCurrent)
            true
        }

        val factor =
                if (amountMedia.baseValue > 0.0)
                    amountCurrent.baseValue / amountMedia.baseValue
                else 0.0
        Text(
            "Estimated difference: ${floor(factor * 100).toInt()}% of the original size",
            Modifier.padding(horizontal = Consistent.Pad.regular)
        )
    }
}