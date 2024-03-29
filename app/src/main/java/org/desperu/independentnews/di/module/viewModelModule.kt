package org.desperu.independentnews.di.module

import androidx.databinding.ObservableField
import org.desperu.independentnews.base.ui.BaseBindingActivity
import org.desperu.independentnews.base.ui.BaseBindingFragment
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.database.SourceWithData
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListViewModel
import org.desperu.independentnews.ui.settings.SettingsViewModel
import org.desperu.independentnews.ui.showArticle.ArticleViewModel
import org.desperu.independentnews.ui.showArticle.ImageRouter
import org.desperu.independentnews.ui.showImages.fragment.ImageViewModel
import org.desperu.independentnews.ui.sources.fragment.sourceDetail.SourceDetailViewModel
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
            get(),
            get { parametersOf(fragment) }
        )
    }

    /**
     * Provides the ArticleViewModel instance.
     */
    viewModel { (article: Article, router: ImageRouter) ->
        ArticleViewModel(
            ObservableField(article),
            get(),
            get(),
            router
        )
    }

    // TODO which is the better way ? interface as param in vm or only property ??
    /**
     * Provides the SourceListViewModel instance.
     */
    viewModel {
        SourcesListViewModel(
            get(),
            get()
        )
    }

    /**
     * Provides the SourceDetailViewModel instance.
     */
    viewModel { (sourceWithData: SourceWithData, fragment: BaseBindingFragment) ->
        SourceDetailViewModel(
            sourceWithData,
            get { parametersOf(fragment) }
        )
    }

    /**
     * Provides the ImageViewModel instance.
     */
    viewModel { (imageUrl: Any) ->
        ImageViewModel(imageUrl)
    }

    /**
     * Provides the SettingsViewModel instance.
     */
    viewModel { (activity: BaseBindingActivity) ->
        SettingsViewModel(
            get(),
            get { parametersOf(activity) }
        )
    }
}