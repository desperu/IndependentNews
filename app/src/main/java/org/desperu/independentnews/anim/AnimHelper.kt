package org.desperu.independentnews.anim

import android.content.Context
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
     * @param views         the given list of views to animate.
     * @param startDelay    the start delay of the animation.
     */
    internal fun alphaViewAnimation(views: List<View>, startDelay: Long) {
        val animation: Animation = AlphaAnimation(0f, 1f)
        animation.duration = 500
        animation.interpolator = AccelerateInterpolator()
        animation.startOffset = startDelay
        views.forEach { it.startAnimation(animation) }
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
            Animation.RELATIVE_TO_PARENT, +1f,
            Animation.RELATIVE_TO_PARENT, 0f
        )
        animation.duration = 500
        animation.interpolator = DecelerateInterpolator()
        animation.startOffset = startDelay
        view.startAnimation(animation)
    }

    /**
     * Set from side animation for the given view.
     *
     * @param context       the context from this function is called.
     * @param views         the given list of views to animate.
     * @param startDelay    the start delay of the animation.
     * @param fromLeft      if true animate the view from the left, else animate from the right.
     */
    internal fun fromSideAnimation(
        context: Context,
        views: List<View>,
        startDelay: Long,
        fromLeft: Boolean
    ) {
        val anim = AnimationUtils.makeInAnimation(context, fromLeft)
        anim.startOffset = startDelay
        anim.duration = 250L
        views.forEach { it.startAnimation(anim) }
    }
}
