package org.desperu.independentnews.ui.sources.fragment.sourceList

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.desperu.independentnews.models.SourcePage
import org.desperu.independentnews.models.SourceWithData
import org.desperu.independentnews.ui.showArticle.ShowArticleActivity
import org.desperu.independentnews.ui.sources.SourcesInterface

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
     *
     * @param sourcePage the source page to show in the Activity.
     */
    fun openShowArticle(sourcePage: SourcePage)
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
     * @param sourcePage the source page to show in the Activity.
     */
    override fun openShowArticle(sourcePage: SourcePage) =
        ShowArticleActivity.routeFromActivity(activity, sourcePage)
}