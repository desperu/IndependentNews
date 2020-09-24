package org.desperu.independentnews.ui.main.fragment.articleList

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_article_list.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseBindingFragment
import org.desperu.independentnews.databinding.FragmentArticleListBinding
import org.desperu.independentnews.models.Article
import org.koin.android.ext.android.get
import org.koin.core.KoinComponent
import org.koin.core.parameter.parametersOf

/**
 * The argument name for bundle to received the article list to this Fragment.
 */
const val ARTICLE_LIST: String = "articleList"

/**
 * Fragment to show article list.
 *
 * @constructor Instantiates a new ArticleListFragment.
 */
class ArticleListFragment: BaseBindingFragment(), ArticleListInterface, KoinComponent {

    // FOR DATA
    private lateinit var binding: FragmentArticleListBinding
    private var viewModel = get<ArticleListViewModel>()
    private var articleListAdapter: ArticleListAdapter? = null

    // FOR INTENT
    private val articleList: List<Article>? get() = arguments?.getParcelableArrayList(
        ARTICLE_LIST
    )

    /**
     * Companion object, used to create a new instance of this fragment.
     */
    companion object {
        /**
         * Create a new instance of this fragment and set article.
         * @param articleList the articleList to show in this fragment.
         * @return the new instance of ArticleListFragment.
         */
        fun newInstance(articleList: ArrayList<Article>): ArticleListFragment {
            val articleFragment =
                ArticleListFragment()
            articleFragment.arguments = Bundle()
            articleFragment.arguments?.putParcelableArrayList(ARTICLE_LIST, articleList)
            return articleFragment
        }
    }

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureViewModel()

    override fun configureDesign() {}

    override fun updateDesign() {
        configureKoinDependency()
        configureRecyclerView()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure koin dependency for article list interfaces.
     */
    private fun configureKoinDependency() {
        get<ArticleListInterface> { parametersOf(this) }
    }

    /**
     * Configure data binding, recycler view and view model.
     */
    private fun configureViewModel(): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_article_list, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

//    /**
//     * Configure recycler view, support large screen size and use specific user interface.
//     * Set fall down animation for recycler view items.
//     */
//    private fun configureRecyclerView() {
//        binding.fragmentEstateListRecyclerView.layoutManager =
//            if (!isFrame2Visible) GridLayoutManager(activity, columnNumber)
//            else LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//        val controller = AnimationUtils.loadLayoutAnimation(activity, R.anim.layout_anim_fall_down)
//        binding.fragmentEstateListRecyclerView.layoutAnimation = controller
//        binding.fragmentEstateListRecyclerView.scheduleLayoutAnimation()
//    }

    /**
     * Configure Recycler view.
     */
    private fun configureRecyclerView() {
        articleListAdapter = context?.let { ArticleListAdapter(it, R.layout.item_article) }
        recycler_view.adapter = articleListAdapter
        recycler_view.layoutManager = LinearLayoutManager(context)
        recycler_view.setHasFixedSize(true)
//        updateRecyclerViewAnimDuration() TODO
    }

    // -----------------
    // METHODS OVERRIDE
    // -----------------

    /**
     * Return the main list adapter instance.
     * @return the main list adapter instance.
     */
    override fun getRecyclerAdapter(): ArticleListAdapter? = articleListAdapter
}