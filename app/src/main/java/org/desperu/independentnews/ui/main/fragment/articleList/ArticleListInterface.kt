package org.desperu.independentnews.ui.main.fragment.articleList

/**
 * Interface that's allow communication with it's fragment.
 */
interface ArticleListInterface {

    /**
     * Return the article list adapter instance.
     * @return the article list adapter instance.
     */
    fun getRecyclerAdapter(): ArticleListAdapter?

    /**
     * Apply selected filters to the current article list.
     * @param selectedMap the map of selected filters to apply.
     * @param isFiltered true if apply filters to the list, false otherwise.
     */
    fun filterList(selectedMap: Map<Int, MutableList<String>>, isFiltered: Boolean)

    /**
     * Show no article and hide recycler view, or invert, depends of toShow value.
     * @param toShow true to show no article, false otherwise.
     */
    fun showNoArticle(toShow: Boolean)
}