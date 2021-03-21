package org.desperu.independentnews.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import androidx.core.view.children
import com.leinardi.android.speeddial.FabWithLabelView
import com.leinardi.android.speeddial.SpeedDialView
import org.desperu.independentnews.extension.design.findAnimatedChild

/**
 * Custom SpeedDialView, with implementation of the OnAnimationListener interfaces, start and end.
 *
 * @constructor Instantiate a new MySpeedDialView.
 */
class MySpeedDialView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SpeedDialView(context, attrs, defStyleAttr) {

    // FOR DATA
    private var startListener: OnAnimationStartListener? = null
    private var endListener: OnAnimationEndListener? = null
    private var isAnimRunning = false

    // --------------
    // INTERFACES
    // --------------

    /**
     * On Animation Start Listener, used to call on animation start.
     */
    interface OnAnimationStartListener {

        /**
         * On Pre Start Speed Dial View animation, expand/shrink fabs menu.
         *
         * @param isOpen true if the speed dial is open, false otherwise.
         */
        fun onPreStart(isOpen: Boolean)

        // Same as OnChangeListener.onToggleChanged()
        // Just take care about isOpen value, that is invert
        // because is called after change toggle state.

        /**
         * On Start Speed Dial View animation, expand/shrink fabs menu.
         *
         * @param isOpen true if the speed dial is open, false otherwise.
         */
        fun onStart(isOpen: Boolean)
    }

    /**
     * On Animation End Listener, used to call on animation end.
     */
    interface OnAnimationEndListener {

        /**
         * On End Speed Dial View animation, expand/shrink fabs menu.
         */
        fun onEnd()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Returns the FabWithLabelView for the given id, null if not find.
     *
     * @param id the unique identifier of the FabWithLabelView.
     *
     * @return the FabWithLabelView, null if not find.
     */
    fun getFabWithLabelViewById(id: Int): FabWithLabelView? {
        children.forEach {
            if (it is FabWithLabelView && it.id == id)
                return it
        }

        return null
    }

    /**
     * Set the On Animation Start Listener, erase it if there's already one.
     *
     * @param listener the On Animation Start Listener to set.
     */
    fun setOnAnimationStartListener(listener: OnAnimationStartListener) { startListener = listener }

    /**
     * Remove the On Animation Start Listener.
     */
    fun removeOnAnimationStartListener() { startListener = null }

    /**
     * Set the On Animation End Listener, erase it if there's already one.
     *
     * @param listener the On Animation End Listener to set.
     */
    fun setOnAnimationEndListener(listener: OnAnimationEndListener) { endListener = listener }

    /**
     * Remove the On Animation End Listener.
     */
    fun removeOnAnimationEndListener() { endListener = null }

    /**
     * Remove all listeners.
     */
    fun removeAllListeners() {
        removeOnAnimationStartListener()
        removeOnAnimationEndListener()
    }

    /**
     * Set child animation listener, used to call back [OnAnimationEndListener.onEnd]
     * and reset [isAnimRunning] value.
     *
     * @param isOpen true if the speed dial is open, false otherwise.
     */
    private fun setChildAnimationListener(isOpen: Boolean) {
        val animatedChild = findAnimatedChild(!isOpen)

        if (animatedChild?.animation != null)
            animatedChild.animation.setAnimationListener(object : AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    /**
                     * Needed because we override speed dial animation listener in
                     * [SpeedDialView.hideWithAnimationFabWithLabelView]
                     */
                    if (isOpen) animatedChild.visibility = View.GONE

                    // Call on animation end.
                    endListener?.onEnd()

                    // Reset running value.
                    isAnimRunning = false

                    // Remove this listener.
                    animatedChild.animation.setAnimationListener(null)
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
        else
            // Needed to be sure that isAnimRunning is reset.
            isAnimRunning = false
    }

    // --------------
    // METHODS OVERRIDE
    // --------------

    override fun open() { toggle(true) }

    override fun open(animate: Boolean) { toggle(animate) }

    override fun close() { toggle(true) }

    override fun close(animate: Boolean) { toggle(animate) }

    override fun toggle() { toggle(true) }

    override fun toggle(animate: Boolean) {

        // To prevent reverse anim start during playing.
        if (isAnimRunning) return
        if (animate) isAnimRunning = true

        // Get the speed dial state before switch state.
        val isOpen = isOpen

        // Call on animation pre start, to allow touch sub fabs.
        startListener?.onPreStart(isOpen)

        // Call super to set original speed dial animations.
        super.toggle(animate)

        if (animate) {
            // Call on animation set and start, to allow touch them.
            startListener?.onStart(isOpen)

            // Call to support animation end listener and reset running value.
            setChildAnimationListener(isOpen)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeAllListeners()
    }
}