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
 * @param ideNewsRepository the app repository interface witch provide database and network access.
 * @param articleListInterface articleListInterface the article list interface witch provide fragment interface.
 * @param router the estate router interface witch provide user redirection.
 *
 * @constructor Instantiates a new ArticleListViewModel.
 *
 * @property ideNewsRepository the app repository interface witch provide database and network access to set.
 * @property articleListInterface the article list interface witch provide fragment interface to set.
 * @property router the estate router interface witch provide user redirection to set.
 */
// TODO to comment
class ArticleListViewModel(private val ideNewsRepository: IndependentNewsRepository,
                           private val articleListInterface: ArticleListInterface,
                           private val router: ArticleRouter
): ViewModel() {

    // FOR DATA
    private var articleList: List<Article>? = null
    private var filteredList: List<Article>? = null

    // -----------------
    // NETWORK
    // -----------------

    internal fun fetchRssArticles() = viewModelScope.launch(Dispatchers.IO) {
        ideNewsRepository.fetchRssArticles()
//        updateRecyclerData()
    }

    // -----------------
    // DATABASE
    // -----------------

    internal fun getTopStory() = viewModelScope.launch(Dispatchers.IO) {
        articleList = ideNewsRepository.getTopStory()
        updateRecyclerData()
    }

    internal fun getCategory(category: String) = viewModelScope.launch(Dispatchers.IO) {
        articleList = ideNewsRepository.getCategory(category)
        updateRecyclerData()
    }

    internal fun getAllArticles() = viewModelScope.launch(Dispatchers.IO) {
        articleList = ideNewsRepository.getAllArticles()
        updateRecyclerData()
    }

    // -----------------
    // FILTER
    // -----------------

    /**
     * Apply selected filters to the current article list.
     * @param selectedMap the map of selected filters to apply.
     */
    internal fun filterList(selectedMap: Map<Int, MutableList<String>>) = viewModelScope.launch(Dispatchers.IO) {
        filteredList = if (selectedMap.isNotEmpty())
                           articleList?.let { ideNewsRepository.getFilteredList(selectedMap, it) }
                       else
                           mutableListOf()
        filteredList?.let { updateFilteredList(it) }
    }

    @Suppress("unchecked_cast")
    internal fun updateFilteredList(filteredList: List<Article>) {
        this.filteredList = filteredList
        articleListInterface.getRecyclerAdapter()?.apply {

            if (filteredList.isNotEmpty())
                this.filteredList = filteredList.map { article ->
                    ArticleItemViewModel(article, router)
                }.toMutableList()

            isFiltered = filteredList.isNotEmpty()
        }
    }

    // -----------------
    // UPDATE
    // -----------------

    private fun updateRecyclerData() = viewModelScope.launch(Dispatchers.Main) {
        articleList?.let {
            articleListInterface.getRecyclerAdapter()?.apply {
                updateList(it.map { article -> ArticleItemViewModel(article, router) }.toMutableList())
                notifyDataSetChanged()
            }
        }
    }

    // --- GETTERS ---

    fun getArticleList(): List<Article>? = articleList
}