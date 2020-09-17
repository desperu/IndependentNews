package org.desperu.independentnews.ui.main

import android.view.View
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
                    private val router: ArticleRouter
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
        itemListVM = bastamagRepository.getRssArticles()?.map { ItemListViewModel(it, this@MainViewModel) }
        itemListVM?.let {
            mainInterface.getRecyclerAdapter()?.apply {
                updateList(it.toMutableList())
                notifyDataSetChanged()
            }
        }
    }

//    private fun fetchArticle() = viewModelScope.launch {
//        article = BastamagArticle(bastamagRepository.getArticle("reformes-police-Defund-police-cameras-pietons-desarmement"))
//        title?.value = (article as BaseHtmlArticle).getTitle().toString()
//        article?.let { title?.value = it.toArticle().title }
//    }

    /**
     * Perform article's click user redirection to the show article activity.
     * @param article the clicked article to show.
     * @param clickedView the clicked image view to animate.
     */
    internal fun onClickArticle(article: Article, clickedView: View) {
        itemListVM?.map { it.article }?.let {
            val position = it.indexOf(article)
            router.openShowArticle(it, position, clickedView)
        }
    }

    internal fun getArticlePosition(article: Article): Int? {
        return itemListVM?.map { it.article }?.indexOf(article)
    }

    // --- GETTERS ---

    fun getArticleList(): List<Article>? = itemListVM?.map { it.article }

    fun getArticle() = article
}