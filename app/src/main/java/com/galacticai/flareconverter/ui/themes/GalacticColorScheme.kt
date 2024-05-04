package com.galacticai.flareconverter.ui.themes

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.galacticai.flareconverter.ui.themes.models.ColorSchemeSpec
import com.galacticai.flareconverter.util.Settings

object GalacticColorScheme {
    /** the actual theme mode in use right now
     * @see Settings.Theme
     * @see current
     */
    @Composable
    fun isDark(
        //? performance: avoid new-calling when possible
        state: State<ColorSchemeSpec>? = null
    ): Boolean {
        val theme by state ?: Settings.Theme.rememberObject()
        return theme.isDark ?: isSystemInDarkTheme()
    }

    /** the actual theme mode in use right now
     * @see Settings.Theme
     * @see current
     */
    @Composable
    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
    fun isDynamic(
        //? performance: avoid new-calling when possible
        state: State<ColorSchemeSpec>? = null
    ): Boolean {
        val theme by state ?: Settings.Theme.rememberObject()
        return theme.isDynamic && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    }

    /** the actual theme mode in use right now
     * @see Settings.Theme
     * @see isDark
     * @see isDynamic
     */
    @Composable
    fun current(): ColorScheme {
        val theme = Settings.Theme.rememberObject()

        val isDark = isDark(theme)
        val isDynamic = isDynamic(theme)

        if (isDynamic) {
            val context = LocalContext.current
            return if (isDark) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }

        return if (isDark) dark else light
    }

    val light = lightColorScheme(
        primary = GalacticColor.Light.primary,
        onPrimary = GalacticColor.Light.onPrimary,
        primaryContainer = GalacticColor.Light.primaryContainer,
        onPrimaryContainer = GalacticColor.Light.onPrimaryContainer,
        secondary = GalacticColor.Light.secondary,
        onSecondary = GalacticColor.Light.onSecondary,
        secondaryContainer = GalacticColor.Light.secondaryContainer,
        onSecondaryContainer = GalacticColor.Light.onSecondaryContainer,
        inversePrimary = GalacticColor.Light.inversePrimary,
        tertiary = GalacticColor.Light.tertiary,
        onTertiary = GalacticColor.Light.onTertiary,
        tertiaryContainer = GalacticColor.Light.tertiaryContainer,
        onTertiaryContainer = GalacticColor.Light.onTertiaryContainer,
        background = GalacticColor.Light.background,
        onBackground = GalacticColor.Light.onBackground,
        surface = GalacticColor.Light.surface,
        onSurface = GalacticColor.Light.onSurface,
        surfaceVariant = GalacticColor.Light.surfaceVariant,
        onSurfaceVariant = GalacticColor.Light.onSurfaceVariant,
        surfaceDim = GalacticColor.Light.surfaceDim,
        surfaceBright = GalacticColor.Light.surfaceBright,
        surfaceContainerLowest = GalacticColor.Light.surfaceContainerLowest,
        surfaceContainerLow = GalacticColor.Light.surfaceContainerLow,
        surfaceContainer = GalacticColor.Light.surfaceContainer,
        surfaceContainerHigh = GalacticColor.Light.surfaceContainerHigh,
        surfaceContainerHighest = GalacticColor.Light.surfaceContainerHighest,
        inverseSurface = GalacticColor.Light.inverseSurface,
        inverseOnSurface = GalacticColor.Light.inverseOnSurface,
        outline = GalacticColor.Light.outline,
        outlineVariant = GalacticColor.Light.outlineVariant,
        scrim = GalacticColor.Light.scrim,
        // success = GalacticColor.Light.success,
        // onSuccess = GalacticColor.Light.onSuccess,
        // successContainer = GalacticColor.Light.successContainer,
        // onSuccessContainer = GalacticColor.Light.onSuccessContainer,
        // warning = GalacticColor.Light.warning,
        // onWarning = GalacticColor.Light.onWarning,
        // warningContainer = GalacticColor.Light.warningContainer,
        // onWarningContainer = GalacticColor.Light.onWarningContainer,
        error = GalacticColor.Light.error,
        onError = GalacticColor.Light.onError,
        errorContainer = GalacticColor.Light.errorContainer,
        onErrorContainer = GalacticColor.Light.onErrorContainer,
    )

