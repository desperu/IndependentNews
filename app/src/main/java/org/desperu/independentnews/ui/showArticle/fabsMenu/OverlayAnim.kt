package org.desperu.independentnews.ui.showArticle.fabsMenu

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import androidx.annotation.RequiresApi
import androidx.core.animation.doOnEnd
import androidx.core.view.ViewCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.leinardi.android.speeddial.SpeedDialOverlayLayout
import org.desperu.independentnews.R
import org.desperu.independentnews.anim.AnimHelper
import org.desperu.independentnews.extension.design.bindView
import org.desperu.independentnews.extension.design.getValueAnimator
import org.desperu.independentnews.ui.showArticle.ShowArticleInterface
import org.desperu.independentnews.utils.subFabList
import org.desperu.independentnews.views.MySpeedDialView
import org.koin.core.KoinComponent
import org.koin.core.get
import kotlin.math.sqrt

/**
 * Overlay Anim class that is used to handle overlay animation.
 *
 * @property activity           the show article activity that owns this speed dial.
 * @property speedDialView      the speed dial that owns this overlay.
 * @property overlay            the overlay for which we customize the animation.
 * @property customOverlay      the custom overlay view that is used.
 *
 * @constructor Instantiates a new OverlayAnim.
 *
 * @author Desperu.
 */
class OverlayAnim : KoinComponent {

    // FOR COMMUNICATION
    private val activity = get<ShowArticleInterface>().activity

    // FOR UI
    private val speedDialView: MySpeedDialView by bindView(activity, R.id.fabs_menu)
    private val overlay: SpeedDialOverlayLayout by bindView(activity, R.id.fabs_overlay_layout)
    private val customOverlay: View by bindView(activity, R.id.fabs_custom_overlay)

    /**
     * Customize the speed dial overlay anim for API >= LOLLIPOP.
     *
     * @param isOpen true if the speed dial is open, false otherwise.
     */
    internal fun customizeOverlayAnim(isOpen: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val set = AnimatorSet()

            val alphaAnim = getAlphaAnim(isOpen)
            val circularRevealAnim = getCircularRevealAnim(isOpen)

            set.playTogether(alphaAnim, circularRevealAnim)
            set.start()
        }
    }

    // --------------
    // ANIMATIONS
    // --------------

    /**
     * Returns the circular reveal anim used to display the overlay.
     * The animation is start from the center of the main fab.
     *
     * @param isOpen true if the speed dial is open, false otherwise.
     *
     * @return the circular reveal anim.
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getCircularRevealAnim(isOpen: Boolean): Animator {
        val width = overlay.width
        val height = overlay.height
        val mainFabRect = speedDialView.mainFab.run { Rect().apply(::getGlobalVisibleRect) }

        //Simply use the diagonal of the view
        val finalRadius = sqrt((width * width + height * height).toDouble()).toFloat()

        val anim = ViewAnimationUtils.createCircularReveal(
            overlay,
            mainFabRect.centerX(),
            mainFabRect.centerY(),
            if (!isOpen) 0f else finalRadius,
            if (!isOpen) finalRadius else 0f
        )

        anim.interpolator = FastOutSlowInInterpolator()
        anim.duration = getAnimDuration(isOpen)

        return anim
    }

    /**
     * Returns the alpha animation for the custom overlay anim.
     *
     * @param isOpen true if the speed dial is open, false otherwise.
     *
     * @return the alpha value animator.
     */
    private fun getAlphaAnim(isOpen: Boolean): ValueAnimator {
        // Clear original animation and prepare view for custom anim.
        ViewCompat.animate(overlay).cancel()
        overlay.alpha = if (isOpen) 1f else 0f
        overlay.visibility = View.VISIBLE

        val anim = getValueAnimator(
            isOpen,
            getAnimDuration(isOpen),
            FastOutSlowInInterpolator(),
            { progress -> overlay.alpha = AnimHelper.animatedValue(1, progress) }
        )

        anim.doOnEnd {
            if (isOpen) overlay.visibility = View.GONE
            // Needed if clicked on text size, we has low transparency before
            customOverlay.alpha = 0.95f
        }

        return anim
    }

    /**
     * Returns the animation duration, depends of the speed dial state, open or close.
     *
     * @param isOpen true if the speed dial is open, false otherwise.
     *
     * @return the animation duration.
     */
    private fun getAnimDuration(isOpen: Boolean): Long {
        var duration = activity.resources.getInteger(
            if (!isOpen) R.integer.sd_open_animation_duration
            else R.integer.sd_close_animation_duration
        ).toLong()

        if (isOpen) duration += subFabList.size * 20

        return duration
    }
}