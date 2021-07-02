package org.desperu.independentnews.ui.showArticle.fragment

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.android.synthetic.main.fragment_web.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseBindingFragment
import org.desperu.independentnews.extension.sharedGraphViewModel
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.ui.showArticle.ArticleViewModel
import org.desperu.independentnews.ui.showArticle.ImageRouter
import org.desperu.independentnews.ui.showArticle.design.ArticleDesign
import org.desperu.independentnews.ui.showArticle.webClient.MyWebChromeClient
import org.desperu.independentnews.ui.showArticle.webClient.MyWebViewClient
import org.koin.android.ext.android.get
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

    // FOR DATA
    private val binding get() = viewBinding!!
    private val viewModel: ArticleViewModel by sharedGraphViewModel(
        navGraphId = R.id.nav_graph,
        parameters = { parametersOf(article, get<ImageRouter>()) }
    )
    private var articleDesign: ArticleDesign? = null
    override var mWebViewClient: MyWebViewClient? = null
    override var mWebChromeClient: MyWebChromeClient? = null

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureViewModel()

    override fun configureDesign() {
        configureArticleDesign()
        configureWebView()
        // TODO to handle app bar and suitable scrollable, nested web view needed ???
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
     * Configure the article design.
     */
    private fun configureArticleDesign() {
        articleDesign = ArticleDesign()
        articleDesign?.run {
            if (isFirstPage) setActivityTransition(article, null)
            showFabsMenu(toShow = true, toDelay = true)
        }
    }

    /**
     * Configure the web view client and load url.
     */
    private fun configureWebView() {
        mWebViewClient = MyWebViewClient()
        web_view.webViewClient = mWebViewClient!!

        mWebChromeClient = MyWebChromeClient()
        web_view.webChromeClient = mWebChromeClient

        // TODO add some custom config, pinch zoom ect...
//        web_view.loadUrl(article.url)
        viewModel.article.set(article)

//        Log.e("WebFragment", "$mWebViewClient")
    }

    // --------------
    // METHODS OVERRIDE
    // --------------

    // TODO to check here
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

//    override fun onDestroyView() {
//        mWebViewClient = null
//        mWebChromeClient = null
//        articleDesign = null
//        super.onDestroyView()
//    }
}