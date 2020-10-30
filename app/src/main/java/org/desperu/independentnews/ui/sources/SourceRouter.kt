package org.desperu.independentnews.ui.sources

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.desperu.independentnews.models.Source
import org.desperu.independentnews.ui.showArticle.ShowArticleActivity

/**
 * The source router that allows redirection of the user.
 */
interface SourceRouter {

    /**
     * Redirects the user to the ShowArticle Activity to show sources.
     *
     * @param source the source to show in the Activity.
     * @param imageView the image view to animate.
     */
    fun openShowArticle(source: Source, imageView: View)
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
class SourceRouterImpl(private val activity: AppCompatActivity): SourceRouter {

    /**
     * Redirects the user to the Sources Activity to manage sources.
     * @param source the source to show in the Activity.
     * @param imageView the image view to animate.
     */
    override fun openShowArticle(source: Source, imageView: View) =
        ShowArticleActivity.routeFromActivity(activity, source)
}