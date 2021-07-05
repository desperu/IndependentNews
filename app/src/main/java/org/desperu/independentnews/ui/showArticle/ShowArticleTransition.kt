package org.desperu.independentnews.ui.showArticle

import android.animation.Animator
import android.animation.AnimatorSet
import android.os.Build
import android.transition.ChangeImageTransform
import android.transition.Transition
import android.transition.TransitionValues
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.core.view.children
import org.desperu.independentnews.R
import org.desperu.independentnews.anim.AnimHelper.animatedValue
import org.desperu.independentnews.extension.design.blendColors
import org.desperu.independentnews.extension.design.getValueAnimator
import org.desperu.independentnews.extension.design.setScale
import org.desperu.independentnews.service.ResourceService
import org.desperu.independentnews.views.webview.MyWebView
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * Custom transition class to animate transition between article list in main to show article.
 * Work with the shared element, (the image view), with move animation set in the activity,
 * add here a change image transform.
 * The custom animations, animate the content on the top and bottom of the image view,
 * with alpha, translateY and scale animations.
 *
 * @property isEnter                true for enter transition, false for return transition.
 * @property resources              the resource service access through it's interface.
 * @property changeImageTransform   the change image transform used for the image animation.
 * @property coordinator            the coordinator of this layout.
 * @property topContainer           the top container to animate.
 * @property webContainer           the web (bottom) container to animate.
 * @property webView                the web view of this layout.
 *
 * @constructor Instantiate a new ShowArticleTransition.
 *
 * @param isEnter                   true for enter transition, false for return transition to set.
 *
 * @author Desperu.
 */
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class ShowArticleTransition(private val isEnter: Boolean) : Transition(), KoinComponent {

    // FOR DATA
    private val resources: ResourceService = get()
    private val changeImageTransform = ChangeImageTransform()
    private lateinit var coordinator: View
    private lateinit var topContainer: View
    private lateinit var webContainer: FrameLayout
    private lateinit var webView: MyWebView
    private val bgColor = resources.getColor(android.R.color.white)
    private val transparent = resources.getColor(R.color.colorArticleTransition)


    // --------------
    // METHODS OVERRIDE
    // --------------

    override fun captureStartValues(transitionValues: TransitionValues) {
        changeImageTransform.captureStartValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        changeImageTransform.captureEndValues(transitionValues)
    }

    override fun createAnimator(
        sceneRoot: ViewGroup?,
        startValues: TransitionValues?,
        endValues: TransitionValues?
    ): Animator {

        val animatorSet = AnimatorSet()

        if (startValues != null && endValues != null) {
            val view = endValues.view

            if (view.id == R.id.article_image) {

                // Get views and prepare layout before animation
                getViews(view)
                prepareAnimation()

                // Set the animator, the change image transform
                // and the top bottom animator.
                val imageTransform =
                    changeImageTransform.createAnimator(sceneRoot, startValues, endValues)
                val topBottomAnim = getTopBottomAnim(imageTransform)

                // Play the change image transform and the top bottom animator together
                animatorSet.playTogether(imageTransform, topBottomAnim)

                // TODO Try to play with interpolator to set progress value,
                //  for return anim on scroll down on image touch
            }
        }

        return animatorSet
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Get teh views needed for the animation.
     *
     * @param view the view from the base animation, here the image view.
     */
    private fun getViews(view: View) {
        coordinator = view.rootView.findViewById(R.id.article_root_view)
        topContainer = view.rootView.findViewById(R.id.article_metadata_container)
        webContainer = view.rootView.findViewById(R.id.web_view_container)
        webView = view.rootView.findViewById(R.id.article_web_view)
    }

    // --------------
    // ANIMATION
    // --------------

    /**
     * Prepare the views for the animation.
     */
    private fun prepareAnimation() {
        topContainer.setBackgroundColor(bgColor)
        webContainer.setBackgroundColor(bgColor)
    }

    /**
     * Returns the top bottom animator, use a serialized animate value setter for the top and the bottom,
     * so they should be synchronized.
     * We sync this anim on the given base animator, with interpolator and duration.
     *
     * @param baseAnim the base animator on which we sync this (interpolator and duration).
     *
     * @return the top and bottom animator.
     */
    private fun getTopBottomAnim(baseAnim: Animator): Animator =
        getValueAnimator(
            isEnter,
            baseAnim.duration,
            baseAnim.interpolator,
            { progress ->

                animateView(topContainer, progress) // Anim top container
                animateView(webContainer, progress) // Anim bottom container
                backgroundAnim(progress) // Anim the background of the views.
            }
        )

    /**
     * Animate the given view with progress value, fo top or bottom.
     *
     * ***We switch between top and bottom with the given view type***
     *
     * We animate the alpha container, with a custom animation speed,
     * divided by two before 50% of progress, and with speed up after.
     *
     * Translation animation on Y axis, depends if from top or bottom,
     * translate the views from it's full height to it's normal position.
     *
     * Scale just a little the container for a smoothly animation.
     *
     * On the end of the animation, show the content of the containers,
     * at 80% of progress.
     *
     * @param view      the container view to animate.
     * @param progress  the progression value used to animate the view.
     */
    private fun animateView(view: View, progress: Float) {
        val byTwo = progress / 2
        val afterMiddle = (progress - 0.5f) * 2

        // Container alpha animation
        view.alpha = if (progress < 0.5f) byTwo
                     else byTwo + (byTwo * afterMiddle)

        // Container Y translation
        val fromYDelta = if (view is FrameLayout) coordinator.bottom - view.top // From bottom
                         else view.top - view.bottom // From top
        view.translationY = animatedValue(fromYDelta, progress)

        // Container scale animation, for more smoothly animation entry
        view.setScale(0.7f + (0.3f * progress)) // 0.7f ??? -->try, was 0.5f before

        // Content alpha animation, for animation end
        (view as ViewGroup?)?.children?.forEach { it.alpha = (progress - 0.8f) * 5 }
    }

    /**
     * Animate the background color of the views, coordinator, top and bottom container.
     *
     * @param progress  the progression value used to animate the view.
     */
    private fun backgroundAnim(progress: Float) {
        // Root view, the coordinator, background color
        if (progress >= 0.9f) {
            val coordinatorColor = blendColors(transparent, bgColor, (progress - 0.9f) * 10)
            coordinator.setBackgroundColor(coordinatorColor)
        }

        // Containers background color
        val containerColor = blendColors(bgColor, bgColor, progress)
        topContainer.setBackgroundColor(containerColor)
        webContainer.setBackgroundColor(containerColor)
    }
}