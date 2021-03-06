package org.desperu.independentnews.ui.showArticle

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Pair
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toDrawable
import androidx.core.os.postDelayed
import androidx.core.view.doOnAttach
import androidx.core.view.drawToBitmap
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_show_article.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.content_loading_bar.*
import kotlinx.android.synthetic.main.layout_fabs_menu.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseBindingActivity
import org.desperu.independentnews.databinding.ActivityShowArticleBinding
import org.desperu.independentnews.di.module.ui.showArticleModule
import org.desperu.independentnews.extension.parseHtml.mToString
import org.desperu.independentnews.extension.shareArticle
import org.desperu.independentnews.helpers.DialogHelper
import org.desperu.independentnews.helpers.SystemUiHelper
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.ui.main.MainActivity
import org.desperu.independentnews.ui.main.UPDATED_USER_ARTICLES
import org.desperu.independentnews.ui.showArticle.design.ArticleDesign
import org.desperu.independentnews.ui.showArticle.fabsMenu.FabsMenu
import org.desperu.independentnews.ui.showArticle.webClient.MyWebChromeClient
import org.desperu.independentnews.ui.showArticle.webClient.MyWebViewClient
import org.desperu.independentnews.ui.sources.SourcesActivity
import org.desperu.independentnews.ui.sources.WAS_EXPANDED
import org.desperu.independentnews.utils.CANT_PARSE
import org.desperu.independentnews.utils.RC_SHOW_ARTICLE
import org.desperu.independentnews.utils.Utils.isHtmlData
import org.desperu.independentnews.utils.Utils.isSourceArticleUrl
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.ByteArrayOutputStream
import java.lang.ref.WeakReference


/**
 * The name of the arguments to received data for this Activity.
 */
