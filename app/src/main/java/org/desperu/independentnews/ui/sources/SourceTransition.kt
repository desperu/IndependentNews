package org.desperu.independentnews.ui.sources

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.transition.Transition
import androidx.transition.TransitionValues

/**
 * Constant values for the transition.
 */
private const val PROGRESSBAR_PROPERTY = "cornerRadius"
private const val TRANSITION_PROPERTY = "DrawableTransition:cornerRadius"

/**
 * Custom transition class to animate drawable shape.
 *
 * @constructor Instantiate a new SourceTransition.
 */
class SourceTransition : Transition() {

    override fun createAnimator(sceneRoot: ViewGroup, startValues: TransitionValues?,
                                endValues: TransitionValues?): Animator? {
        if (startValues != null && endValues != null && endValues.view is ImageView) {
            val drawable = (endValues.view as ImageView).background

//            val startValue = (startValues.values[TRANSITION_PROPERTY] as Float?) ?: 500f
//            val endValue = (endValues.values[TRANSITION_PROPERTY] as Float?) ?: 6f
            val startValue = 500f
            val endValue = 6f
            val list = arrayOf(startValue, endValue)

            if (startValue != endValue) {
                val objectAnimator = ObjectAnimator
                    .ofFloat(drawable, PROGRESSBAR_PROPERTY, *list.toFloatArray())
                objectAnimator.interpolator = DecelerateInterpolator()

                return objectAnimator
            }
        }

        return null
    }

    /**
     * Capture values in the transition values.
     *
     * @param transitionValues the transition value to update.
     */
    private fun captureValues(transitionValues: TransitionValues) {
//        if (transitionValues.view is ImageView) {
//            // Save current corner radius in the transitionValues Map
//            val drawable = (transitionValues.view as ImageView).background
//            val gradientDrawable = GradientDrawable()
//            gradientDrawable.draw(Canvas(drawable.toBitmap()))
//            transitionValues.values[TRANSITION_PROPERTY] = gradientDrawable.cornerRadius // not work always 0.0
//        }
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }
}