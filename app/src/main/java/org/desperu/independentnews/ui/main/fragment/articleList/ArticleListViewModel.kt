package org.desperu.independentnews.ui.main.fragment.articleList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.repositories.IndependentNewsRepository

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
class ArticleListViewModel(private val ideNewsRepository: IndependentNewsRepository,
                           private val articleListInterface: ArticleListInterface
): ViewModel() {

    // FOR DATA
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

    // -----------------
    // FILTER
    // -----------------

    /**
     * Apply selected filters to the current article list.
     *
     * @param selectedMap the map of selected filters to apply.
     * @param isFiltered true if apply filters to the list, false otherwise.
     */
    internal fun filterList(selectedMap: Map<Int,
                            MutableList<String>>,
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
     * Update recycler adapter list.
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
    private fun updateRecyclerData() = viewModelScope.launch(Dispatchers.Main) {
        articleList?.let {
            articleListInterface.getRecyclerAdapter()?.apply {
                updateList(it.map { article -> ArticleItemViewModel(article) }.toMutableList())
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
    private fun updateFilteredList(isFiltered: Boolean) = viewModelScope.launch(Dispatchers.Main) {
        articleListInterface.getRecyclerAdapter()?.apply {

            if (isFiltered)
                this.filteredList = this@ArticleListViewModel.filteredList!!.map { article ->
                    ArticleItemViewModel(article)
                }.toMutableList()

            this.isFiltered = isFiltered
        }

        articleListInterface.showNoArticle(isFiltered && filteredList.isNullOrEmpty())
    }
}