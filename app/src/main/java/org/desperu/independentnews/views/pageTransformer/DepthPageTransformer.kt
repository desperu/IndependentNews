package org.desperu.independentnews.views.pageTransformer

import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs

/**
 * Min scale constant used to perform animation.
 */
private const val MIN_SCALE = 0.75f

/**
 * Depth Page Transformer used to customize page transition.
 *
 * @constructor Instantiates a new DepthPageTransformer.
 */
class DepthPageTransformer : ViewPager.PageTransformer, PageTransformerInterface {

    //FOR DATA
    private var position: Float = 0.0f

    override fun transformPage(view: View, position: Float) {

        // Store the current position on each function call
        this.position = position

        view.apply {
            val pageWidth = width
            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    alpha = 0.0f
                }
                position <= 0 -> { // [-1,0]
                    // Use the default slide transition when moving to the left page
                    alpha = 1.0f
                    translationX = 0.0f
                    scaleX = 1.0f
                    scaleY = 1.0f
                }
                position <= 1 -> { // (0,1]
                    // Fade the page out.
                    alpha = 1 - position

                    // Counteract the default slide transition
                    translationX = pageWidth * -position

                    // Scale the page down (between MIN_SCALE and 1)
                    val scaleFactor = (MIN_SCALE + (1 - MIN_SCALE) * (1 - abs(position)))
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                }
                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    alpha = 0f
                }
            }
        }
    }

    /**
     * Returns the current value of the page position.
     */
    override fun getPosition(): Float = position
}