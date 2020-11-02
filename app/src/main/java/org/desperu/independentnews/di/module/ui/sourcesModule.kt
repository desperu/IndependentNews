package org.desperu.independentnews.di.module.ui

import org.desperu.independentnews.base.ui.BaseActivity
import org.desperu.independentnews.base.ui.BaseBindingFragment
import org.desperu.independentnews.ui.sources.fragment.sourceList.SourceRouter
import org.desperu.independentnews.ui.sources.fragment.sourceList.SourceRouterImpl
import org.desperu.independentnews.ui.sources.SourcesInterface
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
}