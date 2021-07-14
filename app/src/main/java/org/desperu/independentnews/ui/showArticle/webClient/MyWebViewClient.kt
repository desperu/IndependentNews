package org.desperu.independentnews.ui.showArticle.webClient

import android.graphics.Bitmap
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.independentnews.extension.parseHtml.mToString
import org.desperu.independentnews.extension.sendMailTo
import org.desperu.independentnews.extension.showInBrowser
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.ui.showArticle.ImageRouter
import org.desperu.independentnews.ui.showArticle.ShowArticleInterface
import org.desperu.independentnews.ui.showArticle.design.ArticleDesignInterface
import org.desperu.independentnews.ui.showArticle.design.ScrollHandlerInterface
import org.desperu.independentnews.utils.Utils.isHtmlData
import org.desperu.independentnews.utils.Utils.isImageUrl
import org.desperu.independentnews.utils.Utils.isMailTo
import org.desperu.independentnews.utils.Utils.isSameDataType
import org.desperu.independentnews.utils.Utils.isSourceArticleUrl
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

/**
 * Web view client for the web view.
 *
 * @constructor Instantiate a new MyWebViewClient.
 */
class MyWebViewClient : WebViewClient(), KoinComponent {

    // FOR COMMUNICATION
    private val showArticleInterface: ShowArticleInterface = get()
    private val activity = showArticleInterface.activity
    private val viewModel = showArticleInterface.viewModel
    private val articleDesign: ArticleDesignInterface by inject()
    private val scrollHandler: ScrollHandlerInterface by inject()
    private val router: ImageRouter = get()

    // FOR DATA
    var actualUrl: String = ""

    // --------------
    // METHODS OVERRIDE
    // --------------

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        url?.let {
            actualUrl = it
            handleNavigation(it)
            updateWebViewStart(it)
        }
    }

    override fun onPageCommitVisible(view: WebView?, url: String?) {
        super.onPageCommitVisible(view, url)
        articleDesign.handleDesign(100)
    }
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        articleDesign.handleDesign(101) // To finish loading page
    }

    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val url = request?.url.mToString()
            handleRedirect(url)
        } else {
            scrollHandler.saveScrollPosition()
            super.shouldOverrideUrlLoading(view, request)
        }
    }

    // --------------
    // NAVIGATION
    // --------------

    /**
     * Handle note redirection into the page, if it is scroll to bottom of page.
     *
     * @param url the url that the loading start.
     *
     * @return true if consumed, false otherwise.
     */
    private fun handleRedirect(url: String): Boolean = when {
        isImageUrl(url) -> { router.openShowImages(arrayListOf(url)); true }
        url.endsWith(".pdf") -> { activity.showInBrowser(url); true }
        isMailTo(url) -> { activity.sendMailTo(url); true }
        else -> {
            val fromHtmlData = isHtmlData(actualUrl)

            // Prepare to load a new page.
            articleDesign.handleDesign(0)
            addPageToHistory()

            if (fromHtmlData) showPage(url)

            // Else handle by the web view
            fromHtmlData
        }
    }

    /**
     * Show the new page for the given url,
     * handle different data type and switch fragment if needed.
     *
     * @param newUrl the new url to display.
     */
    private fun showPage(newUrl: String) = activity.lifecycleScope.launch(Dispatchers.Main) {
        if (isSameDataType(actualUrl, newUrl)) {
            if (isSourceArticleUrl(newUrl))
                viewModel.fetchAndSetArticle(newUrl)
            else
                activity.webView.loadUrl(newUrl)
        } else {
            val article = if (isSourceArticleUrl(newUrl)) viewModel.fetchArticle(newUrl)
                          else Article(url = newUrl)
            showArticleInterface.showFragment(article)
        }
    }

    /**
     * Show previous page in the web view.
     *
     * @param actualUrl     the actual url of the web view.
     * @param previousPage  the previous page pair, Article and scroll position.
     */
    internal fun webViewBack(actualUrl: String, previousPage: Pair<Article, Int>) {
        val isSameDataType = isSameDataType(actualUrl, previousPage.first.url)
        val newIsHtmlData = isHtmlData(previousPage.first.url)

        // TODO not good when switch fragment, switch article design instance too
        scrollHandler.scrollPosition = previousPage.second // Needed every times

        if (isSameDataType) { // Already back if is html data, when call viewModel.previousPage()
            articleDesign.handleDesign(0)
            if (!newIsHtmlData) activity.webView.goBack()
        } else
            showPage(previousPage.first.url)
    }

    // --------------
    // UTILS
    // --------------

    /**
     * Update web view start loading, update text size, background, margins and css style.
     *
     * @param url the new url to load.
     */
    private fun updateWebViewStart(url: String) = activity.lifecycleScope.launch(Dispatchers.Main) {
        val sourceName = viewModel.article.get()?.source?.name ?: ""
        activity.webView.updateWebViewStart(url, sourceName, viewModel.getCss())
    }

    /**
     * Add the previous page in the navigation history.
     */
    private fun addPageToHistory() {
        if (!articleDesign.isRefresh)
            viewModel.addPage(
                Pair(
                    if (isHtmlData(actualUrl)) viewModel.article.get() ?: Article()
                    else Article(url = actualUrl),
                    scrollHandler.scrollable.scrollY
                )
            )
    }

    /**
     * Handle web view navigation, hide article data container, show loading progress,
     * hide scroll view during loading.
     *
     * @param url the url that the loading start.
     */
    private fun handleNavigation(url: String) {
        articleDesign.hideArticleDataContainer(!isHtmlData(url))

        if (viewModel.navHistory.size > 0) articleDesign.handleDesign(0)
    }
}