    val lightContrastMedium = lightColorScheme(
        primary = GalacticColor.Light.Contrast.Medium.primary,
        onPrimary = GalacticColor.Light.Contrast.Medium.onPrimary,
        primaryContainer = GalacticColor.Light.Contrast.Medium.primaryContainer,
        onPrimaryContainer = GalacticColor.Light.Contrast.Medium.onPrimaryContainer,
        secondary = GalacticColor.Light.Contrast.Medium.secondary,
        onSecondary = GalacticColor.Light.Contrast.Medium.onSecondary,
        secondaryContainer = GalacticColor.Light.Contrast.Medium.secondaryContainer,
        onSecondaryContainer = GalacticColor.Light.Contrast.Medium.onSecondaryContainer,
        inversePrimary = GalacticColor.Light.Contrast.Medium.inversePrimary,
        tertiary = GalacticColor.Light.Contrast.Medium.tertiary,
        onTertiary = GalacticColor.Light.Contrast.Medium.onTertiary,
        tertiaryContainer = GalacticColor.Light.Contrast.Medium.tertiaryContainer,
        onTertiaryContainer = GalacticColor.Light.Contrast.Medium.onTertiaryContainer,
        background = GalacticColor.Light.Contrast.Medium.background,
        onBackground = GalacticColor.Light.Contrast.Medium.onBackground,
        surface = GalacticColor.Light.Contrast.Medium.surface,
        onSurface = GalacticColor.Light.Contrast.Medium.onSurface,
        surfaceVariant = GalacticColor.Light.Contrast.Medium.surfaceVariant,
        onSurfaceVariant = GalacticColor.Light.Contrast.Medium.onSurfaceVariant,
        surfaceDim = GalacticColor.Light.Contrast.Medium.surfaceDim,
        surfaceBright = GalacticColor.Light.Contrast.Medium.surfaceBright,
        surfaceContainerLowest = GalacticColor.Light.Contrast.Medium.surfaceContainerLowest,
        surfaceContainerLow = GalacticColor.Light.Contrast.Medium.surfaceContainerLow,
        surfaceContainer = GalacticColor.Light.Contrast.Medium.surfaceContainer,
        surfaceContainerHigh = GalacticColor.Light.Contrast.Medium.surfaceContainerHigh,
        surfaceContainerHighest = GalacticColor.Light.Contrast.Medium.surfaceContainerHighest,
        inverseSurface = GalacticColor.Light.Contrast.Medium.inverseSurface,
        inverseOnSurface = GalacticColor.Light.Contrast.Medium.inverseOnSurface,
        outline = GalacticColor.Light.Contrast.Medium.outline,
        outlineVariant = GalacticColor.Light.Contrast.Medium.outlineVariant,
        scrim = GalacticColor.Light.Contrast.Medium.scrim,
        // success = GalacticColor.Light.Contrast.Medium.success,
        // onSuccess = GalacticColor.Light.Contrast.Medium.onSuccess,
        // successContainer = GalacticColor.Light.Contrast.Medium.successContainer,
        // onSuccessContainer = GalacticColor.Light.Contrast.Medium.onSuccessContainer,
        // warning = GalacticColor.Light.Contrast.Medium.warning,
        // onWarning = GalacticColor.Light.Contrast.Medium.onWarning,
        // warningContainer = GalacticColor.Light.Contrast.Medium.warningContainer,
        // onWarningContainer = GalacticColor.Light.Contrast.Medium.onWarningContainer,
        error = GalacticColor.Light.Contrast.Medium.error,
        onError = GalacticColor.Light.Contrast.Medium.onError,
        errorContainer = GalacticColor.Light.Contrast.Medium.errorContainer,
        onErrorContainer = GalacticColor.Light.Contrast.Medium.onErrorContainer,
    )

