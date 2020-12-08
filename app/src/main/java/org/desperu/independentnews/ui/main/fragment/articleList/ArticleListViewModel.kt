package org.desperu.independentnews.ui.main.fragment.articleList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.toArticleItemVMList
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.repositories.IndependentNewsRepository
import org.desperu.independentnews.service.ResourceService
import org.desperu.independentnews.utils.*
import org.koin.java.KoinJavaComponent.getKoin

/**
 * View Model witch provide data for article list.
 *
 * @property ideNewsRepository      the app repository interface witch provide database and network access.
 * @property articleListInterface   the article list interface witch provide fragment interface.
 *
 * @constructor Instantiates a new ArticleListViewModel.
 *
 * @param ideNewsRepository         the app repository interface witch provide database and network
 *                                  access to set.
 * @param articleListInterface      the article list interface witch provide fragment interface to set.
 */
class ArticleListViewModel(
    private val ideNewsRepository: IndependentNewsRepository,
    private val articleListInterface: ArticleListInterface
): ViewModel() {

    // FOR DATA
    private val resources: ResourceService = getKoin().get()
    private var articleList: List<Article>? = null
    private var filteredList: List<Article>? = null

    // -----------------
    // DATABASE
    // -----------------

    /**
     * Get top story article list from database, and dispatch to recycler adapter.
     */
    internal fun getTopStory() = viewModelScope.launch(Dispatchers.IO) {
        articleList = ideNewsRepository.getTopStory()
        updateRecyclerData()
    }

    /**
     * Get asked category article list from database, and dispatch to recycler adapter.
     *
     * @param categories the category list to search for in database.
     */
    internal fun getCategory(categories: List<String>) = viewModelScope.launch(Dispatchers.IO) {
        articleList = ideNewsRepository.getCategory(categories)
        updateRecyclerData()
    }

    /**
     * Get all article list from database, and dispatch to recycler adapter.
     */
    internal fun getAllArticles() = viewModelScope.launch(Dispatchers.IO) {
        articleList = ideNewsRepository.getAllArticles()
        updateRecyclerData()
    }

    /**
     * Refresh article list from the database, and take care about the current displayed fragment.
     * Use diff utils support to animate change between the two list.
     *
     * @param fragKey the actual fragment key.
     */
    internal fun refreshList(fragKey: Int?) = viewModelScope.launch(Dispatchers.IO) {
        val isFiltered = articleListInterface.getRecyclerAdapter()?.isFiltered == true

        // Refresh the new list from the database
        val newArticles = when(fragKey) {
            FRAG_TOP_STORY -> ideNewsRepository.getTopStory()
            FRAG_ECOLOGY -> ideNewsRepository.getCategory(resources.getStringArray(R.array.filter_ecology).asList())
            FRAG_SOCIAL -> ideNewsRepository.getCategory(resources.getStringArray(R.array.filter_social).asList())
            FRAG_ENERGY -> ideNewsRepository.getCategory(resources.getStringArray(R.array.filter_energy).asList())
            FRAG_HEALTH -> ideNewsRepository.getCategory(resources.getStringArray(R.array.filter_health).asList())
            FRAG_ALL_ARTICLES -> ideNewsRepository.getAllArticles()
            else -> if (!isFiltered) articleList else filteredList
        }

        newArticles?.let { updateListWithAnim(it) }
    }

    /**
     * Show the new downloaded articles.
     */
    internal fun showNewArticles() = viewModelScope.launch(Dispatchers.IO) {
        articleList = ideNewsRepository.getTopStory()
        articleList?.let { updateListWithAnim(it) }
    }

    // -----------------
    // FILTER
    // -----------------

    /**
     * Apply selected filters to the current article list.
     *
     * @param selectedMap the map of selected filters to apply.
     * @param isFiltered true if apply filters to the list, false otherwise.
     */
    internal fun filterList(
        selectedMap: Map<Int, MutableList<String>>,
        isFiltered: Boolean
    ) = viewModelScope.launch(Dispatchers.IO) {

        filteredList = if (isFiltered)
                           articleList?.let { ideNewsRepository.getFilteredList(selectedMap, it) }
                       else
                           mutableListOf()
        updateFilteredList(isFiltered)
    }

    // -----------------
    // UPDATE
    // -----------------

    /**
     * Update recycler adapter list, use for specific list today article,
     * from the notification click.
     *
     * @param articleList the new article list to populate.
     */
    internal fun updateList(
        articleList: List<Article>?
    ) = viewModelScope.launch(Dispatchers.Main) {

        articleList?.let {
            this@ArticleListViewModel.articleList = articleList
            updateRecyclerData()
        }
    }

    /**
     * Update list into article list adapter.
     */
    private suspend fun updateRecyclerData() = withContext(Dispatchers.Main) {
        articleList?.let {
            articleListInterface.getRecyclerAdapter()?.apply {
                updateList(it.toArticleItemVMList().toMutableList())
                notifyDataSetChanged()
            }
        }
        articleListInterface.showNoArticle(articleList.isNullOrEmpty())
        articleListInterface.showFilterMotion(!articleList.isNullOrEmpty())
    }

    /**
     * Update filtered list into article list adapter.
     *
     * If the list is empty show no article find.
     *
     * @param isFiltered true if apply filters to the list, false otherwise.
     */
    private suspend fun updateFilteredList(isFiltered: Boolean) = withContext(Dispatchers.Main) {
        articleListInterface.getRecyclerAdapter()?.apply {

            if (isFiltered) {
                this.filteredList =
                    this@ArticleListViewModel.filteredList.toArticleItemVMList().toMutableList()
            }

            this.isFiltered = isFiltered
        }

        articleListInterface.showNoArticle(isFiltered && filteredList.isNullOrEmpty())
    }

    /**
     * Update list with anim diff util support, to animate differences between the two list.
     *
     * @param newArticleList the new article list to display to the user.
     */
    private suspend fun updateListWithAnim(
        newArticleList: List<Article>
    ) = withContext(Dispatchers.Main) {

        articleListInterface.apply {
            val isFiltered = getRecyclerAdapter()?.isFiltered == true

            if (newArticleList.isNotEmpty()) {
                if (!isFiltered) {
                    // Use filtered list for Diff Utils support and update
                    filteredList = newArticleList
                    updateFilteredList(true)
                }

                // Update article list here (view model) and in adapter
                withContext(Dispatchers.IO) { articleList = newArticleList }
                getRecyclerAdapter()?.updateList(articleList.toArticleItemVMList().toMutableList())

                // Unfilter list (no visible for the user), and need for isFiltered value
                // Synchronize the Fab Filter visibility and state
                filteredList = null
                updateFilteredList(false)
                showFilterMotion(true)
                updateFiltersMotionState(false)

            } else {
                updateList(newArticleList)
                showNoArticle(true)
            }
        }
    }
}