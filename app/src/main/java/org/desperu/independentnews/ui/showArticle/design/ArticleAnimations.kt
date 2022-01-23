package org.desperu.independentnews.ui.showArticle.design

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.os.postDelayed
import androidx.core.transition.doOnEnd
import androidx.core.widget.ContentLoadingProgressBar
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.design.bindView
import org.desperu.independentnews.extension.design.getValueAnimator
import org.desperu.independentnews.ui.showArticle.fabsMenu.IconAnim
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.math.sqrt

/**
 * Article Animations that provide animations for Show Article Activity.
 */
interface ArticleAnimations {

    /**
     * Show the scroll view, with alpha animation, delayed after set the scroll Y position.
     */
    fun showScrollView()

    /**
     * Resume paused article to the saved scroll position, with drawable transition,
     * play to pause and smooth scroll to the saved position.
     * Delay this animation after the activity shared element enter transition.
     *
     * @param scrollPercent the scroll position to restore.
     */
    fun resumePausedArticle(scrollPercent: Float)
}

/**
 * Implementation of the ArticleAnimations which use an Activity instance to play animations.
 *
 * @property activity   the Activity instance used to play animations.
 *
 * @constructor Instantiate a new ArticleAnimationsImpl.
 *
 * @param activity  the Activity instance used to play animations, to set.
 */
class ArticleAnimationsImpl(private val activity: AppCompatActivity) : ArticleAnimations, KoinComponent {

    // FOR COMMUNICATION
    private val scrollHandler: ScrollHandlerInterface by inject()

    // FOR DESIGN
    private val scrollable: ViewGroup get() = scrollHandler.scrollable
    private val loadingAnimBar: ContentLoadingProgressBar by bindView(activity, R.id.content_loading_bar)

    // --------------
    // METHODS
    // --------------

    // TODO use swipe container YES and MaterialFadeThrough anim ???
    /**
     * Show the scroll view, with alpha animation, delayed after set the scroll Y position.
     */
    override fun showScrollView() {
        // To prepare the view before the animation
//        if (scrollable.alpha != 0f) scrollable.alpha = 0f // TODO not always the good scrollable, use doOnPreDraw ???
//        Log.e(javaClass.enclosingClass?.name, "Show ScrollView Scrollable : $scrollable")
//
//        // TODO add do to execute each pass and swipe container to loading progress, and expand app bar on reload ??
//        waitCondition(activity.lifecycleScope, 2000L, { scrollHandler.hasScroll }) {
//            Log.e(javaClass.enclosingMethod?.name, "show scroll view") // TODO to remove
//            loadingAnimBar.hide()
//            getSVAlphaAnim().start()
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//                getCircularReveal().start()
//        }
    }

    /**
     * Returns the alpha animator for the scroll view.
     *
     * @return the alpha animator for the scroll view.
     */
    private fun getSVAlphaAnim(): ValueAnimator =
        getValueAnimator(
            true,
            300L,
            DecelerateInterpolator(),
            { progress -> scrollable.alpha = progress }
        )

    /**
     * Returns the circular reveal animator for the page transition.
     *
     * @return the circular reveal animator.
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getCircularReveal(): Animator {
        val width = scrollable.width
        val height = scrollable.height

        //Simply use the diagonal of the view
        val finalRadius = sqrt((width * width + height * height).toFloat())

        val anim = ViewAnimationUtils.createCircularReveal(
            scrollable,
            width / 2,
            height / 2,
            0f,
            finalRadius
        )

        anim.interpolator = FastOutSlowInInterpolator()
        anim.duration = 300L

        return anim
    }

    /**
     * Resume paused article to the saved scroll position, with drawable transition,
     * play to pause and smooth scroll to the saved position.
     * Delay this animation after the activity shared element enter transition.
     *
     * @param scrollPercent the scroll position to restore.
     */
    override fun resumePausedArticle(scrollPercent: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.enterTransition.doOnEnd {
                if (scrollable.isShown) { // To prevent wrong call
                    // Play drawable animation
                    val anim = IconAnim().getIconAnim(R.id.pause_to_play, null)
                    // Restore scroll position
                    anim.doOnEnd { scrollHandler.smoothScrollTo(scrollPercent) }
                    anim.start()
                }
            }
        } else
            Handler(Looper.getMainLooper()).postDelayed(500L) {
                scrollHandler.smoothScrollTo(scrollPercent)
            }
    }
}