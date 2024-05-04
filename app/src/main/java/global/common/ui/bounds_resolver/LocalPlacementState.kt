package global.common.ui.bounds_resolver

import androidx.compose.runtime.compositionLocalOf


val LocalPlacementState = compositionLocalOf<PlacementState> {
    error("Placement state is not provided")
}