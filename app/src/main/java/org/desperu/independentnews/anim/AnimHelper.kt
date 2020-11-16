package org.desperu.independentnews.anim

import android.view.View
import android.view.animation.*
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

/**
 * Animation helper object, to animate view.
 */
object AnimHelper {

    /**
     * Set scale animation for the given view.
     *
     * @param view          the given view to animate.
     * @param startDelay    the start delay of the animation.
     */
    internal fun scaleViewAnimation(view: View, startDelay: Long) {
        // Reset view
        view.scaleX = 0f
        view.scaleY = 0f
        // Animate view
        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setInterpolator(FastOutSlowInInterpolator())
            .setStartDelay(startDelay)
            .setDuration(300)
            .start()
    }

    /**
     * Set alpha animation for the given view.
     *
     * @param view          the given view to animate.
     * @param startDelay    the start delay of the animation.
     */
    internal fun alphaViewAnimation(view: View, startDelay: Long) {
        val animation: Animation = AlphaAnimation(0f, 1f)
        animation.duration = 500
        animation.interpolator = AccelerateInterpolator()
        animation.startOffset = startDelay
        view.startAnimation(animation)
    }

    /**
     * Set from bottom animation for the given view.
     *
     * @param view          the given view to animate.
     * @param startDelay    the start delay of the animation.
     */
    internal fun fromBottomAnimation(view: View, startDelay: Long) {
        val animation: Animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, 0f,
            Animation.RELATIVE_TO_PARENT, + 1f,
            Animation.RELATIVE_TO_PARENT, 0f
        )
        animation.duration = 500
        animation.interpolator = DecelerateInterpolator()
        animation.startOffset = startDelay
        view.startAnimation(animation)
    }
}
