package org.desperu.independentnews.ui.main.fragment.articleList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.models.web.bastamag.BastamagArticle
import org.desperu.independentnews.repositories.BastamagRepository
import org.desperu.independentnews.ui.main.MainInterface
import org.koin.java.KoinJavaComponent.inject

/**
 * View Model witch provide data for article list.
 *
 * @param bastamagRepository the bastamag repository interface witch provide database access.
 * @param router the estate router interface witch provide user redirection.
 *
 * @constructor Instantiates a new EstateListViewModel.
 *
 * @property bastamagRepository the bastamag repository interface witch provide database access to set.
 * @property router the estate router interface witch provide user redirection to set.
 */
// TODO to comment
class ArticleListViewModel(private val bastamagRepository: BastamagRepository,
                           private val router: ArticleRouter // TODO can use get<> {} koin function
): ViewModel() {

    // FOR DATA
    private val articleListInterface: ArticleListInterface by inject(ArticleListInterface::class.java)
    private var itemListVM: List<ItemListViewModel>? = null
    private var article: BastamagArticle? = null

    init {
        fetchBastamagRss()
//        fetchArticle()
    }

    private fun fetchBastamagRss() = viewModelScope.launch(Dispatchers.Main) {
        itemListVM = bastamagRepository.getRssArticles()?.map {
            ItemListViewModel(
                it,
                router
            )
        }
        itemListVM?.let {
            articleListInterface.getRecyclerAdapter()?.apply {
                updateList(it.toMutableList())
                notifyDataSetChanged()
            }
        }
    }

    internal fun getArticles() = viewModelScope.launch(Dispatchers.Main) {
        itemListVM = bastamagRepository.getArticles().map {
            ItemListViewModel(
                it,
                router
            )
        }
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