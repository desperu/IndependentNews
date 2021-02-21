package org.desperu.independentnews.anim

import android.content.Context
import android.view.View
import android.view.animation.*
import android.view.animation.Animation.AnimationListener
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

/**
 * Animation helper object, to animate view.
 */
object AnimHelper {

    // --------------
    // SINGLE ANIMATION
    // --------------

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
//        clearAnimAfterPlaying(view.animate(), listOf(view))
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
        clearAnimAfterPlaying(animation, views)
        views.forEach {
            it.postOnAnimation { it.visibility = if (toShow) View.VISIBLE else View.INVISIBLE } // TODO use listener start/end ???
            it.animation = animation
        }
        animation.start()
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
        clearAnimAfterPlaying(animation, listOf(view))
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
        views.forEach { it.animation = anim }
        clearAnimAfterPlaying(anim, views)
        anim.start()
    }

    /**
     * Clear animation after playing for each view in the given list.
     *
     * @param anim  the animation on which clear at end.
     * @param views the list of views for which clear animation.
     */
    private fun clearAnimAfterPlaying(anim: Animation, views: List<View>) {
        anim.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                views.forEach { it.clearAnimation() }
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    // --------------
    // SYNCHRONISED ANIMATION
    // --------------

    /**
     * Apply from side animation for the given views.
     *
     * @param views         the given list of views to animate.
     * @param progress      the value animator, used to animate views.
     * @param fromLeft      if true animate the view from the left, else animate from the right.
     */
    internal fun fromSideAnimator(views: List<View>, progress: Float, fromLeft: Boolean) {
        views.forEach {
            val value = if (fromLeft) -it.right else it.left
            it.translationX = animatedValue(value, progress)
        }
    }

    /**
     * Returns the animated value.
     *
     * @param value         the value to animate.
     * @param progress      the value animator, used to animate views.
     *
     * @return the animated value.
     */
    internal fun animatedValue(value: Int, progress: Float): Float =
        value - (value * progress)
}
