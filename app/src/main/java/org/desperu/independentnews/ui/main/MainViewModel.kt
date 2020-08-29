package org.desperu.independentnews.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.desperu.independentnews.models.api.bastamag.BastamagArticle
import org.desperu.independentnews.models.api.rss.RssResponse
import org.desperu.independentnews.network.bastamag.BastamagRssService
import org.desperu.independentnews.network.bastamag.BastamagWebService

class MainViewModel(private val bastamagRssService: BastamagRssService,
                    private val bastamagWebService: BastamagWebService
): ViewModel() {

    // FOR DATA
    private var result: RssResponse? = null
    private var article: BastamagArticle? = null

    init {
        fetchBastamagRss()
        fetchArticle()
    }

    private fun fetchBastamagRss() = viewModelScope.launch{
        result = bastamagRssService.getRssArticles()
    }

    private fun fetchArticle() = viewModelScope.launch {
        article = BastamagArticle(bastamagWebService.getArticle("reformes-police-Defund-police-cameras-pietons-desarmement"), 0)
    }

    // --- GETTERS ---

    fun getResults() = result

    fun getArticle() = article
}