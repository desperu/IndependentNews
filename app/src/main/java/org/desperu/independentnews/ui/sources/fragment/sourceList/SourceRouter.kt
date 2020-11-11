package org.desperu.independentnews.ui.sources.fragment.sourceList

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.models.SourceWithData
import org.desperu.independentnews.ui.showArticle.ShowArticleActivity
import org.desperu.independentnews.ui.sources.SourcesInterface
// TODO to move with activity, use in the two fragments.
/**
 * The source router that allows redirection of the user.
 */
interface SourceRouter {

    /**
     * Redirects the user to the SourcesDetailFragment to show sources detail.
     *
     * @param sourceWithData the source with data to show in the fragment.
     * @param imageView the image view to animate.
     * @param itemPosition the position of the source item in the recycler view.
     */
    fun showSourceDetail(sourceWithData: SourceWithData, imageView: View, itemPosition: Int)

    /**
     * Redirects the user to the ShowArticle Activity to show sources.
     * @param article the article to show in the Activity.
     */
    fun openShowArticle(article: Article)
}

/**
 * Implementation of the SourceRouter.
 *
 * @property activity the Activity that is used to perform redirection.
 *
 * @constructor Instantiates a new ArticleRouterImpl.
 *
 * @param activity the Activity that is used to perform redirection to set.
 */
class SourceRouterImpl(private val activity: AppCompatActivity): SourceRouter {

    /**
     * Redirects the user to the SourcesDetailFragment to show sources detail.
     *
     * @param sourceWithData the source with data to show in the fragment.
     * @param imageView the image view to animate.
     * @param itemPosition the position of the source item in the recycler view.
     */
    override fun showSourceDetail(sourceWithData: SourceWithData, imageView: View, itemPosition: Int) =
        (activity as SourcesInterface).showSourceDetail(sourceWithData, imageView, itemPosition)

    /**
     * Redirects the user to the Sources Activity to manage sources.
     * @param article the article to show in the Activity.
     */
    override fun openShowArticle(article: Article) =
        ShowArticleActivity.routeFromActivity(activity, article, null)
}