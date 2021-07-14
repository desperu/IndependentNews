package org.desperu.independentnews.ui.showArticle.design

import android.view.ViewGroup
import androidx.annotation.FloatRange

/**
 * Interface to allow communications with Scroll Handler.
 */
interface ScrollHandlerInterface {

    /**
     * The scrollable view group of the current fragment.
     */
    val scrollable: ViewGroup

    /**
     * The current scroll position.
     */
    var scrollPosition: Int

    /**
     * True if has scroll, false otherwise.
     */
    var hasScroll: Boolean

    /**
     * Setup the scroll listener on the nested scroll view.
     */
    fun setupScrollListener()

    /**
     * Returns the current scroll y value in percent.
     *
     * @param svScrollY the scroll y value, def value 0.
     *
     * @return the current scroll y value in percent.
     */
    fun getScrollYPercent(svScrollY: Int = 0): Float

    /**
     * Save the scroll position of the scroll view.
     */
    fun saveScrollPosition()

    /**
     * Scroll vertically to the y position value, if null restore scroll position.
     *
     * @param y the y value, vertical axe, to scroll to.
     */
    fun scrollTo(y: Int?)

    /**
     * Scroll vertically to the y percent value.
     *
     * @param yPercent the y percent value, vertical axe, to scroll to.
     */
    fun scrollTo(@FloatRange(from = 0.0, to = 1.0) yPercent: Float)

    /**
     * Smooth scroll vertically to the scroll percent value.
     *
     * @param scrollPercent the scroll position to restore.
     */
    fun smoothScrollTo(@FloatRange(from = 0.0, to = 1.0) scrollPercent: Float)
}