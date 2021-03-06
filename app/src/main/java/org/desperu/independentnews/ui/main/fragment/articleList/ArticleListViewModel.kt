package org.desperu.independentnews.ui.main.fragment.articleList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.toArticleItemVMList
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.repositories.IndependentNewsRepository
import org.desperu.independentnews.repositories.database.UserArticleRepository
import org.desperu.independentnews.service.ResourceService
import org.desperu.independentnews.utils.*
import org.koin.java.KoinJavaComponent.getKoin

/**
 * View Model witch provide data for article list.
 *
 * @property ideNewsRepository      the app repository interface which provide database and network access.
 * @property userArticleRepository  the repository which provide user article database access.
 * @property articleListInterface   the article list interface which provide fragment interface.
 *
 * @constructor Instantiates a new ArticleListViewModel.
 *
 * @param ideNewsRepository         the app repository interface which provide database and network
 *                                  access to set.
 * @param userArticleRepository     the repository which provide user article database access to set.
 * @param articleListInterface      the article list interface which provide fragment interface to set.
 */
class ArticleListViewModel(
    private val ideNewsRepository: IndependentNewsRepository,
    private val userArticleRepository: UserArticleRepository,
    private val articleListInterface: ArticleListInterface
): ViewModel() {

    // FOR DATA
    private val resources: ResourceService = getKoin().get()
    private val articleList get() = articleListInterface.getRecyclerAdapter()?.list?.map { (it as ArticleItemViewModel).article }
    private val filteredList get() = articleListInterface.getRecyclerAdapter()?.filteredList?.map { (it as ArticleItemViewModel).article }

    // -----------------
    // DATABASE
    // -----------------

    /**
     * Get top story article list from database, and dispatch to recycler adapter.
     */
    internal fun getTopStory() = viewModelScope.launch(Dispatchers.IO) {
        val articleList = ideNewsRepository.getTopStory()
        updateRecyclerData(articleList)
    }

    /**
     * Get asked category article list from database, and dispatch to recycler adapter.
     *
     * @param categories the category list to search for in database.
     */
    internal fun getCategory(categories: List<String>) = viewModelScope.launch(Dispatchers.IO) {
        val articleList = ideNewsRepository.getCategory(categories)
        updateRecyclerData(articleList)
    }

    /**
     * Get all article list from database, and dispatch to recycler adapter.
     */
    internal fun getAllArticles() = viewModelScope.launch(Dispatchers.IO) {
        val articleList = ideNewsRepository.getAllArticles()
        updateRecyclerData(articleList)
    }

    /**
     * Get all favorite articles from database, and dispatch to recycler adapter.
     */
    internal fun getFavorites() = viewModelScope.launch(Dispatchers.IO) {
        val articleList = userArticleRepository.getAllFavoriteArticles()
        updateRecyclerData(articleList)
    }

    /**
     * Get all paused articles from database, and dispatch to recycler adapter.
     */
    internal fun getPaused() = viewModelScope.launch(Dispatchers.IO) {
        val articleList = userArticleRepository.getAllPausedArticles()
        updateRecyclerData(articleList)
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
        val articleList = ideNewsRepository.getTopStory()
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

        val filteredList =
            if (isFiltered)
                articleList?.let { ideNewsRepository.getFilteredList(selectedMap, it) }
            else
                mutableListOf()

        updateFilteredList(isFiltered, filteredList)
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

        updateRecyclerData(articleList)
    }

    /**
     * Update list into article list adapter.
     *
     * @param articleList the new article list to set.
     */
    private suspend fun updateRecyclerData(
        articleList: List<Article>?
    ) = withContext(Dispatchers.Main) {

        articleList?.let {
            articleListInterface.getRecyclerAdapter()?.apply {
                updateList(it.toArticleItemVMList(articleListInterface).toMutableList())
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
     * @param isFiltered    true if apply filters to the list, false otherwise.
     * @param filteredList  the new filtered article list to set.
     */
    private suspend fun updateFilteredList(
        isFiltered: Boolean,
        filteredList: List<Article>?
    ) = withContext(Dispatchers.Main) {

        articleListInterface.getRecyclerAdapter()?.apply {

            if (isFiltered) {
                this.filteredList = filteredList
                        .toArticleItemVMList(articleListInterface)
                        .toMutableList()
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
                    updateFilteredList(true, newArticleList)
                }

                // Update article list in adapter
                val adapterList = newArticleList.toArticleItemVMList(articleListInterface)
                getRecyclerAdapter()?.updateList(adapterList.toMutableList())

                // Unfilter list (no visible for the user), and need for isFiltered value
                // Synchronize the Fab Filter visibility and state
                updateFilteredList(false, emptyList())
                showFilterMotion(true)
                updateFiltersMotionState(false)

            } else {
                updateList(newArticleList)
                showNoArticle(true)
            }
        }
    }
}