package org.desperu.independentnews.ui.main

import android.view.View.OnClickListener
import androidx.lifecycle.ViewModel
import org.desperu.independentnews.R
import org.desperu.independentnews.models.Article

// TODO to comment
class ItemListViewModel(val article: Article,
                        private val mainViewModel: MainViewModel
): ViewModel() {

    val position get() = mainViewModel.getArticlePosition(article)

    // ------------
    // LISTENERS
    // ------------

    val onClickImage = OnClickListener {
        mainViewModel.onClickArticle(article, it)
    }

    val onClickDescriptionContainer = OnClickListener {
        mainViewModel.onClickArticle(article, it.rootView.findViewById(R.id.image))
    }
}