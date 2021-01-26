package org.desperu.independentnews.ui.showArticle

import android.animation.ValueAnimator
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_show_article.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.independentnews.R
import org.desperu.independentnews.anim.AnimHelper.animatedValue
import org.desperu.independentnews.anim.AnimHelper.fromSideAnimator
import org.desperu.independentnews.base.ui.BaseBindingActivity
import org.desperu.independentnews.databinding.ActivityShowArticleBinding
import org.desperu.independentnews.di.module.ui.showArticleModule
import org.desperu.independentnews.extension.design.bindView
import org.desperu.independentnews.extension.design.getValueAnimator
import org.desperu.independentnews.extension.design.setScale
import org.desperu.independentnews.extension.parseHtml.mToString
import org.desperu.independentnews.extension.showInBrowser
import org.desperu.independentnews.helpers.SystemUiHelper
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.ui.sources.SourcesActivity
import org.desperu.independentnews.ui.sources.WAS_EXPANDED
import org.desperu.independentnews.utils.RC_SHOW_ARTICLE
import org.desperu.independentnews.utils.Utils.getPageNameFromUrl
import org.desperu.independentnews.utils.Utils.isImageUrl
import org.desperu.independentnews.utils.Utils.isNoteRedirect
import org.desperu.independentnews.utils.Utils.isHtmlData
import org.desperu.independentnews.utils.Utils.isSourceArticleUrl
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * The name of the arguments to received data for this Activity.
 */
const val ARTICLE: String = "article"           // For article
const val IS_EXPANDED: String = "isExpanded"    // For app bar size

/**
 * Activity to show articles list.
 *
 * @constructor Instantiates a new ShowArticleActivity.
 */
class ShowArticleActivity: BaseBindingActivity(showArticleModule), ShowArticleInterface {

    // FROM BUNDLE
    private val article: Article
        get() = intent.getParcelableExtra(ARTICLE)
            ?: Article(title = getString(R.string.show_article_activity_article_error))
    private val isExpanded: Boolean get() = intent.getBooleanExtra(IS_EXPANDED, true)

    // FOR DATA
    private lateinit var binding: ActivityShowArticleBinding
    private val router: ImageRouter = get { parametersOf(this) }
    private val viewModel: ArticleViewModel by viewModel { parametersOf(article, router) }
    private var mWebChromeClient: WebChromeClient? = null
    override var inCustomView: Boolean = false
    private lateinit var actualUrl: String

    // FOR UI
    private val sv: NestedScrollView by bindView(R.id.article_scroll_view)
    private var scrollPosition = -1
    private var isNoteRedirect = false
    private var noteScrollPosition = -1
    private var isWebViewDesigned = false
    private var navigationCount = -1
    private lateinit var animator: ValueAnimator

    /**
     * Companion object, used to redirect to this Activity.
     */
    companion object {
        /**
         * Redirects from an Activity to this Activity with transition animation.
         *
         * @param activity      the activity use to perform redirection.
         * @param article       the article to show in this activity.
         * @param imageView     the image view to animate.
         * @param isExpanded    true if the app bar is expanded, false if is collapsed.
         */
        fun routeFromActivity(activity: AppCompatActivity,
                              article: Article,
                              imageView: View?,
                              isExpanded: Boolean) {
            val intent = Intent(activity, ShowArticleActivity::class.java)
                .putExtra(ARTICLE, article)
                .putExtra(IS_EXPANDED, isExpanded)

            // Create animation transition scene
            val options =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && imageView != null) {
                    ActivityOptions.makeSceneTransitionAnimation(
                        activity,
                        imageView,
                        activity.getString(R.string.animation_main_to_show_article)
                    )
                } else
                    null

            if (activity is SourcesActivity) // To synchronize app bar size
                activity.startActivityForResult(intent, RC_SHOW_ARTICLE, options?.toBundle())
            else
                activity.startActivity(intent, options?.toBundle())
        }
    }

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureDataBinding()

    override fun configureDesign() {
        configureKoinDependency()
        configureWebView()
        configureAppBar()
        configureViewAnimations()
        postponeSceneTransition()
        scheduleStartPostponedTransition(article_image)
        setActivityTransition()
        setupProgressBarWithScrollView()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure koin dependency for show article activity.
     */
    private fun configureKoinDependency() {
        get<ShowArticleInterface> { parametersOf(this) }
        get<SystemUiHelper> { parametersOf(this) }
    }

    /**
     * Configure data binding and return the root view.
     *
     * @return the binding root view.
     */
    private fun configureDataBinding(): View {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_article)
        binding.viewModel = viewModel
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
//            setSupportZoom(false) // TODO try to remove all zoom
//            loadWithOverviewMode = true
//            builtInZoomControls = true // It seems to be good
//            displayZoomControls = false
        }

        // Test to fix zoom bug...
