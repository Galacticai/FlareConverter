package com.galacticai.flareconverter.ui.themes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.galacticai.flareconverter.R
import com.galacticai.flareconverter.util.Consistent

const val FontName = "Quicksand"

@Composable
fun GalacticTheme(
    isDark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorSchemeBase =
        if (isDark) darkColorScheme() else lightColorScheme()

    val colorScheme = colorSchemeBase.copy(
        primary = colorResource(R.color.primary),
        onPrimary = colorResource(R.color.onPrimary),
        primaryContainer = colorResource(R.color.primaryContainer),
        onPrimaryContainer = colorResource(R.color.onPrimaryContainer),
        secondary = colorResource(R.color.secondary),
        onSecondary = colorResource(R.color.onSecondary),
        secondaryContainer = colorResource(R.color.secondaryContainer),
        onSecondaryContainer = colorResource(R.color.onSecondaryContainer),
        background = colorResource(R.color.background),
        onBackground = colorResource(R.color.onBackground),
        surface = colorResource(R.color.surface),
        surfaceVariant = colorResource(R.color.background),
        onSurface = colorResource(R.color.onSurface),
        error = colorResource(R.color.error),
        onError = colorResource(R.color.onError),
        errorContainer = colorResource(R.color.errorContainer),
        onErrorContainer = colorResource(R.color.onErrorContainer),
    )

    val shapes = MaterialTheme.shapes.copy(
        extraSmall = MaterialTheme.shapes.extraSmall.copy(CornerSize(Consistent.Pad.smallX)),
        small = MaterialTheme.shapes.small.copy(CornerSize(Consistent.Pad.regular)),
        medium = MaterialTheme.shapes.medium.copy(CornerSize(Consistent.Pad.medium)),
        large = MaterialTheme.shapes.large.copy(CornerSize(Consistent.Pad.big)),
        extraLarge = MaterialTheme.shapes.extraLarge.copy(CornerSize(Consistent.Pad.large)),
    )

    MaterialTheme(colorScheme, shapes) {
        content()
    }
}

@Composable
fun ThemedScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(MaterialTheme.colorScheme.background),
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable (PaddingValues) -> Unit
) {
    GalacticTheme {
        Scaffold(
            modifier,
            topBar,
            bottomBar,
            snackbarHost,
            floatingActionButton,
            floatingActionButtonPosition,
            containerColor,
            contentColor,
            contentWindowInsets,
            content,
        )
    }
}