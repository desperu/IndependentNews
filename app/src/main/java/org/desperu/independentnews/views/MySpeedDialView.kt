package org.desperu.independentnews.views

import android.content.Context
import android.util.AttributeSet
import com.leinardi.android.speeddial.SpeedDialView

/**
 * Custom SpeedDialView, with implementation of the OnAnimationListener.
 *
 * @constructor Instantiate a new MySpeedDialView.
 */
class MySpeedDialView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SpeedDialView(context, attrs, defStyleAttr) {

    // FOR DATA
    private var listener: OnAnimationListener? = null

    /**
     * On Animation Listener, used to call on animation start.
     */
    interface OnAnimationListener {

        // Same as OnChangeListener.onToggleChanged()
        // Just take care about isOpen value, that is invert
        // because is called after change toggle state.

        /**
         * On Toggle Speed Dial View, expand/shrink fabs menu.
         *
         * @param isOpen true if the speed dial is open, false otherwise.
         */
        fun onToggle(isOpen: Boolean)
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Set the On Animation Listener, erase it if there's already one.
     *
     * @param listener the On Animation Listener to set.
     */
    fun setOnAnimationListener(listener: OnAnimationListener) { this.listener = listener }

    /**
     * Remove the On Animation Listener.
     */
    fun removeOnAnimationListener() { listener = null }

    // --------------
    // METHODS OVERRIDE
    // --------------

    override fun open() { toggle(true) }

    override fun open(animate: Boolean) { toggle(animate) }

    override fun close() { toggle(true) }

    override fun close(animate: Boolean) { toggle(animate) }

    override fun toggle() { toggle(true) }

    override fun toggle(animate: Boolean) {

        // Get the speed dial state before switch state.
        val isOpen = isOpen

        // Call super to set original speed dial animations.
        super.toggle(animate)

        // Call on animation set and start, to allow touch them.
        if (animate)
            listener?.onToggle(isOpen)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeOnAnimationListener()
    }
}