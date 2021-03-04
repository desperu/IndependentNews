package org.desperu.independentnews.ui.main.fragment.articleList

import android.util.Pair
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.ui.showArticle.ShowArticleActivity

/**
 * The article router that allows redirection of the user.
 */
interface ArticleRouter {

    /**
     * Redirects the user to the ShowArticle Activity to show articles.
     *
     * @param article           the article to show in the Activity.
     * @param sharedElements    the shared elements to animate.
     */
    fun openShowArticle(article: Article, vararg sharedElements: Pair<View, String>)
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
     * Redirects the user to the Show Article Activity to show article.
     *
     * @param article           the article to show in the Activity.
     * @param sharedElements    the shared elements to animate.
     */
    override fun openShowArticle(article: Article, vararg sharedElements: Pair<View, String>) =
        ShowArticleActivity.routeFromActivity(activity, article, true, *sharedElements)
}