package org.desperu.independentnews.ui.showArticle

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Pair
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.postDelayed
import androidx.core.view.doOnAttach
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.content_loading_bar.*
import kotlinx.android.synthetic.main.fragment_article.*
import kotlinx.android.synthetic.main.fragment_web.*
import kotlinx.android.synthetic.main.layout_fabs_menu.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseActivity
import org.desperu.independentnews.di.module.ui.showArticleModule
import org.desperu.independentnews.extension.parseHtml.mToString
import org.desperu.independentnews.extension.shareArticle
import org.desperu.independentnews.extension.sharedGraphViewModel
import org.desperu.independentnews.helpers.DialogHelper
import org.desperu.independentnews.helpers.SystemUiHelper
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.ui.main.MainActivity
import org.desperu.independentnews.ui.main.UPDATED_USER_ARTICLES
import org.desperu.independentnews.ui.showArticle.design.ArticleDesignInterface
import org.desperu.independentnews.ui.showArticle.fabsMenu.FabsMenu
import org.desperu.independentnews.ui.showArticle.fragment.FragmentInterface
import org.desperu.independentnews.ui.sources.SourcesActivity
import org.desperu.independentnews.ui.sources.WAS_EXPANDED
import org.desperu.independentnews.utils.CANT_PARSE
import org.desperu.independentnews.utils.RC_SHOW_ARTICLE
import org.desperu.independentnews.utils.Utils.isHtmlData
import org.desperu.independentnews.utils.Utils.isSourceArticleUrl
import org.desperu.independentnews.views.NoScrollWebView
import org.koin.android.ext.android.get
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
 * Activity to show article content, in custom article frag or in web frag.
 *
 * @constructor Instantiates a new ShowArticleActivity.
 */
class ShowArticleActivity: BaseActivity(showArticleModule), ShowArticleInterface {

    // TODO add a GestureOverlayView to detect ^ event to back home, the article list.

    // FROM BUNDLE
    override val article: Article
        get() = intent.getParcelableExtra(ARTICLE)
            ?: Article(title = getString(R.string.show_article_activity_article_error))
    private val isExpanded: Boolean get() = intent.getBooleanExtra(IS_EXPANDED, true)
    override val transitionBg: ByteArray? get() = intent.getByteArrayExtra(TRANSITION_BG)

    // FOR DATA
    private val router: ImageRouter = get { parametersOf(this) }
    override val viewModel: ArticleViewModel by sharedGraphViewModel(
        navHostId = R.id.nav_host_fragment,
        navGraphId = R.id.nav_graph,
        parameters = { parametersOf(article, router) }
    )
    override val activity = this // TODO wrong usage ???
    private val articleDesign: ArticleDesignInterface get() = get()
    private val fragmentInterface: FragmentInterface get() = get { parametersOf(getCurrentFragment()) }
    private val mWebViewClient get() = fragmentInterface.mWebViewClient
    private val mWebChromeClient get() = fragmentInterface.mWebChromeClient
    override val webView: NoScrollWebView get() = article_web_view ?: web_view
    private lateinit var navController: NavController

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

    override fun getActivityLayout(): Int = R.layout.activity_show_article

    override fun configureDesign() {
        configureKoinDependency()
        configureNavController()
        configureAppBar()
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
        get<DialogHelper> { parametersOf(this) }
    }

    /**
     * Configure the navigation controller and set graph to show start destination with arguments.
     */
    private fun configureNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bundle = Bundle()
        bundle.putParcelable(ARTICLE, article)
        // Set graph and so, show start fragment (article frag).
        navController.setGraph(R.navigation.nav_graph, bundle)

        // If is not html data, show web fragment.
        if (!isHtmlData(article.article)) showFragment(article)
    }

    /**
     * Configure the appbar and show icons.
     */
    private fun configureAppBar() {
        appbar.showAppBarIcon(listOf(R.id.back_arrow_icon, R.id.share_icon))
        appbar.doOnAttach { appbar.syncAppBarSize(appbar, isExpanded) }
    }

    // --------------
    // FRAGMENT
    // --------------

    /**
     * Show fragment for the given article, or web url.
     *
     * @param article the article to display in the fragment.
     */
    override fun showFragment(article: Article) {
        val actionId =
            if (article.id == 0L) R.id.action_articleFragment_to_webFragment
            else R.id.action_webFragment_to_articleFragment
        val bundle = Bundle()
        bundle.putParcelable(ARTICLE, article)

        navController.navigate(actionId, bundle)
    }

    /**
     * Return the current fragment instance host by the navigation host fragment.
     *
     * @return the current fragment instance.
     */
    private fun getCurrentFragment(): Fragment? {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        return navHostFragment?.childFragmentManager?.primaryNavigationFragment
    }

    // --------------
    // METHODS OVERRIDE
    // --------------

    override fun onAttachedToWindow() { super.onAttachedToWindow(); FabsMenu() }

    override fun onResume() {
        super.onResume()
        webView.onResume()
        handleImplicitIntent()

        // TODO try to restore activity options to enable return transition when have stop activity
        //  line below change nothing, try to re use one shared element only
//        if (articleDesign?.isFirstPage == false) setActivityTransition()
    }

    override fun onPause() { super.onPause(); webView.onPause() }

    override fun onStop() {
        super.onStop()
        if (mWebChromeClient?.inCustomView == true) hideCustomView()
    }

    override fun onDestroy() {
        super.onDestroy()
        supportFinishAfterTransition()
    }

    override fun onBackPressed() = when {
        mWebChromeClient?.inCustomView == true -> hideCustomView()
        fabs_menu.isOpen -> fabs_menu.close()
        viewModel.navHistory.size > 0 ->
            mWebViewClient?.webViewBack(
                mWebViewClient?.actualUrl ?: error("Can't retrieved the actual url"),
                viewModel.previousPage()
            ) ?: Unit
        else -> {
            sendResult()
            sendUpdatedUserArticles()
            articleDesign.showFabsMenu(false)
//            super.onBackPressed()
            finishAfterTransition()
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

    /**
     * Convenience call for on back pressed, allow system to handle back action.
     */
    override fun goBack() = super.onBackPressed() // TODO to remove

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
    private fun shareArticle() {
        val actualUrl = mWebViewClient?.actualUrl.mToString()

        if (isHtmlData(actualUrl))
            shareArticle(viewModel.article.get()?.title, viewModel.article.get()?.url)
        else
            shareArticle(webView.title, actualUrl)
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
                viewModel.fetchAndSetArticle(url)
            else
                get<DialogHelper>().showDialog(CANT_PARSE)
        }
    }
}