package org.desperu.independentnews.ui.main

import android.view.View
import android.view.View.OnClickListener
import androidx.lifecycle.ViewModel
import org.desperu.independentnews.R
import org.desperu.independentnews.models.Article

/**
 * View Model witch provide data for article item.
 *
 * @param article the given article data for this view model.
 * @param mainViewModel the instance of parent view model.
 *
 * @constructor Instantiates a new ItemListViewModel.
 *
 * @property article the given article data for this view model to set.
 * @property mainViewModel the instance of parent view model to set.
 */
class ItemListViewModel(val article: Article,
                        private val mainViewModel: MainViewModel
): ViewModel() {

    // FOR DATA
    val position: Int? get() = mainViewModel.getArticlePosition(article)

    // ------------
    // LISTENERS
    // ------------

    /**
     * On click image listener.
     */
    val onClickImage = OnClickListener {
        mainViewModel.onClickArticle(article, it)
    }

    /**
     * On click description container listener.
     */
    val onClickDescriptionContainer = OnClickListener {
        mainViewModel.onClickArticle(article, (it.parent as View).findViewById(R.id.image))
    }
}