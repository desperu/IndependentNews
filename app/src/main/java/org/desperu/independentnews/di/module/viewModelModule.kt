package org.desperu.independentnews.di.module

import org.desperu.independentnews.base.ui.BaseBindingActivity
import org.desperu.independentnews.base.ui.BaseBindingFragment
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.models.SourceWithData
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListViewModel
import org.desperu.independentnews.ui.settings.SettingsViewModel
import org.desperu.independentnews.ui.showArticle.ArticleViewModel
import org.desperu.independentnews.ui.showArticle.ImageRouter
import org.desperu.independentnews.ui.showImages.fragment.ShowImageViewModel
import org.desperu.independentnews.ui.sources.fragment.sourceDetail.SourceDetailInterface
import org.desperu.independentnews.ui.sources.fragment.sourceDetail.SourceDetailViewModel
import org.desperu.independentnews.ui.sources.fragment.sourceList.SourceListInterface
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
            get { parametersOf(fragment) }, // TODO remove from parameter, and call get with KoinComponent support
            get()
        )
    }

    /**
     * To handle definition error
     */
    single {
//        ArticleRouter()
        // TODO try to separate view Model module by activity.
        //  should be better to use factory koin instance,
        //  and no cast for interface, and no module for frag, only activity
        //  ----It's not the problem, koin module error but above, the real error !!! ------
    }

    /**
     * Provides the ArticleViewModel instance.
     */
    viewModel { (article: Article, router: ImageRouter) ->
        ArticleViewModel(
            article,
            router
        )
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
     * Provides the SourceDetailViewModel instance.
     */
    viewModel { (sourceWithData: SourceWithData, fragment: BaseBindingFragment) ->
        SourceDetailViewModel(
            sourceWithData,
            fragment as SourceDetailInterface
        )
    }

    /**
     * Provides the ShowImageViewModel instance.
     */
    viewModel { (imageUrl: String) ->
        ShowImageViewModel(imageUrl)
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