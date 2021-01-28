package org.desperu.independentnews.ui.showArticle

/**
 * Interface to allow communications with Show Article Activity.
 */
interface ShowArticleInterface {

    /**
     * Marker to save custom view state, full screen or not.
     */
    var inCustomView: Boolean

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
}