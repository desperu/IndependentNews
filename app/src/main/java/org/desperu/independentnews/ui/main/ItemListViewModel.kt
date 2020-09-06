package org.desperu.independentnews.ui.main

import android.view.View
import androidx.lifecycle.ViewModel
import org.desperu.independentnews.models.Article
import org.koin.java.KoinJavaComponent

class ItemListViewModel(val article: Article): ViewModel() {

    // FOR DATA
    private val mainInterface: MainInterface by KoinJavaComponent.inject(MainInterface::class.java)

    // ------------
    // LISTENERS
    // ------------

    val onClickImage = View.OnClickListener {
        mainInterface.navigateToShowArticle(article, it)
    }
}