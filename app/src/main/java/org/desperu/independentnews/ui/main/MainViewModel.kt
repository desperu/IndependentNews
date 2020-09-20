package org.desperu.independentnews.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.models.web.bastamag.BastamagArticle
import org.desperu.independentnews.repositories.BastamagRepository
import org.koin.java.KoinJavaComponent.inject

// TODO to comment
class MainViewModel(private val bastamagRepository: BastamagRepository,
                    private val router: ArticleRouter // TODO can use get<> {} koin function
): ViewModel() {

    // FOR DATA
    private val mainInterface: MainInterface by inject(MainInterface::class.java)
    private var itemListVM: List<ItemListViewModel>? = null
    private var article: BastamagArticle? = null

    init {
        fetchBastamagRss()
//        fetchArticle()
    }

    private fun fetchBastamagRss() = viewModelScope.launch(Dispatchers.Main) {
        itemListVM = bastamagRepository.getRssArticles()?.map { ItemListViewModel(it, router) }
        itemListVM?.let {
            mainInterface.getRecyclerAdapter()?.apply {
                updateList(it.toMutableList())
                notifyDataSetChanged()
            }
        }
    }

    internal fun getArticles() = viewModelScope.launch(Dispatchers.Main) {
        itemListVM = bastamagRepository.getArticles().map { ItemListViewModel(it, router) }
        itemListVM?.let {
            mainInterface.getRecyclerAdapter()?.apply {
                updateList(it.toMutableList())
                notifyDataSetChanged()
            }
        }
    }

    // --- GETTERS ---

    fun getArticleList(): List<Article>? = itemListVM?.map { it.article }

    fun getArticle() = article
}