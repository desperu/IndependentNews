package org.desperu.independentnews.ui.showArticle.fragment

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ScrollView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
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
    private val viewModel: ArticleViewModel by viewModel { parametersOf(article) }
    private val scrollView: ScrollView by lazy { article_scroll_view }
    private var scrollListener: Any? = null

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
     * Configure scroll view listener to setup progress bar and app bar with the scroll Y value.
     */
    private fun configureScrollViewListener() {
        var svOldScrollX = 0
        var svOldScrollY = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollListener = View.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                setupProgressBarWithScrollView(scrollY)
                setupAppBarWithScrollView(v, scrollX, scrollY, oldScrollX, oldScrollY)
            }
            scrollView.setOnScrollChangeListener(scrollListener as View.OnScrollChangeListener)
        }
        else {
            scrollListener = ViewTreeObserver.OnScrollChangedListener {
                val scrollX = scrollView.scrollX
                val scrollY = scrollView.scrollY
                setupProgressBarWithScrollView(scrollY)
                setupAppBarWithScrollView(scrollView, scrollX, scrollY, svOldScrollX, svOldScrollY)
                svOldScrollX = scrollX
                svOldScrollY = scrollY
            }
            scrollView.viewTreeObserver.addOnScrollChangedListener(scrollListener as ViewTreeObserver.OnScrollChangedListener)
        }
    }

    /**
     * Remove scroll view listener.
     */
    private fun removeScrollViewListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            scrollView.setOnScrollChangeListener(null)
        else
            scrollView.viewTreeObserver.removeOnScrollChangedListener(scrollListener as ViewTreeObserver.OnScrollChangedListener?)
        scrollListener = null
    }

    /**
     * Update transition name of the shared element (image).
     */
    private fun updateTransitionName() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            article_image.transitionName =
                getString(R.string.animation_main_to_show_article) + fragPosition
    }

    // -----------------
    // METHODS OVERRIDES
    // -----------------

    override fun onResume() {
        super.onResume()
        configureScrollViewListener()
    }
// TODO always mistake for restored frag
    override fun onPause() {
        super.onPause()
        removeScrollViewListener()
    }

    // --------------
    // UI
    // --------------

    /**
     * Schedule start postponed transition after the article_image has been measured.
     */
    private fun scheduleStartPostponedTransition() =
        (activity as ShowArticleInterface).scheduleStartPostponedTransition(article_image)

    // --------------
    // UTILS
    // --------------

    /**
     * Setup progress bar with scroll view scroll position.
     * @param scrollY the scroll Y position of the scroll view.
     */
    private fun setupProgressBarWithScrollView(scrollY: Int) {
        val scrollHeight = scrollView.getChildAt(0).bottom - scrollView.measuredHeight
        val progress = (scrollY.toFloat() / scrollHeight.toFloat()) * 100f

        // Use animation for API >= Nougat (24)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            article_progress_bar.setProgress(progress.toInt(), true)
        else
            article_progress_bar.progress = progress.toInt()
    }

    /**
     * Setup app bar with scroll view scroll position.
     * @param sv the scrollView.
     * @param scrollX the scroll X position of the scroll view.
     * @param scrollY the scroll Y position of the scroll view.
     * @param oldScrollX the old scroll X position of the scroll view.
     * @param oldScrollY the old scroll Y position of the scroll view.
     */
    private fun setupAppBarWithScrollView(sv: View, scrollX: Int, scrollY: Int, oldScrollX: Int, oldScrollY: Int) {
        val target = sv.parent.parent as ViewPager
        val coordinator = target.parent as CoordinatorLayout
        val appbar = coordinator.findViewById<AppBarLayout>(R.id.appbar)
        val behavior = (appbar.layoutParams as CoordinatorLayout.LayoutParams).behavior

        val dxConsumed = if (scrollX <= 0) 0 else scrollX - oldScrollX
        val dyConsumed = if (scrollY <= 0) 0 else scrollY - oldScrollY

//        behavior?.onStartNestedScroll(coordinator, appbar, target, target, ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.SCROLL_AXIS_VERTICAL)
        behavior?.onNestedScroll(
            coordinator, appbar, target,
            dxConsumed, dyConsumed, oldScrollX, oldScrollY,
            ViewCompat.SCROLL_AXIS_VERTICAL, intArrayOf(0, 0)
        )
    }
}