    val lightContrastHigh = lightColorScheme(
        primary = GalacticColor.Light.Contrast.High.primary,
        onPrimary = GalacticColor.Light.Contrast.High.onPrimary,
        primaryContainer = GalacticColor.Light.Contrast.High.primaryContainer,
        onPrimaryContainer = GalacticColor.Light.Contrast.High.onPrimaryContainer,
        secondary = GalacticColor.Light.Contrast.High.secondary,
        onSecondary = GalacticColor.Light.Contrast.High.onSecondary,
        secondaryContainer = GalacticColor.Light.Contrast.High.secondaryContainer,
        onSecondaryContainer = GalacticColor.Light.Contrast.High.onSecondaryContainer,
        inversePrimary = GalacticColor.Light.Contrast.High.inversePrimary,
        tertiary = GalacticColor.Light.Contrast.High.tertiary,
        onTertiary = GalacticColor.Light.Contrast.High.onTertiary,
        tertiaryContainer = GalacticColor.Light.Contrast.High.tertiaryContainer,
        onTertiaryContainer = GalacticColor.Light.Contrast.High.onTertiaryContainer,
        background = GalacticColor.Light.Contrast.High.background,
        onBackground = GalacticColor.Light.Contrast.High.onBackground,
        surface = GalacticColor.Light.Contrast.High.surface,
        onSurface = GalacticColor.Light.Contrast.High.onSurface,
        surfaceVariant = GalacticColor.Light.Contrast.High.surfaceVariant,
        onSurfaceVariant = GalacticColor.Light.Contrast.High.onSurfaceVariant,
        surfaceDim = GalacticColor.Light.Contrast.High.surfaceDim,
        surfaceBright = GalacticColor.Light.Contrast.High.surfaceBright,
        surfaceContainerLowest = GalacticColor.Light.Contrast.High.surfaceContainerLowest,
        surfaceContainerLow = GalacticColor.Light.Contrast.High.surfaceContainerLow,
        surfaceContainer = GalacticColor.Light.Contrast.High.surfaceContainer,
        surfaceContainerHigh = GalacticColor.Light.Contrast.High.surfaceContainerHigh,
        surfaceContainerHighest = GalacticColor.Light.Contrast.High.surfaceContainerHighest,
        inverseSurface = GalacticColor.Light.Contrast.High.inverseSurface,
        inverseOnSurface = GalacticColor.Light.Contrast.High.inverseOnSurface,
        outline = GalacticColor.Light.Contrast.High.outline,
        outlineVariant = GalacticColor.Light.Contrast.High.outlineVariant,
        scrim = GalacticColor.Light.Contrast.High.scrim,
        // success = GalacticColor.Light.Contrast.High.success,
        // onSuccess = GalacticColor.Light.Contrast.High.onSuccess,
        // successContainer = GalacticColor.Light.Contrast.High.successContainer,
        // onSuccessContainer = GalacticColor.Light.Contrast.High.onSuccessContainer,
        // warning = GalacticColor.Light.Contrast.High.warning,
        // onWarning = GalacticColor.Light.Contrast.High.onWarning,
        // warningContainer = GalacticColor.Light.Contrast.High.warningContainer,
        // onWarningContainer = GalacticColor.Light.Contrast.High.onWarningContainer,
        error = GalacticColor.Light.Contrast.High.error,
        onError = GalacticColor.Light.Contrast.High.onError,
        errorContainer = GalacticColor.Light.Contrast.High.errorContainer,
        onErrorContainer = GalacticColor.Light.Contrast.High.onErrorContainer,
    )


    val dark = lightColorScheme(
        primary = GalacticColor.Dark.primary,
        onPrimary = GalacticColor.Dark.onPrimary,
        primaryContainer = GalacticColor.Dark.primaryContainer,
        onPrimaryContainer = GalacticColor.Dark.onPrimaryContainer,
        secondary = GalacticColor.Dark.secondary,
        onSecondary = GalacticColor.Dark.onSecondary,
        secondaryContainer = GalacticColor.Dark.secondaryContainer,
        onSecondaryContainer = GalacticColor.Dark.onSecondaryContainer,
        inversePrimary = GalacticColor.Dark.inversePrimary,
        tertiary = GalacticColor.Dark.tertiary,
        onTertiary = GalacticColor.Dark.onTertiary,
        tertiaryContainer = GalacticColor.Dark.tertiaryContainer,
        onTertiaryContainer = GalacticColor.Dark.onTertiaryContainer,
        background = GalacticColor.Dark.background,
        onBackground = GalacticColor.Dark.onBackground,
        surface = GalacticColor.Dark.surface,
        onSurface = GalacticColor.Dark.onSurface,
        surfaceVariant = GalacticColor.Dark.surfaceVariant,
        onSurfaceVariant = GalacticColor.Dark.onSurfaceVariant,
        surfaceDim = GalacticColor.Dark.surfaceDim,
        surfaceBright = GalacticColor.Dark.surfaceBright,
        surfaceContainerLowest = GalacticColor.Dark.surfaceContainerLowest,
        surfaceContainerLow = GalacticColor.Dark.surfaceContainerLow,
        surfaceContainer = GalacticColor.Dark.surfaceContainer,
        surfaceContainerHigh = GalacticColor.Dark.surfaceContainerHigh,
        surfaceContainerHighest = GalacticColor.Dark.surfaceContainerHighest,
        inverseSurface = GalacticColor.Dark.inverseSurface,
        inverseOnSurface = GalacticColor.Dark.inverseOnSurface,
        outline = GalacticColor.Dark.outline,
        outlineVariant = GalacticColor.Dark.outlineVariant,
        scrim = GalacticColor.Dark.scrim,
        // success = GalacticColor.Dark.success,
        // onSuccess = GalacticColor.Dark.onSuccess,
        // successContainer = GalacticColor.Dark.successContainer,
        // onSuccessContainer = GalacticColor.Dark.onSuccessContainer,
        // warning = GalacticColor.Dark.warning,
        // onWarning = GalacticColor.Dark.onWarning,
        // warningContainer = GalacticColor.Dark.warningContainer,
        // onWarningContainer = GalacticColor.Dark.onWarningContainer,
        error = GalacticColor.Dark.error,
        onError = GalacticColor.Dark.onError,
        errorContainer = GalacticColor.Dark.errorContainer,
        onErrorContainer = GalacticColor.Dark.onErrorContainer,
    )

