package org.desperu.independentnews.ui.showArticle.design

import androidx.annotation.FloatRange

/**
 * Interface to allow communications with Article Design.
 */
interface ArticleDesignInterface {

    /**
     * True if it's the first page, false otherwise.
     */
    var isFirstPage: Boolean

    /**
     * Used to store the page scroll position.
     */
    var scrollPosition: Int

    /**
     * Returns the current scroll y value in percent.
     *
     * @param svScrollY the scroll y value, def value 0.
     *
     * @return the current scroll y value in percent.
     */
    fun getScrollYPercent(svScrollY: Int = 0): Float

    /**
     * Save the scroll position of the scroll view position.
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
     * Update loading progress bar, in app bar, with the new progress value.
     *
     * @param newProgress the new progress value.
     */
    fun updateLoadingProgress(newProgress: Int)

    /**
     * Handle layout design, used between page navigation to hide or show ui elements.
     *
     * @param progress the loading progress of the page.
     */
    fun handleDesign(progress: Int)

    /**
     * Hide article data container, depends of toHide value.
     *
     * @param toHide true to hide data container, false to show.
     */
    fun hideArticleDataContainer(toHide: Boolean)
}