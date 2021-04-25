package org.desperu.independentnews.ui.sources

import android.animation.Animator
import android.os.Build
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.animation.addListener
import androidx.transition.*
import java.lang.Exception

/**
 * Custom transition class to animate transition between source list and source detail.
 *
 * @constructor Instantiate a new SourceTransition.
 */
class SourceTransition : Transition() {

    // FOR DATA
    private val containerTransform = ChangeBounds()
    private val imageTransform = ChangeBounds() // ChangeImageTransform() has bad animation result

    // --------------
    // METHODS OVERRIDE
    // --------------

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator? {

        val view = endValues?.view
        val anim = when (view) {
            is CardView -> containerTransform.createAnimator(sceneRoot, startValues, endValues)
            is ImageView -> imageTransform.createAnimator(sceneRoot, startValues, endValues)
            else -> throw Exception("Animator for asked view $view not found !")
        }

        anim?.addListener(onEnd = { view.invalidate() })
        return anim
    }



    override fun captureStartValues(transitionValues: TransitionValues) {
        containerTransform.captureStartValues(transitionValues)
        imageTransform.captureStartValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        containerTransform.captureEndValues(transitionValues)
        imageTransform.captureEndValues(transitionValues)
    }
}