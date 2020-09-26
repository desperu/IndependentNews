package org.desperu.independentnews.ui.main.fragment.articleList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.models.web.bastamag.BastamagArticle
import org.desperu.independentnews.repositories.BastamagRepository

/**
 * View Model witch provide data for article list.
 *
 * @param bastamagRepository the bastamag repository interface witch provide database access.
 * @param articleListInterface articleListInterface the article list interface witch provide fragment interface.
 * @param router the estate router interface witch provide user redirection.
 *
 * @constructor Instantiates a new EstateListViewModel.
 *
 * @property bastamagRepository the bastamag repository interface witch provide database access to set.
 * @property articleListInterface the article list interface witch provide fragment interface to set.
 * @property router the estate router interface witch provide user redirection to set.
 */
// TODO to comment
class ArticleListViewModel(private val bastamagRepository: BastamagRepository,
                           private val articleListInterface: ArticleListInterface,
                           private val router: ArticleRouter // TODO can use get<> {} koin function
): ViewModel() {

    // FOR DATA
    private var itemListVM: List<ArticleItemViewModel>? = null
    private var article: BastamagArticle? = null

    init {
//        fetchBastamagRss()
//        fetchArticle()
    }

    private fun fetchBastamagRss() = viewModelScope.launch(Dispatchers.IO) {
        itemListVM = bastamagRepository.getRssArticles()?.map { ArticleItemViewModel(it, router) }
        updateRecyclerData()
    }

    internal fun getTopStory() = viewModelScope.launch(Dispatchers.IO) {
        itemListVM = bastamagRepository.getTopStory()?.map { ArticleItemViewModel(it, router) }
        updateRecyclerData()
    }

    internal fun getCategory(category: String) = viewModelScope.launch(Dispatchers.IO) {
        itemListVM = bastamagRepository.getCategory(category)?.map {
            ArticleItemViewModel(it, router)
        }
        updateRecyclerData()
    }

    internal fun getAllArticles() = viewModelScope.launch(Dispatchers.IO) {
        itemListVM = bastamagRepository.getAllArticles()?.map { ArticleItemViewModel(it, router) }
        updateRecyclerData()
    }

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

    fun getArticle() = article
}