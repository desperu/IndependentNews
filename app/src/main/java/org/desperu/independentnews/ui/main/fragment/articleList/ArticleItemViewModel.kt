package org.desperu.independentnews.ui.main.fragment.articleList

import android.view.View
import android.view.View.OnClickListener
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.independentnews.R
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.repositories.IndependentNewsRepository
import org.koin.core.KoinComponent
import org.koin.core.get

/**
 * View Model witch provide data for article item.
 *
 * @property article            the given article data for this view model.
 * @property ideNewsRepository  the app repository interface witch provide database and network access.
 * @property router             the router that allows redirection of the user.
 *
 * @constructor Instantiates a new ArticleItemViewModel.
 *
 * @param article               the given article data for this view model to set.
 */
class ArticleItemViewModel(val article: Article): ViewModel(), KoinComponent {

    // FOR DATA
    private val ideNewsRepository: IndependentNewsRepository = get()
    private val router: ArticleRouter get() = get()
    val isRead = ObservableBoolean(article.read)

    // ------------
    // LISTENERS
    // ------------

    /**
     * On click image listener.
     */
    val onClickImage = OnClickListener {
        markAsRead()
        router.openShowArticle(article, it)
    }

    /**
     * On click title listener.
     */
    val onClickTitle = OnClickListener {
        markAsRead()
        router.openShowArticle(article, (it.parent as View).findViewById(R.id.image))
    }

    /**
     * On click description container listener.
     */
    val onClickDescriptionContainer = OnClickListener {
        markAsRead()
        router.openShowArticle(article, (it.parent as View).findViewById(R.id.image))
    }

    // ------------
    // UTILS
    // ------------

    /**
     * Mark the article as read in current list and database.
     */
    private fun markAsRead() = viewModelScope.launch(Dispatchers.IO) {
        isRead.set(true)
        ideNewsRepository.markArticleAsRead(article)
    }
}