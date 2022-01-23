package org.desperu.independentnews.ui.showArticle.fragment

import android.view.View
import android.webkit.WebView
import androidx.core.view.doOnNextLayout
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseBindingFragment
import org.desperu.independentnews.extension.design.bindView
import org.desperu.independentnews.extension.sharedGraphViewModel
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.ui.showArticle.ArticleViewModel
import org.desperu.independentnews.ui.showArticle.ImageRouter
import org.desperu.independentnews.ui.showArticle.ShowArticleInterface
import org.desperu.independentnews.ui.showArticle.design.ScrollHandlerInterface
import org.desperu.independentnews.ui.showArticle.webClient.MyWebChromeClient
import org.desperu.independentnews.ui.showArticle.webClient.MyWebViewClient
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

/**
 * Web Fragment used to display web page.
 *
 * @constructor instantiate a new WebFragment.
 */
class WebFragment : BaseBindingFragment(), FragmentInterface {

    // FROM BUNDLE
    private val safeArgs: WebFragmentArgs by navArgs()
    private val article: Article
        get() = safeArgs.article ?: Article(title = getString(R.string.show_article_activity_article_error))

    // FOR DESIGN
    private val webView: WebView by bindView(R.id.web_view)

    // FOR DATA
    private val binding get() = viewBinding!!
    private val viewModel: ArticleViewModel by sharedGraphViewModel(
        navGraphId = R.id.nav_graph,
        parameters = { parametersOf(article, get<ImageRouter>()) }
    )
    private val scrollHandler: ScrollHandlerInterface by inject()
    override lateinit var mWebViewClient: MyWebViewClient
    override lateinit var mWebChromeClient: MyWebChromeClient

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureViewModel()

    override fun configureDesign() {
        configureWebView()
    }

    override fun updateDesign() {}

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure data binding, recycler view and view model.
     */
    private fun configureViewModel(): View {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_web, container, false)
        binding.setVariable(org.desperu.independentnews.BR.viewModel, viewModel)
        return binding.root
    }

    /**
     * Configure the web view client and load url.
     */
    private fun configureWebView() {
        mWebViewClient = MyWebViewClient()
        webView.webViewClient = mWebViewClient

        mWebChromeClient = MyWebChromeClient()
        webView.webChromeClient = mWebChromeClient

        // TODO add some custom config, pinch zoom ect...
        viewModel.article.set(article)
    }

    // --------------
    // METHODS OVERRIDE
    // --------------

    override fun onResume() {
        super.onResume()
        webView.doOnNextLayout {
            get<ShowArticleInterface>().updateAppBarOnTouch()
            scrollHandler.setupScrollListener()
        }
    }
}