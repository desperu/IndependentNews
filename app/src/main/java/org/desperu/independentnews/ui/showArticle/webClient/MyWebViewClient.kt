package org.desperu.independentnews.ui.showArticle.webClient

import android.graphics.Bitmap
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_show_article.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.design.bindView
import org.desperu.independentnews.extension.parseHtml.mToString
import org.desperu.independentnews.extension.showInBrowser
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.ui.showArticle.ImageRouter
import org.desperu.independentnews.ui.showArticle.ShowArticleInterface
import org.desperu.independentnews.ui.showArticle.design.ArticleDesignInterface
import org.desperu.independentnews.utils.Utils
import org.desperu.independentnews.utils.Utils.isHtmlData
import org.desperu.independentnews.utils.Utils.isImageUrl
import org.desperu.independentnews.utils.Utils.isNoteRedirect
import org.desperu.independentnews.utils.Utils.isSourceArticleUrl
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.parameter.parametersOf

/**
 * Web view client for the web view.
 *
 * @constructor Instantiate a new MyWebViewClient.
 */
class MyWebViewClient : WebViewClient(), MyWebViewClientInterface, KoinComponent {

    // FOR COMMUNICATION
    private val showArticleInterface: ShowArticleInterface = get()
    private val activity = showArticleInterface.activity
    private val viewModel = showArticleInterface.viewModel
    private val articleDesign: ArticleDesignInterface = get()
    private val router: ImageRouter = get()

    // FOR DESIGN
    private val sv: NestedScrollView by bindView(activity, R.id.article_scroll_view)

    // FOR DATA
    override lateinit var actualUrl: String
    internal var navigationCount = -1
    private var isNoteRedirect = false
    private var noteScrollPosition = -1

    init {
        configureKoinDependency()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure koin dependency for article design.
     */
    private fun configureKoinDependency() {
        get<MyWebViewClientInterface> { parametersOf(this) }
    }

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

    override fun onPageFinished(view: WebView?, url: String?) {
        articleDesign.handleDesign(101)
        super.onPageFinished(view, url)
    }

    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val url = request?.url.mToString()
            handleRedirect(url)
        } else {
            articleDesign.saveScrollPosition()
            super.shouldOverrideUrlLoading(view, request)
        }
    }

    /**
     * Update web view start loading, update text size, background, margins and css style.
     *
     * @param url the new url to load.
     */
    private fun updateWebViewStart(url: String) = activity.lifecycleScope.launch(Dispatchers.Main) {
        val sourceName = viewModel.article.get()?.source?.name ?: ""
        activity.web_view.updateWebViewStart(url, sourceName, viewModel.getCss())
    }

    /**
     * Show previous page in the web view.
     */
    internal fun webViewBack(previousPage: Pair<Article?, Int>?) {
        articleDesign.handleDesign(0)
        articleDesign.scrollPosition = previousPage?.second ?: 0
        if (previousPage?.first == null) activity.web_view.goBack()
        navigationCount -= 2
    }

    // --------------
    // UTILS
    // --------------

    /**
     * Handle note redirection into the page, if it is scroll to bottom of page.
     *
     * @param url the url that the loading start.
     *
     * @return true if consumed, false otherwise.
     */
    private fun handleRedirect(url: String): Boolean = when {
        isNoteRedirect(Utils.getPageNameFromUrl(url)) -> {
            if (!isNoteRedirect) noteScrollPosition = sv.scrollY
//            scrollPosition = -1
            isNoteRedirect = !isNoteRedirect
            val svBottom = sv.getChildAt(0).bottom - sv.measuredHeight
            val y = if (isNoteRedirect) svBottom else noteScrollPosition
            sv.smoothScrollTo(sv.scrollX, y, 1000)
            true
        }
        isImageUrl(url) -> { router.openShowImages(arrayListOf(url)); true }
        url.endsWith(".pdf") -> { activity.showInBrowser(url); true }
        isSourceArticleUrl(url) -> {
            articleDesign.handleDesign(0)
            addPageToHistory()
            viewModel.fetchArticle(url)
            true
        }
        else -> {
            articleDesign.handleDesign(0)
            addPageToHistory()
            if (noteScrollPosition == -1) isNoteRedirect = false
            false
        }
        // TODO handle if is blank !!!1
    }

    /**
     * Add the previous page in the navigation history.
     */
    private fun addPageToHistory() {
        viewModel.addPage(
            navigationCount,
            Pair(if (isHtmlData(actualUrl)) viewModel.article.get() else null, sv.scrollY)
        )
    }

    /**
     * Handle web view navigation, hide article data container, show loading progress,
     * hide scroll view during loading and manage navigation count.
     *
     * @param url the url that the loading start.
     */
    private fun handleNavigation(url: String) {
        articleDesign.hideArticleDataContainer(!isHtmlData(url))

        if (navigationCount >= 0) articleDesign.handleDesign(0)

        if (url.contains(showArticleInterface.article.article)) navigationCount = 0
        else navigationCount += 1
    }
}