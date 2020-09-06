package org.desperu.independentnews.ui.main

import android.view.View
import org.desperu.independentnews.models.Article

/**
 * Interface to allow communications with Main Activity.
 */
interface MainInterface {

    /**
     * Return the main list adapter instance.
     * @return the main list adapter instance.
     */
    fun getRecyclerAdapter(): MainListAdapter?

    /**
     * Navigate to Show Article Activity.
     * @param article the article to show.
     * @param clickedView the clicked view.
     */
    fun navigateToShowArticle(article: Article, clickedView: View)
}