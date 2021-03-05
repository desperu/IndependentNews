package org.desperu.independentnews.ui.main.fragment.articleList

import android.util.Pair
import android.view.View
import android.view.View.OnClickListener
import androidx.core.view.doOnNextLayout
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.independentnews.R
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.repositories.IndependentNewsRepository
import org.desperu.independentnews.service.ResourceService
import org.desperu.independentnews.ui.main.MainInterface
import org.koin.core.KoinComponent
import org.koin.core.get

/**
 * View Model witch provide data for article item.
 *
 * @property article            the given article data for this view model.
 * @property ideNewsRepository  the app repository interface witch provide database and network access.
 * @property mainInterface      the interface that allow communication with main activity.
 * @property router             the router that allows redirection of the user.
 * @property isRead             true if is read, false otherwise.
 *
 * @constructor Instantiates a new ArticleItemViewModel.
 *
 * @param article               the given article data for this view model to set.
 */
class ArticleItemViewModel(val article: Article): ViewModel(), KoinComponent {

    // FOR DATA
    private val ideNewsRepository: IndependentNewsRepository = get()
    private val mainInterface: MainInterface = get()
    private val router: ArticleRouter get() = get()
    private val resource: ResourceService = get()
    val isRead = ObservableBoolean(article.read)

    // ------------
    // LISTENERS
    // ------------

    /**
     * On click image, title and description container listener.
     */
    val onClickArticle = OnClickListener {
        val imageView = (it.parent as View).findViewById<View>(R.id.image)
        val imageName = resource.getString(R.string.animation_main_to_show_article)

        mainInterface.showFilterMotion(false)
        router.openShowArticle(article, Pair(imageView, imageName))
        imageView.doOnNextLayout { markAsRead() }
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