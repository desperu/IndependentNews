package org.desperu.independentnews.ui.main.fragment.articleList

import android.view.View
import android.view.View.OnClickListener
import androidx.lifecycle.ViewModel
import org.desperu.independentnews.R
import org.desperu.independentnews.models.Article

/**
 * View Model witch provide data for article item.
 *
 * @param article the given article data for this view model.
 * @param router the router that allows redirection of the user.
 *
 * @constructor Instantiates a new ArticleItemViewModel.
 *
 * @property article the given article data for this view model to set.
 * @property router the router that allows redirection of the user to set.
 */
class ArticleItemViewModel(val article: Article,
                           private val router: ArticleRouter
): ViewModel() {

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
     * On click description container listener.
     */
    val onClickDescriptionContainer = OnClickListener {
        router.openShowArticle(article, (it.parent as View).findViewById(R.id.image))
    }
}