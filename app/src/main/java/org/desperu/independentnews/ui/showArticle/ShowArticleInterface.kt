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
     * Restore the scroll position of the scroll view.
     */
    fun restoreScrollPosition()

    /**
     * Update web view design, css style and margins.
     */
    fun updateWebViewDesign()

    /**
     * Update web view margins.
     */
    fun updateWebViewMargins()
}