//        web_view.setInitialScale(500)
//        web_view.settings.defaultZoom = WebSettings.ZoomDensity.MEDIUM
//        web_view.settings.useWideViewPort = true // Seems to work

        web_view.webViewClient = webViewClient!!

        mWebChromeClient = MyWebChromeClient(web_view)
        web_view.webChromeClient = mWebChromeClient
    }

    /**
     * Configure the appbar and show icons.
     */
    private fun configureAppBar() {
        appbar.showAppBarIcon(listOf(R.id.back_arrow_icon, R.id.share_icon))
        appbar.doOnAttach { appbar.syncAppBarSize(appbar, isExpanded) }
    }

    /**
     * Web view client for the web view.
     */
    private var webViewClient: WebViewClient? = object : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            url?.let {
                actualUrl = it
                isWebViewDesigned = false
                handleNavigation(it)
                lifecycleScope.launch(Dispatchers.Main) {
//                    if (!isWebViewDesigned)
                        web_view.updateWebViewStart(
                            it,
                            viewModel.article.get()?.source?.name ?: "",
                            viewModel.getCss()
                        )
//                    isWebViewDesigned = true
                }
            }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
//            url?.let { if (it == actualUrl) updateDesign(it, false) }
//            web_view.updateWebViewFinish("", "")
            article_scroll_progress_bar.visibility = View.VISIBLE
            appbar_loading_progress_bar.visibility = View.INVISIBLE
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
                saveScrollPosition()
                super.shouldOverrideUrlLoading(view, request)
            }
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
//        web_view.onFinishTemporaryDetach()
//        mWebChromeClient = null
    }

    override fun onBackPressed() = when {
        inCustomView -> hideCustomView()
        web_view.canGoBack() && navigationCount > 0 -> {
//            article_scroll_view.visibility = View.INVISIBLE
//            web_view.goBack()
            val previousPage = viewModel.previousPage(navigationCount)
            scrollPosition = previousPage?.second ?: 0
            if (previousPage?.first == null)
//                viewModel.previousPage(navigationCount)
//            else
                web_view.goBack()
            navigationCount -= 2
        }
//        article_loading_progress_bar.isShown -> {
////            web_view.goBack()
//            isWebViewDesigned = false
//            updateDesign(actualUrl)
//            handleNavigation(actualUrl)
//            web_view.stopLoading()
//        }
//        isNoteRedirect -> { isNoteRedirect = false; scrollTo(noteScrollPosition) }
        else -> { sendResult(); super.onBackPressed() }
    }

    override fun onDestroy() {
        webViewClient = null
        mWebChromeClient = null
        super.onDestroy()
    }

    // --------------
    // ACTION
    // --------------

    /**
     * On click back arrow icon menu.
     */
    @Suppress("unused_parameter")
    fun onClickBackArrow(v: View) = onBackPressed()

    /**
     * On click share icon menu.
     */
    @Suppress("unused_parameter")
    fun onClickShare(v: View) = shareArticle()

    // --------------
    // ANIMATION
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
     * Start custom enter animations together with scene transition.
     *
     * @param sharedElement the shared element to animate for the transition.
     */
    private fun scheduleStartPostponedTransition(sharedElement: View) {
        sharedElement.doOnPreDraw { supportStartPostponedEnterTransition() }
    }

    /**
     * Configure views animation when activity appear (enter animation).
     */
    private fun configureViewAnimations() {
        if (viewModel.article.get()?.id != 0L) {
            animator =
                getValueAnimator(
                    true,
                    resources.getInteger(R.integer.enter_anim_duration).toLong(),
                    AccelerateInterpolator(),
                    { progress ->

                        article_source_name.apply { // From left
                            translationX = animatedValue(-right, progress)
                            article_source_image.translationX = translationX // to sync with name anim
                        }
                        fromSideAnimator(listOf(article_subtitle, article_date), progress, false)
                        fromSideAnimator(listOf(article_author), progress, true)
                        article_title.setScale(progress)
                        web_view.apply {
                            alpha = (progress - 0.65f) / 0.35f // (progress - 0.8f) / 0.2f // not shown because css update
                            translationY = animatedValue(sv.bottom - web_view.top, progress)//100.dp - 100.dp * progress
                        }
//                        article_root_view.alpha = progress // create anim mistake
                    }
                )

            article_image.postOnAnimation { animator.start() }
            article_image.postOnAnimationDelayed(animator.duration * 2) { clearAnimations() }
        }
    }

    /**
     * Clear all animated value for each views. Needed to prevent ui mistake,
     * when not play anim until it's end.
     */
    private fun clearAnimations() {
        val views = listOf(article_source_image, article_source_name, article_subtitle, article_date, article_author)
        views.forEach { it.translationX = 0f }
        article_title.setScale(1f)
        web_view.apply { alpha = 1f; translationY = 0f }
    }

    /**
     * Set custom activity transition, only for source detail to source page transition.
     */
    private fun setActivityTransition() {
        if (viewModel.article.get()?.id == 0L)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    // --------------
    // UI
    // --------------

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
        // TODO on scroll video should pause ...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            sv.setOnScrollChangeListener { _, _, scrollY, _, _ -> svScrollY = scrollY; setup() }
        else
            sv.viewTreeObserver.addOnScrollChangedListener { svScrollY = 0; setup() }
    }

    /**
     * Update layout and web view design.
     *
     * @param url the actual url of the web view.
     * @param fromProgress true if call from onProgressChanged, false otherwise.
     */
    private fun updateDesign(url: String, fromProgress: Boolean) {
//        if (fromProgress && !isSourceUrl(url)) return

//        if (!isWebViewDesigned)
//            lifecycleScope.launch { web_view.updateWebViewFinish(actualUrl, viewModel.getCssStyle()) }
//            web_view.updateWebViewDesign(article.sourceName, actualUrl, article.cssUrl)
        article_loading_progress_bar.hide()
        article_scroll_view.visibility = View.VISIBLE
//        web_view.zoomOut()
//        web_view.settings.textZoom = web_view.settings.textZoom
//        if (isHtmlData(url) && scrollPosition > -1)// && !isWebViewDesigned)
//            sv.scrollTo(sv.scrollX, scrollPosition)
//        else if (!isHtmlData(url))
//            sv.scrollTo(sv.scrollX, 0)

        if (!isWebViewDesigned)
            sv.doOnPreDraw {
                sv.postDelayed({
                    sv.scrollTo(sv.scrollX, if (scrollPosition > -1) scrollPosition else 0)
                    scrollPosition = -1
                }, 50) // To wait apply css, and properly scroll
            }

        isWebViewDesigned = true
    }

    private fun handleDesign(progress: Int) {
        when {
            progress == 0 -> {
                sv.visibility = View.INVISIBLE

                article_scroll_progress_bar.visibility = View.INVISIBLE // Scroll position
                article_loading_progress_bar.apply { visibility = View.VISIBLE; show() } // Loading anim
                appbar_loading_progress_bar.visibility = View.VISIBLE // Loading progress
            }
            progress > 80 -> {
                article_loading_progress_bar.hide()
                sv.visibility = View.VISIBLE
                sv.scrollTo(sv.scrollX, if (scrollPosition > -1) scrollPosition else 0)
                return
            }
            progress > 100 -> {
                article_scroll_progress_bar.visibility = View.VISIBLE
                appbar_loading_progress_bar.visibility = View.INVISIBLE
            }
        }
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
     * Update web view design, css style and margins.
     */
    override fun updateWebViewDesign() {
        if (!::actualUrl.isInitialized) return
        updateDesign(actualUrl, true)
    }

    // --------------
    // UTILS
    // --------------

    /**
     * Handle note redirection into the page, if it is scroll to bottom of page.
     *
     * @param url the url that the loading start.
     */
    private fun handleRedirect(url: String) = when {
        isNoteRedirect(getPageNameFromUrl(url)) -> {
            if (!isNoteRedirect) noteScrollPosition = sv.scrollY
            scrollPosition = -1
            isNoteRedirect = !isNoteRedirect
            val svBottom = sv.getChildAt(0).bottom - sv.measuredHeight
            scrollTo(if (isNoteRedirect) svBottom else noteScrollPosition)
            true
        }
        isImageUrl(url) -> { router.openShowImages(arrayListOf(url)); true }
        url.endsWith(".pdf") -> { showInBrowser(url); true }
        isSourceArticleUrl(url) -> {
            addPageToHistory()
            viewModel.fetchArticle(url)
            true
        }
        else -> {
            addPageToHistory()
//            if (!isHtmlData(url) && url.isNotBlank()) saveScrollPosition()
            if (noteScrollPosition == -1) isNoteRedirect = false
            false
        }
        // TODO handle if is blank !!!
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
        hideArticleDataContainer(!isHtmlData(url))

        if (navigationCount >= 0) {

            sv.visibility = View.INVISIBLE

            article_scroll_progress_bar.visibility = View.INVISIBLE // Scroll position
            article_loading_progress_bar.apply { visibility = View.VISIBLE; show() } // Loading anim
            appbar_loading_progress_bar.visibility = View.VISIBLE // Loading progress
        }

        if (url.contains(article.article)) navigationCount = 0
        else navigationCount += 1
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
        if (isHtmlData(actualUrl)) {
            share.putExtra(Intent.EXTRA_SUBJECT, viewModel.article.get()?.title)
            share.putExtra(Intent.EXTRA_TEXT, viewModel.article.get()?.url)

        } else {
            share.putExtra(Intent.EXTRA_SUBJECT, web_view.title)
            share.putExtra(Intent.EXTRA_TEXT, actualUrl)
        }

        startActivity(Intent.createChooser(share, getString(R.string.show_article_activity_share_chooser_title)))
    }

    /**
     * Send result for SourcesActivity, to synchronize app bar size.
     */
    private fun sendResult() {
        setResult(
            RESULT_OK,
            Intent(this, SourcesActivity::class.java)
                .putExtra(WAS_EXPANDED, appbar.isExpanded)
        )
    }
}