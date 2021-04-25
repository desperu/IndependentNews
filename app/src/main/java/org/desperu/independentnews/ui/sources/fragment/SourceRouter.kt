package org.desperu.independentnews.ui.sources.fragment

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.database.SourceWithData
import org.desperu.independentnews.ui.showArticle.ShowArticleActivity
import org.desperu.independentnews.ui.sources.SourcesInterface

/**
 * The source router that allows redirection of the user.
 */
interface SourceRouter {

    /**
     * Redirects the user to the SourcesDetailFragment to show sources detail.
     *
     * @param sourceWithData    the source with data to show in the fragment.
     * @param itemPosition      the position of the source item in the recycler view.
     * @param sharedElements    the shared elements to animate during transition.
     */
    fun showSourceDetail(
        sourceWithData: SourceWithData,
        itemPosition: Int,
        vararg sharedElements: View
    )

    /**
     * Redirects the user to the ShowArticle Activity to show sources.
     *
     * @param article       the article to show in the Activity.
     * @param isExpanded    true if the app bar is expanded, false if is collapsed.
     */
    fun openShowArticle(article: Article, isExpanded: Boolean)
}

/**
 * Implementation of the SourceRouter.
 *
 * @property activity the Activity that is used to perform redirection.
 *
 * @constructor Instantiates a new SourceRouterImpl.
 *
 * @param activity the Activity that is used to perform redirection to set.
 */
class SourceRouterImpl(private val activity: AppCompatActivity): SourceRouter {

    /**
     * Redirects the user to the SourcesDetailFragment to show sources detail.
     *
     * @param sourceWithData    the source with data to show in the fragment.
     * @param itemPosition      the position of the source item in the recycler view.
     * @param sharedElements    the shared elements to animate during transition.
     */
    override fun showSourceDetail(
        sourceWithData: SourceWithData,
        itemPosition: Int,
        vararg sharedElements: View
    ) =
        (activity as SourcesInterface)
            .showSourceDetail(sourceWithData, itemPosition, *sharedElements)

    /**
     * Redirects the user to the Sources Activity to manage sources.
     *
     * @param article       the article to show in the Activity.
     * @param isExpanded    true if the app bar is expanded, false if is collapsed.
     */
    override fun openShowArticle(article: Article, isExpanded: Boolean) =
        ShowArticleActivity.routeFromActivity(activity, article, isExpanded)
}