package org.desperu.independentnews.di.module.ui

import org.desperu.independentnews.base.ui.BaseBindingActivity
import org.desperu.independentnews.ui.sources.SourceRouter
import org.desperu.independentnews.ui.sources.SourceRouterImpl
import org.desperu.independentnews.ui.sources.SourcesInterface
import org.koin.dsl.module

/**
 * Koin module which provide dependencies related to sources activity.
 */
val sourcesModule = module {

    /**
     * Provides a SourcesInterface from the instance of SourcesActivity.
     */
    single { (activity: BaseBindingActivity) ->
        activity as SourcesInterface
    }

    /**
     * Provides a SourceRouter interface from the instance of SourcesActivity.
     */
    single<SourceRouter> { (activity: BaseBindingActivity) ->
        SourceRouterImpl(
            activity
        )
    }
}