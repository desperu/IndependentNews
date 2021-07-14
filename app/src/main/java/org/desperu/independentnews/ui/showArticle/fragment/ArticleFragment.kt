package org.desperu.independentnews.ui.showArticle.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnNextLayout
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.android.synthetic.main.fragment_article.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseBindingFragment
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
 * Article Fragment used to display whole the article contents.
 *
 * @constructor instantiate a new ArticleFragment.
 */
class ArticleFragment : BaseBindingFragment(), FragmentInterface {

    // FROM BUNDLE
    private val safeArgs: ArticleFragmentArgs by navArgs()
    private val article: Article
        get() = safeArgs.article ?: Article(title = getString(R.string.show_article_activity_article_error))

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
        configureWebViewClient()// TODO force portrait to prevent anim bug !!!
    }

    override fun updateDesign() {}

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure data binding, recycler view and view model.
     */
    private fun configureViewModel(): View {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_article, container, false)
        binding.setVariable(org.desperu.independentnews.BR.viewModel, viewModel)
        return binding.root
    }

    /**
     * Configure the web view client.
     */
    private fun configureWebViewClient() {
        mWebViewClient = MyWebViewClient()
        article_web_view.webViewClient = mWebViewClient

        mWebChromeClient = MyWebChromeClient()
        article_web_view.webChromeClient = mWebChromeClient
    }

    // -----------------
    // METHODS OVERRIDE
    // -----------------

    override fun onResume() {
        super.onResume()
        article_scroll_view.doOnNextLayout {
            get<ShowArticleInterface>().updateAppBarOnTouch()
            viewModel.setUserArticleState() // Need ArticleDesignInterface Koin instance set
            scrollHandler.setupScrollListener()
        }
    }

    // TODO to check here
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enterTransition = MaterialFadeThrough()
//        reenterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }
}