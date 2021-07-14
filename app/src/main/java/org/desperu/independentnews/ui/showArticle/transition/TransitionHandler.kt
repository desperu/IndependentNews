package org.desperu.independentnews.ui.showArticle.transition

import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Build
import android.transition.Transition
import android.transition.TransitionInflater
import android.transition.TransitionSet
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toDrawable
import androidx.core.transition.doOnEnd
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.fragment_article.*
import org.desperu.independentnews.R
import org.desperu.independentnews.helpers.AsyncHelper.waitCondition
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.ui.showArticle.ShowArticleInterface
import org.desperu.independentnews.ui.showArticle.design.ScrollHandlerInterface
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * Transition handler, it help to handle transition for activity and fragments.
 *
 * @constructor Instantiate a new TransitionHelper.
 */
class TransitionHandler : KoinComponent {

    // FOR COMMUNICATION
    private val activity = get<ShowArticleInterface>().activity
    private val scrollHandler: ScrollHandlerInterface = get()

    // --------------
    // METHODS
    // --------------

    /**
     * Postpone the shared elements enter transition, because the shared elements
     * is an image downloaded from network.
     */
    internal fun postponeSceneTransition() = activity.supportPostponeEnterTransition()

    /**
     * Schedules the shared element transition to be started immediately
     * after the shared element has been measured and laid out within the
     * activity's view hierarchy.
     * Start custom enter animations together with scene transition.
     *
     * @param sharedElement the shared element to animate for the transition.
     */
    internal fun scheduleStartPostponedTransition(sharedElement: View) {
        sharedElement.doOnPreDraw {
            // Used here to be sure that the web content is full loaded.
            waitCondition(
                activity.lifecycleScope,
                2000L,
                { scrollHandler.hasScroll }

            ) { activity.supportStartPostponedEnterTransition() }
        }
    }

    /**
     * Set the activity transition.
     *
     * @param article         the current article shown in the web view.
     * @param transitionBg    the background drawable used for the transition.
     */
    internal fun setActivityTransition(article: Article?, transitionBg: ByteArray?) {
        // TODO article image request layout, or check image before start activity
        val drawable = if (transitionBg != null && activity.article_image.drawable != null) {
            val bitmap = BitmapFactory.decodeByteArray(transitionBg, 0, transitionBg.size)
            bitmap.toDrawable(activity.resources)
        } else
            null

        handleTransition(article, drawable)
    }

    /**
     * Handle activity transitions, switch transition, depends of given data.
     *
     * Set custom activity transition, if the article id equal zero,
     * the show article activity was call from source detail, so set the specific transition.
     *
     * Else, and only for API >= LOLLIPOP, add shared element activity transition.
     *
     * Else, update views background.
     *
     * @param article       the current article shown in the web view.
     * @param bgDrawable    the background drawable used for the transition.
     */
    private fun handleTransition(article: Article?, bgDrawable: Drawable?) {
        when {
            article?.id == 0L -> {
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                activity.webView.updateBackground()
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && bgDrawable != null -> {
                activity.window.setBackgroundDrawable(bgDrawable)
                activity.window.sharedElementEnterTransition = getActivityTransition(true)
                activity.window.sharedElementReturnTransition = getActivityTransition(false)
                // To be sure that the coordinator and containers have a background color set.
                activity.window.enterTransition.doOnEnd { activity.webView.updateBackground() }
            }

            else -> activity.webView.updateBackground() // Add from bottom anim ??
        }
    }

    /**
     * Returns the activity transitions for enter and return transitions.
     * We use move transition for the shared element (the article image),
     * and add a custom transition animation with [ShowArticleTransition].
     *
     * @param isEnter true if is enter transition, false if is return.
     *
     * @return return the activity transition set.
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getActivityTransition(isEnter: Boolean): Transition {
        val transitionSet = TransitionSet()
        val moveTransition = TransitionInflater.from(activity)
            .inflateTransition(android.R.transition.move)

        transitionSet.addTransition(moveTransition)
        transitionSet.addTransition(ShowArticleTransition(isEnter))

        return transitionSet
    }
}