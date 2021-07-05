package org.desperu.independentnews.ui.showArticle.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.doOnNextLayout
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.android.synthetic.main.fragment_article.*
import kotlinx.android.synthetic.main.fragment_web.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseBindingFragment
import org.desperu.independentnews.extension.sharedGraphViewModel
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.ui.showArticle.ArticleViewModel
import org.desperu.independentnews.ui.showArticle.ImageRouter
import org.desperu.independentnews.ui.showArticle.ShowArticleInterface
import org.desperu.independentnews.ui.showArticle.design.ArticleDesign
import org.desperu.independentnews.ui.showArticle.webClient.MyWebChromeClient
import org.desperu.independentnews.ui.showArticle.webClient.MyWebViewClient
import org.koin.android.ext.android.get
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
    private val showArticle: ShowArticleInterface = get()
    private var articleDesign: ArticleDesign? = null
    override var mWebViewClient: MyWebViewClient? = null
    override var mWebChromeClient: MyWebChromeClient? = null

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureViewModel()

    override fun configureDesign() {
        configureArticleDesign() // TODO force portrait to prevent anim bug !!!
        setUserArticleState()
        configureWebViewClient()
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
     * Configure the article design.
     */
    private fun configureArticleDesign() {
        articleDesign = ArticleDesign()

        articleDesign?.apply {
            postponeSceneTransition()
            this@ArticleFragment.setActivityTransition()
            scheduleStartPostponedTransition(article_image)
            showFabsMenu(true, showArticle.transitionBg == null)
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
        article_web_view.webViewClient = mWebViewClient!!

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
        }
    }

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

    // --------------
    // UI
    // --------------

    /**
     * Set the activity transition.
     */
    private fun setActivityTransition() {
        val drawable = if (showArticle.transitionBg != null) {
            val length = showArticle.transitionBg?.size ?: 0
            val bitmap = BitmapFactory.decodeByteArray(showArticle.transitionBg, 0, length)
            bitmap.toDrawable(resources)
        } else
            null

        articleDesign?.setActivityTransition(article, drawable)
    }
}