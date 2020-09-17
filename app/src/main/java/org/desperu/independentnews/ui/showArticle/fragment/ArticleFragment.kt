package org.desperu.independentnews.ui.showArticle.fragment

import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import kotlinx.android.synthetic.main.fragment_article.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.BaseBindingFragment
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.ui.showArticle.ShowArticleInterface
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


/**
 * The name of the argument to received article for this Fragment.
 */
const val ARTICLE = "article"

/**
 * The name of the argument to received the position of this Fragment into the view pager.
 */
const val FRAG_POSITION = "fragPosition"

/**
 * Fragment to show article with title, image, category, author and published date.
 *
 * @constructor Instantiates a new ArticleFragment.
 */
class ArticleFragment: BaseBindingFragment() {

    // FROM BUNDLE TODO to perfect
    private val article: Article get() = arguments?.getParcelable(ARTICLE) ?: Article(title = "Error to retrieved article !")
    private val fragPosition: Int get() = arguments?.getInt(FRAG_POSITION) ?: -1

    // FOR DATA
    private lateinit var binding: ViewDataBinding
    private val viewModel by viewModel<ArticleViewModel> { parametersOf(article) }

    /**
     * Companion object, used to create a new instance of this fragment.
     */
    companion object {
        /**
         * Create a new instance of this fragment and set article.
         * @param article the article to show in this fragment.
         * @param fragPosition the position of this fragment into the view pager.
         * @return the new instance of ArticleFragment.
         */
        fun newInstance(article: Article, fragPosition: Int): ArticleFragment {
            val articleFragment = ArticleFragment()
            articleFragment.arguments = Bundle()
            articleFragment.arguments?.putParcelable(ARTICLE, article)
            articleFragment.arguments?.putInt(FRAG_POSITION, fragPosition)
            return articleFragment
        }
    }

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureDataBinding()

    override fun configureDesign() {
        configureWebView()
    }

    override fun updateDesign() {
        updateTransitionName()
        scheduleStartPostponedTransition()
        setupProgressBarWithScrollView()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure data binding and return the root view.
     * @return the binding root view.
     */
    private fun configureDataBinding(): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_article, container, false)
        binding.setVariable(org.desperu.independentnews.BR.viewModel, viewModel)
        return binding.root
    }

    /**
     * Configure the web view.
     */
    private fun configureWebView() {
        web_view.settings.javaScriptEnabled = true
        web_view.settings.javaScriptCanOpenWindowsAutomatically = true
        web_view.settings.setSupportZoom(true)
        web_view.settings.setNeedInitialFocus(false)

        // Set css style for the web view.
        web_view.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView, url: String) {
                val cssLink = article?.css
                val js = "var link = document.createElement('link');" +
                        " link.setAttribute('rel', 'stylesheet');" +
                        " link.setAttribute('href','$cssLink');" +
                        " link.setAttribute('type','text/css');" +
                        " document.head.appendChild(link);"
                view.evaluateJavascript(js, null)
                super.onPageFinished(view, url)
            }
        }
        // disable scroll on touch
//        web_view.setOnTouchListener { _, event -> event.action == MotionEvent.ACTION_MOVE }
    }

//    /**
//     * Configure and show Web View with Progress Bar.
//     */
//    private fun configureAndShowWebViewWithProgressBar() {
//
//        // Set progress bar with page loading.
//        webView.setWebChromeClient(object : WebChromeClient() {
//            override fun onProgressChanged(view: WebView, newProgress: Int) {
//                super.onProgressChanged(view, newProgress)
//                progressBar.setVisibility(View.VISIBLE)
//                progressBar.setMax(100)
//                progressBar.setProgress(newProgress)
//            }
//        })
//
//        // Force links and redirects to open in the WebView.
//        webView.setWebViewClient(object : WebViewClient() {
//            override fun onPageStarted(
//                view: WebView,
//                url: String,
//                favicon: Bitmap
//            ) {
//                super.onPageStarted(view, url, favicon)
//                articleUrl = url
//                progressBar.setProgress(0)
//            }
//
//            override fun onPageFinished(view: WebView, url: String) {
//                super.onPageFinished(view, url)
//                progressBar.setVisibility(View.GONE)
//                swipeRefreshLayout.setRefreshing(false)
//            }
//        })
//        webView.loadUrl(articleUrl)
//    }

    /**
     * Update transition name of the shared element (image).
     */
    private fun updateTransitionName() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            article_image.transitionName =
                getString(R.string.animation_main_to_show_article) + fragPosition
    }

    // --------------
    // UI
    // --------------

    /**
     * Schedule start postponed transition after the article_image has been measured.
     */
    private fun scheduleStartPostponedTransition() =
        (activity as ShowArticleInterface).scheduleStartPostponedTransition(article_image)

    /**
     * Setup progress bar with scroll view scroll position.
     */
    private fun setupProgressBarWithScrollView() {
        val sv = article_scroll_view
        var svScrollY = 0
        val setup = {
            svScrollY = if (svScrollY != 0) svScrollY else sv.scrollY
            val scrollHeight = sv.getChildAt(0).bottom - sv.measuredHeight
            val progress = (svScrollY.toFloat() / scrollHeight.toFloat()) * 100f

            // Use animation for API >= Nougat (24)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                article_progress_bar.setProgress(progress.toInt(), true)
            else
                article_progress_bar.progress = progress.toInt()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            sv.setOnScrollChangeListener { _, _, scrollY, _, _ -> svScrollY = scrollY; setup() }
        else
            sv.viewTreeObserver.addOnScrollChangedListener { svScrollY = 0; setup() }
    }
}