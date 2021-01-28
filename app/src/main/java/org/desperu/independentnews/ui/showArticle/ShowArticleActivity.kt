package org.desperu.independentnews.ui.showArticle

import android.animation.ValueAnimator
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.*
import androidx.core.widget.ContentLoadingProgressBar
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_show_article.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
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
    private val scrollBar: ProgressBar by bindView(R.id.article_scroll_progress_bar)
    private val loadingAnimBar: ContentLoadingProgressBar by bindView(R.id.article_loading_progress_bar)
    private val loadingProgressBar: ProgressBar by bindView(R.id.appbar_loading_progress_bar)
    private var scrollPosition = -1
    private var isNoteRedirect = false
    private var noteScrollPosition = -1
    private var isLayoutDesigned = false
    private var hasScroll = false
    private var isFirstPage = true
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
        }

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
                isLayoutDesigned = false
                hasScroll = false
                handleNavigation(it)
                updateWebViewStart(it)
            }
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            handleDesign(101)
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
    }

    override fun onBackPressed() = when {
        inCustomView -> hideCustomView()
        web_view.canGoBack() && navigationCount > 0 -> {
            handleDesign(0)
            val previousPage = viewModel.previousPage(navigationCount)
            scrollPosition = previousPage?.second ?: 0
            if (previousPage?.first == null) web_view.goBack()
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
                scrollBar.setProgress(progress.toInt(), true)
            else
                scrollBar.progress = progress.toInt()
        }
        // TODO on scroll video should pause ...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            sv.setOnScrollChangeListener { _, _, scrollY, _, _ -> svScrollY = scrollY; setup() }
        else
            sv.viewTreeObserver.addOnScrollChangedListener { svScrollY = 0; setup() }
    }

    /**
     * Handle layout design, used between page navigation to hide or show ui elements.
     *
     * @param progress the loading progress of the page.
     */
    override fun handleDesign(progress: Int) {
        when {
            progress == 0 -> {
                sv.visibility = View.INVISIBLE
                sv.alpha = 0f

                scrollBar.apply { visibility = View.INVISIBLE; this.progress = 0 }
                loadingAnimBar.apply { visibility = View.VISIBLE; show() }
                loadingProgressBar.visibility = View.VISIBLE
            }
            progress in 80..100 -> {
                if (!isLayoutDesigned && !isFirstPage) {
                    isLayoutDesigned = true
                    loadingAnimBar.hide()
                    if (!isHtmlData(actualUrl)) scrollTo(null)
                    sv.visibility = View.VISIBLE

                    val anim = getValueAnimator(
                        true,
                        300L,
                        DecelerateInterpolator(),
                        { progressVal -> sv.alpha = progressVal }
                    )

                    val startAnim = lifecycleScope.async(Dispatchers.Main) {
                        do { delay(50) } while (!hasScroll)
                        anim.start()
                    }
                    startAnim[startAnim.key]
                }
            }
            progress > 100 -> {
                scrollBar.visibility = View.VISIBLE
                loadingProgressBar.visibility = View.INVISIBLE
                isFirstPage = false
            }
        }
    }

    /**
     * Update web view start loading, update text size, background, margins and css style.
     *
     * @param url the new url to load.
     */
    private fun updateWebViewStart(url: String) = lifecycleScope.launch(Dispatchers.Main) {
        val sourceName = viewModel.article.get()?.source?.name ?: ""
        web_view.updateWebViewStart(url, sourceName, viewModel.getCss())
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
     * Scroll vertically to the y position value, if null restore scroll position.
     *
     * @param y the y value, vertical axe, to scroll to.
     */
    override fun scrollTo(y: Int?) {
        sv.doOnPreDraw {

            val scrollY = when {
                y != null -> y
                scrollPosition > -1 -> scrollPosition
                else -> 0
            }

            sv.scrollTo(sv.scrollX, scrollY)
            scrollPosition = -1
            hasScroll = true
        }
    }

    /**
     * Update loading progress bar, in the app bar, with the new progress value.
     *
     * @param newProgress the new progress value.
     */
    override fun updateProgress(newProgress: Int) { loadingProgressBar.progress = newProgress }

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
//            scrollPosition = -1
            isNoteRedirect = !isNoteRedirect
            val svBottom = sv.getChildAt(0).bottom - sv.measuredHeight
            val y = if (isNoteRedirect) svBottom else noteScrollPosition
            sv.smoothScrollTo(sv.scrollX, y, 1000)
            true
        }
        isImageUrl(url) -> { router.openShowImages(arrayListOf(url)); true }
        url.endsWith(".pdf") -> { showInBrowser(url); true }
        isSourceArticleUrl(url) -> {
            handleDesign(0)
            addPageToHistory()
            viewModel.fetchArticle(url)
            true
        }
        else -> {
            handleDesign(0)
            addPageToHistory()
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

        if (navigationCount >= 0) handleDesign(0)

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