package org.desperu.independentnews.ui.showArticle

import androidx.fragment.app.Fragment
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.views.webview.MyWebView

/**
 * Interface to allow communications with Show Article Activity.
 */
interface ShowArticleInterface {

    /**
     * The instance of the ShowArticleActivity.
     */
    val activity: ShowArticleActivity

    /**
     * The view model of the ShowArticleActivity.
     */
    val viewModel: ArticleViewModel

    /**
     * The web view of the current fragment.
     */
    val webView: MyWebView

    /**
     * The article currently show in the web view.
     */
    val article: Article

    /**
     * The screen shot of the previous activity used as background for the transition.
     */
    val transitionBg: ByteArray?

    /**
     * Show fragment for the given article, or web url.
     *
     * @param article the article to display in the fragment.
     */
    fun showFragment(article: Article)

    /**
     * Return the current fragment instance host by the navigation host fragment.
     *
     * @return the current fragment instance.
     */
    fun getCurrentFragment(): Fragment?

    /**
     * Convenience call for on back pressed, allow system to handle back action.
     */
    fun goBack()

    /**
     * Update app bar on touch listener, used to finish app bar anim.
     */
    fun updateAppBarOnTouch()
}