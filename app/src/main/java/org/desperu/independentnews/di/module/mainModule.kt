package org.desperu.independentnews.di.module

import org.desperu.independentnews.base.BaseActivity
import org.desperu.independentnews.ui.main.MainInterface
import org.koin.dsl.module

/**
 * Koin module which provide dependencies related to list filters.
 */
val mainModule = module {

    /**
     * Provides a MainCommunication interface from the instance of MainActivity.
     */
    single { (activity: BaseActivity) ->
        activity as MainInterface
    }
}