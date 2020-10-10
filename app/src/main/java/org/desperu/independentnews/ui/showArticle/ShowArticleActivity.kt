package org.desperu.independentnews.ui.showArticle

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.view.View
import android.view.ViewTreeObserver
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import kotlinx.android.synthetic.main.activity_show_article.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseBindingActivity
import org.desperu.independentnews.models.Article
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
class ShowArticleActivity: BaseBindingActivity() {

    // FROM BUNDLE // TODO to perfect
    private val article: Article get() = intent.getParcelableExtra(ARTICLE) ?: Article(title = "Error to retrieved article !")

    // FOR DATA
    private lateinit var binding: ViewDataBinding
    private val viewModel: ArticleViewModel by viewModel { parametersOf(article) }

    /**
     * Companion object, used to redirect to this Activity.
     */
    companion object {
        /**
         * Redirects from an Activity to this Activity with transition animation.
         * @param activity the activity use to perform redirection.
         * @param article the article to show in this activity.
         * @param imageView the image view to animate.
         */
        fun routeFromActivity(activity: AppCompatActivity,
                              article: Article,
                              imageView: View) {
            val intent = Intent(activity, ShowArticleActivity::class.java)
                .putExtra(ARTICLE, article)

            // Start Animation
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
        showChildActivityIcon()
        postponeSceneTransition()
        scheduleStartPostponedTransition(article_image)
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_article)
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
                val cssLink = article?.cssUrl
                val js = "var link = document.createElement('link');" +
                        " link.setAttribute('rel', 'stylesheet');" +
                        " link.setAttribute('href','$cssLink');" +
                        " link.setAttribute('type','text/css');" +
                        " document.head.appendChild(link);"
                view.evaluateJavascript(js, null)
                super.onPageFinished(view, url)
            }
        }// TODO use retro for click on article Basta or Reporterre
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
    fun onClickShare(v: View) {}

    // --------------
    // UI
    // --------------

    /**
     * Postpone the shared elements enter transition, because the shared elements
     * are in the fragment of the view pager.
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
        sharedElement.viewTreeObserver.addOnPreDrawListener( // TODO use callback from glide download... or share image from list to this activity (better perf and rapidity)
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    sharedElement.viewTreeObserver.removeOnPreDrawListener(this)
                    supportStartPostponedEnterTransition()
                    return true
                }
            }
        )
    }

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