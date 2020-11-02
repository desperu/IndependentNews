package org.desperu.independentnews.di.module

import org.desperu.independentnews.base.ui.BaseBindingActivity
import org.desperu.independentnews.base.ui.BaseBindingFragment
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.models.Source
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListInterface
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListViewModel
import org.desperu.independentnews.ui.settings.SettingsViewModel
import org.desperu.independentnews.ui.showArticle.ArticleViewModel
import org.desperu.independentnews.ui.sources.fragment.sourceList.SourceListInterface
import org.desperu.independentnews.ui.sources.fragment.sourceList.SourceViewModel
import org.desperu.independentnews.ui.sources.fragment.sourceList.SourcesListViewModel
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
            fragment as ArticleListInterface,
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
     * Provides the SourceListViewModel instance.
     */
    viewModel { (fragment: BaseBindingFragment) ->
        SourcesListViewModel(
            get(),
            fragment as SourceListInterface,
            get()
        )
    }

    /**
     * Provides the SourceViewModel instance.
     */
    viewModel { (source: Source, itemPosition: Int, fragment: BaseBindingFragment) -> // TODO not good here ...
        SourceViewModel(
            source,
            itemPosition,
            get()
        )
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