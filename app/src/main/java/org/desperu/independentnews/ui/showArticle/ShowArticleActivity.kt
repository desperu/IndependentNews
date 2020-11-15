package org.desperu.independentnews.ui.showArticle

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnPreDraw
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import kotlinx.android.synthetic.main.activity_show_article.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseBindingActivity
import org.desperu.independentnews.extension.design.bindView
import org.desperu.independentnews.extension.parseHtml.mToString
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.utils.Utils.getPageNameFromUrl
import org.desperu.independentnews.utils.Utils.isNoteRedirect
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
    private var scrollPosition = -1
    private var navigationCount = 0
    private var isNoteRedirect = false
    private var noteScrollPosition = -1

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

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            url?.let {
                actualUrl = it
//                web_view.updateTextSize(actualUrl, article.sourceName)

                handleNavigation(it)
            }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            url?.let { updateDesign(it) }
            super.onPageFinished(view, url)
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            saveScrollPosition()
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val url = request?.url.mToString()
                if (!isNoteRedirect && isNoteRedirect(getPageNameFromUrl(url)))
//                    || (!isNoteRedirect(getPageNameFromUrl(url)) && !isSourceUrl(url)))
                    noteScrollPosition = sv.scrollY
                handleRedirect(url)
            } else
                super.shouldOverrideUrlLoading(view, request)
        }
    }

    // --------------
    // METHODS OVERRIDE
    // --------------

    override fun onPause() { super.onPause(); web_view.onPause() }

    override fun onResume() { super.onResume(); web_view.onResume() }

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
        article_loading_progress_bar.isShown -> updateDesign(actualUrl)
        isNoteRedirect -> { isNoteRedirect = false; scrollTo(noteScrollPosition) }
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
     * Update layout and web view design.
     *
     * @param url the actual url of the web view.
     */
    private fun updateDesign(url: String) {
        if (!web_view.isDesignProperlySet(url, article.sourceName))
            web_view.updateWebViewDesign(article.sourceName, actualUrl, article.cssUrl)
        article_loading_progress_bar.hide()
        article_scroll_view.visibility = View.VISIBLE
//                web_view.settings.textZoom = web_view.settings.textZoom
        if (isSourceUrl(url) && scrollPosition > -1) scrollTo(scrollPosition)
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
     * Scroll horizontally to the y position value, use smooth scroll with custom duration.
     *
     * @param y the y, horizontal axe, value to scroll to.
     */
    private fun scrollTo(y: Int) { sv.smoothScrollTo(sv.scrollX, y, 1000) }

    /**
     * Save the scroll position of the scroll view.
     */
    override fun saveScrollPosition() { scrollPosition = sv.scrollY }

    /**
     * Restore the saved scroll position of the scroll view.
     */
    override fun restoreScrollPosition() { sv.doOnPreDraw { scrollTo(scrollPosition) } }

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
     * Update margins of the web view, needed for Reporterre source only, so switch if it is or not.
     */
    override fun updateWebViewMargins() { // TODO to check with reporterre and navigation
        if (!::actualUrl.isInitialized) return
        web_view.updateMargins(actualUrl, article.sourceName)
    }

    // --------------
    // UTILS
    // --------------

    /**
     * Handle note redirection into the page, if it is scroll to bottom of page.
     *
     * @param url the url that the loading start.
     */
    private fun handleRedirect(url: String) =
        if (isNoteRedirect(getPageNameFromUrl(url))) {
            isNoteRedirect = !isNoteRedirect
            val svBottom = sv.getChildAt(0).bottom - sv.measuredHeight
            scrollTo(if (isNoteRedirect) svBottom else noteScrollPosition)
            true
        } else {
            if (noteScrollPosition == -1) isNoteRedirect = false
            false
        }

    /**
     * Handle web view navigation, hide article data container, show loading progress,
     * hide scroll view during loading and manage navigation count.
     *
     * @param url the url that the loading start.
     */
    private fun handleNavigation(url: String) {
        hideArticleDataContainer(!isSourceUrl(url))
        if (!isSourceUrl(url)) {
            sv.scrollY = 0
            article_loading_progress_bar.apply { visibility = View.VISIBLE; show() }

            navigationCount += 1
            if (navigationCount == 1)
                article_scroll_view.visibility = View.INVISIBLE
        } else
            navigationCount = 0
    }

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