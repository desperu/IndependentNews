package org.desperu.independentnews.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.independentnews.models.web.bastamag.BastamagArticle
import org.desperu.independentnews.repositories.BastamagRepository
import org.koin.java.KoinJavaComponent.inject

class MainViewModel(private val bastamagRepository: BastamagRepository): ViewModel() {

    // FOR DATA
    private val mainInterface: MainInterface by inject(MainInterface::class.java)
    private var itemListVM: List<ItemListViewModel>? = null
    private var article: BastamagArticle? = null

    init {
        fetchBastamagRss()
//        fetchArticle()
    }

    private fun fetchBastamagRss() = viewModelScope.launch(Dispatchers.Main) {
        itemListVM = bastamagRepository.getRssArticles()?.map { ItemListViewModel(it) }
        itemListVM?.let {
            mainInterface.getRecyclerAdapter()?.updateList(it.toMutableList())
            mainInterface.getRecyclerAdapter()?.notifyDataSetChanged()
        }
    }

//    private fun fetchArticle() = viewModelScope.launch {
//        article = BastamagArticle(bastamagRepository.getArticle("reformes-police-Defund-police-cameras-pietons-desarmement"))
//        title?.value = (article as BaseHtmlArticle).getTitle().toString()
//        article?.let { title?.value = it.toArticle().title }
//    }

    // --- GETTERS ---

    fun getItemListVM() = itemListVM

    fun getArticle() = article
}