const val ARTICLE: String = "article"                       // For article data
const val IS_EXPANDED: String = "isExpanded"                // For app bar size
const val TRANSITION_BG: String = "transitionBackground"    // For transition background

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
    private val transitionBg: ByteArray? get() = intent.getByteArrayExtra(TRANSITION_BG)

    // FOR DATA
    private lateinit var binding: ActivityShowArticleBinding
    private val router: ImageRouter = get { parametersOf(this) }
    override val viewModel: ArticleViewModel by viewModel { parametersOf(article, router) }
    override val activity = this // TODO wrong usage ???
    private var articleDesign: ArticleDesign? = null
    private var mWebViewClient: MyWebViewClient? = null
    private var mWebChromeClient: MyWebChromeClient? = null
    private val navigationCount get() = mWebViewClient?.navigationCount ?: -1

    /**
     * Companion object, used to redirect to this Activity.
     */
    companion object {
        /**
         * Redirects from an Activity to this Activity with transition animation.
         *
         * @param activity          the activity use to perform redirection.
         * @param article           the article to show in this activity.
         * @param isExpanded        true if the app bar is expanded, false if is collapsed.
         * @param sharedElements    the shared elements to animate.
         */
        fun routeFromActivity(
            activity: AppCompatActivity,
            article: Article,
            isExpanded: Boolean,
            vararg sharedElements: Pair<View, String>, // TODO try without list
        ) {
            val intent = Intent(activity, ShowArticleActivity::class.java)
                .putExtra(ARTICLE, article)
                .putExtra(IS_EXPANDED, isExpanded)

            val hasShared = (sharedElements.getOrNull(0)?.first as? ImageView)?.drawable != null

            // Create animation transition scene
            val options =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && hasShared) {

                    // Save previous activity window to use as background during the transition
                    val decorBitmap = WeakReference(activity.window.decorView.drawToBitmap())
                    val out =  ByteArrayOutputStream()
                    decorBitmap.get()?.compress(Bitmap.CompressFormat.JPEG, 0, out)

                    intent.putExtra(TRANSITION_BG, out.toByteArray())

                    // Add content loading bar if transition time > 500 millis
                    Handler(Looper.getMainLooper()).postDelayed(500) {
                        activity.content_loading_bar.show()
                    }
                    
                    ActivityOptions.makeSceneTransitionAnimation(activity, *sharedElements)
                } else
                    null

            // To synchronize app bar size for SourceActivity
            // or to update user article state for MainActivity
            activity.startActivityForResult(intent, RC_SHOW_ARTICLE, options?.toBundle())
        }
    }

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureDataBinding()

    override fun configureDesign() {
        configureKoinDependency()
        configureArticleDesign() // TODO force portrait ot prevent anim bug !!!
        setUserArticleState()
        configureWebViewClient()
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
        get<DialogHelper> { parametersOf(this) }
    }

    /**
     * Configure the article design.
     */
    private fun configureArticleDesign() {
        articleDesign = ArticleDesign()

        articleDesign?.apply {
            postponeSceneTransition()
            this@ShowArticleActivity.setActivityTransition()
            scheduleStartPostponedTransition(article_image)
            showFabsMenu(true, transitionBg == null)
        }
    }

    /**
     * Set user article state after Article Design, for koin instance lifecycle.
     */
    private fun setUserArticleState() { viewModel.setUserArticleState() }

    /**
     * Configure the web view client.
     */
    private fun configureWebViewClient() {
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

    override fun onResume() {
        super.onResume()
        web_view.onResume()
        handleImplicitIntent()

        // TODO try to restore activity options to enable return transition when have stop activity
        //  line below change nothing, try to re use one shared element only
//        if (articleDesign?.isFirstPage == false) setActivityTransition()
    }

    override fun onPause() { super.onPause(); web_view.onPause() }

    override fun onStop() {
        super.onStop()
        if (mWebChromeClient?.inCustomView == true) hideCustomView()
    }

    override fun onDestroy() {
        mWebViewClient = null
        mWebChromeClient = null
        articleDesign = null
        super.onDestroy()
        supportFinishAfterTransition()
    }

    override fun onBackPressed() = when {
        mWebChromeClient?.inCustomView == true -> hideCustomView()
        fabs_menu.isOpen -> fabs_menu.close()
        web_view.canGoBack() && navigationCount > 0 -> {
            mWebViewClient?.webViewBack(viewModel.previousPage(navigationCount))
            Unit
        }
        else -> {
            sendResult()
            sendUpdatedUserArticles()
            articleDesign?.showFabsMenu(false)
            super.onBackPressed()
        }
    }

    override fun finishAfterTransition() {
        // To support article set on pause, call this at end of animation.
        sendUpdatedUserArticles()
        super.finishAfterTransition()
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

    /**
     * Set the activity transition.
     */
    private fun setActivityTransition() {
        val drawable = if (transitionBg != null) {
            val length = transitionBg?.size ?: 0
            val bitmap = BitmapFactory.decodeByteArray(transitionBg, 0, length)
            bitmap.toDrawable(resources)
        } else
            null

        articleDesign?.setActivityTransition(article, drawable)
    }

    // --------------
    // UTILS
    // --------------

    /**
     * Share article with title and url, to other applications.
     */
    private fun shareArticle() {
        val actualUrl = mWebViewClient?.actualUrl.mToString()

        if (isHtmlData(actualUrl))
            shareArticle(viewModel.article.get()?.title, viewModel.article.get()?.url)
        else
            shareArticle(web_view.title, actualUrl)
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

    /**
     * Send result for MainActivity, to refresh user articles states.
     */
    private fun sendUpdatedUserArticles() {
        setResult(
            RESULT_OK,
            Intent(this, MainActivity::class.java)
                .putExtra(UPDATED_USER_ARTICLES, viewModel.updatedUserArticles.toLongArray())
        )
    }

    /**
     * Handle received implicit intent to this activity,
     * parse url if it's a source url, otherwise display can't parse alert dialog.
     */
    private fun handleImplicitIntent() {
        val url = when {
            intent.action == Intent.ACTION_SEND && intent.type?.startsWith("text/") == true ->
                intent.getStringExtra(Intent.EXTRA_TEXT).mToString()

            intent.action == Intent.ACTION_VIEW -> intent.dataString
            else -> null
        }

        url?.let {
            // Clear intent data to avoid reload.
            intent.removeExtra(Intent.EXTRA_TEXT)
            intent.data = null

            if (isSourceArticleUrl(url))
                viewModel.fetchArticle(url)
            else
                get<DialogHelper>().showDialog(CANT_PARSE)
        }
    }
}