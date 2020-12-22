package org.desperu.independentnews.di.module.ui

import org.desperu.independentnews.base.ui.BaseActivity
import org.desperu.independentnews.base.ui.BaseBindingFragment
import org.desperu.independentnews.ui.showArticle.ImageRouter
import org.desperu.independentnews.ui.showArticle.ImageRouterImpl
import org.desperu.independentnews.ui.sources.SourcesInterface
import org.desperu.independentnews.ui.sources.fragment.SourceRouter
import org.desperu.independentnews.ui.sources.fragment.SourceRouterImpl
import org.desperu.independentnews.ui.sources.fragment.sourceDetail.SourceDetailInterface
import org.desperu.independentnews.ui.sources.fragment.sourceList.SourceListInterface
import org.koin.dsl.module

/**
 * Koin module which provide dependencies related to sources activity.
 */
val sourcesModule = module {

    /**
     * Provides a SourcesInterface from the instance of SourcesActivity.
     */
    single { (activity: BaseActivity) ->
        activity as SourcesInterface
    }

    /**
     * Provides a SourceRouter interface from the instance of SourceActivity.
     */
    single<SourceRouter> { (activity: BaseActivity) ->
        SourceRouterImpl(
            activity
        )
    }

    /**
     * Provides a ImageRouter from the instance of SourcesActivity.
     */
    single<ImageRouter>(override = true) { (activity: BaseActivity) ->
        ImageRouterImpl(activity)
    }

    /**
     * Provides a SourceListInterface from the instance of SourceListFragment.
     */
    single { (fragment: BaseBindingFragment) ->
        fragment as SourceListInterface
    }

    /**
     * Provides a SourceDetailInterface from the instance of SourceDetailFragment.
     */
    single { (fragment: BaseBindingFragment) ->
        fragment as SourceDetailInterface
    }
}