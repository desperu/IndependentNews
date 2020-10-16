package org.desperu.independentnews.ui.main.fragment.articleList

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
     * @param article the article to show in the Activity.
     * @param imageView the image view to animate.
     */
    fun openShowArticle(article: Article, imageView: View)
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
     * @param article the article to show in the Activity.
     * @param imageView the image view to animate.
     */
    override fun openShowArticle(article: Article, imageView: View) =
        ShowArticleActivity.routeFromActivity(activity, article, imageView)
}