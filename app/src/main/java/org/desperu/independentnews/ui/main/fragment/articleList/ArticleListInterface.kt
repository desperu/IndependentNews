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
}