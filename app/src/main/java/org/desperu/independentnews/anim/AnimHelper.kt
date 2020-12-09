package org.desperu.independentnews.anim

import android.content.Context
import android.view.View
import android.view.animation.*
import androidx.core.view.postOnAnimationDelayed
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
            .setDuration(300L)
            .start()
        clearAnimAfterPlaying(listOf(view), startDelay + 300L)
    }

    /**
     * Set alpha animation for the given view.
     *
     * @param views         the given list of views to animate.
     * @param startDelay    the start delay of the animation.
     * @param toShow        true to fade in, false to fade out.
     */
    internal fun alphaViewAnimation(views: List<View>, startDelay: Long, toShow: Boolean) {
        val animation: Animation = if (toShow) AlphaAnimation(0f, 1f) else AlphaAnimation(1f, 0f)
        animation.duration = 500L
        animation.interpolator = AccelerateInterpolator()
        animation.startOffset = startDelay
        clearAnimAfterPlaying(views, startDelay + animation.duration)
        views.forEach {
            it.postOnAnimation { it.visibility = if (toShow) View.VISIBLE else View.INVISIBLE }
            it.startAnimation(animation)
        }
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
        animation.duration = 500L
        animation.interpolator = DecelerateInterpolator()
        animation.startOffset = startDelay
        view.startAnimation(animation)
        clearAnimAfterPlaying(listOf(view), startDelay + animation.duration)
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
        clearAnimAfterPlaying(views, startDelay + anim.duration)
    }

    /**
     * Clear animation after playing for each view in the given list.
     *
     * @param views the list of views for which clear animation.
     * @param delay the post delay after which clear animation.
     */
    private fun clearAnimAfterPlaying(views: List<View>, delay: Long) {
        val fullDelay = delay + 500L
        views.forEach {
            it.postOnAnimationDelayed(fullDelay) {
                it.clearAnimation()
            }
        }
    }
}
