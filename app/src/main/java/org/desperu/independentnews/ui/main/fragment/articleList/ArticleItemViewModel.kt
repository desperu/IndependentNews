package org.desperu.independentnews.ui.main.fragment.articleList

import android.view.View
import android.view.View.OnClickListener
import androidx.lifecycle.ViewModel
import org.desperu.independentnews.R
import org.desperu.independentnews.models.Article
import org.koin.core.KoinComponent
import org.koin.core.get

/**
 * View Model witch provide data for article item.
 *
 * @property article    the given article data for this view model.
 * @property router     the router that allows redirection of the user.
 *
 * @constructor Instantiates a new ArticleItemViewModel.
 *
 * @param article       the given article data for this view model to set.
 */
class ArticleItemViewModel(val article: Article): ViewModel(), KoinComponent {

    // FOR DATA
    private val router: ArticleRouter get() = get()

    // ------------
    // LISTENERS
    // ------------

    /**
     * On click image listener.
     */
    val onClickImage = OnClickListener {
        router.openShowArticle(article, it)
    }

    /**
     * On click title listener.
     */
    val onClickTitle = OnClickListener {
        router.openShowArticle(article, (it.parent as View).findViewById(R.id.image))
    }

    /**
     * On click description container listener.
     */
    val onClickDescriptionContainer = OnClickListener {
        router.openShowArticle(article, (it.parent as View).findViewById(R.id.image))
    }
}