package org.desperu.independentnews.ui.showArticle.design

/**
 * Interface to allow communications with Article Design.
 */
interface ArticleDesignInterface {

    /**
     * Used to store the current scroll position.
     */
    var scrollPosition: Int

    /**
     * True if it's the first page, false otherwise.
     */
    var isFirstPage: Boolean

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
     * Update app bar loading progress bar with the new progress value.
     *
     * @param newProgress the new progress value.
     */
    fun updateProgress(newProgress: Int)

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