    val darkContrastMedium = lightColorScheme(
        primary = GalacticColor.Dark.Contrast.Medium.primary,
        onPrimary = GalacticColor.Dark.Contrast.Medium.onPrimary,
        primaryContainer = GalacticColor.Dark.Contrast.Medium.primaryContainer,
        onPrimaryContainer = GalacticColor.Dark.Contrast.Medium.onPrimaryContainer,
        secondary = GalacticColor.Dark.Contrast.Medium.secondary,
        onSecondary = GalacticColor.Dark.Contrast.Medium.onSecondary,
        secondaryContainer = GalacticColor.Dark.Contrast.Medium.secondaryContainer,
        onSecondaryContainer = GalacticColor.Dark.Contrast.Medium.onSecondaryContainer,
        inversePrimary = GalacticColor.Dark.Contrast.Medium.inversePrimary,
        tertiary = GalacticColor.Dark.Contrast.Medium.tertiary,
        onTertiary = GalacticColor.Dark.Contrast.Medium.onTertiary,
        tertiaryContainer = GalacticColor.Dark.Contrast.Medium.tertiaryContainer,
        onTertiaryContainer = GalacticColor.Dark.Contrast.Medium.onTertiaryContainer,
        background = GalacticColor.Dark.Contrast.Medium.background,
        onBackground = GalacticColor.Dark.Contrast.Medium.onBackground,
        surface = GalacticColor.Dark.Contrast.Medium.surface,
        onSurface = GalacticColor.Dark.Contrast.Medium.onSurface,
        surfaceVariant = GalacticColor.Dark.Contrast.Medium.surfaceVariant,
        onSurfaceVariant = GalacticColor.Dark.Contrast.Medium.onSurfaceVariant,
        surfaceDim = GalacticColor.Dark.Contrast.Medium.surfaceDim,
        surfaceBright = GalacticColor.Dark.Contrast.Medium.surfaceBright,
        surfaceContainerLowest = GalacticColor.Dark.Contrast.Medium.surfaceContainerLowest,
        surfaceContainerLow = GalacticColor.Dark.Contrast.Medium.surfaceContainerLow,
        surfaceContainer = GalacticColor.Dark.Contrast.Medium.surfaceContainer,
        surfaceContainerHigh = GalacticColor.Dark.Contrast.Medium.surfaceContainerHigh,
        surfaceContainerHighest = GalacticColor.Dark.Contrast.Medium.surfaceContainerHighest,
        inverseSurface = GalacticColor.Dark.Contrast.Medium.inverseSurface,
        inverseOnSurface = GalacticColor.Dark.Contrast.Medium.inverseOnSurface,
        outline = GalacticColor.Dark.Contrast.Medium.outline,
        outlineVariant = GalacticColor.Dark.Contrast.Medium.outlineVariant,
        scrim = GalacticColor.Dark.Contrast.Medium.scrim,
        // success = GalacticColor.Dark.Contrast.Medium.success,
        // onSuccess = GalacticColor.Dark.Contrast.Medium.onSuccess,
        // successContainer = GalacticColor.Dark.Contrast.Medium.successContainer,
        // onSuccessContainer = GalacticColor.Dark.Contrast.Medium.onSuccessContainer,
        // warning = GalacticColor.Dark.Contrast.Medium.warning,
        // onWarning = GalacticColor.Dark.Contrast.Medium.onWarning,
        // warningContainer = GalacticColor.Dark.Contrast.Medium.warningContainer,
        // onWarningContainer = GalacticColor.Dark.Contrast.Medium.onWarningContainer,
        error = GalacticColor.Dark.Contrast.Medium.error,
        onError = GalacticColor.Dark.Contrast.Medium.onError,
        errorContainer = GalacticColor.Dark.Contrast.Medium.errorContainer,
        onErrorContainer = GalacticColor.Dark.Contrast.Medium.onErrorContainer,
    )

