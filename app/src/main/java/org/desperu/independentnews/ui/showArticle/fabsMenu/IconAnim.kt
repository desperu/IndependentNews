package org.desperu.independentnews.ui.showArticle.fabsMenu

import android.animation.ValueAnimator
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.appcompat.graphics.drawable.AnimatedStateListDrawableCompat
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.updateLayoutParams
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_show_article.*
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.design.getValueAnimator
import org.desperu.independentnews.extension.design.screenWidth
import org.desperu.independentnews.ui.showArticle.ShowArticleInterface
import org.koin.core.KoinComponent
import org.koin.core.get

/**
 * Icon animation class, used to animate icons for user actions, favorite and paused article.
 *
 * @property activity               the activity used to play the animation.
 * @property coordinatorLayout      the coordinator layout of hte activity.
 * @property centerX                the center on X axis of the root view.
 * @property centerY                the center on Y axis of the root view.
 * @property iconAnimDuration       the anim duration of the icon animation.
 * @property drawableAnimDuration   the anim duration of the drawable transition.
 *
 * @constructor Instantiate a new IconAnim.
 *
 * @author Desperu.
 */
class IconAnim : KoinComponent {

    // FOR COMMUNICATION
    private val activity = get<ShowArticleInterface>().activity // get() = to get each call

    // FOR DATA
    private val coordinatorLayout: CoordinatorLayout = activity.article_root_view
    private val centerX = activity.screenWidth / 2
    private val centerY = coordinatorLayout.height / 2
    // Create the bindInteger
    private val iconAnimDuration = activity.resources.getInteger(R.integer.sub_fab_icon_anim_duration).toLong()
    private val drawableAnimDuration = activity.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

    // -----------------
    // CONFIGURATION
    // -----------------

    /**
     * Create and returns the animation for the icon, depends of the given id.
     *
     * @param id            the unique identifier of the sub fab.
     * @param subFab        the sub floating action button to animate icon.
     *
     * @return the animation for the icon.
     *
     * @throws IllegalArgumentException if the [id] was not found.
     */
    internal fun getIconAnim(@IdRes id: Int, subFab: FloatingActionButton?): ValueAnimator =
        when (id) {
            R.id.fab_star -> getStarAnim(id, subFab)
            R.id.fab_pause -> getPauseAnim(id, subFab)
            R.id.play_to_pause -> getPauseAnim(id, null)
            R.id.pause_to_play -> getPlayAnim(id)
            else -> throw IllegalArgumentException("Unique identifier not found : $id")
        }

    /**
     * Create an image view for the animation, set it's image drawable,
     * add to the root view, and position :
     * - below the sub fab icon
     * - in the center of the screen.
     *
     * @param view      the view used to position the start of the animation.
     * @param drawable  the drawable for the animation.
     *
     * @return the created image view for the animation.
     */
    private fun createViewForAnim(view: View?, drawable: Any): ImageView {
        val animatedView = ImageView(activity)
        val imageDrawable = drawable as? AnimatedStateListDrawableCompat ?: drawable as Drawable

        animatedView.setImageDrawable(imageDrawable)
        coordinatorLayout.addView(animatedView)

        // Position animated view below sub fab icon.
        val global = view?.run { Rect().apply(::getGlobalVisibleRect) }
        animatedView.updateLayoutParams<CoordinatorLayout.LayoutParams> {
            global?.let {
                leftMargin = global.left + view.paddingLeft
                topMargin = global.top
            }
        }

        return animatedView
    }

    // --------------
    // ANIMATIONS
    // --------------

    /**
     * Returns the star animation.
     *
     * @param id        the unique identifier of the sub fab.
     * @param subFab    the sub floating action button clicked.
     *
     * @return the star animation.
     */
    private fun getStarAnim(@IdRes id: Int, subFab: FloatingActionButton?): ValueAnimator {
        val animatedStar = subFab?.drawable?.constantState?.newDrawable()
        val animatedView = animatedStar?.let { createViewForAnim(subFab, it) }

        val anim = getIconAnimator(id, animatedView, iconAnimDuration)
        anim.doOnEnd {
            applyEndAnim(animatedView, 0L) { coordinatorLayout.removeView(animatedView) }
        }

        return anim
    }

