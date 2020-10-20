package org.desperu.independentnews.di.module

import org.desperu.independentnews.base.ui.BaseBindingActivity
import org.desperu.independentnews.base.ui.BaseBindingFragment
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListViewModel
import org.desperu.independentnews.ui.settings.SettingsInterface
import org.desperu.independentnews.ui.settings.SettingsViewModel
import org.desperu.independentnews.ui.showArticle.ArticleViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

/**
 * Koin module which provides view model dependencies.
 */
val viewModelModule = module {

    /**
     * Provides the ArticleListViewModel instance.
     */
    viewModel { (fragment: BaseBindingFragment) ->
        ArticleListViewModel(
            get(),
            get { parametersOf(fragment) },
            get()
        )
    }

    /**
     * Provides the ArticleViewModel instance.
     */
    viewModel { (article: Article) ->
        ArticleViewModel(article)
    }

    /**
     * Provides the SettingsViewModel instance.
     */
    viewModel { (activity: BaseBindingActivity) ->
        SettingsViewModel(
            get(),
            get(),
            get { parametersOf(activity) }
        )
    }
}