package org.desperu.independentnews.ui.main.fragment.articleList

/**
 * Interface that's allow communication with it's fragment.
 */
interface ArticleListInterface {

    /**
     * The key of the fragment.
     */
    val fragKey: Int?

    /**
     * Return the article list adapter instance.
     * @return the article list adapter instance.
     */
    fun getRecyclerAdapter(): ArticleListAdapter?

    /**
     * Show no article and hide recycler view, or invert, depends of toShow value.
     * @param toShow true to show no article, false otherwise.
     */
    fun showNoArticle(toShow: Boolean)

    /**
     * Show or hide filter motion, depends of toShow value.
     * @param toShow true to show filter motion, false to hide.
     */
    fun showFilterMotion(toShow: Boolean)

    /**
     * Update filters motion state adapter state, when switch fragment.
     * @param isFiltered true if the adapter is filtered, false otherwise.
     */
    fun updateFiltersMotionState(isFiltered: Boolean)

    /**
     * Close swipe action container.
     */
    fun closeSwipeContainer()
}