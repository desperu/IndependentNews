package org.desperu.independentnews.ui.showArticle

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.doOnAttach
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.activity_show_article.*
import kotlinx.android.synthetic.main.app_bar.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseBindingActivity
import org.desperu.independentnews.databinding.ActivityShowArticleBinding
import org.desperu.independentnews.di.module.ui.showArticleModule
import org.desperu.independentnews.extension.parseHtml.mToString
import org.desperu.independentnews.helpers.SystemUiHelper
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.ui.showArticle.design.ArticleDesign
import org.desperu.independentnews.ui.showArticle.webClient.MyWebChromeClient
import org.desperu.independentnews.ui.showArticle.webClient.MyWebViewClient
import org.desperu.independentnews.ui.sources.SourcesActivity
import org.desperu.independentnews.ui.sources.WAS_EXPANDED
import org.desperu.independentnews.utils.RC_SHOW_ARTICLE
import org.desperu.independentnews.utils.Utils.isHtmlData
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

    // TODO add a GestureOverlayView to detect ^ event to back home, the article list.

    // FROM BUNDLE
    override val article: Article
        get() = intent.getParcelableExtra(ARTICLE)
            ?: Article(title = getString(R.string.show_article_activity_article_error))
    private val isExpanded: Boolean get() = intent.getBooleanExtra(IS_EXPANDED, true)

    // FOR DATA
    private lateinit var binding: ActivityShowArticleBinding
    private val router: ImageRouter = get { parametersOf(this) }
    override val viewModel: ArticleViewModel by viewModel { parametersOf(article, router) }
    private var articleDesign: ArticleDesign? = null
    private var mWebViewClient: MyWebViewClient? = null
    private var mWebChromeClient: MyWebChromeClient? = null
    override var inCustomView: Boolean = false
    private val navigationCount get() = mWebViewClient?.navigationCount ?: -1
    override val activity = this

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
        configureArticleDesign()
        configureWebView()
        configureAppBar()
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
        binding.viewModel = viewModel
        return binding.root
    }

    /**
     * Configure koin dependency for show article activity.
     */
    private fun configureKoinDependency() {
        get<ShowArticleInterface> { parametersOf(this) }
        get<SystemUiHelper> { parametersOf(this) }
    }

    /**
     * Configure the article design.
     */
    private fun configureArticleDesign() {
        articleDesign = ArticleDesign()

        articleDesign?.apply {
            val articleId = viewModel.article.get()?.id

            configureViewAnimations(articleId)
            postponeSceneTransition()
            scheduleStartPostponedTransition(article_image)
            setActivityTransition(articleId)
            setupProgressBarWithScrollView()
        }
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

        mWebViewClient = MyWebViewClient()
        web_view.webViewClient = mWebViewClient!!

        mWebChromeClient = MyWebChromeClient()
        web_view.webChromeClient = mWebChromeClient
    }

    /**
     * Configure the appbar and show icons.
     */
    private fun configureAppBar() {
        appbar.showAppBarIcon(listOf(R.id.back_arrow_icon, R.id.share_icon))
        appbar.doOnAttach { appbar.syncAppBarSize(appbar, isExpanded) }
    }

    // --------------
    // METHODS OVERRIDE
    // --------------

    override fun onAttachedToWindow() { super.onAttachedToWindow(); FabsMenu() }

    override fun onResume() { super.onResume(); web_view.onResume() }

    override fun onPause() { super.onPause(); web_view.onPause() }

    override fun onStop() {
        super.onStop()
        if (inCustomView) hideCustomView()
    }

    override fun onDestroy() {
        mWebViewClient = null
        mWebChromeClient = null
        articleDesign = null
        super.onDestroy()
        supportFinishAfterTransition()
    }

    override fun onBackPressed() = when {
        inCustomView -> hideCustomView()
        web_view.canGoBack() && navigationCount > 0 -> {
            mWebViewClient?.webViewBack(viewModel.previousPage(navigationCount))
            Unit
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
    // UI
    // --------------

    /**
     * Hide video custom view.
     */
    private fun hideCustomView() { mWebChromeClient?.onHideCustomView() }

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
        val actualUrl = mWebViewClient?.actualUrl.mToString()

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