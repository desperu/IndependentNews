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
     * Set decor view system Ui visibility flags.
     *
     * @param flags the system ui flags to set.
     */
    fun setDecorUiVisibility(flags: Int)

    /**
     * Set the screen orientation flags.
     *
     * @param flags the orientation flags to set.
     */
    fun setOrientation(flags: Int)

    /**
     * Update web view margins.
     */
    fun updateWebViewMargins()
}