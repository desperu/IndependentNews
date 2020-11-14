package org.desperu.independentnews.ui.showArticle

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import kotlinx.android.synthetic.main.activity_show_article.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseBindingActivity
import org.desperu.independentnews.extension.design.bindView
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.utils.Utils.isSourceUrl
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * The name of the argument to received article for this Activity.
 */
const val ARTICLE: String = "article"

/**
 * Activity to show articles list.
 *
 * @constructor Instantiates a new ShowArticleActivity.
 */
class ShowArticleActivity: BaseBindingActivity(), ShowArticleInterface {

    // FROM BUNDLE
    private val article: Article
        get() = intent.getParcelableExtra(ARTICLE)
            ?: Article(title = getString(R.string.show_article_activity_article_error))

    // FOR DATA
    private lateinit var binding: ViewDataBinding
    private val viewModel: ArticleViewModel by viewModel { parametersOf(article) }
    private var mWebChromeClient: WebChromeClient? = null
    override var inCustomView: Boolean = false
    private lateinit var actualUrl: String

    // FOR UI
    private val sv: NestedScrollView by bindView(R.id.article_scroll_view)
//    private var margins = 0
    private var scrollPosition = -1
    private var navigationCount = 0

    /**
     * Companion object, used to redirect to this Activity.
     */
    companion object {
        /**
         * Redirects from an Activity to this Activity with transition animation.
         *
         * @param activity the activity use to perform redirection.
         * @param article the article to show in this activity.
         * @param imageView the image view to animate.
         */
        fun routeFromActivity(activity: AppCompatActivity,
                              article: Article,
                              imageView: View?) {
            val intent = Intent(activity, ShowArticleActivity::class.java)
                .putExtra(ARTICLE, article)

            // Start Animation
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && imageView != null) {
                val options = ActivityOptions.makeSceneTransitionAnimation(
                    activity,
                    imageView,
                    activity.getString(R.string.animation_main_to_show_article)
                )
                activity.startActivity(intent, options.toBundle())
            } else {
                activity.startActivity(intent)
            }
        }
    }

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureDataBinding()

    override fun configureDesign() {
        configureWebView()
        configureAppBar()
        showAppBarIcon(listOf(R.id.back_arrow_icon, R.id.share_icon))
        postponeSceneTransition()
        scheduleStartPostponedTransition(article_image)
        setActivityTransition()
        setupProgressBarWithScrollView()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure data binding and return the root view.
     *
     * @return the binding root view.
     */
    private fun configureDataBinding(): View {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_article)
        binding.setVariable(org.desperu.independentnews.BR.viewModel, viewModel)
        return binding.root
    }

    /**
     * Configure the web view.
     */
    @Suppress("Deprecation", "SetJavaScriptEnabled")
    private fun configureWebView() {
        web_view.settings.apply {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
        }

        // Set css style for the web view.
        web_view.webViewClient = webViewClient

        mWebChromeClient = MyWebChromeClient(this, web_view)
        web_view.webChromeClient = mWebChromeClient
    }

    /**
     * Web view client for the web view.
     */
    private val webViewClient = object : WebViewClient() {

        // Force links and redirects to open in the WebView.
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            url?.let {
                actualUrl = url
                web_view.updateTextSize(actualUrl, article.sourceName)

                // Handle web view navigation
                hideArticleDataContainer(!isSourceUrl(url))
                if (!isSourceUrl(url)) {
                    sv.scrollY = 0
                    article_loading_progress_bar.apply { show(); visibility = View.VISIBLE }

                    navigationCount += 1
                    if (navigationCount == 1)
                        article_scroll_view.visibility = View.INVISIBLE
                } else
                    navigationCount = 0
            }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            url?.let { if (!web_view.isDesignProperlySet(it, article.sourceName)) updateWebViewDesign() }
            article_loading_progress_bar.hide()
            article_scroll_view.visibility = View.VISIBLE
            web_view.settings.textZoom = web_view.settings.textZoom
            url?.let { if (isSourceUrl(it) && scrollPosition > -1) restoreScrollPosition() }
            super.onPageFinished(view, url)
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            saveScrollPosition()
            return super.shouldOverrideUrlLoading(view, request)
        }
    }

    // --------------
    // METHODS OVERRIDE
    // --------------

    override fun onPause() {
        super.onPause()
        web_view.onPause()
    }

    override fun onResume() {
        super.onResume()
        web_view.onResume()
    }

    override fun onStop() {
        super.onStop()
        if (inCustomView) hideCustomView()
    }

    override fun onBackPressed() = when {
        inCustomView -> hideCustomView()
        web_view.canGoBack() -> {
            article_scroll_view.visibility = View.INVISIBLE
            web_view.goBack()
        }
        else -> super.onBackPressed()
    }

    // --------------
    // ACTION
    // --------------

    /**
     * On click back arrow icon menu.
     */
    @Suppress("unused_parameter")
    fun onClickBackArrow(v: View) = onClickBackArrow()

    /**
     * On click share icon menu.
     */
    @Suppress("unused_parameter")
    fun onClickShare(v: View) = shareArticle()

    // --------------
    // UI
    // --------------

    /**
     * Postpone the shared elements enter transition, because the shared elements
     * is an image downloaded from network.
     */
    private fun postponeSceneTransition() = supportPostponeEnterTransition()

    /**
     * Schedules the shared element transition to be started immediately
     * after the shared element has been measured and laid out within the
     * activity's view hierarchy.
     *
     * @param sharedElement the shared element to animate for the transition.
     */
    private fun scheduleStartPostponedTransition(sharedElement: View) {
        sharedElement.doOnPreDraw { supportStartPostponedEnterTransition() }
    }

    /**
     * Set custom activity transition, only for source detail to source page transition.
     */
    private fun setActivityTransition() {
        if (article.id == 0L)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    /**
     * Setup progress bar with scroll view scroll position.
     */
    private fun setupProgressBarWithScrollView() {
        var svScrollY = 0
        val setup = {
            svScrollY = if (svScrollY != 0) svScrollY else sv.scrollY
            val scrollHeight = sv.getChildAt(0).bottom - sv.measuredHeight
            val progress = (svScrollY.toFloat() / scrollHeight.toFloat()) * 100f

            // Use animation for API >= Nougat (24)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                article_scroll_progress_bar.setProgress(progress.toInt(), true)
            else
                article_scroll_progress_bar.progress = progress.toInt()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            sv.setOnScrollChangeListener { _, _, scrollY, _, _ -> svScrollY = scrollY; setup() }
        else
            sv.viewTreeObserver.addOnScrollChangedListener { svScrollY = 0; setup() }
    }

    /**
     * Hide article data container, depends of toHide value.
     *
     * @param toHide true to hide data container, false to show.
     */
    private fun hideArticleDataContainer(toHide: Boolean) {
        article_data_container.visibility = if (toHide) View.GONE else View.VISIBLE
    }

    /**
     * Hide video custom view.
     */
    private fun hideCustomView() { mWebChromeClient?.onHideCustomView() }

    /**
     * Save the scroll position of the scroll view.
     */
    override fun saveScrollPosition() { scrollPosition = sv.scrollY }

    /**
     * Restore the saved scroll position of the scroll view.
     */
    override fun restoreScrollPosition() { sv.doOnPreDraw { sv.scrollY = scrollPosition } }

    /**
     * Set decor system ui visibility.
     *
     * @param flags the decor system ui visibility flags to set.
     */
    override fun setDecorUiVisibility(flags: Int) { window.decorView.systemUiVisibility = flags }

    /**
     * Set requested screen orientation.
     *
     * @param flags the screen orientation flags to set.
     */
    override fun setOrientation(flags: Int) { requestedOrientation = flags }

    /**
     * Update the design of the web view, with css and javascript support.
     * Set custom margins to the web view for Reporterre source.
     */
    override fun updateWebViewDesign() {
        if (!::actualUrl.isInitialized) return
        web_view.updateWebViewDesign(article.sourceName, actualUrl, article.cssUrl)
    }

    // --------------
    // UTILS
    // --------------

    /**
     * Share article with title and url, to other applications.
     */
    @Suppress("deprecation")
    private fun shareArticle() {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
        } else
            share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        if (isSourceUrl(actualUrl)) {
            share.putExtra(Intent.EXTRA_SUBJECT, article.title)
            share.putExtra(Intent.EXTRA_TEXT, article.url)

        } else {
            share.putExtra(Intent.EXTRA_SUBJECT, web_view.title)
            share.putExtra(Intent.EXTRA_TEXT, actualUrl)
        }

        startActivity(Intent.createChooser(share, getString(R.string.show_article_activity_share_chooser_title)))
    }
}