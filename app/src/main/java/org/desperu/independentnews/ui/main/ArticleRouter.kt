package org.desperu.independentnews.ui.main

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.ui.showArticle.ShowArticleActivity

/**
 * The article router that allows redirection of the user.
 */
interface ArticleRouter {

    /**
     * Redirects the user to the ShowArticle Activity to show articles.
     * @param articleList the article list to show in the Activity.
     * @param position the position of the clicked article in the list.
     * @param clickedView the clicked view.
     */
    fun openShowArticle(articleList: List<Article>,
                        position: Int,
                        clickedView: View
    )
}

/**
 * Implementation of the ArticleRouter.
 *
 * @property activity the Activity that is used to perform redirection.
 *
 * @constructor Instantiates a new ArticleRouterImpl.
 *
 * @param activity the Activity that is used to perform redirection to set.
 */
class ArticleRouterImpl(private val activity: AppCompatActivity): ArticleRouter {

    /**
     * Redirects the user to the ManageEstate Activity to manage estate.
     * @param articleList the article list to show in the Activity.
     * @param position the position of the clicked article in the list.
     * @param clickedView the clicked view.
     */
    override fun openShowArticle(articleList: List<Article>,
                                 position: Int,
                                 clickedView: View
    ) =
        ShowArticleActivity.routeFromActivity(activity, ArrayList(articleList), position, clickedView)
}