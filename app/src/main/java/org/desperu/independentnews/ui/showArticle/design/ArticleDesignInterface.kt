package org.desperu.independentnews.ui.showArticle.design

/**
 * Interface to allow communications with Article Design.
 */
interface ArticleDesignInterface {

    /**
     * True if it's the first page, false otherwise.
     */
    var isFirstPage: Boolean

    /**
     * True if is a refresh page, false otherwise.
     */
    var isRefresh: Boolean

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

    /**
     * Update scroll progress bar, below the app bar, with the new scroll Y value.
     * If the given scroll Y is null, direct get the current value in the nested scroll view.
     *
     * @param scrollY the new scroll Y value.
     */
    fun updateScrollProgress(scrollY: Int)

    /**
     * Show or hide fabs menu, depends of to show value.
     *
     * @param toShow    true to show the fabs menu, false to hide.
     * @param toDelay   true to delay show fabs menu, false to do on transition end.
     */
    fun showFabsMenu(toShow: Boolean, toDelay: Boolean = true)

    /**
     * Show the remove pause dialog, when reach the bottom of a paused article.
     */
    fun showRemovePausedDialog()
}