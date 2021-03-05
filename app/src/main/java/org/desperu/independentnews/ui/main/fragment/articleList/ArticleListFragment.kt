package org.desperu.independentnews.ui.main.fragment.articleList

import android.os.Bundle
import android.view.View
import androidx.core.view.postOnAnimationDelayed
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_article_list.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseBindingFragment
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.ui.main.MainInterface
import org.desperu.independentnews.ui.main.animationPlaybackSpeed
import org.desperu.independentnews.utils.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.parametersOf

/**
 * The argument name for bundle to received the fragment key to this Fragment.
 */
const val FRAG_KEY: String = "fragKey"

/**
 * The argument name for bundle to received the today articles in this Fragment.
 */
const val TODAY_ARTICLES_FRAG: String = "todayArticlesFrag"

/**
 * Fragment to show article list.
 *
 * @constructor Instantiates a new ArticleListFragment.
 */
class ArticleListFragment: BaseBindingFragment(), ArticleListInterface {

    // FOR DATA
    private val binding get() = viewBinding!!
    private val viewModel = get<ArticleListViewModel> { parametersOf(this) }
    private var articleListAdapter: ArticleListAdapter? = null
    private val mainInterface get() = getKoin().getOrNull<MainInterface>()
    private val loadingDuration: Long
        get() = (resources.getInteger(R.integer.loadingAnimDuration) / animationPlaybackSpeed).toLong()

    // FOR BUNDLE
    private val fragKey: Int? get() = arguments?.getInt(FRAG_KEY, NO_FRAG)
    private val todayArticles: List<Article>? get() = arguments?.getParcelableArrayList(TODAY_ARTICLES_FRAG)

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
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_article_list, container, false)
        binding.setVariable(org.desperu.independentnews.BR.viewModel, viewModel)
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
        FRAG_ECOLOGY -> viewModel.getCategory(resources.getStringArray(R.array.filter_ecology).asList())
        FRAG_SOCIAL -> viewModel.getCategory(resources.getStringArray(R.array.filter_social).asList())
        FRAG_ENERGY -> viewModel.getCategory(resources.getStringArray(R.array.filter_energy).asList())
        FRAG_HEALTH -> viewModel.getCategory(resources.getStringArray(R.array.filter_health).asList())
        FRAG_ALL_ARTICLES -> viewModel.getAllArticles()
        FRAG_TODAY_ARTICLES -> viewModel.updateList(todayArticles)
        else -> viewModel.getTopStory()
    }

    // -----------------
    // METHODS OVERRIDE
    // -----------------

    override fun onResume() {
        super.onResume()
        showFilterMotion(true)
        articleListAdapter?.isFiltered?.let { updateFiltersMotionState(it) }
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
     * @param isFiltered true if apply filters to the list, false otherwise.
     */
    override fun filterList(selectedMap: Map<Int, MutableList<String>>, isFiltered: Boolean) {
        viewModel.filterList(selectedMap, isFiltered)
    }

    /**
     * Refresh the article list from the database, and display with DiffUtils support.
     */
    override fun refreshList() { viewModel.refreshList(fragKey) }

    /**
     * Show the new articles.
     */
    internal fun showNewArticles() {
        viewModel.showNewArticles()
        recycler_view.postOnAnimationDelayed(300L) {
            recycler_view.smoothScrollToPosition(0)
        }
    }

    // -----------------
    // UI
    // -----------------

    /**
     * Show no article and hide recycler view, or invert, depends of toShow value.
     * @param toShow true to show no article, false otherwise.
     */
    override fun showNoArticle(toShow: Boolean) {
        no_article_find?.visibility = if (toShow) View.VISIBLE else View.INVISIBLE
    }

    /**
     * Show or hide filter motion, depends of toShow value.
     * @param toShow true to show filter motion, false to hide.
     */
    override fun showFilterMotion(toShow: Boolean) { mainInterface?.showFilterMotion(toShow) }

    /**
     * Update filters motion state adapter state, when switch fragment.
     * @param isFiltered true if the adapter is filtered, false otherwise.
     */
    override fun updateFiltersMotionState(isFiltered: Boolean) {
        mainInterface?.updateFiltersMotionState(isFiltered)
    }

    // --- GETTERS ---

    /**
     * Return the main list adapter instance.
     * @return the main list adapter instance.
     */
    override fun getRecyclerAdapter(): ArticleListAdapter? = articleListAdapter
}