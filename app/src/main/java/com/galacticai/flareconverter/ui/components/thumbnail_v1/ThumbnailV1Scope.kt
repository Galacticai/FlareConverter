package com.galacticai.flareconverter.ui.components.thumbnail_v1

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.galacticai.flareconverter.R
import com.galacticai.flareconverter.ui.components.inputs.resolution_input.ResolutionInputScopeMarker
import global.common.models.progressive.Progressive
import java.io.File

/** [Thumbnail] scope */
@ResolutionInputScopeMarker
@Stable
interface ThumbnailV1Scope {
    val colors: ThumbnailV1Colors
    val inFile: Progressive<File>?
    val showPercent: Boolean
    val mimeCategory: String?

    companion object {
        /** remember [ThumbnailV1Scope] */
        @Composable
        fun remember(
            inFile: Progressive<File>?,
            showPercent: Boolean,
            /** performance: specify to skip parsing */
            mimeCategory: String?,
        ): ThumbnailV1Scope {
            //TODO: future resolve for source then again for thumbnail

            val colors = when (val f = inFile) {
                is Progressive.Failed -> when (f.type) {
                    Progressive.Failed.Type.Error -> ThumbnailV1Colors.from(
                        R.color.error,
                        R.color.error,
                        R.color.errorContainer
                    )

                    else -> ThumbnailV1Colors.from(
                        R.color.warning,
                        R.color.warning,
                        R.color.warningContainer
                    )
                }

                null -> ThumbnailV1Colors.from(
                    R.color.surface,
                    R.color.background,
                    R.color.background
                )

                else -> ThumbnailV1Colors.from(
                    R.color.primary,
                    R.color.primaryContainer,
                    R.color.surface
                )
            }

            return remember(
                colors, inFile,
                showPercent, mimeCategory
            ) {
                object : ThumbnailV1Scope {
                    override val colors = colors
                    override val inFile = inFile
                    override val showPercent = showPercent
                    override val mimeCategory = mimeCategory
                }
            }
        }
    }
}