    val darkContrastHigh = lightColorScheme(
        primary = GalacticColor.Dark.Contrast.High.primary,
        onPrimary = GalacticColor.Dark.Contrast.High.onPrimary,
        primaryContainer = GalacticColor.Dark.Contrast.High.primaryContainer,
        onPrimaryContainer = GalacticColor.Dark.Contrast.High.onPrimaryContainer,
        secondary = GalacticColor.Dark.Contrast.High.secondary,
        onSecondary = GalacticColor.Dark.Contrast.High.onSecondary,
        secondaryContainer = GalacticColor.Dark.Contrast.High.secondaryContainer,
        onSecondaryContainer = GalacticColor.Dark.Contrast.High.onSecondaryContainer,
        inversePrimary = GalacticColor.Dark.Contrast.High.inversePrimary,
        tertiary = GalacticColor.Dark.Contrast.High.tertiary,
        onTertiary = GalacticColor.Dark.Contrast.High.onTertiary,
        tertiaryContainer = GalacticColor.Dark.Contrast.High.tertiaryContainer,
        onTertiaryContainer = GalacticColor.Dark.Contrast.High.onTertiaryContainer,
        background = GalacticColor.Dark.Contrast.High.background,
        onBackground = GalacticColor.Dark.Contrast.High.onBackground,
        surface = GalacticColor.Dark.Contrast.High.surface,
        onSurface = GalacticColor.Dark.Contrast.High.onSurface,
        surfaceVariant = GalacticColor.Dark.Contrast.High.surfaceVariant,
        onSurfaceVariant = GalacticColor.Dark.Contrast.High.onSurfaceVariant,
        surfaceDim = GalacticColor.Dark.Contrast.High.surfaceDim,
        surfaceBright = GalacticColor.Dark.Contrast.High.surfaceBright,
        surfaceContainerLowest = GalacticColor.Dark.Contrast.High.surfaceContainerLowest,
        surfaceContainerLow = GalacticColor.Dark.Contrast.High.surfaceContainerLow,
        surfaceContainer = GalacticColor.Dark.Contrast.High.surfaceContainer,
        surfaceContainerHigh = GalacticColor.Dark.Contrast.High.surfaceContainerHigh,
        surfaceContainerHighest = GalacticColor.Dark.Contrast.High.surfaceContainerHighest,
        inverseSurface = GalacticColor.Dark.Contrast.High.inverseSurface,
        inverseOnSurface = GalacticColor.Dark.Contrast.High.inverseOnSurface,
        outline = GalacticColor.Dark.Contrast.High.outline,
        outlineVariant = GalacticColor.Dark.Contrast.High.outlineVariant,
        scrim = GalacticColor.Dark.Contrast.High.scrim,
        // success = GalacticColor.Dark.Contrast.High.success,
        // onSuccess = GalacticColor.Dark.Contrast.High.onSuccess,
        // successContainer = GalacticColor.Dark.Contrast.High.successContainer,
        // onSuccessContainer = GalacticColor.Dark.Contrast.High.onSuccessContainer,
        // warning = GalacticColor.Dark.Contrast.High.warning,
        // onWarning = GalacticColor.Dark.Contrast.High.onWarning,
        // warningContainer = GalacticColor.Dark.Contrast.High.warningContainer,
        // onWarningContainer = GalacticColor.Dark.Contrast.High.onWarningContainer,
        error = GalacticColor.Dark.Contrast.High.error,
        onError = GalacticColor.Dark.Contrast.High.onError,
        errorContainer = GalacticColor.Dark.Contrast.High.errorContainer,
        onErrorContainer = GalacticColor.Dark.Contrast.High.onErrorContainer,
    )
}