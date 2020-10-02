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
 * @property ideNewsRepository the aoo repository interface witch provide database and network access to set.
 * @property articleListInterface the article list interface witch provide fragment interface to set.
 * @property router the estate router interface witch provide user redirection to set.
 */
// TODO to comment
class ArticleListViewModel(private val ideNewsRepository: IndependentNewsRepository,
                           private val articleListInterface: ArticleListInterface,
                           private val router: ArticleRouter
): ViewModel() {

    // FOR DATA
    private var itemListVM: List<ArticleItemViewModel>? = null

    // -----------------
    // NETWORK
    // -----------------

    internal fun fetchRssArticles() = viewModelScope.launch(Dispatchers.IO) {
        itemListVM = ideNewsRepository.getRssArticles()?.map { ArticleItemViewModel(it, router) }
        updateRecyclerData()
    }

    // -----------------
    // DATABASE
    // -----------------

    internal fun getTopStory() = viewModelScope.launch(Dispatchers.IO) {
        itemListVM = ideNewsRepository.getTopStory()?.map { ArticleItemViewModel(it, router) }
        updateRecyclerData()
    }

    internal fun getCategory(category: String) = viewModelScope.launch(Dispatchers.IO) {
        itemListVM = ideNewsRepository.getCategory(category)?.map {
            ArticleItemViewModel(it, router)
        }
        updateRecyclerData()
    }

    internal fun getAllArticles() = viewModelScope.launch(Dispatchers.IO) {
        itemListVM = ideNewsRepository.getAllArticles()?.map { ArticleItemViewModel(it, router) }
        updateRecyclerData()
    }

    // -----------------
    // UPDATE
    // -----------------

    private fun updateRecyclerData() = viewModelScope.launch(Dispatchers.Main) {
        itemListVM?.let {
            articleListInterface.getRecyclerAdapter()?.apply {
                updateList(it.toMutableList())
                notifyDataSetChanged()
            }
        }
    }

    // --- GETTERS ---

    fun getArticleList(): List<Article>? = itemListVM?.map { it.article }
}