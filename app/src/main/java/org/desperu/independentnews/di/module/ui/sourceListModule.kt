package org.desperu.independentnews.di.module.ui

import org.desperu.independentnews.base.ui.BaseBindingFragment
import org.desperu.independentnews.ui.sources.fragment.sourceList.SourceListInterface
import org.koin.dsl.module

/**
 * Koin module which provide dependencies related to sources list fragment.
 */
val sourceListModule = module { // TODO to remove use activity module ...

    /**
     * Provides a SourceListInterface from the instance of SourceListFragment.
     */
    single { (fragment: BaseBindingFragment) ->
        fragment as SourceListInterface
    }
}