    /**
     * Returns the pause animator, with drawable transition play to pause.
     *
     * @param id        the unique identifier of the sub fab.
     * @param subFab    the sub floating action button clicked.
     *
     * @return the pause animator.
     */
    private fun getPauseAnim(@IdRes id: Int, subFab: FloatingActionButton?): ValueAnimator {
        val animatedDrawable =
            AnimatedStateListDrawableCompat.create(activity, R.drawable.asl_play_pause, null)
        val animatedView = animatedDrawable?.let { createViewForAnim(subFab, it) }

        val anim = getIconAnimator(id, animatedView, iconAnimDuration)
        // doOnStart lock coordinator motion event ??
        anim.doOnEnd {
            // Animate drawable play to pause !
            animatedView?.setImageState(intArrayOf(R.attr.state_pause), true)

            applyEndAnim(animatedView, drawableAnimDuration * 2) {
                coordinatorLayout.removeView(animatedView)
                activity.supportFinishAfterTransition()
            }
        }

        return anim
    }

    /**
     * Returns the play animator, with drawable transition pause to play,
     * and scroll to saved position.
     *
     * @param id the unique identifier of the animation.
     *
     * @return the play animator.
     */
    private fun getPlayAnim(@IdRes id: Int): ValueAnimator {
        val animatedDrawable =
            AnimatedStateListDrawableCompat.create(activity, R.drawable.asl_play_pause, null)
        val animatedView = animatedDrawable?.let { createViewForAnim(null, it) }
        animatedView?.setImageState(intArrayOf(R.attr.state_pause), true)

        val anim = getIconAnimator(id, animatedView, iconAnimDuration)
//        anim.startDelay = iconAnimDuration
        anim.doOnEnd {
            // Animate drawable transition pause to play
            animatedView?.setImageState(intArrayOf(R.attr.state_play), true)

            applyEndAnim(animatedView, drawableAnimDuration * 2, null)
        }

        return anim
    }

    // --------------
    // ANIMATORS
    // --------------

    /**
     * Returns the icon value animator, animate the given view to the center of the screen,
     * with scale and fade anim.
     *
     * @param id            the unique identifier of the animation.
     * @param animatedView  the animated view to animate.
     * @param duration      the duration of the animation.
     *
     * @return the star animation.
     */
    private fun getIconAnimator(
        @IdRes id: Int,
        animatedView: View?,
        duration: Long
    ): ValueAnimator =

        getValueAnimator(
            true,
            duration,
            FastOutSlowInInterpolator(),
            { progress ->

                animatedView?.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                    height = (activity.screenWidth * progress).toInt()
                    width = height

                    leftMargin -= ((leftMargin - centerX + width / 2) * progress).toInt()
                    topMargin -= ((topMargin - centerY + height / 2) * progress).toInt()
                }

                animatedView?.alpha =
                    if (id == R.id.fab_star)
//                        0.7f + (1 - progress) / 3.3f
                        0.6f + (1 - progress) / 2.5f
                    else
                        // For play and pause animations
                        0.9f + (1 - progress) / 10
            }
        )

    /**
     * Apply the end animation on the given animated view, scale and alpha animation.
     *
     * @param animatedView  the view to animate.
     * @param startDelay    the start delay to set to the animation.
     * @param endAction     the end action to execute.
     */
    private fun applyEndAnim(animatedView: View?, startDelay: Long, endAction: (() -> Unit)?) {
        animatedView?.animate()
            ?.setStartDelay(startDelay)
            ?.setDuration(150L)
            ?.scaleX(4f)
            ?.scaleY(4f)
            ?.alpha(0f)
            ?.withEndAction { endAction?.invoke() }
            ?.start()
    }
}