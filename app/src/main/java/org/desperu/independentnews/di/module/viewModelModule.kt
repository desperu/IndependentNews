package org.desperu.independentnews.di.module

import org.desperu.independentnews.models.Article
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListFragment
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListViewModel
import org.desperu.independentnews.ui.showArticle.ArticleViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin module which provides view model dependencies.
 */
val viewModelModule = module {

    /**
     * Provides the ArticleListViewModel instance.
     */
    viewModel { (fragment: ArticleListFragment) ->
        ArticleListViewModel(
            get(),
            fragment,
            get()
        )
    }

    /**
     * Provides the ArticleViewModel instance.
     */
    viewModel { (article: Article) ->
        ArticleViewModel(article)
    }
}