package global.common

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs
import kotlin.math.max


class ZoomOutPageTransformer(
    val minScale: Float = .85f,
    val minAlpha: Float = .5f
) : ViewPager2.PageTransformer {

    override fun transformPage(view: View, position: Float) {
        view.apply {
            val pageWidth = width
            val pageHeight = height
            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    alpha = 0f
                }

                position <= 1 -> { // [-1,1]
                    // Modify the default slide transition to shrink the page as well.
                    val scaleFactor = max(minScale, 1 - abs(position))
                    val vMargin = pageHeight * (1 - scaleFactor) / 2
                    val hMargin = pageWidth * (1 - scaleFactor) / 2
                    translationX = if (position < 0)
                        hMargin - vMargin / 2
                    else hMargin + vMargin / 2

                    // Scale the page down (between MIN_SCALE and 1).
                    scaleX = scaleFactor
                    scaleY = scaleFactor

                    // Fade the page relative to its size.
                    alpha =
                        minAlpha + (((scaleFactor - minScale) / (1 - minScale)) * (1 - minAlpha))
                }

                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    alpha = 0f
                }
            }
        }
    }
}
