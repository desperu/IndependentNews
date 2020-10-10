package org.desperu.independentnews.ui.main.fragment.articleList

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_article_list.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseBindingFragment
import org.desperu.independentnews.databinding.FragmentArticleListBinding
import org.desperu.independentnews.ui.main.animationPlaybackSpeed
import org.desperu.independentnews.utils.*
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

/**
 * The argument name for bundle to received the fragment key to this Fragment.
 */
const val FRAG_KEY: String = "fragKey"

/**
 * Fragment to show article list.
 *
 * @constructor Instantiates a new ArticleListFragment.
 */
class ArticleListFragment: BaseBindingFragment(), ArticleListInterface {

    // FOR DATA
    private lateinit var binding: FragmentArticleListBinding
    private var viewModel = get<ArticleListViewModel> { parametersOf(this) }
    private var articleListAdapter: ArticleListAdapter? = null
    private val loadingDuration: Long
        get() = (resources.getInteger(R.integer.loadingAnimDuration) / animationPlaybackSpeed).toLong()

    // FOR BUNDLE
    private val fragKey: Int? get() = arguments?.getInt(FRAG_KEY, NO_FRAG)

    /**
     * Companion object, used to create a new instance of this fragment.
     */
    companion object {
        /**
         * Create a new instance of this fragment and set fragment key.
         * @param fragKey the fragment key to configure the data to show in this fragment.
         * @return the new instance of ArticleListFragment.
         */
        fun newInstance(fragKey: Int): ArticleListFragment {
            val articleFragment =
                ArticleListFragment()
            articleFragment.arguments = Bundle()
            articleFragment.arguments?.putInt(FRAG_KEY, fragKey)
            return articleFragment
        }
    }

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureViewModel()

    override fun configureDesign() {
        configureRecyclerView()
    }

    override fun updateDesign() {
        configureCorrespondingFragment()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure data binding, recycler view and view model.
     */
    private fun configureViewModel(): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_article_list, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    /**
     * Configure Recycler view.
     */
    private fun configureRecyclerView() {
        articleListAdapter = context?.let { ArticleListAdapter(it, R.layout.item_article) }
        recycler_view.adapter = articleListAdapter
        recycler_view.layoutManager = LinearLayoutManager(context)
        recycler_view.setHasFixedSize(true)
        updateRecyclerViewAnimDuration()
    }

    /**
     * Configure corresponding fragment for the given key.
     */
    private fun configureCorrespondingFragment() = when(fragKey) {
        FRAG_TOP_STORY -> viewModel.getTopStory()
        FRAG_SANTE -> viewModel.getCategory(CAT_SANTE)
        FRAG_SOCIAL -> viewModel.getCategory(CAT_SOCIAL)
        FRAG_CLIMAT -> viewModel.getCategory(CAT_CLIMAT)
        FRAG_ALL_ARTICLES -> viewModel.getAllArticles()
        else -> viewModel.getTopStory()
    }

    // -----------------
    // UPDATE
    // -----------------

    /**
     * Update RecyclerView Item Animation Durations
     */
    private fun updateRecyclerViewAnimDuration() = recycler_view.itemAnimator?.run {
        removeDuration = loadingDuration * 60 / 100
        addDuration = loadingDuration
    }

    /**
     * Apply selected filters to the current article list.
     * @param selectedMap the map of selected filters to apply.
     */
    override fun filterList(selectedMap: Map<Int, MutableList<String>>) {
        viewModel.filterList(selectedMap)
    }

    // --- GETTERS ---

    /**
     * Return the main list adapter instance.
     * @return the main list adapter instance.
     */
    override fun getRecyclerAdapter(): ArticleListAdapter? = articleListAdapter
}